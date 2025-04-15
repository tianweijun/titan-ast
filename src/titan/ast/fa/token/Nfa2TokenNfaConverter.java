package titan.ast.fa.token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.PrimaryGrammarContent.NfaPrimaryGrammarContent;
import titan.ast.grammar.PrimaryGrammarContent.NfaPrimaryGrammarContentEdge;

/**
 * 表述nfa的文本结构类似一下例子： 'OneLineMacro nfa(0,10) : 0[#]1 1[\r]2 1~[\r]4 2[\401]7 2[\n]3 2~[\n]4 4~[\r]4
 * 4[\r]5 4[\401]8 5~[\n]4 5[\n]6 5[\401]9 6[]10 7[]10 8[]10 9[]10 ;' 将表述nfa的文本设置对应的nfa.
 *
 * @author tian wei jun
 */
public class Nfa2TokenNfaConverter {

  Collection<Grammar> nfaGrammars;

  /** 默认构造器初始化字段：grammarCharset、escapeCharGetter. */
  public Nfa2TokenNfaConverter(Collection<Grammar> nfaGrammars) {
    setNfaGrammars(nfaGrammars);
  }

  public void setNfaGrammars(Collection<Grammar> nfaGrammars) {
    if (null != nfaGrammars) {
      this.nfaGrammars = nfaGrammars;
    } else {
      this.nfaGrammars = new ArrayList<>(0);
    }
  }

  public void convert() {
    for (Grammar nfaGrammar : nfaGrammars) {
      NfaPrimaryGrammarContent nfaContent =
          (NfaPrimaryGrammarContent) nfaGrammar.primaryGrammarContent;
      TokenNfa tokenNfa = doConvert(nfaContent);
      ((TokenNfable) nfaGrammar).setTokenNfa(tokenNfa);
    }
  }

  private TokenNfa doConvert(NfaPrimaryGrammarContent nfaContent) {
    HashMap<String, TokenNfaState> states = createStates(nfaContent);
    // build edges
    for (NfaPrimaryGrammarContentEdge edge : nfaContent.edges) {
      TokenNfaState from = states.get(edge.from);
      TokenNfaState to = states.get(edge.to);
      char[] chars = edge.chars;
      if (chars.length < 1) {
        from.addEdge(TokenNfa.EPSILON, to);
      } else {
        switch (edge.type) {
          case SEQUENCE_CHARS -> {
            TokenNfaState prev = from;
            for (char ch : edge.chars) {
              TokenNfaState next = new TokenNfaState();
              prev.addEdge(ch & 0x000000FF, next);
              prev = next;
            }
            prev.addEdge(TokenNfa.EPSILON, to);
          }
          case ONE_CHAR_OPTION_CHARSET -> {
            for (char ch : edge.chars) {
              from.addEdge(ch & 0x000000FF, to);
            }
          }
        }
      }
    }
    // tokenNfa
    TokenNfa tokenNfa = new TokenNfa();
    tokenNfa.start = states.get(nfaContent.start);
    tokenNfa.end = states.get(nfaContent.end);
    return tokenNfa;
  }

  private HashMap<String, TokenNfaState> createStates(NfaPrimaryGrammarContent nfaContent) {
    HashMap<String, TokenNfaState> states = new HashMap<>(nfaContent.edges.size());
    states.computeIfAbsent(nfaContent.start, k -> new TokenNfaState());
    states.computeIfAbsent(nfaContent.end, k -> new TokenNfaState());
    for (NfaPrimaryGrammarContentEdge edge : nfaContent.edges) {
      states.computeIfAbsent(edge.from, k -> new TokenNfaState());
      states.computeIfAbsent(edge.to, k -> new TokenNfaState());
    }
    return states;
  }
}
