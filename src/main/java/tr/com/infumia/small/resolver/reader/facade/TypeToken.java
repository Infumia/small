package tr.com.infumia.small.resolver.reader.facade;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T> {

  private final Type rawType;

  public TypeToken() {
    this.rawType = TypeToken.getSuperclassTypeParameter(this.getClass());
  }

  private static Type getSuperclassTypeParameter(final Class<?> subclass) {
    final Type superclass = subclass.getGenericSuperclass();
    if (superclass instanceof Class) {
      throw new RuntimeException("Type parameter not found");
    }
    final ParameterizedType parameterized = (ParameterizedType) superclass;
    return parameterized.getActualTypeArguments()[0];
  }

  public Type getRawType() {
    return this.rawType;
  }
}
