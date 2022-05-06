package tr.com.infumia.small.resolver.reader.resolution;

import java.net.URL;

@FunctionalInterface
public interface PreResolutionDataProviderFactory {

  PreResolutionDataProvider create(final URL resolutionFileURL);
}
