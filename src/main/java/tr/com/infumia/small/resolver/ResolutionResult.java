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
import java.util.Objects;
import tr.com.infumia.small.resolver.data.Repository;

public final class ResolutionResult {

  private final URL checksumURL;

  private final URL dependencyURL;

  private final boolean isAggregator;

  private final Repository repository;

  public ResolutionResult(final Repository repository, final URL dependencyURL, final URL checksumURL, final boolean isAggregator) {
    this.repository = repository;
    this.dependencyURL = dependencyURL;
    this.checksumURL = checksumURL;
    this.isAggregator = isAggregator;
    if (!isAggregator) {
      Objects.requireNonNull(dependencyURL, "Resolved URL must not be null for non-aggregator dependencies");
    }
  }

  public URL getChecksumURL() {
    return this.checksumURL;
  }

  public URL getDependencyURL() {
    return this.dependencyURL;
  }

  public Repository getRepository() {
    return this.repository;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.dependencyURL.toString(), this.checksumURL.toString(), this.isAggregator);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final ResolutionResult that = (ResolutionResult) o;
    // String comparison to avoid all blocking calls
    return this.dependencyURL.toString().equals(that.toString()) &&
      Objects.equals(this.checksumURL.toString(), that.getChecksumURL().toString()) &&
      this.isAggregator == that.isAggregator;
  }

  public boolean isAggregator() {
    return this.isAggregator;
  }
}
