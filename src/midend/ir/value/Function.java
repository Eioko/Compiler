package midend.ir.value;

import backend.component.MipsFunction;
import backend.operand.MipsPhyReg;
import midend.ir.IrModule;
import midend.ir.type.DataType;
import midend.ir.type.FunctionType;

import java.util.ArrayList;
import java.util.HashMap;

import static backend.MipsModule.*;
import static utils.Configs.regAlloca;

public class Function extends Value {
    private final ArrayList<Argument> arguments = new ArrayList<>();
    //基本块
    private final ArrayList<BasicBlock> blocks = new ArrayList<>();

    private final DataType returnType;

    private HashMap<Value, MipsPhyReg> valueRegMap = new HashMap<>();

    public Function(String name, FunctionType functionType) {
        super("@f" + name, functionType, IrModule.getInstance());
        this.returnType = ((FunctionType)getValueType()).getReturnType();
        for(int i = 0; i<functionType.getArguments().size(); i++) {
            Argument argument = new Argument(i , functionType.getArguments().get(i), this);
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
    public ArrayList<BasicBlock> getBlocks() {
        return blocks;
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

    private MipsFunction mipsFunction = null;
    public MipsFunction getMipsFunction(){
        return mipsFunction;
    }
    public void setMipsFunction(MipsFunction mipsFunction){
        this.mipsFunction = mipsFunction;
    }

    public HashMap<Value, MipsPhyReg> getValueRegMap(){
        return valueRegMap;
    }

    public void toMips(){
        if(!regAlloca){
            setCurrentFunction(this);
            for (int i = 0; i < this.arguments.size(); i++) {
                if (i < 4) {
                    recordValueRegMap(this.arguments.get(i), MipsPhyReg.getReg(MipsPhyReg.Register.A0.ordinal() + i));
                }
                recordStackFotValue(this.arguments.get(i));
            }
        }
        // 这里没有办法取进行分配空间等，因为还未遍历，不能得到函数栈空间情况
        for(BasicBlock block : blocks){
            block.toMips(this);
        }
    }
}
