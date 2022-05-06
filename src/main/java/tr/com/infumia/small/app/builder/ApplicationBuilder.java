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

package tr.com.infumia.small.app.builder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import tr.com.infumia.small.app.Application;
import tr.com.infumia.small.downloader.DependencyDownloaderFactory;
import tr.com.infumia.small.downloader.URLDependencyDownloaderFactory;
import tr.com.infumia.small.downloader.output.DependencyOutputWriterFactory;
import tr.com.infumia.small.downloader.output.OutputWriterFactory;
import tr.com.infumia.small.downloader.strategy.ChecksumFilePathStrategy;
import tr.com.infumia.small.downloader.strategy.FilePathStrategy;
import tr.com.infumia.small.downloader.verify.ChecksumCalculator;
import tr.com.infumia.small.downloader.verify.ChecksumDependencyVerifierFactory;
import tr.com.infumia.small.downloader.verify.DependencyVerifierFactory;
import tr.com.infumia.small.downloader.verify.FileChecksumCalculator;
import tr.com.infumia.small.downloader.verify.PassthroughDependencyVerifierFactory;
import tr.com.infumia.small.injector.DependencyInjector;
import tr.com.infumia.small.injector.DependencyInjectorFactory;
import tr.com.infumia.small.injector.SimpleDependencyInjectorFactory;
import tr.com.infumia.small.injector.helper.InjectionHelperFactory;
import tr.com.infumia.small.injector.loader.Injectable;
import tr.com.infumia.small.logging.LogDispatcher;
import tr.com.infumia.small.logging.MediatingProcessLogger;
import tr.com.infumia.small.logging.ProcessLogger;
import tr.com.infumia.small.relocation.JarFileRelocatorFactory;
import tr.com.infumia.small.relocation.RelocatorFactory;
import tr.com.infumia.small.relocation.facade.JarRelocatorFacadeFactory;
import tr.com.infumia.small.relocation.facade.ReflectiveJarRelocatorFacadeFactory;
import tr.com.infumia.small.relocation.helper.RelocationHelperFactory;
import tr.com.infumia.small.relocation.helper.VerifyingRelocationHelperFactory;
import tr.com.infumia.small.relocation.meta.FlatFileMetaMediatorFactory;
import tr.com.infumia.small.relocation.meta.MetaMediatorFactory;
import tr.com.infumia.small.resolver.CachingDependencyResolverFactory;
import tr.com.infumia.small.resolver.DependencyResolverFactory;
import tr.com.infumia.small.resolver.data.Repository;
import tr.com.infumia.small.resolver.enquirer.PingingRepositoryEnquirerFactory;
import tr.com.infumia.small.resolver.enquirer.RepositoryEnquirerFactory;
import tr.com.infumia.small.resolver.mirrors.MirrorSelector;
import tr.com.infumia.small.resolver.mirrors.SimpleMirrorSelector;
import tr.com.infumia.small.resolver.pinger.HttpURLPinger;
import tr.com.infumia.small.resolver.pinger.URLPinger;
import tr.com.infumia.small.resolver.reader.dependency.DependencyDataProvider;
import tr.com.infumia.small.resolver.reader.dependency.DependencyDataProviderFactory;
import tr.com.infumia.small.resolver.reader.dependency.ExternalDependencyDataProviderFactory;
import tr.com.infumia.small.resolver.reader.dependency.GsonDependencyDataProviderFactory;
import tr.com.infumia.small.resolver.reader.facade.GsonFacadeFactory;
import tr.com.infumia.small.resolver.reader.facade.ReflectiveGsonFacadeFactory;
import tr.com.infumia.small.resolver.reader.resolution.GsonPreResolutionDataProviderFactory;
import tr.com.infumia.small.resolver.reader.resolution.PreResolutionDataProvider;
import tr.com.infumia.small.resolver.reader.resolution.PreResolutionDataProviderFactory;
import tr.com.infumia.small.resolver.strategy.MavenChecksumPathResolutionStrategy;
import tr.com.infumia.small.resolver.strategy.MavenPathResolutionStrategy;
import tr.com.infumia.small.resolver.strategy.MavenPomPathResolutionStrategy;
import tr.com.infumia.small.resolver.strategy.MavenSnapshotPathResolutionStrategy;
import tr.com.infumia.small.resolver.strategy.MediatingPathResolutionStrategy;
import tr.com.infumia.small.resolver.strategy.PathResolutionStrategy;

