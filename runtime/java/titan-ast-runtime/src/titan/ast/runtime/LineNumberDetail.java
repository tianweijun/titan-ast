package titan.ast.runtime;

/**
 * .
 *
 * @author tian wei jun
 */
public class LineNumberDetail {
  public final LineNumberRange[] lineNumberRanges;

  public LineNumberDetail(LineNumberRange[] lineNumberRanges) {
    this.lineNumberRanges = lineNumberRanges;
  }

  public LineNumberRangeDto getLineNumberRangeDto(int bytePosition) {
    int left = 0;
    int right = lineNumberRanges.length - 1;

    while (left <= right) {
      int mid = left + (right - left) / 2; // 计算中间元素的索引
      LineNumberRange midLineNumberRange = lineNumberRanges[mid];
      if (bytePosition >= midLineNumberRange.start && bytePosition < midLineNumberRange.end) {
        // 找到目标值，返回行号lineNumber=index+1
        return new LineNumberRangeDto(mid + 1, midLineNumberRange.start, midLineNumberRange.end);
      } else if (bytePosition < midLineNumberRange.start) {
        right = mid - 1; // 目标值在左半部分
      } else {
        left = mid + 1; // 目标值在右半部分
      }
    }
    return null;
  }

  public static class LineNumberRange {
    public int start;
    public int end;

    public LineNumberRange(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }

  public static class LineNumberRangeDto {
    public int lineNumber;
    public int start;
    public int end;

    public LineNumberRangeDto(int lineNumber, int start, int end) {
      this.lineNumber = lineNumber;
      this.start = start;
      this.end = end;
    }
  }
}
