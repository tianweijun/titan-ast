package titan.ast.runtime;

import java.util.ArrayList;

/**
 * 抽象语法树.
 *
 * @author tian wei jun
 */
public abstract class Ast {
  public final AstGrammar grammar;

  public ArrayList<Ast> children = new ArrayList<>();

  public Ast(AstGrammar grammar) {
    this.grammar = grammar;
  }
}
