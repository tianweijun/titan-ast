package titan.ast.impl.ast.contextast;

public interface ParseTree {

  void accept(Visitor visitor);
}