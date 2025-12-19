package backend.optimize;

import backend.MipsModule;
import backend.component.MipsBlock;
import backend.component.MipsFunction;
import backend.instruction.*;
import backend.operand.*;
import utils.Pair;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static backend.operand.MipsPhyReg.SP;


public class RegAllocator {
    private final MipsModule mipsModule = MipsModule.getInstance();
    private final int REG_NUM = MipsPhyReg.allocatableRegIds.size();
    private HashMap<MipsBlock , BlockActive> liveMap;
    private HashMap<MipsOperand, HashSet<MipsOperand>> adjList;

    private HashSet<Pair<MipsOperand, MipsOperand>> adjSet;
    private HashMap<MipsOperand, MipsOperand> alias;
    private HashMap<MipsOperand, HashSet<MipsMove>> moveList;
    private HashSet<MipsOperand> simplifyWorklist;
    private HashSet<MipsOperand> freezeWorklist;
    private HashSet<MipsOperand> spillWorklist;
    private HashSet<MipsOperand> spilledNodes;
    private HashSet<MipsOperand> coalescedNodes;
    private Stack<MipsOperand> selectStack;
    private HashSet<MipsMove> worklistMoves;
    private HashSet<MipsMove> activeMoves;
    private HashSet<MipsInstruction> coalescedMoves;
    private HashSet<MipsMove> constrainedMoves;
    private HashSet<MipsMove> frozenMoves;
    private HashMap<MipsOperand, Integer> degree;
    MipsVirReg vReg = null;
    MipsInstruction firstUseNode = null;
    MipsInstruction lastDefNode = null;
    MipsBlock firstUseBlock = null;
    MipsBlock lastDefBlock = null;
    /**
     * 统计每个虚拟寄存器在函数内出现的次数（use+def）
     */
    HashMap<MipsOperand, Integer> occCounts;

    private void init(MipsFunction function) {
        liveMap = BlockActive.activeAnalyze(function);
        adjList = new HashMap<>();
        adjSet = new HashSet<>();
        alias = new HashMap<>();
        moveList = new HashMap<>();
        simplifyWorklist = new HashSet<>();
        freezeWorklist = new HashSet<>();
        spillWorklist = new HashSet<>();
        spilledNodes = new HashSet<>();
        coalescedNodes = new HashSet<>();
        selectStack = new Stack<>();

        worklistMoves = new HashSet<>();
        activeMoves = new HashSet<>();

        coalescedMoves = new HashSet<>();
        frozenMoves = new HashSet<>();
        constrainedMoves = new HashSet<>();

        occCounts = new HashMap<>();
        degree = new HashMap<>();
        // 对于物理寄存器，需要度无限大
        for (int i = 0; i < 32; i++) {
            degree.put(MipsPhyReg.getReg(i), Integer.MAX_VALUE);
        }
    }

    /**
     * 添加冲突边(注意为无向图)
     * @param u
     * @param v
     */
    private void addEdge(MipsOperand u, MipsOperand v) {
        if (u.equals(v) || adjSet.contains(new Pair<>(u, v)) ) {
            return;
        }
        adjSet.add(new Pair<>(u, v));
        adjSet.add(new Pair<>(v, u));
        if(!u.isPreColored()){
            if (!adjList.containsKey(u)) {
                adjList.put(u, new HashSet<>());
            }
            adjList.get(u).add(v);
            degree.put(u, degree.getOrDefault(u, 0) + 1);
        }
        if(!v.isPreColored()){
            if (!adjList.containsKey(v)) {
                adjList.put(v, new HashSet<>());
            }
            adjList.get(v).add(u);
            degree.put(v, degree.getOrDefault(v, 0) + 1);
        }
    }

