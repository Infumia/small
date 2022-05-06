//
// MIT License
//
// Copyright (c) 2021 Vaishnav Anil
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package tr.com.infumia.small.resolver;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import tr.com.infumia.small.logging.LogDispatcher;
import tr.com.infumia.small.logging.ProcessLogger;
import tr.com.infumia.small.resolver.data.Dependency;
import tr.com.infumia.small.resolver.data.Repository;
import tr.com.infumia.small.resolver.enquirer.RepositoryEnquirer;
import tr.com.infumia.small.resolver.enquirer.RepositoryEnquirerFactory;
import tr.com.infumia.small.resolver.pinger.URLPinger;

public final class CachingDependencyResolver implements DependencyResolver {

  private static final String FAILED_RESOLUTION_MESSAGE = "[FAILED TO RESOLVE]";

  private static final ProcessLogger LOGGER = LogDispatcher.getMediatingLogger();

  private final Map<Dependency, ResolutionResult> cachedResults = new ConcurrentHashMap<>();

  private final Map<String, ResolutionResult> preResolvedResults;

  private final Map<Dependency, List<String>> predefinedRepositories;

  private final Collection<RepositoryEnquirer> repositories;

  private final URLPinger urlPinger;

  public CachingDependencyResolver(final URLPinger urlPinger, final Collection<Repository> repositories, final RepositoryEnquirerFactory enquirerFactory, final Map<String, ResolutionResult> preResolvedResults,
                                   final Map<Dependency, List<String>> predefinedRepositories) {
    this.urlPinger = urlPinger;
    this.preResolvedResults = new ConcurrentHashMap<>(preResolvedResults);
    this.repositories = repositories.stream()
      .map(enquirerFactory::create)
      .collect(Collectors.toSet());
    this.predefinedRepositories = predefinedRepositories;
  }

  public CachingDependencyResolver(final URLPinger urlPinger, final Collection<Repository> repositories, final RepositoryEnquirerFactory enquirerFactory, final Map<String, ResolutionResult> preResolvedResults) {
    this(urlPinger, repositories, enquirerFactory, preResolvedResults, new HashMap<>());
  }

  @Override
  public Optional<ResolutionResult> resolve(final Dependency dependency) {
    return Optional.ofNullable(this.cachedResults.computeIfAbsent(dependency, this::attemptResolve));
  }

  private ResolutionResult attemptResolve(final Dependency dependency) {
    final ResolutionResult preResolvedResult = this.preResolvedResults.get(dependency.toString());
    if (preResolvedResult != null) {
      if (preResolvedResult.isAggregator()) {
        return preResolvedResult;
      }
      final boolean isDependencyURLValid = this.urlPinger.ping(preResolvedResult.getDependencyURL());
      final URL checksumURL = preResolvedResult.getChecksumURL();
      final boolean isChecksumURLValid = checksumURL == null || this.urlPinger.ping(checksumURL);
      if (isDependencyURLValid && isChecksumURLValid) {
        return preResolvedResult;
      }
    }
    final List<RepositoryEnquirer> enquirers = new ArrayList<>(this.repositories);
    final List<String> predefinedRepositories = this.predefinedRepositories.get(dependency);
    if (predefinedRepositories != null) {
      final Optional<ResolutionResult> result = enquirers.stream()
        .filter(enquirer -> predefinedRepositories.contains(enquirer.repository().getName()))
        .map(enquirer -> enquirer.enquire(dependency))
        .filter(Objects::nonNull)
        .findFirst();
      if (result.isPresent()) {
        final ResolutionResult found = result.get();
        final String foundUrl = Optional.ofNullable(found.getDependencyURL())
          .map(Objects::toString)
          .orElse(CachingDependencyResolver.FAILED_RESOLUTION_MESSAGE);
        CachingDependencyResolver.LOGGER.debug("Resolved {0} @ {1}", dependency.getArtifactId(), foundUrl);
        return found;
      }
    }
    final Optional<ResolutionResult> result = enquirers.stream().parallel()
      .map(enquirer -> enquirer.enquire(dependency))
      .filter(Objects::nonNull)
      .findFirst();
    final String resolvedResult = result.map(ResolutionResult::getDependencyURL).map(Objects::toString).orElse(CachingDependencyResolver.FAILED_RESOLUTION_MESSAGE);
    CachingDependencyResolver.LOGGER.debug("Resolved {0} @ {1}", dependency.getArtifactId(), resolvedResult);
    return result.orElse(null);
  }
}
