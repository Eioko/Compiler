package midend.ir.value;

import backend.MipsBuilder;
import backend.MipsModule;
import backend.instruction.MipsLi;
import backend.instruction.MipsMove;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import backend.operand.MipsPhyReg;
import midend.ir.constant.ConstInt;
import midend.ir.instruction.Instruction;
import midend.ir.type.ValueType;

import java.util.ArrayList;

public class Value {
    protected String name;
    protected ValueType valueType;
    private Value parent;

    protected MipsModule mipsModule = MipsModule.getInstance();

    protected final ArrayList<User> users;
    public Value(String name, ValueType valueType, Value parent) {
        this.name = name;
        this.valueType = valueType;
        users = new ArrayList<>();
        this.parent = parent;
    }

    public String getName() {
        return name;
    }
    public ValueType getValueType() {
        return valueType;
    }
    public void addUser(User user) {
        users.add(user);
    }
    public Value getParent() {
        return parent;
    }

    public MipsOperand toMipsOperand(boolean canImm, Function irFunction, BasicBlock irBlock, int opIndex) {
        if(mipsModule.getOperandMapping(this) != null) {
            MipsOperand operand = mipsModule.getOperandMapping(this);
            if(!canImm && operand instanceof MipsImm) {
                if((((MipsImm) operand).getNumber()) == 0){
                    return new MipsPhyReg(MipsPhyReg.Register.ZERO);
                }else{
                    MipsPhyReg reg;
                    if(opIndex == 0){
                        reg = new MipsPhyReg(MipsPhyReg.Register.T0);
                    }else if(opIndex == 1){
                        reg = new MipsPhyReg(MipsPhyReg.Register.T1);
                    }else{
                        reg = new MipsPhyReg(MipsPhyReg.Register.T2);
                    }
                    MipsLi li = new MipsLi(reg, operand);
                    irBlock.getMipsBlock().addInstruction(li);
                    return reg;
                }
            }else{
                return operand;
            }
        }
        if(this instanceof Instruction){
            MipsPhyReg reg;
            if(opIndex == 0){
                reg = new MipsPhyReg(MipsPhyReg.Register.T0);
            }else if(opIndex == 1){
                reg = new MipsPhyReg(MipsPhyReg.Register.T1);
            }else{
                reg = new MipsPhyReg(MipsPhyReg.Register.T2);
            }
            mipsModule.operandMap.put(this, reg);
            return reg;
        }else if(this instanceof ConstInt){
            int num = ((ConstInt) this).getNumber();
            if(canImm && inSignedShortRange(num)){
                return new MipsImm(num);
            }else{
                if(num == 0){
                    return new MipsPhyReg(MipsPhyReg.Register.ZERO);
                }else {
                    MipsPhyReg reg;
                    if(opIndex == 0){
                        reg = new MipsPhyReg(MipsPhyReg.Register.T0);
                    }else if(opIndex == 1){
                        reg = new MipsPhyReg(MipsPhyReg.Register.T1);
                    }else{
                        reg = new MipsPhyReg(MipsPhyReg.Register.T2);
                    }
                    MipsLi li = new MipsLi(reg, new MipsImm(num));
                    irBlock.getMipsBlock().addInstruction(li);
                    return reg;
                }
            }
        }
        return null;
    }

    public boolean inSignedShortRange(int num){
        return num >= -32768 && num <= 32767;
    }

    public boolean inUnsignedShortRange(int num){
        return num >= 0 && num <= 65535;
    }
}
