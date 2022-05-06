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

package tr.com.infumia.small.injector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import tr.com.infumia.small.injector.helper.InjectionHelper;
import tr.com.infumia.small.injector.helper.InjectionHelperFactory;
import tr.com.infumia.small.injector.loader.Injectable;
import tr.com.infumia.small.resolver.ResolutionResult;
import tr.com.infumia.small.resolver.data.Dependency;
import tr.com.infumia.small.resolver.data.DependencyData;

public final class SimpleDependencyInjector implements DependencyInjector {

  private final InjectionHelperFactory injectionHelperFactory;

  public SimpleDependencyInjector(final InjectionHelperFactory injectionHelperFactory) {
    this.injectionHelperFactory = injectionHelperFactory;
  }

  @Override
  public void inject(final Injectable injectable, final DependencyData data, final Map<String, ResolutionResult> preResolvedResults) throws ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException {
    final InjectionHelper helper = this.injectionHelperFactory.create(data, preResolvedResults);
    this.injectDependencies(injectable, helper, data.getDependencies());
  }

  private void injectDependencies(final Injectable injectable, final InjectionHelper injectionHelper, final Collection<Dependency> dependencies) throws ReflectiveOperationException {
    for (final Dependency dependency : dependencies) {
      try {
        final File depJar = injectionHelper.fetch(dependency);
        if (depJar == null) {
          continue;
        }
        injectable.inject(depJar.toURI().toURL());
        this.injectDependencies(injectable, injectionHelper, dependency.getTransitive());
      } catch (final IOException e) {
        throw new InjectionFailedException(dependency, e);
      } catch (final IllegalAccessException | InvocationTargetException | URISyntaxException e) {
        e.printStackTrace();
      }
    }
  }
}
