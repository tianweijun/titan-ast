package titan.ast.impl.ast;

import titan.ast.AstRuntimeException;
import titan.ast.impl.ast.contextast.Ast2ContextAstConvertor;
import titan.ast.impl.ast.contextast.ContextAst;
import titan.ast.runtime.RichAstGeneratorResult;
import titan.ast.runtime.RichAstGeneratorResult.RichAstParseErrorData;
import titan.ast.runtime.RichAstGeneratorResult.RichAstResultType;
import titan.ast.runtime.RichAstGeneratorResult.RichTokenParseErrorData;
import titan.ast.runtime.RichAstGeneratorResult.RichTokensResultType;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstBuilder {

  static final String AST_PACKAGE = "titan.ast.impl.ast.contextast";

  public static ContextAst build(String grammarFilePath) {
    RuntimeAutomataRichAstApplication astApplication = RuntimeAutomataRichAstApplicationFactory.getAstApplication();
    RichAstGeneratorResult richAstGeneratorResult = astApplication.buildRichAst(grammarFilePath);
    if (!richAstGeneratorResult.isOk()) {
      throw new AstRuntimeException(getErrorMsg(grammarFilePath, richAstGeneratorResult));
    }
    return new Ast2ContextAstConvertor(richAstGeneratorResult.getOkAst(), AST_PACKAGE).convert();
  }

  private static String getErrorMsg(String grammarFilePath, RichAstGeneratorResult richAstGeneratorResult) {
    if (richAstGeneratorResult.richTokensResult.getType() == RichTokensResultType.TOKEN_PARSE_ERROR) {
      RichTokenParseErrorData error =
          richAstGeneratorResult.richTokensResult.getRichTokenParseErrorData();
      return String.format(
          "an error occurred because the text is not matching any grammar of token,error near %s[%d-%d,%d-%d): %s",
          grammarFilePath,error.startLineNumber, error.startOffsetInLine, error.endLineNumber, error.endOffsetInLine,
          error.errorText);

    }
    if (richAstGeneratorResult.richAstResult.getType() == RichAstResultType.AST_PARSE_ERROR) {
      RichAstParseErrorData error =
          richAstGeneratorResult.richAstResult.getRichAstParseErrorData();
      return String.format(
          "an error occurred because the text is not matching any grammar of nonterimal,error near %s[%d-%d,%d-%d): "
              + "%s",
          grammarFilePath,error.startLineNumber, error.startOffsetInLine, error.endLineNumber, error.endOffsetInLine,
          error.errorText);

    }
    return richAstGeneratorResult.getErrorMsg();
  }
}
