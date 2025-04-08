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
public class VisitorCodeGenerator {

  final String contextAstFileDirectory;
  final String javaPackage;

  public VisitorCodeGenerator(String contextAstFileDirectory, String primaryPackage) {
    this.contextAstFileDirectory = contextAstFileDirectory;
    this.javaPackage = "package " + primaryPackage + ";";
  }

  public void generate() {
    createVisitor();
    createAbstractVisitor();
  }

  private void createAbstractVisitor() {
    String filePath = contextAstFileDirectory + "AbstractVisitor.java";
    String content = getAbstractVisitorContent();
    FileUtil.createtFileIfNotExists(content, filePath);
  }

  private void createVisitor() {
    String filePath = contextAstFileDirectory + "Visitor.java";
    String content = getVisitorContent();
    FileUtil.createtFileIfNotExists(content, filePath);
  }

  private String getAbstractVisitorContent() {
    StringBuilder visitNonterminalContextAstFuncs = new StringBuilder();
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    for (Grammar grammar : languageGrammar.nonterminals.values()) {
      if (languageGrammar.augmentedNonterminal.name.equals(grammar.name)) {
        continue;
      }
      String firstUpperCaseGrammarName = CodeGenerator.getFirstUpperCaseGrammarName(grammar.name);
      String firstLowerCaseGrammarName = CodeGenerator.getFirstLowerCaseGrammarName(grammar.name);
      String visitNonterminalContextAstFunc =
          String.format(
              """

                @Override
                public void visit%sAst(%sAst %sAst) {
                  visitChildren(%sAst);
                }
              """,
              firstUpperCaseGrammarName,
              firstUpperCaseGrammarName,
              firstLowerCaseGrammarName,
              firstLowerCaseGrammarName);
      visitNonterminalContextAstFuncs.append(visitNonterminalContextAstFunc);
    }
    return String.format(
        """
      %s

      public class AbstractVisitor implements Visitor {

        @Override
        public void visit(ContextAst contextAst) {
          contextAst.accept(this);
        }

        @Override
        public void visitChildren(ContextAst contextAst) {
          for (ContextAst child : contextAst.children) {
            visit(child);
          }
        }

        @Override
        public void visitTerminalContextAst(TerminalContextAst terminalContextAst) {
          visitChildren(terminalContextAst);
        }
        %s
      }""",
        javaPackage, visitNonterminalContextAstFuncs.toString());
  }

  private String getVisitorContent() {
    StringBuilder visitNonterminalContextAstFuncs = new StringBuilder();
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    for (Grammar grammar : languageGrammar.nonterminals.values()) {
      if (languageGrammar.augmentedNonterminal.name.equals(grammar.name)) {
        continue;
      }
      String firstUpperCaseGrammarName = CodeGenerator.getFirstUpperCaseGrammarName(grammar.name);
      String firstLowerCaseGrammarName = CodeGenerator.getFirstLowerCaseGrammarName(grammar.name);
      String visitNonterminalContextAstFunc =
          String.format(
              "  void visit%sAst(%sAst %sAst);\n",
              firstUpperCaseGrammarName, firstUpperCaseGrammarName, firstLowerCaseGrammarName);
      visitNonterminalContextAstFuncs.append(visitNonterminalContextAstFunc);
    }
    return String.format(
        """
       %s

       public interface Visitor {
         void visit(ContextAst contextAst);
         void visitChildren(ContextAst contextAst);
         void visitTerminalContextAst(TerminalContextAst terminalContextAst);
       %s}""",
        javaPackage, visitNonterminalContextAstFuncs.toString());
  }
}
