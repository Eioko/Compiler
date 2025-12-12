package midend.ir.value;

import backend.MipsModule;
import backend.component.MipsBlock;
import backend.component.MipsFunction;
import backend.instruction.*;
import backend.operand.*;
import midend.ir.constant.ConstInt;
import midend.ir.instruction.Instruction;
import midend.ir.type.ValueType;
import utils.Pair;

import java.util.ArrayList;

import static backend.operand.MipsPhyReg.SP;

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
    public ArrayList<User> getUsers() {
        return users;
    }
    public MipsOperand toSimpleReg(boolean canImm, Function irFunction, BasicBlock irBlock, int opIndex) {
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
        MipsPhyReg reg;
        if(opIndex == 0){
            reg = new MipsPhyReg(MipsPhyReg.Register.T0);
        }else if(opIndex == 1){
            reg = new MipsPhyReg(MipsPhyReg.Register.T1);
        }else{
            reg = new MipsPhyReg(MipsPhyReg.Register.T2);
        }
        return reg;
    }
    public MipsOperand genTmpReg(Function irFunction) {
        MipsFunction mipsFunction = irFunction.getMipsFunction();
        MipsVirReg tmpReg = new MipsVirReg();
        mipsFunction.addUsedVirReg(tmpReg);
        return tmpReg;
    }
    public MipsOperand toMipsOperand(boolean canImm, Function irFunction, BasicBlock irBlock) {
        if(mipsModule.getOperandMapping(this) != null) {
            MipsOperand operand = mipsModule.getOperandMapping(this);
            if(!canImm && operand instanceof MipsImm) {
                if((((MipsImm) operand).getNumber()) == 0){
                    return new MipsPhyReg(MipsPhyReg.Register.ZERO);
                }else{
                    MipsOperand tmp = genTmpReg(irFunction);
                    MipsLi li = new MipsLi((MipsPhyReg) tmp, operand);
                    irBlock.getMipsBlock().addInstruction(li);
                    return tmp;
                }
            }else{
                return operand;
            }
        }
        if (this instanceof Argument && irFunction.getArguments().contains(this)) {
            return parseArgOperand((Argument) this, irFunction);
        }
        else if (this instanceof GlobalVariable) {
            return parseGlobalOperand((GlobalVariable) this, irFunction, irBlock);
        } else if (this instanceof ConstInt) {
            return parseConstIntOperand(((ConstInt) this).getNumber(), canImm, irFunction, irBlock);
        } else {
            return genDstOperand(this, irFunction);
        }
    }

    public MipsOperand parseConstIntOperand(int imm, boolean canImm, Function irFunction, BasicBlock irBlock) {
        MipsBlock mipsBlock = irBlock.getMipsBlock();
        MipsFunction mipsFunction = irFunction.getMipsFunction();
        MipsImm mipsImm = new MipsImm(imm);
        if (inSignedShortRange(imm) && canImm) {
            return mipsImm;
        } else {
            if (imm == 0) {
                return new MipsPhyReg(MipsPhyReg.Register.ZERO);
            } else {
                MipsVirReg dst = new MipsVirReg();
                mipsFunction.addUsedVirReg(dst);
                mipsBlock.addInstruction( new MipsLi(dst, mipsImm));
                return dst;
            }
        }
    }


    public MipsOperand parseArgOperand(Argument irArgument, Function irFunction) {
        MipsFunction mipsFunction = irFunction.getMipsFunction();
        int rank = irArgument.getArgId();
        if(irFunction.getBlocks().isEmpty()){
            throw new RuntimeException("Function has no basic block when parsing argument operand.");
        }
        MipsBlock firstBlock = irFunction.getBlocks().get(0).getMipsBlock();
        MipsVirReg dstVirReg = new MipsVirReg();
        mipsModule.operandMap.put(irArgument, dstVirReg);
        mipsFunction.addUsedVirReg(dstVirReg);

        if (rank < 4) {
            MipsPhyReg srcReg = switch (rank) {
                case 0 -> new MipsPhyReg(MipsPhyReg.Register.A0);
                case 1 -> new MipsPhyReg(MipsPhyReg.Register.A1);
                case 2 -> new MipsPhyReg(MipsPhyReg.Register.A2);
                default -> new MipsPhyReg(MipsPhyReg.Register.A3);
            };
            MipsMove mipsMove = new MipsMove(dstVirReg, srcReg);
            firstBlock.addInstruction(mipsMove);
        } else {
            int stackPos = rank - 4;
            MipsImm mipsOffset = new MipsImm(stackPos * 4);
            mipsFunction.addArgOffset(mipsOffset);
            MipsLw mipsLw = new MipsLw(dstVirReg, mipsOffset, SP);
            firstBlock.addInstrHead(mipsLw);
        }
        return dstVirReg;
    }

    public MipsOperand parseGlobalOperand(GlobalVariable irGlobal, Function irFunction, BasicBlock irBlock) {
        MipsBlock mipsBlock = irBlock.getMipsBlock();

        MipsOperand dst = genTmpReg(irFunction);
        MipsLa mipsLa = new MipsLa(dst, new MipsLabel(irGlobal.getName()));
        mipsBlock.addInstruction(mipsLa);
        return dst;
    }

    public MipsOperand genDstOperand(Value irValue, Function irFunction) {
        MipsFunction mipsFunction = irFunction.getMipsFunction();
        assert irValue instanceof Instruction : "Wrong Operand of instruction!";
        MipsVirReg dstReg = new MipsVirReg();
        mipsFunction.addUsedVirReg(dstReg);
        mipsModule.operandMap.put(irValue, dstReg);
        return dstReg;
    }

    public boolean inSignedShortRange(int num){
        return num >= -32768 && num <= 32767;
    }

    //--------------------------------------Mem2Reg--------------------------------
    public void dropUser(User user) {
        if (!users.contains(user)) {
            throw new AssertionError("User not found in users list.");
        }
        users.remove(user);
    }
    public void replaceAllUsesWith(Value replacement) {
        ArrayList<User> usersClone = new ArrayList<>(users);
        for (User user : usersClone) {
            for (int i = 0; i < user.getNumOps(); i++) {
                if (user.getUsedValue(i) == this) {
                    user.setUsedValue(i, replacement);
                }
            }
        }
        users.clear();
    }

}
