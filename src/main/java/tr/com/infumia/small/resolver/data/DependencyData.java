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

package tr.com.infumia.small.resolver.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public final class DependencyData {

  private final Collection<Dependency> dependencies;

  private final Collection<Mirror> mirrors;

  private final Collection<Repository> repositories;

  public DependencyData(
    final Collection<Mirror> mirrors,
    final Collection<Repository> repositories,
    final Collection<Dependency> dependencies
  ) {
    this.mirrors = Collections.unmodifiableCollection(mirrors);
    this.repositories = Collections.unmodifiableCollection(repositories);
    this.dependencies = Collections.unmodifiableCollection(dependencies);
  }

  public Collection<Dependency> getDependencies() {
    return this.dependencies;
  }

  public Collection<Mirror> getMirrors() {
    return this.mirrors;
  }

  public Collection<Repository> getRepositories() {
    return this.repositories;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.repositories, this.dependencies);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final DependencyData that = (DependencyData) o;
    return this.isCollectionEqual(this.repositories, that.repositories) && this.isCollectionEqual(this.dependencies, that.dependencies);
  }

  @Override
  public String toString() {
    return "DependencyData{" +
      "mirrors=" + this.mirrors +
      ", repositories=" + this.repositories +
      ", dependencies=" + this.dependencies +
      '}';
  }

  private <T> boolean isCollectionEqual(final Collection<T> a, final Collection<T> b) {
    return a.containsAll(b) && b.containsAll(a);
  }
}
