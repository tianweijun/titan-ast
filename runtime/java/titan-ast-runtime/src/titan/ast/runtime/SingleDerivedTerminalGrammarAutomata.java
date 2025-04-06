package titan.ast.runtime;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import titan.ast.runtime.AstGeneratorResult.TokensResult;
import titan.ast.runtime.DerivedTerminalGrammarAutomataData.RootTerminalGrammarMap;

/**
 * .
 *
 * @author tian wei jun
 */
class SingleDerivedTerminalGrammarAutomata extends DfaTokenAutomata {
  Grammar rootTerminalGrammar;
  HashMap<String, Grammar> textTerminalMap;

  /**
   * 初始化oneTokenStringBuilder、eof、dfa.
   *
   * @param derivedTerminalGrammarAutomataData 关键字有限自动机
   * @param dfa 识别token的 确定有限状态自动机
   */
  SingleDerivedTerminalGrammarAutomata(
      DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData, TokenDfa dfa) {
    super(dfa);
    RootTerminalGrammarMap rootTerminalGrammarMap =
        derivedTerminalGrammarAutomataData.rootTerminalGrammarMaps.get(0);
    this.rootTerminalGrammar = rootTerminalGrammarMap.rootTerminalGrammar;
    this.textTerminalMap = rootTerminalGrammarMap.textTerminalMap;
  }

  @Override
  public TokensResult buildToken(InputStream byteInputStream) {
    TokensResult tokensResult = super.buildToken(byteInputStream);
    if (tokensResult.isOk()) {
      buildToken(tokensResult.getOkData());
    }
    return tokensResult;
  }

  List<Token> buildToken(List<Token> tokens) {
    for (Token token : tokens) {
      if (token.terminal == rootTerminalGrammar) {
        Grammar terminal = textTerminalMap.get(token.text);
        if (null != terminal) {
          token.terminal = terminal;
        }
      }
    }
    return tokens;
  }
}
