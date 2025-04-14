package titan.ast.impl.ast.regexp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import titan.ast.AstContext;
import titan.ast.grammar.DerivedTerminalGrammarAutomataDetail.RootTerminalGrammarMapDetail;
import titan.ast.grammar.GrammarAction;
import titan.ast.grammar.GrammarAttribute;
import titan.ast.grammar.GrammarAttribute.LazinessTerminalGrammarAttribute;
import titan.ast.grammar.GrammarCreater;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.NonterminalGrammar;
import titan.ast.grammar.PrimaryGrammarContent;
import titan.ast.grammar.PrimaryGrammarContent.NfaPrimaryGrammarContent;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;
import titan.ast.grammar.TerminalFragmentGrammar;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.grammar.regexp.AndCompositeRegExp;
import titan.ast.grammar.regexp.OrCompositeRegExp;
import titan.ast.grammar.regexp.ParenthesisRegExp;
import titan.ast.grammar.regexp.RepeatTimes;
import titan.ast.impl.ast.contextast.AbstractVisitor;
import titan.ast.impl.ast.contextast.AndCompositeRegExpAst;
import titan.ast.impl.ast.contextast.CompilationUnitAst;
import titan.ast.impl.ast.contextast.ContextAst;
import titan.ast.impl.ast.contextast.DerivedTerminalGrammarBeginningAst;
import titan.ast.impl.ast.contextast.DerivedTerminalGrammarBlockAst;
import titan.ast.impl.ast.contextast.ExclusiveOrCompositeRegExpAst;
import titan.ast.impl.ast.contextast.GrammarActionAst;
import titan.ast.impl.ast.contextast.GrammarAttributeAst;
import titan.ast.impl.ast.contextast.GrammarAttributesAst;
import titan.ast.impl.ast.contextast.GrammarUnitRegExpAst;
import titan.ast.impl.ast.contextast.IdentifierAst;
import titan.ast.impl.ast.contextast.InclusiveOrCompositeRegExpAst;
import titan.ast.impl.ast.contextast.NfaTerminalGrammarAst;
import titan.ast.impl.ast.contextast.NonterminalGrammarBlockAst;
import titan.ast.impl.ast.contextast.OneCharOptionCharsetUnitRegExpAst;
import titan.ast.impl.ast.contextast.ParenthesisUnitRegExpAst;
import titan.ast.impl.ast.contextast.RegExpGrammarAst;
import titan.ast.impl.ast.contextast.SequenceCharsUnitRegExpAst;
import titan.ast.impl.ast.contextast.StartGrammarAst;
import titan.ast.impl.ast.contextast.TerminalContextAst;
import titan.ast.impl.ast.contextast.TerminalFragmentGrammarBlockAst;
import titan.ast.impl.ast.contextast.TerminalGrammarAst;
import titan.ast.impl.ast.contextast.TerminalGrammarBlockAst;
import titan.ast.impl.ast.contextast.UnitRegExpAst;

/**
 * .
 *
 * @author tian wei jun
 */
public class GrammarInitializer extends AbstractVisitor {

  final CompilationUnitAst compilationUnitAst;
  LanguageGrammar languageGrammar;
  private RootTerminalGrammarMapDetail rootTerminalGrammarMapDetail;

  public GrammarInitializer(ContextAst contextAst) {
    compilationUnitAst = (CompilationUnitAst) contextAst;
    languageGrammar = AstContext.get().languageGrammar;
  }

  public void init() {
    visitCompilationUnitAst(compilationUnitAst);
  }

  /*
  nonterminalGrammarBeginning
   regExpGrammar*
   nonterminalGrammarEnd
   */
  @Override
  public void visitNonterminalGrammarBlockAst(NonterminalGrammarBlockAst nonterminalGrammarBlockAst) {
    super.visitNonterminalGrammarBlockAst(nonterminalGrammarBlockAst);
    for (ContextAst child : nonterminalGrammarBlockAst.children) {
      if (child instanceof RegExpGrammarAst regExpGrammarAst) {
        RegExpPrimaryGrammarContent regExpPrimaryGrammarContent = regExpGrammarAst.regExpPrimaryGrammarContent;
        NonterminalGrammar nonterminalGrammar = GrammarCreater.createNonterminalGrammar(
            regExpPrimaryGrammarContent);
        languageGrammar.addNonterminalGrammar(nonterminalGrammar);
      }
    }
  }

