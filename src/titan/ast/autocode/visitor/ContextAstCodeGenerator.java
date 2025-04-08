package titan.ast.autocode.visitor;

import titan.ast.AstContext;
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
