package titan.ast.runtime;

/**
 * 自定义运行时异常.
 *
 * @author tian wei jun
 */
public class AstRuntimeException extends RuntimeException {

  public AstRuntimeException(Throwable cause) {
    super(cause);
  }

  public AstRuntimeException(String message) {
    super(message);
  }
}
