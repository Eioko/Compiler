package backend;

import backend.component.MipsFunction;
import backend.component.MipsGlobalVariable;
import backend.operand.MipsOperand;
import midend.ir.value.Value;
import java.util.ArrayList;
import java.util.HashMap;

public class MipsModule {
    //data segment
    private final ArrayList<MipsGlobalVariable> globalVariables = new ArrayList<>();
    //text segment
    private final ArrayList<MipsFunction> functions = new ArrayList<>();

    public final HashMap<Value, MipsOperand> operandMap = new HashMap<>();

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

}
