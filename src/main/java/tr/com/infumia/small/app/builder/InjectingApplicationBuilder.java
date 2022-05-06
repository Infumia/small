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
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.function.Function;
import tr.com.infumia.small.app.AppendingApplication;
import tr.com.infumia.small.app.Application;
import tr.com.infumia.small.injector.DependencyInjector;
import tr.com.infumia.small.injector.loader.Injectable;
import tr.com.infumia.small.injector.loader.InjectableFactory;
import tr.com.infumia.small.resolver.ResolutionResult;
import tr.com.infumia.small.resolver.data.DependencyData;
import tr.com.infumia.small.resolver.reader.dependency.DependencyDataProvider;
import tr.com.infumia.small.resolver.reader.resolution.PreResolutionDataProvider;

public final class InjectingApplicationBuilder extends ApplicationBuilder {

  private final Function<ApplicationBuilder, Injectable> injectableSupplier;

  public InjectingApplicationBuilder(final String applicationName, final Injectable injectable) {
    this(applicationName, it -> injectable);
  }

  public InjectingApplicationBuilder(final String applicationName, final Function<ApplicationBuilder, Injectable> injectableSupplier) {
    super(applicationName);
    this.injectableSupplier = injectableSupplier;
  }

  public static ApplicationBuilder createAppending(final String applicationName) throws ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException {
    final ClassLoader classLoader = ApplicationBuilder.class.getClassLoader();
    return InjectingApplicationBuilder.createAppending(applicationName, classLoader);
  }

  public static ApplicationBuilder createAppending(final String applicationName, final ClassLoader classLoader) throws ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException {
    return new InjectingApplicationBuilder(applicationName, (ApplicationBuilder builder) -> {
      try {
        return InjectableFactory.create(builder.getDownloadDirectoryPath(), builder.getInternalRepositories(), classLoader);
      } catch (final URISyntaxException | ReflectiveOperationException | NoSuchAlgorithmException |
                     IOException exception) {
        exception.printStackTrace();
      }
      return null;
    });
  }

  @Override
  public Application buildApplication() throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException {
    final DependencyDataProvider dataProvider = this.getDataProviderFactory().create(this.getDependencyFileUrl());
    final DependencyData dependencyData = dataProvider.get();
    final DependencyInjector dependencyInjector = this.createInjector();
    final PreResolutionDataProvider preResolutionDataProvider = this.getPreResolutionDataProviderFactory().create(this.getPreResolutionFileUrl());
    final Map<String, ResolutionResult> preResolutionResultMap = preResolutionDataProvider.get();
    dependencyInjector.inject(this.injectableSupplier.apply(this), dependencyData, preResolutionResultMap);
    return new AppendingApplication();
  }
}

