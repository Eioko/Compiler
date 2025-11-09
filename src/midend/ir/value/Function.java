package midend.ir.value;

import midend.ir.IrModule;
import midend.ir.type.DataType;
import midend.ir.type.FunctionType;
import java.util.ArrayList;

public class Function extends Value {
    private final ArrayList<Argument> arguments = new ArrayList<>();
    //基本块
    private final ArrayList<BasicBlock> blocks = new ArrayList<>();
    private final DataType returnType;

    public Function(String name, FunctionType functionType) {
        super("@f" + name, functionType, IrModule.getInstance());
        this.returnType = ((FunctionType)getValueType()).getReturnType();
        for(int i = 0; i<functionType.getArguments().size(); i++) {
            Argument argument = new Argument(i, functionType.getArguments().get(i), this);
            arguments.add(argument);
        }
    }
    public BasicBlock getHeadBlock() {
        //这里是否可能出错？？数组越界？
        return blocks.get(0);
    }

}
