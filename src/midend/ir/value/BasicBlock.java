package midend.ir.value;

import midend.ir.type.LabelType;

public class BasicBlock extends Value{
    public BasicBlock(int num , Function function) {
        super("b"+num, new LabelType(), function);
    }
    public Function getParent(){
        return (Function) super.getParent();
    }
}
