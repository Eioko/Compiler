package midend.ir.type;

public class PointerType extends DataType {
    private ValueType pointeeType
    public PointerType(ValueType type) {
        this.pointeeType = type;
    }
    public ValueType getPointeeType()
    {
        return pointeeType;
    }
}
