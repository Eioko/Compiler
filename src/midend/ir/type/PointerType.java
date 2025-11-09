package midend.ir.type;

public class PointerType extends DataType {
    private ValueType type;
    public PointerType(ValueType type) {
        this.type = type;
    }

}
