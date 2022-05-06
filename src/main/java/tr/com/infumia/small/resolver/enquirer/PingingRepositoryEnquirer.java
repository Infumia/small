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

package tr.com.infumia.small.resolver.enquirer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import tr.com.infumia.small.logging.LogDispatcher;
import tr.com.infumia.small.logging.ProcessLogger;
import tr.com.infumia.small.resolver.ResolutionResult;
import tr.com.infumia.small.resolver.data.Dependency;
import tr.com.infumia.small.resolver.data.Repository;
import tr.com.infumia.small.resolver.pinger.URLPinger;
import tr.com.infumia.small.resolver.strategy.PathResolutionStrategy;

public final class PingingRepositoryEnquirer implements RepositoryEnquirer {

  private static final ProcessLogger LOGGER = LogDispatcher.getMediatingLogger();

  private final PathResolutionStrategy checksumURLCreationStrategy;

  private final PathResolutionStrategy dependencyURLCreationStrategy;

  private final PathResolutionStrategy pomURLCreationStrategy;

  private final Repository repository;

  private final URLPinger urlPinger;

  public PingingRepositoryEnquirer(final Repository repository, final PathResolutionStrategy urlCreationStrategy, final PathResolutionStrategy checksumURLCreationStrategy, final PathResolutionStrategy pomURLCreationStrategy, final URLPinger urlPinger) {
    this.repository = repository;
    this.dependencyURLCreationStrategy = urlCreationStrategy;
    this.checksumURLCreationStrategy = checksumURLCreationStrategy;
    this.pomURLCreationStrategy = pomURLCreationStrategy;
    this.urlPinger = urlPinger;
  }

  @Override
  public ResolutionResult enquire(final Dependency dependency) {
    PingingRepositoryEnquirer.LOGGER.debug("Enquiring repositories to find {0}", dependency.getArtifactId());
    final Optional<URL> resolvedDependency = this.dependencyURLCreationStrategy.pathTo(this.repository, dependency)
      .stream().map(path -> {
        try {
          return new URL(path);
        } catch (final MalformedURLException e) {
          return null;
        }
      }).filter(this.urlPinger::ping)
      .findFirst();
    if (!resolvedDependency.isPresent()) {
      return this.pomURLCreationStrategy.pathTo(this.repository, dependency).stream().map(path -> {
          try {
            return new URL(path);
          } catch (final MalformedURLException e) {
            return null;
          }
        }).filter(this.urlPinger::ping)
        .findFirst()
        .map(url -> new ResolutionResult(this.repository, null, null, true))
        .orElse(null);
    }
    final Optional<URL> resolvedChecksum = this.checksumURLCreationStrategy.pathTo(this.repository, dependency)
      .stream().map(path -> {
        try {
          return new URL(path);
        } catch (final MalformedURLException e) {
          return null;
        }
      }).filter(this.urlPinger::ping)
      .findFirst();
    return new ResolutionResult(this.repository, resolvedDependency.get(), resolvedChecksum.orElse(null), false);
  }

  @Override
  public Repository repository() {
    return this.repository;
  }
}
