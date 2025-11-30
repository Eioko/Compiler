package midend.ir.type;
/*
函数/指令的返回值类型
 */
public abstract class DataType extends ValueType{
    protected final int sizeInBytes;

    protected DataType(int sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }
    public  int getSizeInBytes(){
        return sizeInBytes;
    }
}
