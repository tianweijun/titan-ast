package titan.ast.impl.ast.contextast;

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
  
  @Override
  public void visitStartGrammarAst(StartGrammarAst startGrammarAst) {
    visitChildren(startGrammarAst);
  }

  @Override
  public void visitGrammarUnitRegExpAst(GrammarUnitRegExpAst grammarUnitRegExpAst) {
    visitChildren(grammarUnitRegExpAst);
  }

  @Override
  public void visitSequenceCharsUnitRegExpAst(SequenceCharsUnitRegExpAst sequenceCharsUnitRegExpAst) {
    visitChildren(sequenceCharsUnitRegExpAst);
  }

  @Override
  public void visitOneCharOptionCharsetUnitRegExpAst(OneCharOptionCharsetUnitRegExpAst oneCharOptionCharsetUnitRegExpAst) {
    visitChildren(oneCharOptionCharsetUnitRegExpAst);
  }

  @Override
  public void visitParenthesisUnitRegExpAst(ParenthesisUnitRegExpAst parenthesisUnitRegExpAst) {
    visitChildren(parenthesisUnitRegExpAst);
  }

  @Override
  public void visitUnitRegExpAst(UnitRegExpAst unitRegExpAst) {
    visitChildren(unitRegExpAst);
  }

  @Override
  public void visitAndCompositeRegExpAst(AndCompositeRegExpAst andCompositeRegExpAst) {
    visitChildren(andCompositeRegExpAst);
  }

  @Override
  public void visitExclusiveOrCompositeRegExpAst(ExclusiveOrCompositeRegExpAst exclusiveOrCompositeRegExpAst) {
    visitChildren(exclusiveOrCompositeRegExpAst);
  }

  @Override
  public void visitInclusiveOrCompositeRegExpAst(InclusiveOrCompositeRegExpAst inclusiveOrCompositeRegExpAst) {
    visitChildren(inclusiveOrCompositeRegExpAst);
  }

  @Override
  public void visitGrammarAttributeAst(GrammarAttributeAst grammarAttributeAst) {
    visitChildren(grammarAttributeAst);
  }

  @Override
  public void visitGrammarAttributesAst(GrammarAttributesAst grammarAttributesAst) {
    visitChildren(grammarAttributesAst);
  }

  @Override
  public void visitGrammarActionAst(GrammarActionAst grammarActionAst) {
    visitChildren(grammarActionAst);
  }

  @Override
  public void visitRegExpGrammarAst(RegExpGrammarAst regExpGrammarAst) {
    visitChildren(regExpGrammarAst);
  }

  @Override
  public void visitTerminalFragmentGrammarBeginningAst(TerminalFragmentGrammarBeginningAst terminalFragmentGrammarBeginningAst) {
    visitChildren(terminalFragmentGrammarBeginningAst);
  }

  @Override
  public void visitTerminalFragmentGrammarEndAst(TerminalFragmentGrammarEndAst terminalFragmentGrammarEndAst) {
    visitChildren(terminalFragmentGrammarEndAst);
  }

  @Override
  public void visitTerminalFragmentGrammarBlockAst(TerminalFragmentGrammarBlockAst terminalFragmentGrammarBlockAst) {
    visitChildren(terminalFragmentGrammarBlockAst);
  }

  @Override
  public void visitTerminalGrammarBeginningAst(TerminalGrammarBeginningAst terminalGrammarBeginningAst) {
    visitChildren(terminalGrammarBeginningAst);
  }

  @Override
  public void visitTerminalGrammarEndAst(TerminalGrammarEndAst terminalGrammarEndAst) {
    visitChildren(terminalGrammarEndAst);
  }

  @Override
  public void visitNfaTerminalGrammarAst(NfaTerminalGrammarAst nfaTerminalGrammarAst) {
    visitChildren(nfaTerminalGrammarAst);
  }

  @Override
  public void visitTerminalGrammarAst(TerminalGrammarAst terminalGrammarAst) {
    visitChildren(terminalGrammarAst);
  }

  @Override
  public void visitTerminalGrammarBlockAst(TerminalGrammarBlockAst terminalGrammarBlockAst) {
    visitChildren(terminalGrammarBlockAst);
  }

  @Override
  public void visitDerivedTerminalGrammarBeginningAst(DerivedTerminalGrammarBeginningAst derivedTerminalGrammarBeginningAst) {
    visitChildren(derivedTerminalGrammarBeginningAst);
  }

  @Override
  public void visitDerivedTerminalGrammarEndAst(DerivedTerminalGrammarEndAst derivedTerminalGrammarEndAst) {
    visitChildren(derivedTerminalGrammarEndAst);
  }

  @Override
  public void visitDerivedTerminalGrammarBlockAst(DerivedTerminalGrammarBlockAst derivedTerminalGrammarBlockAst) {
    visitChildren(derivedTerminalGrammarBlockAst);
  }

  @Override
  public void visitNonterminalGrammarBeginningAst(NonterminalGrammarBeginningAst nonterminalGrammarBeginningAst) {
    visitChildren(nonterminalGrammarBeginningAst);
  }

  @Override
  public void visitNonterminalGrammarEndAst(NonterminalGrammarEndAst nonterminalGrammarEndAst) {
    visitChildren(nonterminalGrammarEndAst);
  }

  @Override
  public void visitNonterminalGrammarBlockAst(NonterminalGrammarBlockAst nonterminalGrammarBlockAst) {
    visitChildren(nonterminalGrammarBlockAst);
  }

  @Override
  public void visitItemAst(ItemAst itemAst) {
    visitChildren(itemAst);
  }

  @Override
  public void visitCompilationUnitAst(CompilationUnitAst compilationUnitAst) {
    visitChildren(compilationUnitAst);
  }

  @Override
  public void visitIdentifierAst(IdentifierAst identifierAst) {
    visitChildren(identifierAst);
  }

}