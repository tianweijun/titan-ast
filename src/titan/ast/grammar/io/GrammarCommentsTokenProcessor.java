package titan.ast.grammar.io;

import java.util.Iterator;
import java.util.List;
import titan.ast.AstRuntimeException;

/**
 * 标记注释内容的类型为注释 注释标记,是注释，空白符和eof不算注释算分隔符，这些标记只能是一个token（前后有空白符） 空白符、换行、结束等分隔符不属于注释.
 *
 * @author tian wei jun
 */
public class GrammarCommentsTokenProcessor implements GrammarTokenProcessor {

  private static final String START_OF_SINGLY_COMMENTS = "//";
  private static final String START_OF_MULT_COMMENTS = "/*";
  private static final String END_OF_MULT_COMMENTS = "*/";
  private List<GrammarToken> grammarTokens;

  public GrammarCommentsTokenProcessor() {}

  @Override
  public void process(List<GrammarToken> grammarTokens) {
    this.grammarTokens = grammarTokens;
    build();
  }

  /** 识别并设置注释. */
  public void build() {
    Iterator<GrammarToken> tokensIt = grammarTokens.iterator();
    while (tokensIt.hasNext()) {
      GrammarToken token = tokensIt.next();
      String text = token.text;
      if (text.equals(START_OF_SINGLY_COMMENTS)) {
        token.type = GrammarTokenType.COMMENTS_TEXT;
        setSinglyComments(tokensIt);
      } else if (text.equals(START_OF_MULT_COMMENTS)) {
        token.type = GrammarTokenType.COMMENTS_TEXT;
        setMultComments(tokensIt, token.start);
      }
    }
  }

  /**
   * 无需识别转义的换行(\n),换行在字符集里面的真实值才是真正的换行.
   *
   * @param tokensIt token流的遍历器
   */
  private void setSinglyComments(Iterator<GrammarToken> tokensIt) {
    while (tokensIt.hasNext()) {
      GrammarToken token = tokensIt.next();
      if (token.type == GrammarTokenType.NEWLINE) {
        break;
      }
      if (!GrammarTokenType.isTokenSeparator(token.type)) {
        token.type = GrammarTokenType.COMMENTS_TEXT;
      }
    }
  }

  /**
   * asterisk/?需要分割.
   *
   * @param tokensIt token流的遍历器
   * @param indexOfStartMultComments /asterisk
   */
  private void setMultComments(Iterator<GrammarToken> tokensIt, int indexOfStartMultComments) {
    int endIndex = indexOfStartMultComments + START_OF_MULT_COMMENTS.length();
    StringBuilder contextInfo = new StringBuilder();
    boolean isEnd = false;
    while (tokensIt.hasNext()) {
      GrammarToken token = tokensIt.next();
      contextInfo.append(token.text);
      endIndex = token.start + token.text.length();

      if (token.text.equals(END_OF_MULT_COMMENTS)) {
        token.type = GrammarTokenType.COMMENTS_TEXT;
        isEnd = true;
        break;
      }
      if (!GrammarTokenType.isTokenSeparator(token.type)) {
        token.type = GrammarTokenType.COMMENTS_TEXT;
      }
    }
    if (!isEnd) {
      throw new AstRuntimeException(
          String.format(
              "%s expect '%s',error near [%d,%d):%s",
              START_OF_MULT_COMMENTS,
              END_OF_MULT_COMMENTS,
              indexOfStartMultComments,
              endIndex,
              contextInfo));
    }
  }
}
