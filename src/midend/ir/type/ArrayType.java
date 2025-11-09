package midend.ir.type;

public class ArrayType extends ValueType {
    private int size;
    public ArrayType(int size) {
        this.size = size;
    }
}
