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

import java.util.Collection;
import java.util.Map;
import tr.com.infumia.small.resolver.data.Repository;
import tr.com.infumia.small.resolver.enquirer.RepositoryEnquirerFactory;
import tr.com.infumia.small.resolver.pinger.URLPinger;

public final class CachingDependencyResolverFactory implements DependencyResolverFactory {

  private final URLPinger urlPinger;

  public CachingDependencyResolverFactory(final URLPinger urlPinger) {
    this.urlPinger = urlPinger;
  }

  @Override
  public DependencyResolver create(final Collection<Repository> repositories, final Map<String, ResolutionResult> preResolvedResults, final RepositoryEnquirerFactory enquirerFactory) {
    return new CachingDependencyResolver(this.urlPinger, repositories, enquirerFactory, preResolvedResults);
  }
}
