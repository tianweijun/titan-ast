package titan.ast;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstRuntimeException extends RuntimeException {

  public AstRuntimeException(String message) {
    super(message);
  }

  public AstRuntimeException(Throwable cause) {
    super(cause);
  }

  public AstRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
