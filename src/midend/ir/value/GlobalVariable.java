package midend.ir.value;

import backend.component.MipsGlobalVariable;
import midend.ir.IrModule;
import midend.ir.constant.*;
import midend.ir.type.PointerType;
import midend.ir.type.StringType;

import java.util.ArrayList;

public class GlobalVariable extends User {
    private final boolean isConst;

    public GlobalVariable(String name, Constant initVal, boolean isConst) {
        super("@g"+name, new PointerType(initVal.getValueType()), IrModule.getInstance(), initVal);
        this.isConst = isConst;
    }

    /**
     * 创建静态全局变量
     * @param name
     * @param initVal
     * @param isStatic
     * @param symbolTableId
     */
    public GlobalVariable(String name, Constant initVal,  boolean isStatic, int symbolTableId) {
        super("@s"+name+symbolTableId, new PointerType(initVal.getValueType()), IrModule.getInstance(), initVal);
        this.isConst = false;
    }
    public boolean isConst() {
        return isConst;
    }

    @Override
    public String toString() {
        if(((PointerType)getValueType()).getPointeeType() instanceof StringType){
            return this.getName() + " = private unnamed_addr constant " + getUsedValue(0).getValueType()+ " "
                    + getUsedValue(0).toString() + ", align 1";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(" = dso_local ");
        if (isConst) {
            sb.append("constant ");
        } else {
            sb.append("global ");
        }
        sb.append(((PointerType)this.getValueType()).getPointeeType());
        sb.append(" ");
        sb.append(getUsedValue(0).toString());
        return sb.toString();
    }

    public Constant getInitValue() {
        return (Constant) getUsedValue(0);
    }

    public void toMips(){
        ArrayList<Integer> initVal = new ArrayList<>();

        if(((PointerType)getValueType()).getPointeeType() instanceof StringType){
            MipsGlobalVariable mipsGlobalVariable = new MipsGlobalVariable(this.getName(),
                    ((ConstString)getInitValue()).getMipsString());
            mipsModule.addGlobalVariable(mipsGlobalVariable);
        }else if(getInitValue() instanceof ZeroInitializer){
            int size = ((ZeroInitializer) getInitValue()).getSize();
            MipsGlobalVariable mipsGlobalVariable = new MipsGlobalVariable(this.getName(), size);
            mipsModule.addGlobalVariable(mipsGlobalVariable);
        }else if(getInitValue() instanceof ConstArray){
            initVal = ((ConstArray) getInitValue()).toIntList();
            MipsGlobalVariable mipsGlobalVariable = new MipsGlobalVariable(this.getName(), initVal);
            mipsModule.addGlobalVariable(mipsGlobalVariable);
        }else{
            //单变量
            initVal.add(((ConstInt) getInitValue()).getNumber());
            MipsGlobalVariable mipsGlobalVariable = new MipsGlobalVariable(this.getName(), initVal);
            mipsModule.addGlobalVariable(mipsGlobalVariable);
        }

    }
}
