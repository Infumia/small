package tr.com.infumia.small.resolver.reader.resolution;

import java.net.URL;
import tr.com.infumia.small.resolver.reader.facade.GsonFacade;
import tr.com.infumia.small.resolver.reader.facade.GsonFacadeFactory;

public final class GsonPreResolutionDataProviderFactory implements PreResolutionDataProviderFactory {

  private final GsonFacade gson;

  public GsonPreResolutionDataProviderFactory(final GsonFacadeFactory gson) throws ReflectiveOperationException {
    this(gson.createFacade());
  }

  public GsonPreResolutionDataProviderFactory(final GsonFacade gson) {
    this.gson = gson;
  }

  @Override
  public PreResolutionDataProvider create(final URL resolutionFileURL) {
    final PreResolutionDataReader resolutionDataReader = new GsonPreResolutionDataReader(this.gson);
    return new GsonPreResolutionDataProvider(resolutionDataReader, resolutionFileURL);
  }
}
