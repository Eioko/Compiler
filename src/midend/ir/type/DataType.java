package midend.ir.type;
/*
函数/指令的返回值类型
 */
public abstract class DataType extends ValueType{


    public abstract int getSizeInBytes();
}
