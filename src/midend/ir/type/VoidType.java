package midend.ir.type;

public class VoidType extends DataType {
    public String toString() {
        return "void";
    }

    @Override
    public int getSizeInBytes() {
        return 0;
    }
}
