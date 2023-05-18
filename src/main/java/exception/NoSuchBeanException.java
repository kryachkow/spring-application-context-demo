package exception;

public class NoSuchBeanException extends Exception {
  public NoSuchBeanException() {
  }

  public NoSuchBeanException(String message) {
    super(message);
  }

}
