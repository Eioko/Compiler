package backend;

import backend.component.MipsFunction;
import backend.component.MipsGlobalVariable;
import backend.operand.MipsOperand;
import backend.operand.MipsPhyReg;
import midend.ir.value.Function;
import midend.ir.value.Value;
import java.util.ArrayList;
import java.util.HashMap;

public class MipsModule {
    //data segment
    private final ArrayList<MipsGlobalVariable> globalVariables = new ArrayList<>();
    //text segment
    private final ArrayList<MipsFunction> functions = new ArrayList<>();

    public final HashMap<Value, MipsOperand> operandMap = new HashMap<>();

    //一个函数内部的value到寄存器的映射表，在切换函数时更新
    public static HashMap<Value, MipsPhyReg> valueRegMap = new HashMap<>();

    public static ArrayList<MipsPhyReg> allocatedRegs = new ArrayList<>();

    public MipsFunction currentFunction = null;
    /**
     * 对于sp的相对负值
     */
    public static int stackOffset = 0;

    public static HashMap<Value, Integer> valueStackOffsetMap = new HashMap<>();

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

    public static int allocateStackForValue(Value value){
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
    }

    public static void recordValueRegMap(Value value, MipsOperand reg){
        valueRegMap.put(value, (MipsPhyReg)reg);
    }
    public static MipsPhyReg getValueToReg(Value value, Function function){
        return function.getValueRegMap().get(value);
    }
    public static ArrayList<MipsPhyReg> getAllocatedRegs(){
        return allocatedRegs;
    }
}
