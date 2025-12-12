package backend;

import backend.component.MipsBlock;
import backend.component.MipsFunction;
import backend.component.MipsGlobalVariable;
import backend.instruction.MipsBinary;
import backend.instruction.MipsInstruction;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import backend.operand.MipsPhyReg;
import midend.ir.value.Function;
import midend.ir.value.GlobalVariable;
import midend.ir.value.Value;
import utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;

import static backend.operand.MipsPhyReg.SP;

public class MipsModule {
    //data segment
    private final ArrayList<MipsGlobalVariable> globalVariables = new ArrayList<>();
    //text segment
    private final ArrayList<MipsFunction> functions = new ArrayList<>();

    public final HashMap<Value, MipsOperand> operandMap = new HashMap<>();

    public MipsFunction mainFunction = null;

    //一个函数内部的value到寄存器的映射表，在切换函数时更新
    public static HashMap<Value, MipsPhyReg> valueRegMap = new HashMap<>();

    public MipsFunction currentFunction = null;
    /**
     * 对于sp的相对负值
     */
    public static int stackOffset = 0;

    public static HashMap<Value, Integer> valueStackOffsetMap = new HashMap<>();

    public final HashMap<Pair<MipsBlock, MipsBlock>, ArrayList<MipsInstruction>> phiCopysList = new HashMap<>();

    private static final MipsModule mipsModule = new MipsModule();
    private MipsModule() {}
    public static MipsModule getInstance() {
        return mipsModule;
    }

    public void addGlobalVariable(MipsGlobalVariable globalVariable){
        globalVariables.add(globalVariable);
    }
    public ArrayList<MipsGlobalVariable> getGlobalVariables(){
        return globalVariables;
    }
    public void addFunction(MipsFunction function){
        if (function.getName().equals("main")) {
            mainFunction = function;
        }
        functions.add(function);
    }
    public ArrayList<MipsFunction> getFunctions(){
        return functions;
    }
    public void addOperandMapping(Value value, MipsOperand mipsOperand){
        operandMap.put(value, mipsOperand);
    }
    public MipsOperand getOperandMapping(Value value) {
        return operandMap.get(value);
    }

    public static int allocateStackForValue(MipsBlock mipsBlock,  Value value){
        mipsBlock.addInstruction(new MipsBinary(MipsBinary.BinaryOp.ADDU, SP, SP, new MipsImm(-4)));
        stackOffset -= 4;
        valueStackOffsetMap.put(value, stackOffset);
        return stackOffset;
    }

    public static int recordStackFotValue(Value value){
        stackOffset -= 4;
        valueStackOffsetMap.put(value, stackOffset);
        return stackOffset;
    }

    public static void allocateStackSpace(int a){
        stackOffset -= a;
    }

    public static Integer getValStackOffset(Value value){
        return valueStackOffsetMap.get(value);
    }

    public static int getCurrentStackOffset(){
        return stackOffset;
    }
    public static void setCurrentFunction(Function function){
        mipsModule.currentFunction = function.getMipsFunction();
        stackOffset = 0;
        valueRegMap = function.getValueRegMap();
        valueStackOffsetMap = new HashMap<>();
    }

    public static void recordValueRegMap(Value value, MipsOperand reg){
        valueRegMap.put(value, (MipsPhyReg)reg);
    }
    public static MipsPhyReg getValueToReg(Value value, Function function){
        return valueRegMap.get(value);
    }
    public static ArrayList<MipsPhyReg> getAllocatedRegs(){
        return new ArrayList<>(valueRegMap.values());
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        // data segment
        sb.append(".data\n");
        for (MipsGlobalVariable globalVariable : globalVariables) {
            if(globalVariable.isInit() && !globalVariable.isStr()){
                sb.append(globalVariable.toString()).append("\n");
            }
        }
        for (MipsGlobalVariable globalVariable : globalVariables) {
            if(!globalVariable.isInit() && !globalVariable.isStr()) {
                sb.append(globalVariable.toString()).append("\n");
            }
        }
        for (MipsGlobalVariable globalVariable : globalVariables) {
            if(globalVariable.isStr()){
                sb.append(globalVariable.toString()).append("\n");
            }
        }
        // text segment
        sb.append(".text\n");
        sb.append(mainFunction.toString());

        for (MipsFunction function : functions) {
            if(function != mainFunction){
                sb.append(function.toString()).append("\n");
            }
        }

        return sb.toString();
    }
}
