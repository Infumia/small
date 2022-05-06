package tr.com.infumia.small.resolver.reader.resolution;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import tr.com.infumia.small.resolver.ResolutionResult;

public interface PreResolutionDataReader {

  Map<String, ResolutionResult> read(InputStream inputStream) throws IOException, ReflectiveOperationException;
}
