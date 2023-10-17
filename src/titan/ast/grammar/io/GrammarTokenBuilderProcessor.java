package titan.ast.grammar.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.runtime.AstRuntimeException;

/**
 * 分隔符：空白符、换行读取输入流，按照字符ascll编码分隔符形成token.
 *
 * @author tian wei jun
 */
public class GrammarTokenBuilderProcessor implements GrammarTokenProcessor {

  private int eof = -1;
  private BufferedInputStream byteBufferedInputStream = null;
  private List<GrammarToken> grammarTokens;
  private boolean isEnd = false;
  private GrammarCharset grammarCharset;

  public GrammarTokenBuilderProcessor(InputStream inputStream) {
    grammarCharset = AstContext.get().grammarCharset;
    setSource(inputStream);
  }

  public GrammarTokenBuilderProcessor(String file) {
    grammarCharset = AstContext.get().grammarCharset;
    setSource(file);
  }

  private void setSource(String file) {
    FileInputStream fileInputStream = null;
    try {
      fileInputStream = new FileInputStream(file);
    } catch (Exception e) {
      throw new AstRuntimeException(e);
    }
    setSource(fileInputStream);
  }

  /**
   * 设置 语法文件对应的输入流.
   *
   * @param inputStream 语法文件对应的输入流
   */
  public void setSource(InputStream inputStream) {
    try {
      byteBufferedInputStream = new BufferedInputStream(inputStream);
    } catch (Exception e) {
      throw new AstRuntimeException(e);
    }
  }

  @Override
  public void process(List<GrammarToken> grammarTokens) {
    this.grammarTokens = grammarTokens;

    isEnd = false;
    try {
      while (!isEnd) {
        buildToken();
      }
    } catch (Exception e) {
      close();
      throw e;
    }
  }

  /** 无需识别转义的换行(\n),换行在字符集里面的真实值才是真正的换行. */
  private void buildToken() {
    int peek = eof;
    try {
      byteBufferedInputStream.mark(1);
      peek = byteBufferedInputStream.read();
      byteBufferedInputStream.reset();
    } catch (IOException e) {
      close();
      throw new AstRuntimeException(e);
    }

    if (grammarCharset.isGrammarWhitespace(peek)) {
      buildWhitespaceToken();
    } else if (grammarCharset.isGrammarNewLine(peek)) {
      buildNewlineToken();
    } else if (-1 == peek) {
      isEnd = true;
    } else {
      buildTextToken();
    }
  }

  private void buildNewlineToken() {
    byteBufferedInputStream.mark(1);
    int read = readChar();

    if (grammarCharset.isGrammarNewLine(read)) {
      grammarTokens.add(new GrammarToken(GrammarTokenType.NEWLINE, String.valueOf((char) read)));
    } else {
      reset();
    }
  }

  private void buildTextToken() {
    int read = eof;
    StringBuilder sb = new StringBuilder();
    do {
      byteBufferedInputStream.mark(1);
      read = readChar();

      if (read == eof) {
        break;
      }
      if (grammarCharset.isGrammarWhitespace(read) || grammarCharset.isGrammarNewLine(read)) {
        reset();
        break;
      }
      sb.append((char) read);
    } while (true);

    if (sb.length() > 0) {
      GrammarToken stringToken = new GrammarToken(GrammarTokenType.TEXT, sb.toString());
      grammarTokens.add(stringToken);
    }
  }

  private void buildWhitespaceToken() {
    int read = eof;
    StringBuilder sb = new StringBuilder();
    do {
      byteBufferedInputStream.mark(1);
      read = readChar();

      if (grammarCharset.isGrammarWhitespace(read)) { // Whitespace
        sb.append((char) read);
      } else { // eof or text, back to location
        reset();
        break;
      }
    } while (true);

    if (sb.length() > 0) {
      GrammarToken stringToken = new GrammarToken(GrammarTokenType.WHITE_SPACE, sb.toString());
      grammarTokens.add(stringToken);
    }
  }

  private int readChar() {
    int read = eof;
    try {
      read = byteBufferedInputStream.read();
    } catch (IOException e) {
      close();
      throw new AstRuntimeException(e);
    }
    return read;
  }

  private void reset() {
    try {
      byteBufferedInputStream.reset();
    } catch (IOException e) {
      close();
      throw new AstRuntimeException(e);
    }
  }

  private void close() {
    try {
      byteBufferedInputStream.close();
      byteBufferedInputStream = null;
    } catch (IOException e) {
      throw new AstRuntimeException(e);
    }
  }
}
