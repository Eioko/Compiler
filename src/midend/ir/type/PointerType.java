package midend.ir.type;

public class PointerType extends DataType {
    private DataType pointeeType;
    public PointerType(ValueType type) {
        this.pointeeType = (DataType) type;
    }
    public DataType getPointeeType() {
        return pointeeType;
    }

    public String toString() {
        return pointeeType.toString() + "*";
    }
    @Override
    public int getSizeInBytes() {
        return 4;
    }
}