    public void build(MipsFunction function) {
        LinkedList<MipsBlock> blocks = function.getBlocks();
        for (int i = blocks.size() - 1; i >= 0; i--) {
            MipsBlock block = blocks.get(i);
            HashSet<MipsReg> live = new HashSet<>(liveMap.get(block).getLiveOut());
            LinkedList<MipsInstruction> instructions = block.getInstructions();
            for (int j = instructions.size() - 1; j >= 0; j--) {
                MipsInstruction instruction = instructions.get(j);
                ArrayList<MipsReg> useRegs = instruction.getUseRegs();
                ArrayList<MipsReg> defRegs = instruction.getDefRegs();

                if (instruction instanceof MipsMove) {
                    MipsMove mipsMove = (MipsMove) instruction;
                    MipsOperand dst = mipsMove.getDst();
                    MipsOperand src = mipsMove.getSrc();
                    if (src.needColor() && dst.needColor()) {
                        live.remove((MipsReg) src);

                        moveList.putIfAbsent(src, new HashSet<>());
                        moveList.get(src).add(mipsMove);

                        moveList.putIfAbsent(dst, new HashSet<>());
                        moveList.get(dst).add(mipsMove);
                        // 此时是有可能被合并的
                        worklistMoves.add(mipsMove);
                    }
                }
                for (MipsReg reg : defRegs) {
                    if (reg.needColor()) {
                        live.add(reg);
                    }
                }
                for (MipsReg a : defRegs) {
                    if (a.needColor()) {
                        for (MipsReg b : live) {
                            addEdge(b, a);
                        }
                    }
                }

                // 启发式算法的依据, 出现次数
                for (MipsReg mipsReg : defRegs) {
                    if (mipsReg.needColor()) {
                        occCounts.compute(mipsReg, (k, v) -> v == null ? 1 : v + 1);
                    }
                }
                for (MipsReg mipsReg : useRegs) {
                    if (mipsReg.needColor()) {
                        occCounts.compute(mipsReg, (k, v) -> v == null ? 1 : v + 1);
                    }
                }

                /**
                 * live = live − def
                 * live = live ∪ use
                 */
                defRegs.stream().filter(MipsReg::needColor).forEach(live::remove);
                useRegs.stream().filter(MipsReg::needColor).forEach(live::add);
            }
        }
    }

    private void makeWorklist(MipsFunction function) {
        for (MipsVirReg virReg : function.getUsedVirRegs()) {
            if (degree.getOrDefault(virReg, 0) >= REG_NUM) {
                spillWorklist.add(virReg);
            } else if (moveRelated(virReg)) {
                freezeWorklist.add(virReg);
            } else {
                // 加到 simplifyWorklist 中，就是可以进行化简的
                simplifyWorklist.add(virReg);
            }
        }
    }

    /**
     * 是move操作数
     * @param u
     * @return
     */
    private boolean moveRelated(MipsOperand u) {
        return !nodeMoves(u).isEmpty();
    }
    private Set<MipsMove> nodeMoves(MipsOperand u) {
        Set<MipsMove> result = new HashSet<>();
        Set<MipsMove> moves = moveList.getOrDefault(u, new HashSet<>());
        for (MipsMove move : moves) {
            if (activeMoves.contains(move) || worklistMoves.contains(move)) {
                result.add(move);
            }
        }
        return result;
    }

    private Set<MipsOperand> getAdjacent(MipsOperand u) {
        return adjList.getOrDefault(u, new HashSet<>()).stream()
                .filter(v -> !(selectStack.contains(v) || coalescedNodes.contains(v)))
                .collect(Collectors.toSet());
    }

    private void enableMoves(MipsOperand u) {
        for (MipsMove move : nodeMoves(u)) {
            if (activeMoves.contains(move)) {
                activeMoves.remove(move);
                worklistMoves.add(move);
            }
        }
        for (MipsOperand operand : getAdjacent(u)) {
            for (MipsMove move : nodeMoves(operand)) {
                if (activeMoves.contains(move)) {
                    activeMoves.remove(move);
                    worklistMoves.add(move);
                }
            }
        }
    }

