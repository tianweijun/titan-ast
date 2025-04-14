package titan.ast.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.GrammarType;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.fa.syntax.AstAutomata;
import titan.ast.fa.syntax.AstAutomataType;
import titan.ast.fa.syntax.BacktrackingBottomUpAstAutomata;
import titan.ast.fa.syntax.FollowFilterBacktrackingBottomUpAstAutomata;
import titan.ast.fa.syntax.ProductionRule;
import titan.ast.fa.syntax.SyntaxDfa;
import titan.ast.fa.token.DerivedTerminalGrammarAutomataData;
import titan.ast.fa.token.DerivedTerminalGrammarAutomataData.RootTerminalGrammarMap;
import titan.ast.util.StringUtils;

/**
 * PersistentDatad对应在文件中的对象.
 *
 * @author tian wei jun
 */
public class PersistentDataFile {
  PersistentData persistentData;
  OutputStream outputStream;

  ByteBuffer intByteBuffer = ByteBuffer.allocate(4);

  public PersistentDataFile() {
    intByteBuffer.order(ByteOrder.BIG_ENDIAN);
  }

  public void save(PersistentData persistentData, File outputFile) {
    try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
      save(persistentData, fileOutputStream);
    } catch (IOException e) {
      throw new AstRuntimeException(e);
    }
  }

  public void save(PersistentData persistentData, OutputStream outputStream) {
    this.persistentData = persistentData;
    this.outputStream = outputStream;
    writeStringPool();
    writeGrammars();
    writeDerivedTerminalGrammarAutomata();
    writeTokenDfa();
    writeProductionRules();
    writeAstAutomata();
  }

  private void writeAstAutomata() {
    AstAutomata astAutomata = persistentData.astContext.astAutomata;

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
        persistentData.initTokenDfaStates(persistentData.astContext.tokenDfa);
    for (int data : tokenDfaData) {
      writeInt(data);
    }
  }

  private void writeDerivedTerminalGrammarAutomata() {
    DerivedTerminalGrammarAutomataData derivedTerminalGrammarAutomataData =
        persistentData
            .astContext
            .languageGrammar
            .derivedTerminalGrammarAutomataDetail
            .derivedTerminalGrammarAutomataData;
    List<RootTerminalGrammarMap> rootTerminalGrammarMaps =
        derivedTerminalGrammarAutomataData.rootTerminalGrammarMaps;

    int count = rootTerminalGrammarMaps.size();
    writeInt(count);

    if (count == 0) {
      return;
    }

    for (RootTerminalGrammarMap rootTerminalGrammarMap : rootTerminalGrammarMaps) {
      writeRootTerminalGrammarMap(rootTerminalGrammarMap);
    }
  }

  private void writeRootTerminalGrammarMap(RootTerminalGrammarMap rootTerminalGrammarMap) {
    LinkedHashMap<Grammar, Integer> grammarIntegerMap = persistentData.grammarIntegerMap;
    writeInt(grammarIntegerMap.get(rootTerminalGrammarMap.rootTerminalGrammar));

    int keyWordsSize = rootTerminalGrammarMap.textTerminalMap.size();
    writeInt(keyWordsSize);

    LinkedHashMap<String, Integer> stringPool = persistentData.stringPool;
    for (Entry<String, TerminalGrammar> entry : rootTerminalGrammarMap.textTerminalMap.entrySet()) {
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
