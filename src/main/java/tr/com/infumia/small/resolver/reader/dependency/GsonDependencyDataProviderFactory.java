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

package tr.com.infumia.small.resolver.reader.dependency;

import java.net.URL;
import tr.com.infumia.small.resolver.reader.facade.GsonFacade;
import tr.com.infumia.small.resolver.reader.facade.GsonFacadeFactory;

public final class GsonDependencyDataProviderFactory implements DependencyDataProviderFactory {

  private final GsonFacade gson;

  public GsonDependencyDataProviderFactory(final GsonFacadeFactory gsonFactory) throws ReflectiveOperationException {
    this(gsonFactory.createFacade());
  }

  public GsonDependencyDataProviderFactory(final GsonFacade gson) {
    this.gson = gson;
  }

  @Override
  public DependencyDataProvider create(final URL dependencyFileURL) {
    final DependencyReader dependencyReader = new GsonDependencyReader(this.gson);
    return new URLDependencyDataProvider(dependencyReader, dependencyFileURL);
  }
}
