package midend.ir.type;

public class VoidType extends DataType {
    public VoidType() {
        super(0);
    }
    public String toString() {
        return "void";
    }

    @Override
    public int getSizeInBytes() {
        return 0;
    }
}
