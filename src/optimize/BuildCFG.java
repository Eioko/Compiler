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
     * 注意这里的是并不严谨的，只是删除了前驱为 0 的节点，并且更新了其后继的前驱节点
     * 但是如果更新后的后继也成了前驱为 0 的节点，那么就无能为力了，可以考虑用一个不动点去优化
     * @param func 当前函数
     */
    private void clearUselessBlock(Function func) {
        BasicBlock entry = func.getBlocks().getFirst();

        LinkedList<BasicBlock> blocks = new LinkedList<>(func.getBlocks());
        for ( BasicBlock block : blocks) {
            if (block.getPredecessors().isEmpty() && block != entry) {
                for (BasicBlock successor : block.getSuccessors()) {
                    successor.getPredecessors().remove(block);
                }
                LinkedList<Instruction> instructions = new LinkedList<>(block.getInstList());
                for (Instruction instr : instructions) {
                    instr.dropAllOperands();
                    instr.eraseFromParent();
                }
                func.removeBlock(block);
            }
        }
    }
}
