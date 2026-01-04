package optimize;

import midend.ir.IrModule;
import midend.ir.instruction.Br;
import midend.ir.instruction.Instruction;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;

import java.util.HashSet;
import java.util.LinkedList;

public class BuildCFG {
    public void process() {
        for (Function func  : IrModule.getInstance().getFunctions()) {
            runBBPredSucc(func);
        }
    }

    private void addEdge(BasicBlock pred, BasicBlock succ) {
        pred.addSuccessor(succ);
        succ.addPredecessor(pred);
    }

    private void clear(Function func) {
        for (BasicBlock block : func.getBlocks()) {
            block.getSuccessors().clear();
            block.getPredecessors().clear();
        }
    }

    public void runBBPredSucc(Function func) {
        visited.clear();
        clear(func);
        BasicBlock entry = func.getBlocks().getFirst();
        dfsBlock(entry);
        clearUselessBlock(func);
    }

    private final HashSet<BasicBlock> visited = new HashSet<>();

    private void dfsBlock(BasicBlock curBlock) {
        visited.add(curBlock);
        Instruction instr = curBlock.getInstList().getLast();
        if (instr instanceof Br) {
            Br br = (Br) instr;
            if (br.hasCond) {
                BasicBlock trueBlock = (BasicBlock) br.getUsedValue(1);
                addEdge(curBlock, trueBlock);
                if (!visited.contains(trueBlock)) {
                    dfsBlock(trueBlock);
                }
                BasicBlock falseBlock = (BasicBlock) br.getUsedValue(2);
                addEdge(curBlock, falseBlock);
                if (!visited.contains(falseBlock)) {
                    dfsBlock(falseBlock);
                }
            }
            else {
                BasicBlock nextBlock = (BasicBlock) br.getUsedValue(0);
                addEdge(curBlock, nextBlock);
                if (!visited.contains(nextBlock)) {
                    dfsBlock(nextBlock);
                }
            }
        }
    }

    /**
     * 删除不可达的基本块
     * 基于 DFS 遍历的结果，未被访问的块即为不可达块
     * @param func 当前函数
     */
    private void clearUselessBlock(Function func) {
        java.util.Iterator<BasicBlock> iterator = func.getBlocks().iterator();
        while (iterator.hasNext()) {
            BasicBlock block = iterator.next();
            if (!visited.contains(block)) {
                // 1. 维护后继块：移除前驱引用 + 维护 Phi
                for (BasicBlock successor : block.getSuccessors()) {
                    successor.getPredecessors().remove(block);

                    for (Instruction instr : successor.getInstList()) {
                        if (instr instanceof midend.ir.instruction.Phi) {
                            ((midend.ir.instruction.Phi) instr).removeIncoming(block);
                        } else {
                            break; // Phi 指令一定在基本块的开头
                        }
                    }
                }

                // 2. 维护前驱块：移除后继引用
                for (BasicBlock pred : block.getPredecessors()) {
                    pred.getSuccessors().remove(block);
                }

                // 3. 清除当前块中所有指令的操作数引用
                for (Instruction instr : block.getInstList()) {
                    instr.dropAllOperands();
                }
                block.getInstList().clear();

                // 4. 从函数中移除该块
                iterator.remove();
            }
        }
    }
}
