package titan.ast.autocode.visitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import titan.ast.AstContext;
import titan.ast.AstRuntimeException;
import titan.ast.autocode.CodeGenerator;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.util.FileUtil;

/**
 * .
 *
 * @author tian wei jun
 */
public class ContextAstCodeGenerator {
  final String AST_2_CONTEXT_AST_CONVERTOR_FLIE_NAME = "Ast2ContextAstConvertor.txt";
  final String contextAstFileDirectory;
  final String javaPackage;

  public ContextAstCodeGenerator(String contextAstFileDirectory, String primaryPackage) {
    this.contextAstFileDirectory = contextAstFileDirectory;
    this.javaPackage = "package " + primaryPackage + ";";
  }

  public void generate() {
    createParseTree();
    createContextAst();
    createNonterminalContextAst();
    createTerminalContextAst();
    createNonterminalAsts();
    createAst2ContextAstConvertor();
  }

  private void createAst2ContextAstConvertor() {
    String filePath = contextAstFileDirectory + "Ast2ContextAstConvertor.java";
    File file = new File(filePath);
    if (file.exists()) {
      return;
    }
    try (InputStream inputStream =
            this.getClass().getResourceAsStream("/" + AST_2_CONTEXT_AST_CONVERTOR_FLIE_NAME);
        FileWriter fileWriter = new FileWriter(file); ) {
      fileWriter.write(javaPackage);
      fileWriter.write("\n");
      fileWriter.write(new String(inputStream.readAllBytes(), StandardCharsets.ISO_8859_1));
    } catch (IOException e) {
      throw new AstRuntimeException(e);
    }
  }

  private void createTerminalContextAst() {
    String filePath = contextAstFileDirectory + "TerminalContextAst.java";
    String content =
        String.format(
            """
           %s

           import titan.ast.runtime.AstToken;

           public class TerminalContextAst extends ContextAst {

             public AstToken token;

             @Override
             public void accept(Visitor visitor) {
               visitor.visitTerminalContextAst(this);
             }
           }""",
            javaPackage);
    FileUtil.createtFileIfNotExists(content, filePath);
  }

  private void createNonterminalContextAst() {
    String filePath = contextAstFileDirectory + "NonterminalContextAst.java";
    String content =
        String.format(
            """
           %s

           public abstract class NonterminalContextAst extends ContextAst {

             public String alias;
           }""",
            javaPackage);
    FileUtil.createtFileIfNotExists(content, filePath);
  }

  private void createContextAst() {
    String filePath = contextAstFileDirectory + "ContextAst.java";
    String content =
        String.format(
            """
           %s

           import java.util.ArrayList;
           import titan.ast.runtime.AstGrammar;

           public abstract class ContextAst implements ParseTree {

             public AstGrammar grammar = null;
             public ArrayList<ContextAst> children = new ArrayList<>();

             public ContextAst parent = null;

             @Override
             public void accept(Visitor visitor) {
               visitor.visitChildren(this);
             }
           }""",
            javaPackage);
    FileUtil.createtFileIfNotExists(content, filePath);
  }

  private void createParseTree() {
    String filePath = contextAstFileDirectory + "ParseTree.java";
    String content =
        String.format(
            """
           %s

           public interface ParseTree {

             void accept(Visitor visitor);
           }""",
            javaPackage);
    FileUtil.createtFileIfNotExists(content, filePath);
  }

  private void createNonterminalAsts() {
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    for (Grammar grammar : languageGrammar.nonterminals.values()) {
      if (languageGrammar.augmentedNonterminal.name.equals(grammar.name)) {
        continue;
      }
      String firstUpperCaseGrammarName = CodeGenerator.getFirstUpperCaseGrammarName(grammar.name);
      String filePath = contextAstFileDirectory + firstUpperCaseGrammarName + "Ast.java";
      String content = getNonterminalAstContent(firstUpperCaseGrammarName);
      FileUtil.createtFileIfNotExists(content, filePath);
    }
  }

  private String getNonterminalAstContent(String firstUpperCaseGrammarName) {
    return String.format(
        """
      %s

      public class %sAst extends NonterminalContextAst {

        @Override
        public void accept(Visitor visitor) {
          visitor.visit%sAst(this);
        }
      }""",
        javaPackage, firstUpperCaseGrammarName, firstUpperCaseGrammarName);
  }
}
