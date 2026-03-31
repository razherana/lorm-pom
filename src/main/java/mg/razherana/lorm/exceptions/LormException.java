package mg.razherana.lorm.exceptions;

public class LormException extends RuntimeException {
  public LormException(String message) {
    super(message);
  }

  public LormException(Throwable t) {
    super(t);
  }
}
