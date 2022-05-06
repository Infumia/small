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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import tr.com.infumia.small.app.Application;
import tr.com.infumia.small.injector.DependencyInjector;
import tr.com.infumia.small.injector.loader.InjectableClassLoader;
import tr.com.infumia.small.injector.loader.IsolatedInjectableClassLoader;
import tr.com.infumia.small.resolver.ResolutionResult;
import tr.com.infumia.small.resolver.data.DependencyData;
import tr.com.infumia.small.resolver.reader.dependency.DependencyDataProvider;
import tr.com.infumia.small.resolver.reader.resolution.PreResolutionDataProvider;
import tr.com.infumia.small.util.Modules;
import tr.com.infumia.small.util.Parameters;

public final class IsolatedApplicationBuilder extends ApplicationBuilder {

  private final Object[] arguments;

  private final IsolationConfiguration isolationConfiguration;

  public IsolatedApplicationBuilder(final String applicationName, final IsolationConfiguration isolationConfiguration, final Object[] arguments) {
    super(applicationName);
    this.isolationConfiguration = isolationConfiguration;
    this.arguments = arguments.clone();
  }

  @Override
  public Application buildApplication() throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException {
    final DependencyInjector injector = this.createInjector();
    final URL[] moduleUrls = Modules.extract(this.isolationConfiguration.getModuleExtractor(), this.isolationConfiguration.getModules());
    final InjectableClassLoader classLoader = new IsolatedInjectableClassLoader(moduleUrls, this.isolationConfiguration.getParentClassloader(), Collections.singleton(Application.class));
    final DependencyDataProvider dataProvider = this.getDataProviderFactory().create(this.getDependencyFileUrl());
    final DependencyData selfDependencyData = dataProvider.get();
    final PreResolutionDataProvider preResolutionDataProvider = this.getPreResolutionDataProviderFactory().create(this.getPreResolutionFileUrl());
    final Map<String, ResolutionResult> preResolutionResultMap = preResolutionDataProvider.get();
    injector.inject(classLoader, selfDependencyData, preResolutionResultMap);
    for (final URL module : moduleUrls) {
      final DependencyDataProvider moduleDataProvider = this.getModuleDataProviderFactory().create(module);
      final DependencyData dependencyData = moduleDataProvider.get();
      // TODO:: fetch isolated pre-resolutions
      injector.inject(classLoader, dependencyData, preResolutionResultMap);
    }
    final Class<Application> applicationClass = (Class<Application>) Class.forName(this.isolationConfiguration.getApplicationClass(), true, classLoader);
    // TODO:: Fix constructor resolution
    return applicationClass.getConstructor(Parameters.typesFrom(this.arguments)).newInstance(this.arguments);
  }
}
