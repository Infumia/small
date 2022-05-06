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

package tr.com.infumia.small.downloader.strategy;

import java.io.File;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import tr.com.infumia.small.resolver.data.Dependency;

public final class RelocationFilePathStrategy implements FilePathStrategy {

  private static final String DEPENDENCY_FILE_FORMAT = "%s/%s/%s/%s/relocated/%5$s/%3$s-%4$s.jar";

  private static final Logger LOGGER = Logger.getLogger(FolderedFilePathStrategy.class.getName());

  private final String applicationName;

  private final File rootDirectory;

  private RelocationFilePathStrategy(final String applicationName, final File rootDirectory) {
    this.rootDirectory = rootDirectory;
    this.applicationName = applicationName;
  }

  public static FilePathStrategy createStrategy(final File rootDirectory, final String applicationName) throws IllegalArgumentException {
    if (!rootDirectory.exists()) {
      final boolean created = rootDirectory.mkdirs();
      if (!created) {
        throw new IllegalArgumentException("Could not create specified directory: " + rootDirectory);
      }
    }
    if (!rootDirectory.isDirectory()) {
      throw new IllegalArgumentException("Expecting a directory for download root! " + rootDirectory);
    }
    return new RelocationFilePathStrategy(applicationName, rootDirectory);
  }

  @Override
  public File selectFileFor(final Dependency dependency) {
    final String extendedVersion = Optional.ofNullable(dependency.getSnapshotId()).map(s -> "-" + s).orElse("");
    final String path = String.format(
      RelocationFilePathStrategy.DEPENDENCY_FILE_FORMAT,
      this.rootDirectory.getPath(),
      dependency.getGroupId().replace('.', '/'),
      dependency.getArtifactId(),
      dependency.getVersion() + extendedVersion,
      this.applicationName
    );
    RelocationFilePathStrategy.LOGGER.log(Level.FINEST, "Selected file for relocated " + dependency.getArtifactId() + " at " + path);
    return new File(path);
  }
}