    /**
     * 对一个结点进行减小度数
     * @param u
     */
    private void decreaseDegree(MipsOperand u) {
        int d = degree.get(u);
        degree.put(u, d - 1);

        if (d == REG_NUM) {
            enableMoves(u);
            spillWorklist.remove(u);
            if (moveRelated(u)) {
                freezeWorklist.add(u);
            } else {
                simplifyWorklist.add(u);
            }
        }
    }

    private void simplify() {
        MipsOperand n = simplifyWorklist.iterator().next();
        simplifyWorklist.remove(n);
        selectStack.push(n);
        getAdjacent(n).forEach(this::decreaseDegree);
    }

    /**
     * 合并的结点找被合并的另一个
     * @param u
     * @return
     */
    private MipsOperand getAlias(MipsOperand u) {
        while (coalescedNodes.contains(u)) {
            u = alias.get(u);
        }
        return u;
    }

    private void addWorklist(MipsOperand u) {
        if (!u.isPreColored() && !moveRelated(u) && degree.getOrDefault(u, 0) < REG_NUM) {
            freezeWorklist.remove(u);
            simplifyWorklist.add(u);
        }
    }

    /**
     * v和u是否可以合并
     * @param v
     * @param u
     * @return
     */
    private boolean adjMergeAble(MipsOperand v, MipsOperand u) {
        for (MipsOperand t : getAdjacent(v)) {
            boolean isOk = (degree.get(t) < REG_NUM) ||
                    t.isPreColored() ||
                    adjSet.contains(new Pair<>(t, u));

            if (!isOk) {
                return false;
            }
        }
        // 所有邻居都同意
        return true;
    }

    /**
     * 判断可合并性（保守合并）
     * @param u
     * @param v
     * @return
     */
    private boolean conservative(MipsOperand u, MipsOperand v) {
        Set<MipsOperand> uAdjacent = getAdjacent(u);
        Set<MipsOperand> vAdjacent = getAdjacent(v);
        uAdjacent.addAll(vAdjacent);
        long count = uAdjacent.stream().filter(n -> degree.get(n) >= REG_NUM).count();
        return count < REG_NUM;
    }

    /**
     * 合并u，v
     * @param u
     * @param v
     */
    private void combine(MipsOperand u, MipsOperand v) {
        // 这里做的是把他们从原有的 worklist 中移出
        if (freezeWorklist.contains(v)) {
            freezeWorklist.remove(v);
        }
        else {
            spillWorklist.remove(v);
        }
        coalescedNodes.add(v);
        alias.put(v, u);
        moveList.get(u).addAll(moveList.get(v));
        getAdjacent(v).forEach(t -> {
            addEdge(t, u);
            decreaseDegree(t);
        });
        if (degree.getOrDefault(u, 0) >= REG_NUM && freezeWorklist.contains(u)) {
            freezeWorklist.remove(u);
            spillWorklist.add(u);
        }
    }
    
    private void coalesce() {
        MipsMove objMove = worklistMoves.iterator().next();
        MipsOperand u = getAlias(objMove.getDst());
        MipsOperand v = getAlias(objMove.getSrc());
        if (v.isPreColored()) {
            MipsOperand tmp = u;
            u = v;
            v = tmp;
        }
        worklistMoves.remove(objMove);
        if (u.equals(v)) {
            coalescedMoves.add(objMove);
            addWorklist(u);
        }
        else if (v.isPreColored() || adjSet.contains(new Pair<>(u, v))) {
            constrainedMoves.add(objMove);
            addWorklist(u);
            addWorklist(v);
        }
        //
        else if ((u.isPreColored() && adjMergeAble(v, u)) ||
                (!u.isPreColored() && conservative(u, v))) {
            coalescedMoves.add(objMove);
            combine(u, v);
            addWorklist(u);
        } else {
            activeMoves.add(objMove);
        }
    }

