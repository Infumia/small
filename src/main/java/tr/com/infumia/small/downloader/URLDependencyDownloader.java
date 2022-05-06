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

package tr.com.infumia.small.downloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import tr.com.infumia.small.downloader.output.OutputWriter;
import tr.com.infumia.small.downloader.output.OutputWriterFactory;
import tr.com.infumia.small.downloader.verify.DependencyVerifier;
import tr.com.infumia.small.logging.LogDispatcher;
import tr.com.infumia.small.logging.ProcessLogger;
import tr.com.infumia.small.resolver.DependencyResolver;
import tr.com.infumia.small.resolver.ResolutionResult;
import tr.com.infumia.small.resolver.UnresolvedDependencyException;
import tr.com.infumia.small.resolver.data.Dependency;
import tr.com.infumia.small.util.Connections;

public final class URLDependencyDownloader implements DependencyDownloader {

  private static final byte[] BOM_BYTES = "bom-file".getBytes();

  private static final ProcessLogger LOGGER = LogDispatcher.getMediatingLogger();

  private final DependencyResolver dependencyResolver;

  private final OutputWriterFactory outputWriterProducer;

  private final DependencyVerifier verifier;

  public URLDependencyDownloader(final OutputWriterFactory outputWriterProducer, final DependencyResolver dependencyResolver, final DependencyVerifier verifier) {
    this.outputWriterProducer = outputWriterProducer;
    this.dependencyResolver = dependencyResolver;
    this.verifier = verifier;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Override
  public File download(final Dependency dependency) throws IOException {
    final File expectedOutputFile = this.outputWriterProducer.getStrategy().selectFileFor(dependency);
    if (expectedOutputFile.exists()
      && expectedOutputFile.length() == URLDependencyDownloader.BOM_BYTES.length
      && Arrays.equals(Files.readAllBytes(expectedOutputFile.toPath()), URLDependencyDownloader.BOM_BYTES)
    ) {
      return null;
    }
    if (this.verifier.verify(expectedOutputFile, dependency)) {
      return expectedOutputFile;
    }
    final ResolutionResult result = this.dependencyResolver.resolve(dependency)
      .orElseThrow(() -> new UnresolvedDependencyException(dependency));
    if (result.isAggregator()) {
      expectedOutputFile.getParentFile().mkdirs();
      expectedOutputFile.createNewFile();
      Files.write(expectedOutputFile.toPath(), URLDependencyDownloader.BOM_BYTES);
      return null;
    }
    expectedOutputFile.delete();
    final File checksumFile = this.verifier.getChecksumFile(dependency);
    if (checksumFile != null) {
      checksumFile.delete();
    }
    URLDependencyDownloader.LOGGER.log("Downloading {0}:{1}:{2}...", dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
    final URL url = result.getDependencyURL();
    URLDependencyDownloader.LOGGER.debug("Connecting to {0}", url);
    final URLConnection connection = Connections.createDownloadConnection(url);
    final InputStream inputStream = connection.getInputStream();
    URLDependencyDownloader.LOGGER.debug("Connection successful! Downloading {0}", dependency.getArtifactId() + "...");
    final OutputWriter outputWriter = this.outputWriterProducer.create(dependency);
    URLDependencyDownloader.LOGGER.debug("{0}.Size = {1}", dependency.getArtifactId(), connection.getContentLength());
    final File downloadResult = outputWriter.writeFrom(inputStream, connection.getContentLength());
    Connections.tryDisconnect(connection);
    this.verifier.verify(downloadResult, dependency);
    URLDependencyDownloader.LOGGER.debug("Artifact {0} downloaded successfully!", dependency.getArtifactId());
    URLDependencyDownloader.LOGGER.debug("Downloaded {0} successfully!", dependency.getArtifactId());
    return downloadResult;
  }
}
