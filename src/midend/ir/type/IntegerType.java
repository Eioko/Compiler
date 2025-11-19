package midend.ir.type;

public class IntegerType extends DataType {
    int bitWidth;
    public IntegerType() {
        this.bitWidth = 32;
    }
    public IntegerType(int bitWidth) {
        this.bitWidth = bitWidth;
    }

    public int getBitWidth() {
        return bitWidth;
    }

    @Override
    public String toString() {
        return "i" + bitWidth;
    }
}
