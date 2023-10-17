package titan.ast.output;

import java.util.Iterator;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.logger.Logger;
import titan.ast.target.Token;

public class TokenOutputer {

  private int maxLengthOfLine = 120;

  public void output(List<Token> tokens) {
    if (tokens == null || tokens.isEmpty()) {
      return;
    }
    GrammarCharset grammarCharset = AstContext.get().grammarCharset;
    Iterator<Token> tokensIt = tokens.iterator();
    StringBuilder stringBuilder = new StringBuilder();
    while (tokensIt.hasNext()) {
      Token token = tokensIt.next();
      stringBuilder.append(token.toString()).append(" ");
      if (stringBuilder.length() >= maxLengthOfLine) {
        Logger.info(
            "TokenOutputer output",
            String.format(
                "token:%s", grammarCharset.getDisplayingString(stringBuilder.toString())));
        stringBuilder.delete(0, stringBuilder.length());
      }
    }
    if (stringBuilder.length() > 0) {
      Logger.info(
          "TokenOutputer output",
          String.format("token:%s", grammarCharset.getDisplayingString(stringBuilder.toString())));
      stringBuilder.delete(0, stringBuilder.length());
    }
  }
}