    private void freezeMoves(MipsOperand u) {
        for (MipsMove objMove : nodeMoves(u)) {
            if (activeMoves.contains(objMove)) {
                activeMoves.remove(objMove);
            }
            else {
                worklistMoves.remove(objMove);
            }
            frozenMoves.add(objMove);
            MipsOperand v = getAlias(objMove.getDst()).equals(getAlias(u)) ? getAlias(objMove.getSrc()) : getAlias(objMove.getDst());
            if (!moveRelated(v) && degree.getOrDefault(v, 0) < REG_NUM) {
                freezeWorklist.remove(v);
                simplifyWorklist.add(v);
            }
        }
    }
    private void freeze() {
        MipsOperand u = freezeWorklist.iterator().next();
        freezeWorklist.remove(u);
        simplifyWorklist.add(u);
        freezeMoves(u);
    }

    /**
     * 溢出结点
     */
    private void selectSpill() {
        if (spillWorklist.isEmpty()) {
            return;
        }
        MipsOperand victim = null;
        double bestCost = Double.MAX_VALUE;
        for (MipsOperand u : spillWorklist) {
            int occ = occCounts.getOrDefault(u, 1);          // 使用频率
            int deg = Math.max(1, degree.getOrDefault(u, 1)); // 干涉度，避免除零
            double cost = (double) occ / deg;
            if (cost < bestCost) {
                bestCost = cost;
                victim = u;
            }
        }
        simplifyWorklist.add(victim);
        freezeMoves(victim);
        spillWorklist.remove(victim);
    }

    /**
     * 着色
     * @param func
     */
    private void assignColors(MipsFunction func) {
        HashMap<MipsOperand, MipsOperand> colored = new HashMap<>();

        while (!selectStack.isEmpty()) {
            MipsOperand n = selectStack.pop();
            HashSet<Integer> okColors = new HashSet<>(MipsPhyReg.allocatableRegIds);
            for (MipsOperand w : adjList.getOrDefault(n, new HashSet<>())) {
                MipsOperand a = getAlias(w);
                if (a.isAllocated() || a.isPreColored()) {
                    okColors.remove(((MipsPhyReg) a).getIndex());
                } else if (a instanceof MipsVirReg) {
                    if (colored.containsKey(a)) {
                        MipsOperand color = colored.get(a);
                        okColors.remove(((MipsPhyReg) color).getIndex());
                    }
                }
            }
            if (okColors.isEmpty()) {
                spilledNodes.add(n);
            } else {
                Integer color = okColors.iterator().next();
                colored.put(n, new MipsPhyReg(color, true));
            }
        }
        if (!spilledNodes.isEmpty()) {
            return;
        }
        for (MipsOperand coalescedNode : coalescedNodes) {
            MipsOperand alias = getAlias(coalescedNode);
            if (alias.isPreColored()) {
                colored.put(coalescedNode, alias);
            } else {
                colored.put(coalescedNode, colored.get(alias));
            }
        }

        // 这里完成了替换
        for (MipsBlock block : func.getBlocks()) {
            List<MipsInstruction> instructions = new CopyOnWriteArrayList<>(block.getInstructions());
            for (MipsInstruction instr : instructions) {
                ArrayList<MipsReg> defs = new ArrayList<>(instr.getDefRegs());
                ArrayList<MipsReg> uses = new ArrayList<>(instr.getUseRegs());
                for (MipsReg def : defs) {
                    if (colored.containsKey(def)) {
                        instr.replaceReg(def, colored.get(def));
                    }
                }
                for (MipsReg use : uses) {
                    if (colored.containsKey(use)) {
                        instr.replaceReg(use, colored.get(use));
                    }
                }
            }
        }
    }

