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

package tr.com.infumia.small.injector.helper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import tr.com.infumia.small.downloader.DependencyDownloader;
import tr.com.infumia.small.downloader.DependencyDownloaderFactory;
import tr.com.infumia.small.downloader.output.DependencyOutputWriterFactory;
import tr.com.infumia.small.downloader.output.OutputWriterFactory;
import tr.com.infumia.small.downloader.strategy.FilePathStrategy;
import tr.com.infumia.small.downloader.verify.DependencyVerifierFactory;
import tr.com.infumia.small.injector.DependencyInjectorFactory;
import tr.com.infumia.small.relocation.Relocator;
import tr.com.infumia.small.relocation.RelocatorFactory;
import tr.com.infumia.small.relocation.helper.RelocationHelper;
import tr.com.infumia.small.relocation.helper.RelocationHelperFactory;
import tr.com.infumia.small.resolver.DependencyResolver;
import tr.com.infumia.small.resolver.DependencyResolverFactory;
import tr.com.infumia.small.resolver.ResolutionResult;
import tr.com.infumia.small.resolver.data.DependencyData;
import tr.com.infumia.small.resolver.data.Repository;
import tr.com.infumia.small.resolver.enquirer.RepositoryEnquirerFactory;
import tr.com.infumia.small.resolver.mirrors.MirrorSelector;
import tr.com.infumia.small.resolver.reader.dependency.DependencyDataProviderFactory;

public final class InjectionHelperFactory {

  private final Path downloadDirectoryPath;

  private final DependencyDownloaderFactory downloaderFactory;

  private final RepositoryEnquirerFactory enquirerFactory;

  private final MirrorSelector mirrorSelector;

  private final RelocationHelperFactory relocationHelperFactory;

  private final RelocatorFactory relocatorFactory;

  private final DependencyResolverFactory resolverFactory;

  private final DependencyVerifierFactory verifier;

  public InjectionHelperFactory(final Path downloadDirectoryPath, final RelocatorFactory relocatorFactory, final DependencyDataProviderFactory dataProviderFactory, final RelocationHelperFactory relocationHelperFactory, final DependencyInjectorFactory injectorFactory, final DependencyResolverFactory resolverFactory, final RepositoryEnquirerFactory enquirerFactory, final DependencyDownloaderFactory downloaderFactory, final DependencyVerifierFactory verifier, final MirrorSelector mirrorSelector) {
    this.downloadDirectoryPath = downloadDirectoryPath;
    this.relocatorFactory = relocatorFactory;
    this.relocationHelperFactory = relocationHelperFactory;
    this.resolverFactory = resolverFactory;
    this.enquirerFactory = enquirerFactory;
    this.downloaderFactory = downloaderFactory;
    this.verifier = verifier;
    this.mirrorSelector = mirrorSelector;
  }

  public InjectionHelper create(final DependencyData data, final Map<String, ResolutionResult> preResolvedResults) throws IOException, NoSuchAlgorithmException, URISyntaxException {
    final Collection<Repository> repositories = this.mirrorSelector
      .select(data.getRepositories(), data.getMirrors());
    final Relocator relocator = this.relocatorFactory.create(Collections.emptySet());
    final RelocationHelper relocationHelper = this.relocationHelperFactory.create(relocator);
    final FilePathStrategy filePathStrategy = FilePathStrategy.createDefault(this.downloadDirectoryPath.toFile());
    final OutputWriterFactory outputWriterFactory = new DependencyOutputWriterFactory(filePathStrategy);
    final DependencyResolver resolver = this.resolverFactory.create(repositories, preResolvedResults, this.enquirerFactory);
    final DependencyDownloader downloader = this.downloaderFactory.create(outputWriterFactory, resolver, this.verifier.create(resolver));
    return new InjectionHelper(downloader, relocationHelper);
  }
}
