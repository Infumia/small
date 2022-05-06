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

import tr.com.infumia.small.resolver.data.Repository;
import tr.com.infumia.small.resolver.pinger.URLPinger;
import tr.com.infumia.small.resolver.strategy.PathResolutionStrategy;

public final class PingingRepositoryEnquirerFactory implements RepositoryEnquirerFactory {

  private final PathResolutionStrategy checksumURLCreationStrategy;

  private final PathResolutionStrategy pathResolutionStrategy;

  private final PathResolutionStrategy pomURLCreationStrategy;

  private final URLPinger urlPinger;

  public PingingRepositoryEnquirerFactory(final PathResolutionStrategy pathResolutionStrategy, final PathResolutionStrategy checksumURLCreationStrategy, final PathResolutionStrategy pomURLCreationStrategy, final URLPinger urlPinger) {
    this.pathResolutionStrategy = pathResolutionStrategy;
    this.checksumURLCreationStrategy = checksumURLCreationStrategy;
    this.pomURLCreationStrategy = pomURLCreationStrategy;
    this.urlPinger = urlPinger;
  }

  @Override
  public RepositoryEnquirer create(final Repository repository) {
    return new PingingRepositoryEnquirer(repository, this.pathResolutionStrategy, this.checksumURLCreationStrategy, this.pomURLCreationStrategy, this.urlPinger);
  }

  public PathResolutionStrategy getChecksumURLCreationStrategy() {
    return this.checksumURLCreationStrategy;
  }

  public PathResolutionStrategy getPathResolutionStrategy() {
    return this.pathResolutionStrategy;
  }

  public PathResolutionStrategy getPomURLCreationStrategy() {
    return this.pomURLCreationStrategy;
  }
}
