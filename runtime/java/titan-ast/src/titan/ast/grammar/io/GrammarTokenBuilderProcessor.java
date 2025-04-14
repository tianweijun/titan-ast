package titan.ast.grammar.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import titan.ast.AstRuntimeException;

/**
 * 分隔符：空白符、换行读取输入流，按照字符ascll编码分隔符形成token.
 *
 * @author tian wei jun
 */
public class GrammarTokenBuilderProcessor implements GrammarTokenProcessor {

  private final int eof = -1;
  private BufferedInputStream byteBufferedInputStream = null;
  private List<GrammarToken> grammarTokens;
  private boolean isEnd = false;
  private final Charset charset;
  private final String newlineText;
  ByteBuffer byteBuffer = new ByteBuffer(256);

  public GrammarTokenBuilderProcessor(InputStream inputStream) {
    charset = GrammarCharset.CHARSET;
    newlineText = new String(new byte[] {(byte) GrammarCharset.NEW_LINE}, charset);
    setSource(inputStream);
  }

  public GrammarTokenBuilderProcessor(String file) {
    charset = GrammarCharset.CHARSET;
    newlineText = new String(new byte[] {(byte) GrammarCharset.NEW_LINE}, charset);
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
    if (peek == eof) {
      isEnd = true;
      return;
    }
    char peekChar = (char) (0xFF & peek);
    if (GrammarCharset.isWordSpace(peekChar)) {
      buildWordSpaceToken();
    } else if (GrammarCharset.isNewLine(peekChar)) {
      buildNewlineToken();
    } else {
      buildTextToken();
    }
  }

  private void buildNewlineToken() {
    byteBufferedInputStream.mark(1);
    int read = readByte();

    if (read != eof && GrammarCharset.isNewLine((char) (read & 0xFF))) {
      grammarTokens.add(new GrammarToken(GrammarTokenType.NEWLINE, newlineText));
    } else {
      reset();
    }
  }

  private void buildTextToken() {
    int read = eof;
    byteBuffer.clear();
    do {
      byteBufferedInputStream.mark(1);
      read = readByte();

      if (read == eof) {
        break;
      }
      char readChar = (char) (0xFF & read);
      if (GrammarCharset.isWordSpace(readChar) || GrammarCharset.isNewLine(readChar)) {
        reset();
        break;
      }
      byteBuffer.append(read);
    } while (true);

    if (byteBuffer.length() > 0) {
      GrammarToken stringToken =
          new GrammarToken(GrammarTokenType.TEXT, byteBuffer.toString(charset));
      grammarTokens.add(stringToken);
    }
  }

  private void buildWordSpaceToken() {
    int read = eof;
    byteBuffer.clear();
    do {
      byteBufferedInputStream.mark(1);
      read = readByte();

      if (eof != read && GrammarCharset.isWordSpace((char) (read & 0xFF))) { // Whitespace
        byteBuffer.append(read);
      } else { // eof or text, back to location
        reset();
        break;
      }
    } while (true);

    if (byteBuffer.length() > 0) {
      GrammarToken stringToken =
          new GrammarToken(GrammarTokenType.WORD_SPACE, byteBuffer.toString(charset));
      grammarTokens.add(stringToken);
    }
  }

  private int readByte() {
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
