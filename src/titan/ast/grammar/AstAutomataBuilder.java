package titan.ast.grammar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.grammar.io.GrammarCommentsTokenProcessor;
import titan.ast.grammar.io.GrammarPositionTokenProcessor;
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
    ArrayList<GrammarTokenBuilder> tokenBuilders = new ArrayList<>(grammarFiles.size());
    for (String grammarFile : grammarFiles) {
      tokenBuilders.add(getGrammarTokenBuilder(grammarFile));
    }

    return buildByGrammarTokenBuilders(tokenBuilders);
  }

  /**
   * token 语法 正则式转为正规文法 预测分析表 语法树.
   *
   * @param grammarFile 语法文件路径
   * @return 生成抽象语法树的自动机
   */
  public AstAutomata build(String grammarFile) {
    return buildByGrammarTokenBuilder(getGrammarTokenBuilder(grammarFile));
  }

  public AstAutomata buildByInputStreams(List<InputStream> grammarInputStreams) {
    ArrayList<GrammarTokenBuilder> tokenBuilders = new ArrayList<>(grammarInputStreams.size());
    for (InputStream grammarFilePathStream : grammarInputStreams) {
      tokenBuilders.add(getGrammarTokenBuilder(grammarFilePathStream));
    }

    return buildByGrammarTokenBuilders(tokenBuilders);
  }

  /**
   * token 语法 正则式转为正规文法 预测分析表 语法树.
   *
   * @param grammarFilePathStream 语法文件输入流
   * @return 生成抽象语法树的自动机
   */
  public AstAutomata build(InputStream grammarFilePathStream) {
    return buildByGrammarTokenBuilder(getGrammarTokenBuilder(grammarFilePathStream));
  }

  private GrammarTokenBuilder getGrammarTokenBuilder(String grammarFile) {
    GrammarTokenBuilder tokenBuilder = new GrammarTokenBuilder();
    // create token
    tokenBuilder.addTokenProcessor(new GrammarTokenBuilderProcessor(grammarFile));
    // set token position
    tokenBuilder.addTokenProcessor(new GrammarPositionTokenProcessor());
    // set comments token
    tokenBuilder.addTokenProcessor(new GrammarCommentsTokenProcessor());

    return tokenBuilder;
  }

  private GrammarTokenBuilder getGrammarTokenBuilder(InputStream grammarInputStream) {
    GrammarTokenBuilder tokenBuilder = new GrammarTokenBuilder();
    // create token
    tokenBuilder.addTokenProcessor(new GrammarTokenBuilderProcessor(grammarInputStream));
    // set token position
    tokenBuilder.addTokenProcessor(new GrammarPositionTokenProcessor());
    // set comments token
    tokenBuilder.addTokenProcessor(new GrammarCommentsTokenProcessor());

    return tokenBuilder;
  }

  private AstAutomata buildByGrammarTokenBuilders(List<GrammarTokenBuilder> tokenBuilders) {
    // all grammars init by text
    for (GrammarTokenBuilder grammarTokenBuilder : tokenBuilders) {
      // grammar tokens
      List<GrammarToken> grammarTokens = grammarTokenBuilder.buildTokens();
      // grammar init by text
      TextOfGrammarBuilder textOfGrammarBuilder =
          new TextOfGrammarBuilder(grammarTokens, AstContext.get().languageGrammar);
      textOfGrammarBuilder.build();
    }
    // token自动机
    TokenAutomataBuilder tokenAutomataBuilder = new TokenAutomataBuilder();
    tokenAutomataBuilder.build();
    // 语法自动机
    DfaAstAutomataBuilder dfaAstAutomataBuilder = new DfaAstAutomataBuilder();
    return dfaAstAutomataBuilder.build();
  }

  private AstAutomata buildByGrammarTokenBuilder(GrammarTokenBuilder tokenBuilder) {
    // grammar tokens
    List<GrammarToken> grammarTokens = tokenBuilder.buildTokens();
    // grammar init by text
    TextOfGrammarBuilder textOfGrammarBuilder =
        new TextOfGrammarBuilder(grammarTokens, AstContext.get().languageGrammar);
    textOfGrammarBuilder.build();
    // token自动机
    TokenAutomataBuilder tokenAutomataBuilder = new TokenAutomataBuilder();
    tokenAutomataBuilder.build();
    // 语法自动机
    DfaAstAutomataBuilder dfaAstAutomataBuilder = new DfaAstAutomataBuilder();
    return dfaAstAutomataBuilder.build();
  }
}
