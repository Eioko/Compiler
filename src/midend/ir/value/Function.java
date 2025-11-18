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

    /**
     * main函数专用构造函数
     * @param name 函数名（main）
     * @param functionType 函数类型
     * @param isMain 是否为main函数（必须为true）
     */
    public Function(String name, FunctionType functionType, boolean isMain){
        super("@" + name, functionType, IrModule.getInstance());
        this.returnType = ((FunctionType)getValueType()).getReturnType();
        if(!isMain) {
            throw new AssertionError("This constructor is only for main function");
        }
    }
    public BasicBlock getHeadBlock() {
        //这里是否可能出错？？数组越界？
        return blocks.get(0);
    }

    public DataType getReturnType() {
        return returnType;
    }
    public void addBasicBlock(BasicBlock block) {
        blocks.add(block);
    }
    public ArrayList<Argument> getArguments() {
        return arguments;
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        // function header
        sb.append("\ndefine dso_local ")
          .append(returnType.toString())
          .append(" ")
          .append(getName())
          .append("(");
        for (int i = 0; i < arguments.size(); i++) {
            Argument arg = arguments.get(i);
            sb.append(arg.getValueType().toString()).append(" ").append(arg.getName());
            if (i != arguments.size() - 1) sb.append(", ");
        }
        sb.append(") {")
          .append("\n");
        // body: print all basic blocks in order
        for (BasicBlock block : blocks) {
            sb.append(block.toString());
        }
        sb.append("}");
        return sb.toString();
    }
}
