package myError;

public class SysyError {
    private ErrorType errorType;
    private int lineNum;

    public SysyError(ErrorType errorType, int lineNum) {
        this.errorType = errorType;
        this.lineNum = lineNum;
    }
    public ErrorType getErrorType() {
        return errorType;
    }
    public int getLineNum() {
        return lineNum;
    }

    @Override
    public String toString() {
        return lineNum  + " " + errorType.toString();
    }
}
