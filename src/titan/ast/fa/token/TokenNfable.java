package titan.ast.fa.token;

/**
 * .
 *
 * @author tian wei jun
 */
public interface TokenNfable {

  TokenNfa getTokenNfa();

  void setTokenNfa(TokenNfa tokenNfa);
}
