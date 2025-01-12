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

  public static boolean isGrammarAttributeToken(GrammarToken token) {
    String text = token.text;
    return text.equals(KW_REGEXP_DESCRIPTOR)
        || text.startsWith(PREFIX_NFA_REG_DESCRIPTOR)
        || text.equals(KW_GREEDINESS_DESCRIPTOR)
        || text.equals(KW_LAZINESS_DESCRIPTOR);
  }

  public static boolean isNfaRegexpAttribute(List<GrammarToken> attributes) {
    return null != getNfaRegexpAttributeToken(attributes);
  }

  private static boolean isNfaRegexpAttribute(String text) {
    return text.startsWith(PREFIX_NFA_REG_DESCRIPTOR);
  }

  public static boolean isNormalRegexpAttribute(List<GrammarToken> attributes) {
    return !isNfaRegexpAttribute(attributes);
  }

  public static GrammarToken getNfaRegexpAttributeToken(List<GrammarToken> attributes) {
    GrammarToken nfaRegexpContent = null;
    for (GrammarToken token : attributes) {
      if (isNfaRegexpAttribute(token.text)) {
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
}
