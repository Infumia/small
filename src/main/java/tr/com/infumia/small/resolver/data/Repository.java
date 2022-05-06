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

import java.net.URL;
import java.util.Objects;

public final class Repository {

  private final String name;

  private final URL url;

  public Repository(final URL url, final String name) {
    this.url = url;
    this.name = name;
  }

  public Repository(final URL url) {
    this(url, "maven");
  }

  public String getName() {
    return this.name;
  }

  public URL getUrl() {
    return this.url;
  }

  @Override
  public int hashCode() {
    int result = this.url != null ? this.url.hashCode() : 0;
    result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final Repository that = (Repository) o;
    return Objects.equals(this.url, that.url) && Objects.equals(this.name, that.name);
  }

  @Override
  public String toString() {
    return "Repository{" +
      "url=" + this.url +
      ", name='" + this.name + '\'' +
      '}';
  }
}