    private void clearPhyRegState() {
        for (MipsFunction function : mipsModule.getFunctions()) {
            for (MipsBlock objBlock : function.getBlocks()) {
                for (MipsInstruction instr : objBlock.getInstructions()) {
                    for (MipsReg objReg : instr.getDefRegs()) {
                        if (objReg instanceof MipsPhyReg) {
                            ((MipsPhyReg) objReg).setAllocated(false);
                        }
                    }
                    for (MipsReg objReg : instr.getUseRegs()) {
                        if (objReg instanceof MipsPhyReg) {
                            ((MipsPhyReg) objReg).setAllocated(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * 与栈帧有关
     * @param func
     * @param instr
     */
    private void fixOffset(MipsFunction func, MipsInstruction instr) {
        int offset = func.getAllocaSize();
        MipsImm mipsOffset = new MipsImm(offset);
        if (instr instanceof MipsLw) {
            MipsLw mipsLw= (MipsLw) instr;
            mipsLw.setOffset(mipsOffset);
        } else if (instr instanceof MipsSw) {
            MipsSw mipsSw = (MipsSw) instr;
            mipsSw.setOffset(mipsOffset);
        }
    }

    /**
     * 用于完成将新的，处理溢出的临时变量插入到基本块中的功能
     * @param func 函数
     */
    private void checkPoint(MipsFunction func) {
        if (firstUseNode != null) {
            MipsLw lw = new MipsLw(vReg, new MipsImm(0), SP);
            firstUseBlock.insertBefore(firstUseNode, lw);
            fixOffset(func, lw);

            firstUseNode = null;
        }

        if (lastDefNode != null) {
            MipsSw store = new MipsSw(vReg, new MipsImm(0), SP);
            lastDefBlock.insertAfter(lastDefNode,store);
            fixOffset(func, store);
            lastDefNode = null;
        }

        vReg = null;
    }

    private void rewriteProgram(MipsFunction func) {
        for (MipsOperand n : spilledNodes) {
            for (MipsBlock block : func.getBlocks()) {

                vReg = null;
                firstUseNode = null;
                lastDefNode = null;
                // cntInstr 是 block 中已经处理的指令的个数
                int cntInstr = 0;
                List<MipsInstruction> instructions = new CopyOnWriteArrayList<>(block.getInstructions());
                for (MipsInstruction instr : instructions) {
                    HashSet<MipsReg> defs = new HashSet<>(instr.getDefRegs());
                    HashSet<MipsReg> uses = new HashSet<>(instr.getUseRegs());
                    for (MipsReg use : uses) {
                        if (use.equals(n)) {
                            if (vReg == null) {
                                vReg = new MipsVirReg();
                                func.addUsedVirReg(vReg);
                            }
                            instr.replaceReg(use, vReg);

                            if (firstUseNode == null && lastDefNode == null) {
                                firstUseNode = instr;
                                firstUseBlock = block;
                            }
                        }
                    }
                    for (MipsReg def : defs) {
                        if (def.equals(n)) {
                            if (vReg == null) {
                                vReg = new MipsVirReg();
                                func.addUsedVirReg(vReg);
                            }
                            instr.replaceReg(def, vReg);
                            lastDefNode = instr;
                            lastDefBlock = block;
                        }
                    }
                    if (cntInstr > 30) {
                        checkPoint(func);
                        cntInstr = 0;
                    }
                    cntInstr++;
                }
                checkPoint(func);
            }
            func.addAllocaSize(4);
        }
    }
    
    public void process() {
        for (MipsFunction function : mipsModule.getFunctions()) {
            boolean finished = false;

            while (!finished) {
                init(function);
                build(function);
                makeWorklist(function);
                while (!(simplifyWorklist.isEmpty() && worklistMoves.isEmpty() &&
                        freezeWorklist.isEmpty() && spillWorklist.isEmpty())){
                    if (!simplifyWorklist.isEmpty()) {
                        simplify();
                    }
                    if (!worklistMoves.isEmpty()) {
                        coalesce();
                    }
                    if (!freezeWorklist.isEmpty()) {
                        freeze();
                    }
                    if (!spillWorklist.isEmpty()) {
                        selectSpill();
                    }
                }

                assignColors(function);

                if (spilledNodes.isEmpty()) {
                    finished = true;
                } else {
                    rewriteProgram(function);
                }
            }
        }
        clearPhyRegState();
        for (MipsFunction function : mipsModule.getFunctions()) {
            function.fixStack();
        }
    }
}
