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
  public void visitParenthesisTerminalGrammarUnitRegExpAst(ParenthesisTerminalGrammarUnitRegExpAst parenthesisTerminalGrammarUnitRegExpAst) {
    visitChildren(parenthesisTerminalGrammarUnitRegExpAst);
  }

  @Override
  public void visitTerminalGrammarUnitRegExpAst(TerminalGrammarUnitRegExpAst terminalGrammarUnitRegExpAst) {
    visitChildren(terminalGrammarUnitRegExpAst);
  }

  @Override
  public void visitTerminalGrammarCompositeRegExpAst(TerminalGrammarCompositeRegExpAst terminalGrammarCompositeRegExpAst) {
    visitChildren(terminalGrammarCompositeRegExpAst);
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
  public void visitTerminalFragmentGrammarAst(TerminalFragmentGrammarAst terminalFragmentGrammarAst) {
    visitChildren(terminalFragmentGrammarAst);
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
  public void visitTerminalGrammarActionAst(TerminalGrammarActionAst terminalGrammarActionAst) {
    visitChildren(terminalGrammarActionAst);
  }

  @Override
  public void visitNfaTerminalGrammarAttributesAst(NfaTerminalGrammarAttributesAst nfaTerminalGrammarAttributesAst) {
    visitChildren(nfaTerminalGrammarAttributesAst);
  }

  @Override
  public void visitRegExpTerminalGrammarAttributesAst(RegExpTerminalGrammarAttributesAst regExpTerminalGrammarAttributesAst) {
    visitChildren(regExpTerminalGrammarAttributesAst);
  }

  @Override
  public void visitNfaTerminalGrammarAst(NfaTerminalGrammarAst nfaTerminalGrammarAst) {
    visitChildren(nfaTerminalGrammarAst);
  }

  @Override
  public void visitRegExpTerminalGrammarAst(RegExpTerminalGrammarAst regExpTerminalGrammarAst) {
    visitChildren(regExpTerminalGrammarAst);
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
  public void visitDerivedTerminalGrammarCompositeRegExpAst(DerivedTerminalGrammarCompositeRegExpAst derivedTerminalGrammarCompositeRegExpAst) {
    visitChildren(derivedTerminalGrammarCompositeRegExpAst);
  }

  @Override
  public void visitDerivedTerminalGrammarAst(DerivedTerminalGrammarAst derivedTerminalGrammarAst) {
    visitChildren(derivedTerminalGrammarAst);
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
  public void visitParenthesisNonterminalGrammarUnitRegExpAst(ParenthesisNonterminalGrammarUnitRegExpAst parenthesisNonterminalGrammarUnitRegExpAst) {
    visitChildren(parenthesisNonterminalGrammarUnitRegExpAst);
  }

  @Override
  public void visitNonterminalGrammarUnitRegExpAst(NonterminalGrammarUnitRegExpAst nonterminalGrammarUnitRegExpAst) {
    visitChildren(nonterminalGrammarUnitRegExpAst);
  }

  @Override
  public void visitNonterminalGrammarCompositeRegExpAst(NonterminalGrammarCompositeRegExpAst nonterminalGrammarCompositeRegExpAst) {
    visitChildren(nonterminalGrammarCompositeRegExpAst);
  }

  @Override
  public void visitProductionRuleAst(ProductionRuleAst productionRuleAst) {
    visitChildren(productionRuleAst);
  }

  @Override
  public void visitProductionRulesAst(ProductionRulesAst productionRulesAst) {
    visitChildren(productionRulesAst);
  }

  @Override
  public void visitNonterminalGrammarAst(NonterminalGrammarAst nonterminalGrammarAst) {
    visitChildren(nonterminalGrammarAst);
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