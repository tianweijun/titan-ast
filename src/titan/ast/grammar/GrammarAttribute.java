package titan.ast.grammar;

import java.util.List;
import titan.ast.grammar.io.GrammarToken;

/**
 * GrammarAttribute.
 *
 * @author tian wei jun
 */
public class GrammarAttribute {
  // regexp()
  private static final String KW_REGEXP_DESCRIPTOR = "regexp()";
  // nfa(start,end)
  private static final String PREFIX_NFA_REG_DESCRIPTOR = "nfa";
  // greediness()
  // 能被其他更重的token覆盖，也能被更长的自己覆盖
  private static final String KW_GREEDINESS_DESCRIPTOR = "greediness()";
  // laziness()
  // 能被其他更重的token覆盖，但不能被更长的自己覆盖
  private static final String KW_LAZINESS_DESCRIPTOR = "laziness()";
  // acceptWhenFirstArriveAtTerminalState
  // 一旦识别，拥有最高优先级，直接接受，不可能被其他token覆盖
  private static final String KW_ACCEPT_WHEN_FIRST_ARRIVE_AT_TERMINAL_STATE_DESCRIPTOR =
      "acceptWhenFirstArriveAtTerminalState()";

  public static boolean isGrammarAttributeToken(GrammarToken token) {
    String text = token.text;
    return text.equals(KW_REGEXP_DESCRIPTOR)
        || text.startsWith(PREFIX_NFA_REG_DESCRIPTOR)
        || text.equals(KW_GREEDINESS_DESCRIPTOR)
        || text.equals(KW_LAZINESS_DESCRIPTOR)
        || text.equals(KW_ACCEPT_WHEN_FIRST_ARRIVE_AT_TERMINAL_STATE_DESCRIPTOR);
  }

  public static boolean isNfaRegexpContent(List<GrammarToken> attributes) {
    return null != getNfaRegexpContent(attributes);
  }

  private static boolean isNfaRegexpContent(String text) {
    return text.startsWith(PREFIX_NFA_REG_DESCRIPTOR);
  }

  public static boolean isNormalRegexpContent(List<GrammarToken> attributes) {
    return !isNfaRegexpContent(attributes);
  }

  public static GrammarToken getNfaRegexpContent(List<GrammarToken> attributes) {
    GrammarToken nfaRegexpContent = null;
    for (GrammarToken token : attributes) {
      if (isNfaRegexpContent(token.text)) {
        nfaRegexpContent = token;
        break;
      }
    }
    return nfaRegexpContent;
  }

  public static boolean isLaziness(List<GrammarToken> attributes) {
    for (GrammarToken token : attributes) {
      if (KW_LAZINESS_DESCRIPTOR.equals(token.text)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isAcceptWhenFirstArriveAtTerminalState(List<GrammarToken> attributes) {
    for (GrammarToken token : attributes) {
      if (KW_ACCEPT_WHEN_FIRST_ARRIVE_AT_TERMINAL_STATE_DESCRIPTOR.equals(token.text)) {
        return true;
      }
    }
    return false;
  }
}
