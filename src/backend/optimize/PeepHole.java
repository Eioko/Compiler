package backend.optimize;

import backend.MipsModule;
import backend.component.MipsBlock;
import backend.component.MipsFunction;
import backend.instruction.*;
import backend.operand.MipsImm;
import backend.operand.MipsLabel;
import backend.operand.MipsOperand;
import backend.operand.MipsPhyReg;
import backend.operand.MipsReg;
import utils.Pair;

import java.util.*;

public class PeepHole {

    public void process() {
        boolean finished = false;
        while (!finished) {
            finished = peephole();
            finished &= dataFlowPeephole();
        }
    }
    private boolean peephole() {
        boolean finished = true;

        for (MipsFunction function : MipsModule.getInstance().getFunctions()) {
            ListIterator<MipsBlock> blockIter = function.getBlocks().listIterator(); // Provide block iterator
            while (blockIter.hasNext()) {
                MipsBlock mipsBlock = blockIter.next();
                LinkedList<MipsInstruction> insList = mipsBlock.getInstructions();
                ListIterator<MipsInstruction> iter = insList.listIterator();
                while (iter.hasNext()) {
                    MipsInstruction curInstr = iter.next();
                    if (!addSubSrc2Zero(curInstr, iter)) {
                        finished = false;
                        continue;
                    }
                    if (!movSameDstSrc(curInstr, iter)) {
                        finished = false;
                        continue;
                    }
                    if (!movOverlap(curInstr, iter)) {
                        finished = false;
                        continue;
                    }
                    if (!branchUselessDelete(curInstr, iter, blockIter)) {
                        finished = false;
                        // continue;
                    }
                }
            }
        }

        return finished;
    }

    /**
     * 使用 ListIterator 在遍历中安全删除或替换当前指令。
     * 返回 true = 未修改，false = 有修改（表示还需继续迭代外层）
     */
    private boolean addSubSrc2Zero(MipsInstruction curInstr, ListIterator<MipsInstruction> iter) {
        boolean finished = true;

        if (curInstr instanceof MipsBinary) {
            MipsBinary instr = (MipsBinary) curInstr;
            MipsBinary.BinaryOp op = instr.getOp();
            if (op.equals(MipsBinary.BinaryOp.ADDU) || op.equals(MipsBinary.BinaryOp.SUBU)) {
                boolean isSrc2Zero = (instr.getSrc2() instanceof MipsImm)
                        && (((MipsImm) instr.getSrc2()).getNumber() == 0);

                if (isSrc2Zero) {
                    boolean isDstSrc1Same = instr.getDst().equals(instr.getSrc1());
                    if (isDstSrc1Same) {
                        iter.remove(); // 安全删除当前元素
                    } else {
                        MipsMove mipsMove = new MipsMove(instr.getDst(), instr.getSrc1());
                        iter.set(mipsMove); // 安全替换当前元素
                    }
                    finished = false;
                }
            }
        }
        return finished;
    }
    /**
     * 处理的是 mov 指令源和目的寄存器相同的情况
     * mov r0, r0 => null
     */
    private boolean movSameDstSrc(MipsInstruction curInstr, ListIterator<MipsInstruction> iter) {
        boolean finished = true;

        if (curInstr instanceof MipsMove) {
            MipsMove mipsMove = (MipsMove) curInstr;
            // 相等且没有移位
            if (mipsMove.getDst().equals(mipsMove.getSrc())) {
                iter.remove();
                finished = false;
            }
        }

        return finished;
    }

    /**
     * 处理的是两个 mov 时的赋值覆盖问题
     * mov r0, r1 (cur, remove)
     * mov r0, r2 (next)
     *
     * @param curInstr 当前指令 (刚刚由 iter.next() 返回)
     * @param iter     指令列表的迭代器 (当前游标位于 curInstr 之后)
     * @return finished 是否完成 (如果没有发生删除则为 true，发生删除则为 false)
     */
    private boolean movOverlap(MipsInstruction curInstr, ListIterator<MipsInstruction> iter) {
        boolean finished = true;

        if (curInstr instanceof MipsMove) {
            if (iter.hasNext()) {
                MipsInstruction nextInstr = iter.next(); // Move to the next instruction

                if (nextInstr instanceof MipsMove) {
                    boolean isSameDst = ((MipsMove) nextInstr).getDst().equals(((MipsMove) curInstr).getDst());
                    boolean nextInstrDifferent = !((MipsMove) nextInstr).getSrc().equals(((MipsMove) nextInstr).getDst());

                    if (isSameDst && nextInstrDifferent) {
                        iter.previous();
                        iter.previous(); // Move back to the current instruction
                        iter.remove();   // Remove the current instruction
                        finished = false;
                    } else {
                        iter.previous(); // Move back to the current instruction to maintain iterator state
                    }
                } else {
                    iter.previous(); // Move back to the current instruction to maintain iterator state
                }
            }
        }

        return finished;
    }

