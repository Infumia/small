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

import java.io.File;
import java.io.IOException;
import tr.com.infumia.small.resolver.DependencyResolver;
import tr.com.infumia.small.resolver.data.Dependency;

public final class PassthroughDependencyVerifierFactory implements DependencyVerifierFactory {

  @Override
  public DependencyVerifier create(final DependencyResolver resolver) {
    return new PassthroughVerifier();
  }

  private static final class PassthroughVerifier implements DependencyVerifier {

    @Override
    public File getChecksumFile(final Dependency dependency) {
      return null;
    }

    @Override
    public boolean verify(final File file, final Dependency dependency) throws IOException {
      return file.exists();
    }
  }
}
