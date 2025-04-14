package titan.ast.grammar.io;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 创建者：构造语法文件对应的token流.
 *
 * @author tian wei jun
 */
public class GrammarTokenBuilder {

  private final LinkedList<GrammarTokenProcessor> processors = new LinkedList<>();

  public GrammarTokenBuilder() {}

  public void addTokenProcessor(GrammarTokenProcessor processor) {
    processors.add(processor);
  }

  /**
   * 构造语法文件对应的token流.
   *
   * @return 语法文件对应的tokens
   */
  public List<GrammarToken> buildTokens() {
    List<GrammarToken> grammarTokens = new LinkedList<>();
    Iterator<GrammarTokenProcessor> processorIt = processors.iterator();
    while (processorIt.hasNext()) {
      processorIt.next().process(grammarTokens);
    }
    return grammarTokens;
  }
}
