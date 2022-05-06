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

package tr.com.infumia.small.relocation;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public final class RelocationRule {

  private final Collection<String> exclusions;

  private final Collection<String> inclusions;

  private final String originalPackagePattern;

  private final String relocatedPackagePattern;

  public RelocationRule(final String original, final String relocated, final Collection<String> exclusions, final Collection<String> inclusions) {
    this.originalPackagePattern = original;
    this.relocatedPackagePattern = relocated;
    this.exclusions = exclusions;
    this.inclusions = inclusions;
  }

  public RelocationRule(final String original, final String relocated) {
    this(original, relocated, Collections.emptyList(), Collections.emptyList());
  }

  public Collection<String> getExclusions() {
    return this.exclusions;
  }

  public Collection<String> getInclusions() {
    return this.inclusions;
  }

  public String getOriginalPackagePattern() {
    return this.originalPackagePattern;
  }

  public String getRelocatedPackagePattern() {
    return this.relocatedPackagePattern;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.originalPackagePattern, this.relocatedPackagePattern);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final RelocationRule that = (RelocationRule) o;
    return this.originalPackagePattern.equals(that.originalPackagePattern) &&
      this.relocatedPackagePattern.equals(that.relocatedPackagePattern);
  }

  @Override
  public String toString() {
    return "RelocationRule{" +
      "originalPackagePattern='" + this.originalPackagePattern + '\'' +
      ", relocatedPackagePattern='" + this.relocatedPackagePattern + '\'' +
      '}';
  }
}
