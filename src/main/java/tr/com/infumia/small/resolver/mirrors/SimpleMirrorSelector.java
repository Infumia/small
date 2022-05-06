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

package tr.com.infumia.small.resolver.mirrors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import tr.com.infumia.small.resolver.data.Mirror;
import tr.com.infumia.small.resolver.data.Repository;

public final class SimpleMirrorSelector implements MirrorSelector {

  public static final String ALT_CENTRAL_URL = "https://repo1.maven.org/maven2/";

  public static final String CENTRAL_URL = "https://repo.maven.apache.org/maven2/";

  public static final String DEFAULT_CENTRAL_MIRROR_URL = "https://repo.vshnv.tech/";

  private static final Collection<String> CENTRAL_REPO = Arrays.asList(SimpleMirrorSelector.CENTRAL_URL, SimpleMirrorSelector.ALT_CENTRAL_URL);

  private final Collection<Repository> centralMirrors;

  public SimpleMirrorSelector(final Collection<Repository> centralMirrors) {
    this.centralMirrors = centralMirrors;
  }

  private static boolean isCentral(final Repository repository) {
    final String url = repository.getUrl().toString();
    return SimpleMirrorSelector.CENTRAL_REPO.contains(url);
  }

  @Override
  public Collection<Repository> select(final Collection<Repository> mainRepositories, final Collection<Mirror> mirrors) throws MalformedURLException {
    final Collection<URL> originals = mirrors.stream()
      .map(Mirror::getOriginal)
      .collect(Collectors.toSet());
    final Collection<Repository> resolved = mainRepositories.stream()
      .filter(repo -> !originals.contains(repo.getUrl()))
      .filter(repo -> !SimpleMirrorSelector.isCentral(repo))
      .collect(Collectors.toSet());
    final Collection<Repository> mirrored = mirrors.stream()
      .map(Mirror::getMirroring)
      .map(Repository::new)
      .collect(Collectors.toSet());
    resolved.addAll(mirrored);
    resolved.addAll(this.centralMirrors);
    return resolved;
  }
}