/**
 * Serves as a configuration for different components small will use during injection.
 * Allows completely modifying and adding upon onto the default behaviour when needed.
 */
public abstract class ApplicationBuilder {

  // Default directory where small will try to install dependencies to
  // ~/.small/
  private static final Path DEFAULT_DOWNLOAD_DIRECTORY;

  private final String applicationName;

  private DependencyDataProviderFactory dataProviderFactory;

  private URL dependencyFileUrl;

  private Path downloadDirectoryPath;

  private DependencyDownloaderFactory downloaderFactory;

  private RepositoryEnquirerFactory enquirerFactory;

  private DependencyInjectorFactory injectorFactory;

  private Collection<Repository> internalRepositories;

  private ProcessLogger logger;

  private MirrorSelector mirrorSelector;

  private DependencyDataProviderFactory moduleDataProviderFactory;

  private PreResolutionDataProviderFactory preResolutionDataProviderFactory;

  private URL preResolutionFileUrl;

  private RelocationHelperFactory relocationHelperFactory;

  private RelocatorFactory relocatorFactory;

  private DependencyResolverFactory resolverFactory;

  private DependencyVerifierFactory verifierFactory;

  static {
    final String userHome = System.getProperty("user.home");
    final String defaultPath = String.format("%s/.small", userHome);
    DEFAULT_DOWNLOAD_DIRECTORY = new File(defaultPath).toPath();
  }

  /**
   * Generate a application builder for an application with given name.
   *
   * @param applicationName Name of your application/project. This exists to uniquely identify relocations.
   */
  protected ApplicationBuilder(final String applicationName) {
    this.applicationName = Objects.requireNonNull(applicationName, "Requires non-null application name!");
  }

