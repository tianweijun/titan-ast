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

  public enum NfaPrimaryGrammarContentEdgeType {
    SEQUENCE_CHARS,
    ONE_CHAR_OPTION_CHARSET
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
    public final NfaPrimaryGrammarContentEdgeType type;
    public final String from;
    public final String to;
    public final char[] chars;

    public NfaPrimaryGrammarContentEdge(
        NfaPrimaryGrammarContentEdgeType type, String from, String to, char[] chars) {
      this.type = type;
      this.from = from;
      this.to = to;
      this.chars = chars;
    }
  }
}
