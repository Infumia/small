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
import java.util.Objects;

public final class Dependency {

  private final String artifactId;

  private final String groupId;

  private final String snapshotId;

  private final Collection<Dependency> transitive;

  private final String version;

  public Dependency(final String groupId, final String artifactId, final String version, final String snapshotId, final Collection<Dependency> transitive) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.snapshotId = snapshotId;
    this.transitive = transitive;
  }

  public String getArtifactId() {
    return this.artifactId;
  }

  public String getGroupId() {
    return this.groupId;
  }

  public String getSnapshotId() {
    return this.snapshotId;
  }

  public Collection<Dependency> getTransitive() {
    return this.transitive;
  }

  public String getVersion() {
    return this.version;
  }

  public boolean hasSnapshotId() {
    return this.snapshotId != null && !this.snapshotId.isEmpty();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.groupId, this.artifactId, this.version);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final Dependency that = (Dependency) o;
    return this.groupId.equals(that.groupId) &&
      this.artifactId.equals(that.artifactId) &&
      this.version.equals(that.version);
  }

  @Override
  public String toString() {
    final String snapshotId = this.getSnapshotId();
    final String suffix = this.hasSnapshotId() ? ":" + snapshotId : "";
    return this.getGroupId() + ":" + this.getArtifactId() + ":" + this.getVersion() + suffix;
  }
}
