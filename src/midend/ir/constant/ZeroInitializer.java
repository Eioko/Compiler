package midend.ir.constant;

import midend.ir.type.ArrayType;

public class ZeroInitializer extends Constant{
    private int size;

    public ZeroInitializer(ArrayType arrayType){
        super(arrayType);
        size = arrayType.getSize();
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString(){
        return "zeroinitializer";
    }
}
