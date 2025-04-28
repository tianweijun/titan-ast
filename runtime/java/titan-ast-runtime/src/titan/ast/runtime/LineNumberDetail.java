package titan.ast.runtime;

/**
 * .
 *
 * @author tian wei jun
 */
class LineNumberDetail {
  final LineNumberRange[] lineNumberRanges;

  LineNumberDetail() {
    lineNumberRanges = new LineNumberRange[0];
  }

  LineNumberDetail(LineNumberRange[] lineNumberRanges) {
    this.lineNumberRanges = lineNumberRanges;
  }

  LineNumberRange getLineNumberRange(int bytePosition) {
    int left = 0;
    int right = lineNumberRanges.length - 1;

    while (left <= right) {
      int mid = left + (right - left) / 2; // 计算中间元素的索引
      LineNumberRange midLineNumberRange = lineNumberRanges[mid];
      if (bytePosition >= midLineNumberRange.start && bytePosition < midLineNumberRange.end) {
        return midLineNumberRange;
      } else if (bytePosition < midLineNumberRange.start) {
        right = mid - 1; // 目标值在左半部分
      } else {
        left = mid + 1; // 目标值在右半部分
      }
    }
    return null;
  }

  static class LineNumberRange {
    final int start;
    final int end; // indexOfNewlineByte+1
    final int lineNumber;
    final int indexOfStartToken;
    final int indexOfEndToken;

    LineNumberRange(
        int start, int end, int lineNumber, int indexOfStartToken, int indexOfEndToken) {
      this.start = start;
      this.end = end;
      this.lineNumber = lineNumber;
      this.indexOfStartToken = indexOfStartToken;
      this.indexOfEndToken = indexOfEndToken;
    }
  }
}
