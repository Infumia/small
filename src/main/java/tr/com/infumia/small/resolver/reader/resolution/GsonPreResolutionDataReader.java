package tr.com.infumia.small.resolver.reader.resolution;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import tr.com.infumia.small.resolver.ResolutionResult;
import tr.com.infumia.small.resolver.reader.facade.GsonFacade;
import tr.com.infumia.small.resolver.reader.facade.TypeToken;

public final class GsonPreResolutionDataReader implements PreResolutionDataReader {

  private final GsonFacade gson;

  public GsonPreResolutionDataReader(final GsonFacade gson) {
    this.gson = gson;
  }

  @Override
  public Map<String, ResolutionResult> read(final InputStream inputStream) throws IOException, ReflectiveOperationException {
    final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    final Type rawType = new TypeToken<Map<String, ResolutionResult>>() {
    }.getRawType();
    return this.gson.fromJson(inputStreamReader, rawType);
  }
}
