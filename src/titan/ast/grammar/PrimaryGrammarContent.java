package titan.ast.grammar;

import java.util.List;
import java.util.Set;
import titan.ast.grammar.regexp.OrCompositeRegExp;

/**
 * .
 *
 * @author tian wei jun
 */
public abstract class PrimaryGrammarContent {

  public final PrimaryGrammarContentType type;
  public String grammarName;
  public Set<GrammarAttribute> grammarAttributes;
  public GrammarAction grammarAction;

  protected PrimaryGrammarContent(PrimaryGrammarContentType type) {
    this.type = type;
  }


  public enum PrimaryGrammarContentType {
    REG_EXP,
    NFA
  }

  public static class RegExpPrimaryGrammarContent extends PrimaryGrammarContent {

    public OrCompositeRegExp orCompositeRegExp;

    public RegExpPrimaryGrammarContent() {
      super(PrimaryGrammarContentType.REG_EXP);
    }
  }

  public static class NfaPrimaryGrammarContent extends PrimaryGrammarContent {

    public String start;
    public String end;
    public List<NfaPrimaryGrammarContentEdge> edges;

    public NfaPrimaryGrammarContent() {
      super(PrimaryGrammarContentType.NFA);
    }
  }

  public static class NfaPrimaryGrammarContentEdge {

    public String from;
    public String to;
    public char[] chars;

    public NfaPrimaryGrammarContentEdge(String from, String to, char[] chars) {
      this.from = from;
      this.to = to;
      this.chars = chars;
    }
  }
}
