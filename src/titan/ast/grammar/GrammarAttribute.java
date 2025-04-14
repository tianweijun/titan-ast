package titan.ast.grammar;

/**
 * .
 *
 * @author tian wei jun
 */
public class GrammarAttribute {

  public final TerminalGrammarAttributeEnum type;

  public GrammarAttribute(TerminalGrammarAttributeEnum type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GrammarAttribute that = (GrammarAttribute) o;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }

  public enum TerminalGrammarAttributeEnum {
    NFA_TERMINAL_GRAMMAR_ATTRIBUTE,
    LAZINESS_TERMINAL_GRAMMAR_ATTRIBUTE;
  }

  public static class LazinessTerminalGrammarAttribute extends GrammarAttribute {

    private static LazinessTerminalGrammarAttribute LAZINESS =
        new LazinessTerminalGrammarAttribute();

    public static LazinessTerminalGrammarAttribute get(){
      return LAZINESS;
    }

    private LazinessTerminalGrammarAttribute() {
      super(TerminalGrammarAttributeEnum.LAZINESS_TERMINAL_GRAMMAR_ATTRIBUTE);
    }

  }

  public static class NfaTerminalGrammarAttribute extends GrammarAttribute {

    public final String start;
    public final String end;

    public NfaTerminalGrammarAttribute(String start, String end) {
      super(TerminalGrammarAttributeEnum.NFA_TERMINAL_GRAMMAR_ATTRIBUTE);
      this.start = start;
      this.end = end;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      if (!super.equals(o)) {
        return false;
      }

      NfaTerminalGrammarAttribute that = (NfaTerminalGrammarAttribute) o;
      return start.equals(that.start) && end.equals(that.end);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + start.hashCode();
      result = 31 * result + end.hashCode();
      return result;
    }
  }
}
