package mg.razherana.lorm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.razherana.lorm.exceptions.LormException;

public class LormUtil {
  private LormUtil() {
  }

  @SuppressWarnings("unchecked")
  public <U, T extends Lorm<T>> Map<U, T> mapBy(List<T> lorms, String columnName) {
    Map<U, T> map = new HashMap<>();

    try {
      for (T lorm : lorms)
        map.put((U) lorm.getReflectContainer().getColumn(columnName).getField().get(lorm), lorm);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new LormException(e);
    }

    return map;
  }
}
