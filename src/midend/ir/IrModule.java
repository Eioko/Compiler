package midend.ir;

import backend.MipsModule;
import backend.component.MipsBlock;
import backend.component.MipsFunction;
import midend.ir.value.*;

import java.util.ArrayList;

public class IrModule extends Value {
    private static final IrModule IR_MODULE = new IrModule();

    private final ArrayList<Declare> declares;
    private final ArrayList<Function> functions;
    private final ArrayList<GlobalVariable> globalVariables;
    private IrModule() {
        super("Module", null, null);
        declares = new ArrayList<>();
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

    public void addDeclare(Declare declare){
        for (Value declareNode : declares){
            if (declareNode.equals(declare)){
                throw new AssertionError("declare is already in!");
            }
        }
        declares.add(declare);
    }
    public ArrayList<Declare> getDeclares(){
        return declares;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Declare declare : declares){
            sb.append(declare.toString()).append("\n");
        }
        for(GlobalVariable globalVariable : globalVariables){
            sb.append(globalVariable.toString()).append("\n");
        }
        for(Function function : functions){
            sb.append(function.toString()).append("\n");
        }
        return sb.toString();
    }
    public void irMap(){
        for(Function function : functions){
            MipsFunction mipsFunction = new MipsFunction(function.getName());
            function.setMipsFunction(mipsFunction);
            ArrayList<BasicBlock> blocks = function.getBlocks();
            for(BasicBlock block : blocks){
                MipsBlock mipsBlock = new MipsBlock(block.getName());
                block.setMipsBlock(mipsBlock);
            }
        }
    }

    public void toMips(){
        for(GlobalVariable globalVariable : globalVariables){
            globalVariable.toMips();
        }
        // jump to main

        irMap();
        for(Function function : functions){
            function.toMips();
        }
    }
}
