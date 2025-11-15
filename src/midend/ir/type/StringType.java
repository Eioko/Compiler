package midend.ir.type;

public class StringType extends DataType {
    int len;
    public StringType(int len) {
        this.len = len;
    }

    /**
     * Declare语句中使用
     */
    public StringType() {
        this.len = -1;
    }

    public String toString() {
        return "i8*";
    }
}
