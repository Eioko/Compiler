package midend.ir.type;

public class StringType extends DataType {
    int len;
    public StringType(int len) {
        super(len);
        this.len = len;
    }

    /**
     * Declare语句中使用
     */
    public StringType() {
        super(-1);
        this.len = -1;
    }

    public String toString() {
        if(len == -1){
            return "i8*";
        }
        return "[" + len + " x i8]";
    }

    @Override
    public int getSizeInBytes() {
        return len;
    }
}