  /*
    derivedTerminalGrammarBeginning
     regExpGrammar*
     derivedTerminalGrammarEnd
     */
  @Override
  public void visitDerivedTerminalGrammarBlockAst(DerivedTerminalGrammarBlockAst derivedTerminalGrammarBlockAst) {
    super.visitDerivedTerminalGrammarBlockAst(derivedTerminalGrammarBlockAst);
    for (ContextAst child : derivedTerminalGrammarBlockAst.children) {
      if (child instanceof RegExpGrammarAst regExpGrammarAst) {
        RegExpPrimaryGrammarContent regExpPrimaryGrammarContent = regExpGrammarAst.regExpPrimaryGrammarContent;
        TerminalGrammar terminalGrammar = GrammarCreater.createDerivedTerminalGrammar(
            regExpPrimaryGrammarContent);
        rootTerminalGrammarMapDetail.addTerminalGrammar(terminalGrammar);
      }
    }
  }

  /*
    terminalGrammarBeginning
       terminalGrammar*
       terminalGrammarEnd
     */
  @Override
  public void visitTerminalGrammarBlockAst(TerminalGrammarBlockAst terminalGrammarBlockAst) {
    super.visitTerminalGrammarBlockAst(terminalGrammarBlockAst);
    for (ContextAst child : terminalGrammarBlockAst.children) {
      if (child instanceof TerminalGrammarAst terminalGrammarAst) {
        PrimaryGrammarContent primaryGrammarContent = terminalGrammarAst.primaryGrammarContent;
        TerminalGrammar terminalGrammar = GrammarCreater.createTerminalGrammar(primaryGrammarContent);
        languageGrammar.addTerminalGrammar(terminalGrammar);
      }
    }
  }

  /*
      terminalFragmentGrammarBeginning
       terminalGrammar*
       terminalFragmentGrammarEnd
       */
  @Override
  public void visitTerminalFragmentGrammarBlockAst(TerminalFragmentGrammarBlockAst terminalFragmentGrammarBlockAst) {
    super.visitTerminalFragmentGrammarBlockAst(terminalFragmentGrammarBlockAst);
    for (ContextAst child : terminalFragmentGrammarBlockAst.children) {
      if (child instanceof TerminalGrammarAst terminalGrammarAst) {
        PrimaryGrammarContent primaryGrammarContent = terminalGrammarAst.primaryGrammarContent;
        TerminalFragmentGrammar terminalFragmentGrammar =
            GrammarCreater.createTerminalFragmentGrammar(primaryGrammarContent);
        languageGrammar.addTerminalFragmentGrammar(terminalFragmentGrammar);
      }
    }
  }

  // regExpGrammar | nfaTerminalGrammar
  @Override
  public void visitTerminalGrammarAst(TerminalGrammarAst terminalGrammarAst) {
    super.visitTerminalGrammarAst(terminalGrammarAst);
    ContextAst child = terminalGrammarAst.children.get(0);

    if (child instanceof RegExpGrammarAst regExpGrammarAst) {
      terminalGrammarAst.primaryGrammarContent = regExpGrammarAst.regExpPrimaryGrammarContent;
    }
    if (child instanceof NfaTerminalGrammarAst nfaTerminalGrammarAst) {
      terminalGrammarAst.primaryGrammarContent = nfaTerminalGrammarAst.nfaPrimaryGrammarContent;
    }
  }

  //identifier grammarAttributes? ':' NfaEdge+ grammarAction? ';'
  @Override
  public void visitNfaTerminalGrammarAst(NfaTerminalGrammarAst nfaTerminalGrammarAst) {
    super.visitNfaTerminalGrammarAst(nfaTerminalGrammarAst);
    NfaPrimaryGrammarContent nfaPrimaryGrammarContent = new NfaPrimaryGrammarContent();
    nfaPrimaryGrammarContent.edges = new ArrayList<>(nfaTerminalGrammarAst.children.size());
    for (ContextAst child : nfaTerminalGrammarAst.children) {
      if (child instanceof IdentifierAst identifierAst) {
        nfaPrimaryGrammarContent.grammarName = identifierAst.identifierStr;
        continue;
      }
      if (child instanceof GrammarAttributesAst grammarAttributesAst) {
        nfaPrimaryGrammarContent.grammarAttributes = grammarAttributesAst.grammarAttributes;
        continue;
      }
      if (child instanceof TerminalContextAst terminalContextAst && terminalContextAst.grammar.name.equals("NfaEdge")) {
        nfaPrimaryGrammarContent.edges.add(GrammarParser.getNfaEdge(terminalContextAst.str));
        continue;
      }
      if (child instanceof GrammarActionAst grammarActionAst) {
        nfaPrimaryGrammarContent.grammarAction = grammarActionAst.grammarAction;
      }
    }
    nfaTerminalGrammarAst.nfaPrimaryGrammarContent = nfaPrimaryGrammarContent;
  }

