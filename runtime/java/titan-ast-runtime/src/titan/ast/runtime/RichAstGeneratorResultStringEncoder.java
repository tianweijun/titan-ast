package titan.ast.runtime;

import java.nio.charset.Charset;
import java.util.ArrayList;
import titan.ast.runtime.LineNumberDetail.LineNumberRange;

/**
 * only for AstGeneratorResult2RichResultConverter.
 *
 * @author tian wei jun
 */
class RichAstGeneratorResultStringEncoder {
  private Charset charset = AstGeneratorResult.DEFAULT_CHARSET;

  Charset getCharset() {
    return charset;
  }

  void setCharset(Charset charset) {
    if (charset != null) {
      this.charset = charset;
    }
  }

  void setCharset(String charsetName) {
    setCharset(Charset.forName(charsetName));
  }

  boolean isNeedToEncoding() {
    return !AstGeneratorResult.DEFAULT_CHARSET.equals(charset);
  }

  ArrayList<Token> encodeTokens(ArrayList<Token> tokens) {
    if (isNeedToEncoding()) {
      int indexOfChar = 0;
      for (Token token : tokens) {
        token.start = indexOfChar;
        String text = doEncodeString(token.text);
        token.text = text;
        indexOfChar = text.codePointCount(0, text.length());
      }
    }
    return tokens;
  }

  String encodeString(String str) {
    if (isNeedToEncoding()) {
      return doEncodeString(str);
    }
    return str;
  }

  private String doEncodeString(String str) {
    byte[] bytes = str.getBytes(AstGeneratorResult.DEFAULT_CHARSET);
    return doEncodeBytes(bytes, 0, bytes.length);
  }

  private String doEncodeBytes(byte[] bytes, int offset, int count) {
    return new String(bytes, offset, count, charset);
  }

  private int getCharCount(byte[] buffer, int offset, int count) {
    if (!isNeedToEncoding()) {
      return count;
    }
    String str = doEncodeBytes(buffer, offset, count);
    return str.codePointCount(0, str.length());
  }

  int getOffsetInLine(ArrayList<Token> tokens, LineNumberRange lineNumberRange, int bytePosition) {
    ByteBuffer byteBuffer = new ByteBuffer(bytePosition - lineNumberRange.start + 1);
    int indexOfBytes = tokens.get(lineNumberRange.indexOfStartToken).start;
    for (int indexOfToken = lineNumberRange.indexOfStartToken;
        indexOfToken < tokens.size();
        indexOfToken++) {
      Token token = tokens.get(indexOfToken);
      for (byte byteData : token.text.getBytes(AstGeneratorResult.DEFAULT_CHARSET)) {
        if (indexOfBytes >= lineNumberRange.start && indexOfBytes < bytePosition) {
          byteBuffer.append(byteData);
        }
        ++indexOfBytes;
      }
    }
    return getCharCount(byteBuffer.buffer, 0, byteBuffer.length());
  }
}