    private boolean branchUselessDelete(MipsInstruction curInstr, ListIterator<MipsInstruction> iter,
                                        ListIterator<MipsBlock> blockIter) {
        boolean finished = true;

        if (curInstr instanceof MipsJ) {
            MipsJ mipsJ = (MipsJ) curInstr;

            MipsBlock nextBlock = null;
            if (blockIter.hasNext()) {
                nextBlock = blockIter.next();
                blockIter.previous(); // Restore iterator position
            }
            if(nextBlock == null){
                return true;
            }
            boolean isNear = mipsJ.getTarget().toString().equals(nextBlock.getName());
            if (isNear) {
                    iter.remove(); // Remove the current instruction
                finished = false;
            }
        }
        return finished;
    }
    private Pair<HashMap<MipsOperand, MipsInstruction>,
                HashMap<MipsInstruction, MipsInstruction>> getLiveRangeInBlock(MipsBlock mipsBlock) {
        HashMap<MipsOperand, MipsInstruction> lastWriter = new HashMap<>();
        HashMap<MipsInstruction, MipsInstruction> writerToReader = new HashMap<>();

        for (MipsInstruction instr : mipsBlock.getInstructions()) {
            ArrayList<MipsReg> writeRegs = instr.getWriteRegs();
            ArrayList<MipsReg> readRegs = instr.getReadRegs();

            for (MipsReg readReg : readRegs) {
                if (lastWriter.containsKey(readReg)) {
                    writerToReader.put(lastWriter.get(readReg), instr);
                }
            }

            for (MipsReg writeReg : writeRegs) {
                lastWriter.put(writeReg, instr);
            }

            boolean hasSideEffect = instr instanceof MipsJ ||
                    instr instanceof MipsJr ||
                    instr instanceof MipsSw ||
                    instr instanceof MipsRet ||
                    instr instanceof MipsJal ||
                    instr instanceof MipsSyscall;
            writerToReader.put(instr, hasSideEffect ? instr : null);
        }

        return new Pair<>(lastWriter, writerToReader);
    }
    private MipsInstruction lastReader = null;

    private boolean notWriteSp = true;

    private boolean dataFlowPeephole() {
        boolean finished = true;

        for (MipsFunction function : MipsModule.getInstance().getFunctions()) {
            HashMap<MipsBlock, BlockActive> liveInfoMap = BlockActive.activeAnalyze(function);

            for (MipsBlock mipsBlock : function.getBlocks()) {
                // liveOut 中存着出口活跃的寄存器
                HashSet<MipsReg> liveOut = liveInfoMap.get(mipsBlock).getLiveOut();
                Pair<HashMap<MipsOperand, MipsInstruction>, HashMap<MipsInstruction, MipsInstruction>> retPair =
                        getLiveRangeInBlock(mipsBlock);
                // lastWriter 可以根据寄存器查找上一个写者（最后一次写这个寄存器的指令）
                HashMap<MipsOperand, MipsInstruction> lastWriter = retPair.getFirst();
                // 将当前指令作为写指令，writerToReader 可以根据当前指令查询最后一次的读指令
                HashMap<MipsInstruction, MipsInstruction> writerToReader = retPair.getSecond();

                ListIterator<MipsInstruction> instrIterator = mipsBlock.getInstructions().listIterator();
                while (instrIterator.hasNext()) {
                    MipsInstruction curInstr = instrIterator.next();

                    // 这个是判断当前指令写的寄存器是不是最后一次被写
                    boolean isLastWriter = curInstr.getDefRegs().stream().allMatch(def -> lastWriter.get(def).equals(curInstr));
                    // 这个用来指示当前指令写的寄存器是否在 liveOut 中
                    boolean writeRegInLiveOut = curInstr.getDefRegs().stream().anyMatch(liveOut::contains);

                    // 这里是数据流窥孔的精髓，我们考虑删除的指令必须被限定在基本块内，然后再被限定在窥孔内
                    // 其他的指令我们并不考虑
                    if (!(writeRegInLiveOut && isLastWriter) && !(curInstr instanceof MipsBeqz)) {
                        // 进行一波对于指令的提前分析
                        lastReader = writerToReader.get(curInstr);
                        notWriteSp = curInstr.getDefRegs().stream().noneMatch(def -> def.equals(MipsPhyReg.SP));

                        // 正式开始分析
                        if (deleteUselessLastWriter(instrIterator, curInstr)) {
                            finished = false;
                            continue;
                        }
                        if (movDeleteReplace(instrIterator, curInstr)) {
                            finished = false;
                        }
                    }
                }
            }
        }

        return finished;
    }

    private boolean deleteUselessLastWriter(ListIterator<MipsInstruction> instrIterator, MipsInstruction curInstr) {
        boolean changed = false;
        if (lastReader == null && notWriteSp) {
            instrIterator.remove();
            changed = true;
        }
        return changed;
    }

    /**
     * mov a, b
     * some instruction(use a)
     * =>
     * some instruction(use b)
     */
    private boolean movDeleteReplace(ListIterator<MipsInstruction> instrIterator, MipsInstruction curInstr) {
        if (curInstr instanceof MipsMove) {
            MipsMove mipsMove = (MipsMove) curInstr;
            // 我们不替换立即数和标签，是因为替换的风险很大
            MipsOperand mipsSrc = mipsMove.getSrc();
            if (mipsSrc instanceof MipsImm || mipsSrc instanceof MipsLabel) {
                return false;
            }

            // 检验是否是可以替换的
            if (instrIterator.hasNext()) {
                MipsInstruction nextInstr = instrIterator.next(); // 获取下一条指令
                boolean nextHasSideEffect =
                        nextInstr instanceof MipsJ ||
                                nextInstr instanceof MipsJal ||
                                nextInstr instanceof MipsJr  ||
                                nextInstr instanceof MipsRet ||
                                nextInstr instanceof MipsSyscall;

                if (!Objects.equals(lastReader, nextInstr) || nextHasSideEffect) {
                    instrIterator.previous(); // 恢复迭代器位置
                    return false;
                }
                // 替换指令中的读寄存器
                MipsOperand mipsDst = mipsMove.getDst();
                nextInstr.replaceUseReg(mipsDst, mipsSrc);

                // 删去当前指令
                instrIterator.previous();
                instrIterator.previous(); // 恢复迭代器位置
                instrIterator.remove();   // 删除当前指令
                return true;
            }
        }
        return false;
    }
}
