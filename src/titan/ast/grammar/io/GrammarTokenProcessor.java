package titan.ast.grammar.io;

import java.util.List;

/**
 * 生成语法文件对应的token所需经过处理流程的各种处理器的接口.
 *
 * @author tian wei jun
 */
public interface GrammarTokenProcessor {

  void process(List<GrammarToken> grammarTokens);
}
