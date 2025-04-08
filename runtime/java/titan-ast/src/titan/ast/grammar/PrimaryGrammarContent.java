package titan.ast.grammar;

import java.util.ArrayList;
import java.util.List;
import titan.ast.grammar.regexp.CompositeRegExp;
import titan.ast.runtime.Token;

/**
 * .
 *
 * @author tian wei jun
 */
public abstract class PrimaryGrammarContent {

  public final PrimaryGrammarContentType type;

  public List<Token> tokens = new ArrayList<>();

  protected PrimaryGrammarContent(PrimaryGrammarContentType type) {
    this.type = type;
  }


  public enum PrimaryGrammarContentType {
    REG_EXP,
    NFA
  }

  public static class RegExpPrimaryGrammarContent extends PrimaryGrammarContent {

    // 最顶层是作为wrapper的COMPOSITE正则
    public CompositeRegExp regExp = new CompositeRegExp();

    public RegExpPrimaryGrammarContent() {
      super(PrimaryGrammarContentType.REG_EXP);
    }
  }

  public static class NfaPrimaryGrammarContent extends PrimaryGrammarContent {

    public NfaPrimaryGrammarContent() {
      super(PrimaryGrammarContentType.NFA);
    }
  }
}
