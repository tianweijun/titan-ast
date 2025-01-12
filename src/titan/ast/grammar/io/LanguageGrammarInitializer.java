package titan.ast.grammar.io;

import java.io.InputStream;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.grammar.LanguageGrammar;

/**
 * LanguageGrammarInitializer.
 *
 * @author tian wei jun
 */
public class LanguageGrammarInitializer {

  public void initGrammarByInputStream(InputStream grammarFilePathStream) {
    initGrammarByTokenBuilder(getGrammarTokenBuilder(grammarFilePathStream));
  }

  public void initGrammarByFiles(List<String> grammarFiles) {
    for (String grammarFile : grammarFiles) {
      initGrammarByFile(grammarFile);
    }
  }

  public void initGrammarByFile(String grammarFile) {
    initGrammarByTokenBuilder(getGrammarTokenBuilder(grammarFile));
  }

  private void initGrammarByTokenBuilder(GrammarTokenBuilder tokenBuilder) {
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    // grammar tokens
    List<GrammarToken> grammarTokens = tokenBuilder.buildTokens();
    // grammar init by text
    GrammarInitializer grammarInitializer = new GrammarInitializer(grammarTokens, languageGrammar);
    grammarInitializer.initByGrammarTokens();
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
}
