package midend.ir.type;

public class IntegerType extends DataType {
    int bitWidth;
    public IntegerType() {
        super(4);
        this.bitWidth = 32;
    }
    public IntegerType(int bitWidth) {
        super(4);
        this.bitWidth = bitWidth;
    }

    public int getBitWidth() {
        return bitWidth;
    }

    @Override
    public String toString() {
        return "i" + bitWidth;
    }

    @Override
    public int getSizeInBytes() {
        return 4;
    }
}
