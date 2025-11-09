package midend.ir;

import midend.ir.value.Function;
import midend.ir.value.GlobalVariable;
import midend.ir.value.Value;

import java.util.ArrayList;

public class IrModule extends Value {
    private static final IrModule IR_MODULE = new IrModule();

    private final ArrayList<Function> functions;
    private final ArrayList<GlobalVariable> globalVariables;
    private IrModule() {
        super("Module", null, null);
        functions = new ArrayList<>();
        globalVariables = new ArrayList<>();
    }
    public static IrModule getInstance() {
        return IR_MODULE;
    }


    public void addFunction(Function function){
        for (Value functionNode : functions){
            if (functionNode.equals(function)){
                throw new AssertionError("function is already in!");
            }
        }
        functions.add(function);
    }
    public ArrayList<Function> getFunctions(){
        return functions;
    }
    public void addGlobalVariable(GlobalVariable globalVariable){
        for (Value globalVariableNode : globalVariables){
            if (globalVariableNode.equals(globalVariable)){
                throw new AssertionError("global variable is already in!");
            }
        }
        globalVariables.add(globalVariable);
    }
    public ArrayList<GlobalVariable> getGlobalVariables(){
        return globalVariables;
    }
}