  // identifier grammarAttributes? ':' inclusiveOrCompositeRegExp grammarAction? ';'
  @Override
  public void visitRegExpGrammarAst(RegExpGrammarAst regExpGrammarAst) {
    super.visitRegExpGrammarAst(regExpGrammarAst);
    RegExpPrimaryGrammarContent regExpPrimaryGrammarContent = new RegExpPrimaryGrammarContent();
    for (ContextAst child : regExpGrammarAst.children) {
      if (child instanceof IdentifierAst identifierAst) {
        regExpPrimaryGrammarContent.grammarName = identifierAst.identifierStr;
        continue;
      }
      if (child instanceof GrammarAttributesAst grammarAttributesAst) {
        regExpPrimaryGrammarContent.grammarAttributes = grammarAttributesAst.grammarAttributes;
        continue;
      }
      if (child instanceof InclusiveOrCompositeRegExpAst inclusiveOrCompositeRegExpAst) {
        regExpPrimaryGrammarContent.orCompositeRegExp = inclusiveOrCompositeRegExpAst.orCompositeRegExp;
        continue;
      }
      if (child instanceof GrammarActionAst grammarActionAst) {
        regExpPrimaryGrammarContent.grammarAction = grammarActionAst.grammarAction;
      }
    }
    regExpGrammarAst.regExpPrimaryGrammarContent = regExpPrimaryGrammarContent;
  }

  //Arrow Skip
  @Override
  public void visitGrammarActionAst(GrammarActionAst grammarActionAst) {
    super.visitGrammarActionAst(grammarActionAst);
    grammarActionAst.grammarAction =
        GrammarAction.getActionByString(((TerminalContextAst) grammarActionAst.children.get(1)).str);
  }

  //grammarAttribute+
  @Override
  public void visitGrammarAttributesAst(GrammarAttributesAst grammarAttributesAst) {
    super.visitGrammarAttributesAst(grammarAttributesAst);
    Set<GrammarAttribute> grammarAttributes = new HashSet<>(grammarAttributesAst.children.size());
    for (ContextAst child : grammarAttributesAst.children) {
      if (child instanceof GrammarAttributeAst grammarAttributeAst) {
        grammarAttributes.add(grammarAttributeAst.grammarAttribute);
      }
    }
    grammarAttributesAst.grammarAttributes = grammarAttributes;
  }

  //NfaTerminalGrammarAttribute | LazinessTerminalGrammarAttribute
  @Override
  public void visitGrammarAttributeAst(GrammarAttributeAst grammarAttributeAst) {
    super.visitGrammarAttributeAst(grammarAttributeAst);
    TerminalContextAst terminalContextAst = (TerminalContextAst) grammarAttributeAst.children.get(0);
    String terminalGrammarName = terminalContextAst.grammar.name;
    if ("NfaTerminalGrammarAttribute".equals(terminalGrammarName)) {
      String str = terminalContextAst.str;
      grammarAttributeAst.grammarAttribute = GrammarParser.getNfaTerminalGrammarAttribute(str);
    }
    if ("LazinessTerminalGrammarAttribute".equals(terminalGrammarName)) {
      grammarAttributeAst.grammarAttribute = LazinessTerminalGrammarAttribute.get();
    }
  }

  /*
     exclusiveOrCompositeRegExp
    | inclusiveOrCompositeRegExp '|' exclusiveOrCompositeRegExp
     */
  @Override
  public void visitInclusiveOrCompositeRegExpAst(InclusiveOrCompositeRegExpAst inclusiveOrCompositeRegExpAst) {
    super.visitInclusiveOrCompositeRegExpAst(inclusiveOrCompositeRegExpAst);
    ArrayList<ContextAst> children = inclusiveOrCompositeRegExpAst.children;
    int sizeOfChild = children.size();
    switch (sizeOfChild) {
      case 1 -> {
        OrCompositeRegExp orCompositeRegExp = new OrCompositeRegExp();
        orCompositeRegExp.children.add(((ExclusiveOrCompositeRegExpAst) children.get(0)).andCompositeRegExp);
        inclusiveOrCompositeRegExpAst.orCompositeRegExp = orCompositeRegExp;
      }
      case 3 -> {
        OrCompositeRegExp orCompositeRegExp = ((InclusiveOrCompositeRegExpAst) children.get(0)).orCompositeRegExp;
        orCompositeRegExp.children.add(((ExclusiveOrCompositeRegExpAst) children.get(2)).andCompositeRegExp);
        inclusiveOrCompositeRegExpAst.orCompositeRegExp = orCompositeRegExp;
      }
    }
  }

