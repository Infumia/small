package tr.com.infumia.small.resolver.reader.resolution;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import tr.com.infumia.small.resolver.ResolutionResult;

public final class GsonPreResolutionDataProvider implements PreResolutionDataProvider {

  private final PreResolutionDataReader resolutionDataReader;

  private final URL resolutionFileURL;

  private Map<String, ResolutionResult> cachedData = null;

  public GsonPreResolutionDataProvider(final PreResolutionDataReader resolutionDataReader, final URL resolutionFileURL) {
    this.resolutionDataReader = resolutionDataReader;
    this.resolutionFileURL = resolutionFileURL;
  }

  @Override
  public Map<String, ResolutionResult> get() throws IOException, ReflectiveOperationException {
    if (this.cachedData != null) {
      return this.cachedData;
    }
    try (final InputStream is = this.resolutionFileURL.openStream()) {
      this.cachedData = this.resolutionDataReader.read(is);
      return this.cachedData;
    } catch (final Exception exception) {
      return Collections.emptyMap();
    }
  }
}
