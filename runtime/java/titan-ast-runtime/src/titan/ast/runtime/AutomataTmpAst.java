package titan.ast.runtime;

import java.util.LinkedList;

/**
 * 抽象语法树.
 *
 * @author tian wei jun
 */
abstract class AutomataTmpAst {
  final Grammar grammar;
  final LinkedList<AutomataTmpAst> children = new LinkedList<>();

  AutomataTmpAst(Grammar grammar) {
    this.grammar = grammar;
  }

  abstract AutomataTmpAst cloneForAstAutomata();

  abstract Ast toAst();
}
