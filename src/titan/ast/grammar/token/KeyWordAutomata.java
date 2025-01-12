package titan.ast.grammar.token;

import java.util.HashMap;
import titan.ast.grammar.Grammar;

/**
 * 只保留依赖的数据，真正的实现在runtime里面.
 *
 * @author tian wei jun
 */
public class KeyWordAutomata {
  public static final int EMPTY = 0;
  public static final int NOT_EMPTY = 1;

  public int emptyOrNot = EMPTY;

  public Grammar rootKeyWord = null;

  public HashMap<String, Grammar> textTerminalMap = new HashMap<>();
}
