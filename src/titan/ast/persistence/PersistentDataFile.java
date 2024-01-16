package titan.ast.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.GrammarType;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.grammar.syntax.AstAutomata;
import titan.ast.grammar.syntax.AstAutomataType;
import titan.ast.grammar.syntax.BacktrackingBottomUpAstAutomata;
import titan.ast.grammar.syntax.FollowFilterBacktrackingBottomUpAstAutomata;
import titan.ast.grammar.syntax.ProductionRule;
import titan.ast.grammar.syntax.SyntaxDfa;
import titan.ast.grammar.token.KeyWordAutomata;
import titan.ast.runtime.AstRuntimeException;
import titan.ast.util.StringUtils;

/**
 * PersistentDatad对应在文件中的对象.
 *
 * @author tian wei jun
 */
public class PersistentDataFile {
  PersistentData persistentData;
  FileOutputStream outputStream;

  ByteBuffer intByteBuffer = ByteBuffer.allocate(4);

  public PersistentDataFile() {
    intByteBuffer.order(ByteOrder.BIG_ENDIAN);
  }

  public void save(PersistentData persistentData, File outputFile) {
    this.persistentData = persistentData;
    try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
      this.outputStream = fileOutputStream;

      writeStringPool();
      writeGrammars();
      writeKeyWordAutomata();
      writeTokenDfa();
      writeProductionRules();
      writeAstAutomata();

    } catch (IOException e) {
      throw new AstRuntimeException(e);
    }
  }

  private void writeAstAutomata() {
    AstAutomata astAutomata = persistentData.astAutomata;

    AstAutomataType astAutomataType = astAutomata.getType();
    writeInt(astAutomataType.ordinal());

    switch (astAutomataType) {
      case BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        writeBacktrackingBottomUpAstAutomata((BacktrackingBottomUpAstAutomata) astAutomata);
        break;
      case FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA:
        writeFollowFilterBacktrackingBottomUpAstAutomata(
            (FollowFilterBacktrackingBottomUpAstAutomata) astAutomata);
        break;
      default:
    }
  }

  private void writeFollowFilterBacktrackingBottomUpAstAutomata(
      FollowFilterBacktrackingBottomUpAstAutomata astAutomata) {
    LinkedHashMap<Grammar, Integer> grammarIntegerMap = persistentData.grammarIntegerMap;

    writeInt(grammarIntegerMap.get(astAutomata.startGrammar));
    writeAstDfa(astAutomata.astDfa);

    // follow
    writeInt(grammarIntegerMap.get(astAutomata.eof));
    writeInt(astAutomata.nonterminalFollowMap.size());
    for (Entry<Grammar, Set<Grammar>> entry : astAutomata.nonterminalFollowMap.entrySet()) {
      writeInt(grammarIntegerMap.get(entry.getKey()));

      Set<Grammar> follows = entry.getValue();
      writeInt(follows.size());
      for (Grammar nonterminalFollow : follows) {
        writeInt(grammarIntegerMap.get(nonterminalFollow));
      }
    }
  }

  private void writeBacktrackingBottomUpAstAutomata(BacktrackingBottomUpAstAutomata astAutomata) {
    LinkedHashMap<Grammar, Integer> grammarIntegerMap = persistentData.grammarIntegerMap;
    writeInt(grammarIntegerMap.get(astAutomata.startGrammar));
    writeAstDfa(astAutomata.astDfa);
  }

  private void writeAstDfa(SyntaxDfa astDfa) {
    int[] dataOfAstDfa = persistentData.getSyntaxDfaStates(astDfa);
    for (int data : dataOfAstDfa) {
      writeInt(data);
    }
  }

  private void writeProductionRules() {
    persistentData.initProductionRules();
    Set<ProductionRule> productionRules = persistentData.productionRuleIntegerMap.keySet();
    writeInt(productionRules.size());
    LinkedHashMap<String, Integer> stringPool = persistentData.stringPool;
    LinkedHashMap<Grammar, Integer> grammarIntegerMap = persistentData.grammarIntegerMap;
    for (ProductionRule productionRule : productionRules) {
      writeInt(grammarIntegerMap.get(productionRule.grammar));
      int intSymbolOfAlias = -1;
      if (StringUtils.isNotBlank(productionRule.alias)) {
        intSymbolOfAlias = stringPool.get(productionRule.alias);
      }
      writeInt(intSymbolOfAlias);
      int[] reducingDfaStates = persistentData.getSyntaxDfaStates(productionRule.reducingDfa);
      for (int dataOfReducingDfaState : reducingDfaStates) {
        writeInt(dataOfReducingDfaState);
      }
    }
  }

  private void writeTokenDfa() {
    int[] tokenDfaData =
        persistentData.initTokenDfaStates(AstContext.get().languageGrammar.tokenDfa);
    for (int data : tokenDfaData) {
      writeInt(data);
    }
  }

  private void writeKeyWordAutomata() {
    LinkedHashMap<Grammar, Integer> grammarIntegerMap = persistentData.grammarIntegerMap;
    KeyWordAutomata keyWordAutomata = AstContext.get().languageGrammar.keyWordAutomata;

    writeInt(keyWordAutomata.emptyOrNot);

    if (keyWordAutomata.emptyOrNot == KeyWordAutomata.EMPTY) {
      return;
    }

    writeInt(grammarIntegerMap.get(keyWordAutomata.rootKeyWord));

    int keyWordsSize = keyWordAutomata.textTerminalMap.size();
    writeInt(keyWordsSize);

    LinkedHashMap<String, Integer> stringPool = persistentData.stringPool;
    for (Entry<String, Grammar> entry : keyWordAutomata.textTerminalMap.entrySet()) {
      String text = entry.getKey();
      int intOfText = stringPool.get(text);
      writeInt(intOfText);

      Grammar terminal = entry.getValue();
      Integer intOfTerminal = grammarIntegerMap.get(terminal);
      writeInt(intOfTerminal);
    }
  }

  private void writeGrammars() {
    persistentData.initGrammars();
    Set<Grammar> grammars = persistentData.grammarIntegerMap.keySet();
    writeInt(grammars.size());
    LinkedHashMap<String, Integer> stringPool = persistentData.stringPool;
    for (Grammar grammar : grammars) {
      writeInt(grammar.type.ordinal());
      writeInt(stringPool.get(grammar.name));
      writeInt(grammar.action.ordinal());
      // lookaheadMatchingMode
      if (grammar.type == GrammarType.TERMINAL) {
        TerminalGrammar terminalGrammar = (TerminalGrammar) grammar;
        writeInt(terminalGrammar.lookaheadMatchingMode.ordinal());
      }
    }
  }

  private void writeStringPool() {
    persistentData.initStringPool();
    Set<String> strings = persistentData.stringPool.keySet();
    writeInt(strings.size());
    for (String str : strings) {
      writeString(str);
    }
  }

  private void writeString(String str) {
    byte[] bytes = encodeString(str);
    writeInt(bytes.length);
    doWrite(bytes, 0, bytes.length);
  }

  private byte[] encodeString(String str) {
    char[] chars = str.toCharArray();
    byte[] bytes = new byte[chars.length];
    int indexOfChar = 0;
    while (indexOfChar < chars.length) {
      char ch = chars[indexOfChar];
      bytes[indexOfChar] = (byte) (ch & 0xFF);
      ++indexOfChar;
    }
    return bytes;
  }

  private void writeInt(int value) {
    intByteBuffer.clear();
    intByteBuffer.putInt(value);
    doWrite(intByteBuffer.array(), 0, intByteBuffer.capacity());
  }

  private void doWrite(byte[] bytes, int offset, int length) {
    try {
      outputStream.write(bytes, offset, length);
    } catch (Exception e) {
      throw new AstRuntimeException(e);
    }
  }
}
