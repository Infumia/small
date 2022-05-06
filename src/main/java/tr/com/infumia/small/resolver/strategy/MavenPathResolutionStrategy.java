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

package tr.com.infumia.small.resolver.strategy;

import java.util.Collection;
import java.util.Collections;
import tr.com.infumia.small.resolver.data.Dependency;
import tr.com.infumia.small.resolver.data.Repository;
import tr.com.infumia.small.util.Repositories;

public final class MavenPathResolutionStrategy implements PathResolutionStrategy {

  private static final String PATH_FORMAT = "%s%s/%s/%s/%3$s-%4$s.jar";

  @Override
  public Collection<String> pathTo(final Repository repository, final Dependency dependency) {
    final String repoUrl = Repositories.fetchFormattedUrl(repository);
    return Collections.singleton(
      String.format(
        MavenPathResolutionStrategy.PATH_FORMAT,
        repoUrl,
        dependency.getGroupId().replace('.', '/'),
        dependency.getArtifactId(),
        dependency.getVersion()
      )
    );
  }
}
