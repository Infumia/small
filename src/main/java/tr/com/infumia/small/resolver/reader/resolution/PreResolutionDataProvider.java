package tr.com.infumia.small.resolver.reader.resolution;

import java.io.IOException;
import java.util.Map;
import tr.com.infumia.small.resolver.ResolutionResult;

@FunctionalInterface
public interface PreResolutionDataProvider {

  Map<String, ResolutionResult> get() throws IOException, ReflectiveOperationException;
}