  /**
   * Creates an ApplicationBuilder that allows loading into current classloader.
   *
   * @param name Name of your application/project. This exists to uniquely identify relocations.
   *
   * @return ApplicationBuilder that allows loading into current classloader.
   *
   * @throws URISyntaxException on invalid download path
   * @throws ReflectiveOperationException on attempting to use wrong appender, possible due to unexpected result in
   *   jvm version detection
   * @throws NoSuchAlgorithmException on Selected/Default digest algorithm not existing.
   * @throws IOException on File IO failure
   */
  public static ApplicationBuilder appending(final String name) throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
    return InjectingApplicationBuilder.createAppending(name);
  }

  /**
   * Creates an ApplicationBuilder that allows loading into any given {@link Injectable} instance.
   * For a simple isolated classloader, use {@link tr.com.infumia.small.injector.loader.IsolatedInjectableClassLoader}
   * You can create a {@link Injectable} version of any classloader using
   * {@link tr.com.infumia.small.injector.loader.InjectableFactory#create(Path, Collection)}
   * Alternatively you can provide a custom implementation of {@link Injectable} to specify how the dependencies must be
   * added to the classloader.
   *
   * @param name Name of your application/project. This exists to uniquely identify relocations.
   *
   * @return ApplicationBuilder that allows loading into any given {@link Injectable} instance.
   */
  public static ApplicationBuilder injecting(final String name, final Injectable injectable) {
    return new InjectingApplicationBuilder(name, injectable);
  }

  /**
   * Creates an ApplicationBuilder that allows jar-in-jar dependency loading.
   *
   * @param name Name of your application/project. This exists to uniquely identify relocations.
   * @param config Basic configuration that isolated classloader requires.
   * @param args Arguments to pass to created Application class (specified in <code>config</code>).
   *
   * @return ApplicationBuilder that allows jar-in-jar dependency loading.
   */
  public static ApplicationBuilder isolated(final String name, final IsolationConfiguration config, final Object[] args) {
    return new IsolatedApplicationBuilder(name, config, args);
  }

  public final Application build() throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException {
    final MediatingProcessLogger mediatingLogger = LogDispatcher.getMediatingLogger();
    final ProcessLogger logger = this.getLogger();
    mediatingLogger.addLogger(logger);
    final Application result = this.buildApplication();
    mediatingLogger.removeLogger(logger);
    return result;
  }

  /**
   * Factory that produces {@link DependencyDataProvider} to handle `dependencyFileUrl` (by default small.json)
   * Used to fetch the `small.json` file of current jar-file.
   *
   * @param dataProviderFactory Factory that produces DataProvider to handle `dependencyFileUrl`
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder dataProviderFactory(final DependencyDataProviderFactory dataProviderFactory) {
    this.dataProviderFactory = dataProviderFactory;
    return this;
  }

  /**
   * URL to the json configuration file that defines the dependencies/repositories/mirrors
   *
   * @param dependencyFileUrl URL to the json configuration file (Default being small.json inside jar root generated
   *   by the gradle plugin)
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder dependencyFileUrl(final URL dependencyFileUrl) {
    this.dependencyFileUrl = dependencyFileUrl;
    return this;
  }

  /**
   * Directory to which small will attempt to download dependency files into
   *
   * @param downloadDirectoryPath Download directory for dependencies.
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder downloadDirectoryPath(final Path downloadDirectoryPath) {
    this.downloadDirectoryPath = downloadDirectoryPath;
    return this;
  }

  /**
   *
   */
  public final ApplicationBuilder downloaderFactory(final DependencyDownloaderFactory downloaderFactory) {
    this.downloaderFactory = downloaderFactory;
    return this;
  }

  /**
   * Factory that produces a {@link tr.com.infumia.small.resolver.enquirer.RepositoryEnquirer}
   *
   * @param enquirerFactory Factory that produces a RepositoryEnquirer
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder enquirerFactory(final RepositoryEnquirerFactory enquirerFactory) {
    this.enquirerFactory = enquirerFactory;
    return this;
  }

  /**
   * Factory that produces a {@link DependencyInjector} using <code>relocator</code>
   * {@link DependencyInjector} decides how any given {@link tr.com.infumia.small.resolver.data.DependencyData} is
   * injected
   * into an {@link Injectable}
   *
   * @param injectorFactory Factory that produces a DependencyInjector
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder injectorFactory(final DependencyInjectorFactory injectorFactory) {
    this.injectorFactory = injectorFactory;
    return this;
  }

  public final ApplicationBuilder internalRepositories(final Collection<Repository> repositories) {
    this.internalRepositories = repositories;
    return this;
  }

  public final ApplicationBuilder logger(final ProcessLogger logger) {
    this.logger = logger;
    return this;
  }

  public final ApplicationBuilder mirrorSelector(final MirrorSelector mirrorSelector) {
    this.mirrorSelector = mirrorSelector;
    return this;
  }

  /**
   * Factory that produces DataProvider for modules in jar-in-jar classloading. Ignored if not using
   * jar-in-jar/isolated(...)
   * Used to fetch the `small.json` file of each submodule.
   *
   * @param moduleDataProviderFactory Factory that produces DataProvider for modules in jar-in-jar
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder moduleDataProviderFactory(final DependencyDataProviderFactory moduleDataProviderFactory) {
    this.moduleDataProviderFactory = moduleDataProviderFactory;
    return this;
  }

  /**
   * Factory that produces {@link PreResolutionDataProvider} to handle `preResolutionFileUrl` (by default
   * small-resolutions.json)
   * Used to fetch the `small.json` file of current jar-file.
   *
   * @param preResolutionDataProviderFactory Factory that produces DataProvider to handle `preResolutionFileUrl`
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder preResolutionDataProviderFactory(final PreResolutionDataProviderFactory preResolutionDataProviderFactory) {
    this.preResolutionDataProviderFactory = preResolutionDataProviderFactory;
    return this;
  }

  /**
   * URL to the json configuration file that defines the resolutions for given dependencies
   *
   * @param preResolutionFileUrl URL to the json resolution configuration file (Default being small-resolutions.json
   *   inside jar root generated by the gradle plugin)
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder preResolutionFileUrl(final URL preResolutionFileUrl) {
    this.preResolutionFileUrl = preResolutionFileUrl;
    return this;
  }

  /**
   * Factory that produces a {@link tr.com.infumia.small.relocation.helper.RelocationHelper} using
   * <code>relocator</code>
   * This is an abstraction over {@link tr.com.infumia.small.relocation.Relocator}.
   * It decides the output file for relocation and includes extra steps such as jar verification.
   *
   * @param relocationHelperFactory Factory that produces a RelocationHelper
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder relocationHelperFactory(final RelocationHelperFactory relocationHelperFactory) {
    this.relocationHelperFactory = relocationHelperFactory;
    return this;
  }

  /**
   * Factory class that defines the construction of {@link tr.com.infumia.small.relocation.Relocator}
   * This deals with the actual relocation process.
   * The default implementation uses lucko/JarRelocator
   *
   * @param relocatorFactory Factory class to create Relocator
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder relocatorFactory(final RelocatorFactory relocatorFactory) {
    this.relocatorFactory = relocatorFactory;
    return this;
  }

  /**
   * Factory that produces a {@link DependencyResolverFactory}
   * {@link tr.com.infumia.small.resolver.DependencyResolver} deals with resolving the URLs to a given dependency from a
   * given collection of repositories
   *
   * @param resolverFactory Factory that produces a DependencyResolverFactory
   *
   * @return <code>this</code>
   */
  public final ApplicationBuilder resolverFactory(final DependencyResolverFactory resolverFactory) {
    this.resolverFactory = resolverFactory;
    return this;
  }

  public final ApplicationBuilder verifierFactory(final DependencyVerifierFactory verifierFactory) {
    this.verifierFactory = verifierFactory;
    return this;
  }

  protected final DependencyInjector createInjector() throws IOException, URISyntaxException, NoSuchAlgorithmException, ReflectiveOperationException {
    final InjectionHelperFactory injectionHelperFactory = new InjectionHelperFactory(
      this.getDownloadDirectoryPath(),
      this.getRelocatorFactory(),
      this.getDataProviderFactory(),
      this.getRelocationHelperFactory(),
      this.getInjectorFactory(),
      this.getResolverFactory(),
      this.getEnquirerFactory(),
      this.getDownloaderFactory(),
      this.getVerifierFactory(),
      this.getMirrorSelector()
    );
    return this.getInjectorFactory().create(injectionHelperFactory);
  }

  protected final String getApplicationName() {
    return this.applicationName;
  }

  protected final DependencyDataProviderFactory getDataProviderFactory() throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
    if (this.dataProviderFactory == null) {
      final GsonFacadeFactory gsonFacadeFactory = ReflectiveGsonFacadeFactory.create(this.getDownloadDirectoryPath(), this.getInternalRepositories());
      this.dataProviderFactory = new GsonDependencyDataProviderFactory(gsonFacadeFactory);
    }
    return this.dataProviderFactory;
  }

  protected final URL getDependencyFileUrl() {
    if (this.dependencyFileUrl == null) {
      this.dependencyFileUrl = this.getClass().getClassLoader().getResource("small.json");
    }
    return this.dependencyFileUrl;
  }

  protected final Path getDownloadDirectoryPath() {
    if (this.downloadDirectoryPath == null) {
      this.downloadDirectoryPath = ApplicationBuilder.DEFAULT_DOWNLOAD_DIRECTORY;
    }
    return this.downloadDirectoryPath;
  }

  protected final DependencyDownloaderFactory getDownloaderFactory() {
    if (this.downloaderFactory == null) {
      this.downloaderFactory = new URLDependencyDownloaderFactory();
    }
    return this.downloaderFactory;
  }

  protected final RepositoryEnquirerFactory getEnquirerFactory() {
    if (this.enquirerFactory == null) {
      final PathResolutionStrategy releaseStrategy = new MavenPathResolutionStrategy();
      final PathResolutionStrategy snapshotStrategy = new MavenSnapshotPathResolutionStrategy();
      final PathResolutionStrategy resolutionStrategy = new MediatingPathResolutionStrategy(releaseStrategy, snapshotStrategy);
      final PathResolutionStrategy pomURLCreationStrategy = new MavenPomPathResolutionStrategy();
      final PathResolutionStrategy checksumResolutionStrategy = new MavenChecksumPathResolutionStrategy("SHA-1", resolutionStrategy);
      final URLPinger urlPinger = new HttpURLPinger();
      this.enquirerFactory = new PingingRepositoryEnquirerFactory(resolutionStrategy, checksumResolutionStrategy, pomURLCreationStrategy, urlPinger);
    }
    return this.enquirerFactory;
  }

  protected final DependencyInjectorFactory getInjectorFactory() {
    if (this.injectorFactory == null) {
      this.injectorFactory = new SimpleDependencyInjectorFactory();
    }
    return this.injectorFactory;
  }

  protected final Collection<Repository> getInternalRepositories() throws MalformedURLException {
    if (this.internalRepositories == null) {
      this.internalRepositories = Collections.singleton(new Repository(new URL(SimpleMirrorSelector.ALT_CENTRAL_URL)));
    }
    return this.internalRepositories;
  }

  protected final ProcessLogger getLogger() {
    if (this.logger == null) {
      this.logger = (msg, args) -> {
      };
    }
    return this.logger;
  }

  protected final MirrorSelector getMirrorSelector() throws MalformedURLException {
    if (this.mirrorSelector == null) {
      this.mirrorSelector = new SimpleMirrorSelector(this.getInternalRepositories());
    }
    return this.mirrorSelector;
  }

  protected final DependencyDataProviderFactory getModuleDataProviderFactory() throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
    if (this.moduleDataProviderFactory == null) {
      final GsonFacadeFactory gsonFacadeFactory = ReflectiveGsonFacadeFactory.create(this.getDownloadDirectoryPath(), this.getInternalRepositories());
      this.moduleDataProviderFactory = new ExternalDependencyDataProviderFactory(gsonFacadeFactory);
    }
    return this.moduleDataProviderFactory;
  }

  protected final PreResolutionDataProviderFactory getPreResolutionDataProviderFactory() throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
    if (this.preResolutionDataProviderFactory == null) {
      final GsonFacadeFactory gsonFacadeFactory = ReflectiveGsonFacadeFactory.create(this.getDownloadDirectoryPath(), this.getInternalRepositories());
      this.preResolutionDataProviderFactory = new GsonPreResolutionDataProviderFactory(gsonFacadeFactory);
    }
    return this.preResolutionDataProviderFactory;
  }

  protected final URL getPreResolutionFileUrl() {
    if (this.preResolutionFileUrl == null) {
      this.preResolutionFileUrl = this.getClass().getClassLoader().getResource("small-resolutions.json");
    }
    return this.preResolutionFileUrl;
  }

  protected final RelocationHelperFactory getRelocationHelperFactory() throws NoSuchAlgorithmException, IOException, URISyntaxException {
    if (this.relocationHelperFactory == null) {
      final FileChecksumCalculator checksumCalculator = new FileChecksumCalculator("SHA-256");
      final FilePathStrategy pathStrategy = FilePathStrategy.createRelocationStrategy(this.getDownloadDirectoryPath().toFile(), this.getApplicationName());
      final MetaMediatorFactory mediatorFactory = new FlatFileMetaMediatorFactory();
      this.relocationHelperFactory = new VerifyingRelocationHelperFactory(checksumCalculator, pathStrategy, mediatorFactory);
    }
    return this.relocationHelperFactory;
  }

  protected final RelocatorFactory getRelocatorFactory() throws ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException {
    if (this.relocatorFactory == null) {
      final JarRelocatorFacadeFactory jarRelocatorFacadeFactory = ReflectiveJarRelocatorFacadeFactory.create(this.getDownloadDirectoryPath(), this.getInternalRepositories());
      this.relocatorFactory = new JarFileRelocatorFactory(jarRelocatorFacadeFactory);
    }
    return this.relocatorFactory;
  }

  protected final DependencyResolverFactory getResolverFactory() {
    if (this.resolverFactory == null) {
      final URLPinger pinger = new HttpURLPinger();
      this.resolverFactory = new CachingDependencyResolverFactory(pinger);
    }
    return this.resolverFactory;
  }

  protected final DependencyVerifierFactory getVerifierFactory() throws NoSuchAlgorithmException {
    if (this.verifierFactory == null) {
      final FilePathStrategy filePathStrategy = ChecksumFilePathStrategy.createStrategy(this.getDownloadDirectoryPath().toFile(), "SHA-1");
      final OutputWriterFactory checksumOutputFactory = new DependencyOutputWriterFactory(filePathStrategy);
      final DependencyVerifierFactory fallback = new PassthroughDependencyVerifierFactory();
      final ChecksumCalculator checksumCalculator = new FileChecksumCalculator("SHA-1");
      this.verifierFactory = new ChecksumDependencyVerifierFactory(checksumOutputFactory, fallback, checksumCalculator);
    }
    return this.verifierFactory;
  }

  protected abstract Application buildApplication() throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException;
}