  // andCompositeRegExp AndCompositeRegExpAlias?
  @Override
  public void visitExclusiveOrCompositeRegExpAst(ExclusiveOrCompositeRegExpAst exclusiveOrCompositeRegExpAst) {
    super.visitExclusiveOrCompositeRegExpAst(exclusiveOrCompositeRegExpAst);
    AndCompositeRegExpAst andCompositeRegExpAst = (AndCompositeRegExpAst) exclusiveOrCompositeRegExpAst.children.get(0);
    AndCompositeRegExp andCompositeRegExp = andCompositeRegExpAst.andCompositeRegExp;
    if (exclusiveOrCompositeRegExpAst.children.size() == 2) {
      String andCompositeRegExpAlias = ((TerminalContextAst) exclusiveOrCompositeRegExpAst.children.get(1)).str;
      String alias = GrammarParser.getAliasByAndCompositeRegExpAlias(andCompositeRegExpAlias);
      andCompositeRegExp.setAlias(alias);
    }
    exclusiveOrCompositeRegExpAst.andCompositeRegExp = andCompositeRegExp;
  }

  // unitRegExp+
  @Override
  public void visitAndCompositeRegExpAst(AndCompositeRegExpAst andCompositeRegExpAst) {
    super.visitAndCompositeRegExpAst(andCompositeRegExpAst);
    AndCompositeRegExp andCompositeRegExp = new AndCompositeRegExp();
    andCompositeRegExp.children = new ArrayList<>(andCompositeRegExpAst.children.size());
    for (ContextAst child : andCompositeRegExpAst.children) {
      if (child instanceof UnitRegExpAst unitRegExpAst) {
        andCompositeRegExp.children.add(unitRegExpAst.unitRegExp);
      }
    }
    andCompositeRegExpAst.andCompositeRegExp = andCompositeRegExp;
  }

  /*
      unitRegExp :
          grammarUnitRegExp
        | sequenceCharsUnitRegExp
        | oneCharOptionCharsetUnitRegExp
        | parenthesisUnitRegExp
    ;
       */
  @Override
  public void visitUnitRegExpAst(UnitRegExpAst unitRegExpAst) {
    super.visitUnitRegExpAst(unitRegExpAst);
    ContextAst child = unitRegExpAst.children.get(0);
    if (child instanceof GrammarUnitRegExpAst grammarUnitRegExpAst) {
      unitRegExpAst.unitRegExp = grammarUnitRegExpAst.grammarRegExp;
    }
    if (child instanceof SequenceCharsUnitRegExpAst sequenceCharsUnitRegExpAst) {
      unitRegExpAst.unitRegExp = sequenceCharsUnitRegExpAst.sequenceCharsRegExp;
    }
    if (child instanceof OneCharOptionCharsetUnitRegExpAst oneCharOptionCharsetUnitRegExpAst) {
      unitRegExpAst.unitRegExp = oneCharOptionCharsetUnitRegExpAst.oneCharOptionCharsetRegExp;
    }
    if (child instanceof ParenthesisUnitRegExpAst parenthesisUnitRegExpAst) {
      unitRegExpAst.unitRegExp = parenthesisUnitRegExpAst.parenthesisRegExp;
    }
  }

  //ParenthesisUnitRegExpPrefix inclusiveCompositeRegExp ParenthesisUnitRegExpSuffix
  @Override
  public void visitParenthesisUnitRegExpAst(ParenthesisUnitRegExpAst parenthesisUnitRegExpAst) {
    super.visitParenthesisUnitRegExpAst(parenthesisUnitRegExpAst);
    String parenthesisUnitRegExpSuffix = ((TerminalContextAst) parenthesisUnitRegExpAst.children.get(2)).str;
    RepeatTimes[] repeatTimes = GrammarParser.getRepeateTimesByParenthesisUnitRegExpSuffix(parenthesisUnitRegExpSuffix);
    InclusiveOrCompositeRegExpAst inclusiveOrCompositeRegExpAst =
        (InclusiveOrCompositeRegExpAst) parenthesisUnitRegExpAst.children.get(
            1);
    OrCompositeRegExp orCompositeRegExp = inclusiveOrCompositeRegExpAst.orCompositeRegExp;
    ParenthesisRegExp parenthesisRegExp = new ParenthesisRegExp(orCompositeRegExp);
    parenthesisRegExp.setRepeatTimes(repeatTimes[0], repeatTimes[1]);
    parenthesisUnitRegExpAst.parenthesisRegExp = parenthesisRegExp;
  }

