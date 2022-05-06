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

package tr.com.infumia.small.downloader.verify;

import java.util.logging.Level;
import java.util.logging.Logger;
import tr.com.infumia.small.downloader.output.OutputWriterFactory;
import tr.com.infumia.small.resolver.DependencyResolver;

public final class ChecksumDependencyVerifierFactory implements DependencyVerifierFactory {

  private static final Logger LOGGER = Logger.getLogger(ChecksumDependencyVerifierFactory.class.getName());

  private final ChecksumCalculator checksumCalculator;

  private final DependencyVerifierFactory fallbackVerifierFactory;

  private final OutputWriterFactory outputWriterFactory;

  public ChecksumDependencyVerifierFactory(final OutputWriterFactory outputWriterFactory, final DependencyVerifierFactory fallbackVerifierFactory, final ChecksumCalculator checksumCalculator) {
    this.outputWriterFactory = outputWriterFactory;
    this.fallbackVerifierFactory = fallbackVerifierFactory;
    this.checksumCalculator = checksumCalculator;
  }

  @Override
  public DependencyVerifier create(final DependencyResolver resolver) {
    ChecksumDependencyVerifierFactory.LOGGER.log(Level.FINEST, "Creating verifier...");
    return new ChecksumDependencyVerifier(resolver, this.outputWriterFactory, this.fallbackVerifierFactory.create(resolver), this.checksumCalculator);
  }
}
