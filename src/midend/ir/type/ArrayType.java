package midend.ir.type;

public class ArrayType extends DataType{
    private int size;
    public ArrayType(int size) {
        super(size * 4);
        this.size = size;
    }
    public int getSize() {
        return size;
    }

    public String toString() {
        return "[" + size + " x i32]";
    }

    @Override
    public int getSizeInBytes() {
        return size * 4;
    }
}
