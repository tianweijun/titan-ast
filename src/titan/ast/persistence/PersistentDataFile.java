package titan.ast.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Set;
import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.GrammarType;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.grammar.syntax.ProductionRule;
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
      persistentData.initStringPool();

      writeStringPool();
      writeGrammars();
      writeTokenDfaStates();
      writeInt(persistentData.startGrammar());
      writeProductionRules();
      writeAstDfa();

    } catch (IOException e) {
      throw new AstRuntimeException(e);
    }
  }

  private void writeAstDfa() {
    int[] astDfa = persistentData.getSyntaxDfaStates(AstContext.get().languageGrammar.astDfa);
    for (int dataOfAstDfa : astDfa) {
      writeInt(dataOfAstDfa);
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

  private void writeTokenDfaStates() {
    int[] tokenDfaStates =
        persistentData.initTokenDfaStates(AstContext.get().languageGrammar.tokenDfa);
    for (int dataOfTokenDfaState : tokenDfaStates) {
      writeInt(dataOfTokenDfaState);
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
