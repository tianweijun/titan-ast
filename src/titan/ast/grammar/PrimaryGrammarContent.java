package titan.ast.grammar;

import java.util.*;
import titan.ast.grammar.regexp.OneCharOptionCharsetRegExp.OptionChar;
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
    public final List<OptionChar> optionChars;

    public NfaPrimaryGrammarContentEdge(
        NfaPrimaryGrammarContentEdgeType type,
        String from,
        String to,
        char[] chars,
        List<OptionChar> optionChars) {
      this.type = type;
      this.from = from;
      this.to = to;
      this.chars = chars;
      this.optionChars = optionChars;
    }

    public static NfaPrimaryGrammarContentEdge sequenceCharsEdge(
        String from, String to, String chars) {
      return new NfaPrimaryGrammarContentEdge(
          NfaPrimaryGrammarContentEdgeType.SEQUENCE_CHARS, from, to, chars.toCharArray(), null);
    }

    public static NfaPrimaryGrammarContentEdge optionCharsEdge(
        String from, String to, OptionChar... optionCharsArray) {
      List<OptionChar> optionChars;
      if (optionCharsArray == null || optionCharsArray.length == 0) {
        optionChars = new ArrayList<>();
      } else {
        optionChars = new ArrayList<>(optionCharsArray.length);
        optionChars.addAll(Arrays.asList(optionCharsArray));
      }
      return new NfaPrimaryGrammarContentEdge(
          NfaPrimaryGrammarContentEdgeType.ONE_CHAR_OPTION_CHARSET, from, to, null, optionChars);
    }
  }
}
