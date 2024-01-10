package titan.ast.grammar;

import java.io.InputStream;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.grammar.io.GrammarCommentsTokenProcessor;
import titan.ast.grammar.io.GrammarToken;
import titan.ast.grammar.io.GrammarTokenBuilder;
import titan.ast.grammar.io.GrammarTokenBuilderProcessor;
import titan.ast.grammar.syntax.AstAutomata;
import titan.ast.grammar.syntax.DfaAstAutomataBuilder;
import titan.ast.grammar.token.TokenAutomataBuilder;

/**
 * 建造器：生成抽象语法树的自动机.
 *
 * @author tian wei jun
 */
public class AstAutomataBuilder {

  public AstAutomata buildByFiles(List<String> grammarFiles) {
    GrammarTokenBuilder tokenBuilder = new GrammarTokenBuilder();
    // create token
    for (String grammarFile : grammarFiles) {
      tokenBuilder.addTokenProcessor(new GrammarTokenBuilderProcessor(grammarFile));
    }
    // delete skip token
    tokenBuilder.addTokenProcessor(new GrammarCommentsTokenProcessor());

    return buildByGrammarTokenBuilder(tokenBuilder);
  }

  /**
   * token 语法 正则式转为正规文法 预测分析表 语法树.
   *
   * @param grammarFile 语法文件路径
   * @return 生成抽象语法树的自动机
   */
  public AstAutomata build(String grammarFile) {
    GrammarTokenBuilder tokenBuilder = new GrammarTokenBuilder();
    // create token
    tokenBuilder.addTokenProcessor(new GrammarTokenBuilderProcessor(grammarFile));
    // delete skip token
    tokenBuilder.addTokenProcessor(new GrammarCommentsTokenProcessor());

    return buildByGrammarTokenBuilder(tokenBuilder);
  }

  public AstAutomata buildByInputStreams(List<InputStream> grammarFilePathStreams) {
    GrammarTokenBuilder tokenBuilder = new GrammarTokenBuilder();
    // create token
    for (InputStream grammarFilePathStream : grammarFilePathStreams) {
      tokenBuilder.addTokenProcessor(new GrammarTokenBuilderProcessor(grammarFilePathStream));
    }
    // delete skip token
    tokenBuilder.addTokenProcessor(new GrammarCommentsTokenProcessor());

    return buildByGrammarTokenBuilder(tokenBuilder);
  }

  /**
   * token 语法 正则式转为正规文法 预测分析表 语法树.
   *
   * @param grammarFilePathStream 语法文件输入流
   * @return 生成抽象语法树的自动机
   */
  public AstAutomata build(InputStream grammarFilePathStream) {
    GrammarTokenBuilder tokenBuilder = new GrammarTokenBuilder();
    // create token
    tokenBuilder.addTokenProcessor(new GrammarTokenBuilderProcessor(grammarFilePathStream));
    // delete skip token
    tokenBuilder.addTokenProcessor(new GrammarCommentsTokenProcessor());

    return buildByGrammarTokenBuilder(tokenBuilder);
  }

  private AstAutomata buildByGrammarTokenBuilder(GrammarTokenBuilder tokenBuilder) {
    final AstContext astContext = AstContext.get();
    List<GrammarToken> grammarTokens = tokenBuilder.buildTokens();
    // token
    TextOfGrammarBuilder textOfGrammarBuilder =
        new TextOfGrammarBuilder(grammarTokens, astContext.languageGrammar);
    textOfGrammarBuilder.build();

    TokenAutomataBuilder tokenAutomataBuilder = new TokenAutomataBuilder();
    tokenAutomataBuilder.build();

    // 语法
    DfaAstAutomataBuilder dfaAstAutomataBuilder = new DfaAstAutomataBuilder();
    return dfaAstAutomataBuilder.build();
  }
}