  //oneCharOptionCharsetUnitRegExp : OneCharOptionCharsetUnitRegExp ;
  @Override
  public void visitOneCharOptionCharsetUnitRegExpAst(
      OneCharOptionCharsetUnitRegExpAst oneCharOptionCharsetUnitRegExpAst) {
    super.visitOneCharOptionCharsetUnitRegExpAst(oneCharOptionCharsetUnitRegExpAst);
    String str = ((TerminalContextAst) oneCharOptionCharsetUnitRegExpAst.children.get(0)).str;
    oneCharOptionCharsetUnitRegExpAst.oneCharOptionCharsetRegExp = GrammarParser.getOneCharOptionCharsetRegExp(str);
  }

  // sequenceCharsUnitRegExp : SequenceCharsUnitRegExp ;
  @Override
  public void visitSequenceCharsUnitRegExpAst(SequenceCharsUnitRegExpAst sequenceCharsUnitRegExpAst) {
    super.visitSequenceCharsUnitRegExpAst(sequenceCharsUnitRegExpAst);
    String str = ((TerminalContextAst) sequenceCharsUnitRegExpAst.children.get(0)).str;
    sequenceCharsUnitRegExpAst.sequenceCharsRegExp = GrammarParser.getSequenceCharsRegExp(str);
  }

  // grammarUnitRegExp : identifier | GrammarUnitRegExpRepeatTimes ;
  @Override
  public void visitGrammarUnitRegExpAst(GrammarUnitRegExpAst grammarUnitRegExpAst) {
    super.visitGrammarUnitRegExpAst(grammarUnitRegExpAst);
    ContextAst child = grammarUnitRegExpAst.children.get(0);
    if (child instanceof IdentifierAst identifierAst) {
      grammarUnitRegExpAst.grammarRegExp = GrammarParser.getGrammarRegExp(identifierAst.identifierStr);
    }
    if (child instanceof TerminalContextAst terminalContextAst) {
      grammarUnitRegExpAst.grammarRegExp = GrammarParser.getGrammarUnitRegExpRepeatTimes(terminalContextAst.str);
    }
  }

  // '@DerivedTerminalGrammar' DerivedTerminalGrammarAttribute 'begin' ';'
  @Override
  public void visitDerivedTerminalGrammarBeginningAst(
      DerivedTerminalGrammarBeginningAst derivedTerminalGrammarBeginningAst) {
    super.visitDerivedTerminalGrammarBeginningAst(derivedTerminalGrammarBeginningAst);
    String derivedTerminalGrammarAttribute =
        ((TerminalContextAst) derivedTerminalGrammarBeginningAst.children.get(1)).str;
    String rootTerminalGrammarName = GrammarParser.getRootTerminalGrammarNameByDerivedTerminalGrammarAttribute(
        derivedTerminalGrammarAttribute);
    this.rootTerminalGrammarMapDetail =
        languageGrammar.getRootTerminalGrammarMap(rootTerminalGrammarName);
  }

  // startGrammar : '@StartGrammar' identifier ';'  ;
  @Override
  public void visitStartGrammarAst(StartGrammarAst startGrammarAst) {
    super.visitStartGrammarAst(startGrammarAst);
    IdentifierAst identifierAst = (IdentifierAst) startGrammarAst.children.get(1);
    String startGrammarName = identifierAst.identifierStr;
    languageGrammar.updateStartGrammarName(startGrammarName);
  }

  // identifier : Identifier | Begin | End | Skip ;
  @Override
  public void visitIdentifierAst(IdentifierAst identifierAst) {
    super.visitIdentifierAst(identifierAst);
    identifierAst.identifierStr = ((TerminalContextAst) identifierAst.children.get(0)).str;
  }

  @Override
  public void visitTerminalContextAst(TerminalContextAst terminalContextAst) {
    terminalContextAst.str = terminalContextAst.token.text;
  }
}
