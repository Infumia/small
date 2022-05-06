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
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FileChecksumCalculator implements ChecksumCalculator {

  private static final String DIRECTORY_HASH = "DIRECTORY";

  private static final Logger LOGGER = Logger.getLogger(FileChecksumCalculator.class.getName());

  private final MessageDigest digest;

  public FileChecksumCalculator(final String algorithm) throws NoSuchAlgorithmException {
    this.digest = MessageDigest.getInstance(algorithm);
  }

  @Override
  public String calculate(final File file) throws IOException {
    FileChecksumCalculator.LOGGER.log(Level.FINEST, "Calculating hash for {0}", file.getPath());
    // This helps run IDE environment as a special case
    if (file.isDirectory()) {
      return FileChecksumCalculator.DIRECTORY_HASH;
    }
    this.digest.reset();
    try (final FileInputStream fis = new FileInputStream(file)) {
      final byte[] byteArray = new byte[1024];
      int bytesCount;
      while ((bytesCount = fis.read(byteArray)) != -1) {
        this.digest.update(byteArray, 0, bytesCount);
      }
    }
    final byte[] bytes = this.digest.digest();
    final StringBuilder sb = new StringBuilder();
    for (final byte b : bytes) {
      sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
    }
    sb.trimToSize();
    final String result = sb.toString();
    FileChecksumCalculator.LOGGER.log(Level.FINEST, "Hash for {0} -> {1}", new Object[]{file.getPath(), result});
    return result;
  }
}
