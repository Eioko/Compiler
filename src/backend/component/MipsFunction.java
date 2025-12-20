package backend.component;

import backend.MipsModule;
import backend.instruction.MipsBeqz;
import backend.instruction.MipsInstruction;
import backend.instruction.MipsJ;
import backend.operand.*;
import utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static utils.Configs.regAlloca;

public class MipsFunction {
    private String name;

    public LinkedList<MipsBlock> blocks = new LinkedList<>();
    private final HashSet<MipsVirReg> usedVirRegs = new HashSet<>();
    public LinkedList<MipsBlock> recordBlocks = new LinkedList<>();

    public MipsFunction(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void addBlock(MipsBlock block) {
        blocks.add(block);
    }
    public LinkedList<MipsBlock> getBlocks() {
        return blocks;
    }

    /**
     * 优化用，用callee保存现场，因为要重新计算栈大小
     */
    private int totalStackSize = 0;
    private int allocaSize = 0;
    public void addAllocaSize(int size) {
        allocaSize += size;
    }
    public int getAllocaSize() {
        return allocaSize;
    }
    public int getTotalStackSize() {
        return totalStackSize;
    }
    // 用于调整内存空间的参数偏移
    private final HashSet<MipsImm> argOffsets = new HashSet<>();
    private final HashSet<Integer> calleeSavedRegIndexes = new HashSet<>();
    public void addArgOffset(MipsImm mipsOffset) {
        argOffsets.add(mipsOffset);
    }
    public void addUsedVirReg(MipsVirReg mipsVirReg) {
        usedVirRegs.add(mipsVirReg);
    }
    public HashSet<MipsVirReg> getUsedVirRegs() {
        return usedVirRegs;
    }
    public HashSet<Integer> getCalleeSavedRegIndexes() {
        return calleeSavedRegIndexes;
    }

    public void fixStack() {
        for (MipsBlock mipsBlock : blocks) {
            for (MipsInstruction instr : mipsBlock.getInstructions()) {
                for (MipsReg defReg : instr.getDefRegs()) {
                    if(!(defReg instanceof MipsPhyReg)) continue;
                    int index = ((MipsPhyReg) defReg).getIndex();
                    if (MipsPhyReg.calleeSavedRegIndex.contains(index)) {
                        calleeSavedRegIndexes.add(index);
                    }
                }
            }
        }
        int stackRegSize = 4 * calleeSavedRegIndexes.size();

        totalStackSize = stackRegSize + allocaSize;

        for (MipsImm argOffset : argOffsets) {
            int newOffset = argOffset.getNumber() + totalStackSize;
            argOffset.setNum(newOffset);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":").append("\n");
        if(this == MipsModule.getInstance().mainFunction){
            sb.append("\tmove $fp, $sp\n");
        }else{
            if(regAlloca){
                int stackOffset = -4;
                for (Integer savedRegIndex : calleeSavedRegIndexes)
                {
                    sb.append("\t").append("sw ").append(MipsPhyReg.getReg(savedRegIndex)).append(",\t")
                            .append(stackOffset).append("($sp)\n");
                    stackOffset -= 4;
                }
            }
        }
        if(regAlloca){
            if (totalStackSize != 0) {
                sb.append("\tadd $sp,\t$sp,\t").append(-totalStackSize).append("\n");
            }
        }

        for(MipsBlock block : blocks){
            sb.append(block.toString()).append("\n");
        }
        return sb.toString();
    }
    private final HashSet<MipsBlock> hasSerial = new HashSet<>();
    private void handleTrueCopys(MipsBlock curBlock, MipsBlock succBlock, ArrayList<MipsInstruction> phiCopys) {
        if (! curBlock.getInstructions().isEmpty() && curBlock.getInstructions().getLast() instanceof MipsJ) {
            for(MipsInstruction phiCopy : phiCopys) {
                // 在跳转指令前插入 phiMove 指令
                curBlock.getInstructions().add(curBlock.getInstructions().size() - 1, phiCopy);
            }
        } else {
            for (MipsInstruction phiCopy : phiCopys) {
                curBlock.addInstruction(phiCopy);
            }
        }
    }

    private void handleFalseCopys(MipsBlock curBlock, MipsBlock succBlock, ArrayList<MipsInstruction> phiCopys) {
        if (!phiCopys.isEmpty()) {
            // 如果后继块前只有一个前驱块（当前块），那么就可以直接插入到后继块的最开始
            if (succBlock.getPreds().size() == 1) {
                succBlock.insertPhiCopysHead(phiCopys);
            }
            // 如果后继块前有多个前驱块（无法确定从哪个块来），那么就应该新形成一个块
            else {
                // 新做出一个中转块
                MipsBlock transferBlock = new MipsBlock(curBlock.getName() + "_false_trampoline");

                // 把 phiMov 指令放到这里
                transferBlock.insertPhiCopysHead(phiCopys);

                // 做出一个中转块跳转到的指令
                MipsJ mipsTransferJump = new MipsJ(new MipsLabel(succBlock.getName()));
                transferBlock.addInstruction(mipsTransferJump);

                // transfer 登记前驱后继
                transferBlock.setTrueSucc(succBlock);
                transferBlock.addPred(curBlock);

                // succ 登记前驱后继
                succBlock.removePred(curBlock);
                succBlock.addPred(transferBlock);

                // cur 登记前驱后继
                curBlock.setFalseSucc(transferBlock);

                // 因为 Br 生成的是 Beqz + J，所以 Beqz 不是最后一条指令
                boolean found = false;
                for (int i = curBlock.getInstructions().size() - 1; i >= 0; i--) {
                    MipsInstruction instr = curBlock.getInstructions().get(i);
                    if (instr instanceof MipsBeqz) {
                        MipsBeqz beqz = (MipsBeqz) instr;
                        // 检查这个 Beqz 是否是跳往原来的 succBlock
                        if (beqz.getTarget().getName().equals(succBlock.getName())) {
                            beqz.setTarget(new MipsLabel(transferBlock.getName()));
                            found = true;
                            break;
                        }
                    }
                }
                // 如果没找到 Beqz，可能是其他类型的分支或者逻辑有误，这里可以加个断言或日志
            }
        }
    }

    public void blockSerial(MipsBlock curBlock, HashMap<Pair<MipsBlock, MipsBlock>, ArrayList<MipsInstruction>> phiWaitLists) {
        // 登记
        hasSerial.add(curBlock);
        // 插入当前块,就是序列化当前块
        blocks.addLast(curBlock);

        // 没有后继
        if (curBlock.getTrueSucc() == null && curBlock.getFalseSucc() == null) {
            return;
        }

        // 如果没有错误后继块,说明只有一个后继块，那么就应该考虑与当前块合并
        if (curBlock.getFalseSucc() == null) {
            MipsBlock succBlock = curBlock.getTrueSucc();
            // 这个前驱后继关系用于查询有多少个 phiMove 要插入，一个后继块，直接将这些指令插入到跳转之前即可
            Pair<MipsBlock, MipsBlock> trueLookup = new Pair<>(curBlock, succBlock);
            curBlock.insertPhiMovesTail(phiWaitLists.getOrDefault(trueLookup, new ArrayList<>()));

            // 合并的条件是后继块还未被序列化，此时只需要将当前块最后一条跳转指令移除掉就好了
            if (!hasSerial.contains(succBlock)) {
                curBlock.removeTailInstr();
                blockSerial(succBlock, phiWaitLists);
            }
            // 但是不一定能够被合并成功，因为后继块已经被先序列化了，那么就啥都不需要干了
        }else {
            MipsBlock trueSuccBlock = curBlock.getTrueSucc();
            MipsBlock falseSuccBlock = curBlock.getFalseSucc();

            Pair<MipsBlock, MipsBlock> trueLookup = new Pair<>(curBlock, trueSuccBlock);
            Pair<MipsBlock, MipsBlock> falseLookup = new Pair<>(curBlock, falseSuccBlock);

            handleTrueCopys(curBlock, trueSuccBlock, phiWaitLists.getOrDefault(trueLookup, new ArrayList<>()));
            handleFalseCopys(curBlock, falseSuccBlock, phiWaitLists.getOrDefault(falseLookup, new ArrayList<>()));

            if (!hasSerial.contains(curBlock.getFalseSucc())) {
                blockSerial(curBlock.getFalseSucc(), phiWaitLists);
            }
            if (!hasSerial.contains(curBlock.getTrueSucc())) {
                blockSerial(curBlock.getTrueSucc(), phiWaitLists);
            }
        }
    }
}
