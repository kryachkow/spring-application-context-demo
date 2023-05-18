package exception;

public class InappropriateInjectionException extends RuntimeException{

  public InappropriateInjectionException() {
  }

  public InappropriateInjectionException(String message) {
    super(message);
  }

}
