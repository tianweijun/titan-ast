package titan.ast.runtime;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import titan.ast.runtime.AstGeneratorResult.TokensResult;
import titan.ast.runtime.DerivedTerminalGrammarAutomataData.RootTerminalGrammarMap;

/**
 * .
 *
 * @author tian wei jun
 */
class DerivedTerminalGrammarAutomata extends DfaTokenAutomata {
  Map<Grammar, HashMap<String, Grammar>> rootTerminalGrammarMap;

  /**
   * 初始化oneTokenStringBuilder、eof、dfa.
   *
   * @param derivedTerminalGrammarAutomataData 关键字有限自动机
   * @param dfa 识别token的 确定有限状态自动机
   */
  DerivedTerminalGrammarAutomata(
      DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData, TokenDfa dfa) {
    super(dfa);
    rootTerminalGrammarMap = new HashMap<>(derivedTerminalGrammarAutomataData.count);
    for (RootTerminalGrammarMap map : derivedTerminalGrammarAutomataData.rootTerminalGrammarMaps) {
      rootTerminalGrammarMap.put(map.rootTerminalGrammar, map.textTerminalMap);
    }
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
      HashMap<String, Grammar> textTerminalMap = rootTerminalGrammarMap.get(token.terminal);
      if (textTerminalMap != null) {
        Grammar terminal = textTerminalMap.get(token.text);
        if (null != terminal) {
          token.terminal = terminal;
        }
      }
    }
    return tokens;
  }
}
