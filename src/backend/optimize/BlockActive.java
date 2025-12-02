package backend.optimize;

import backend.component.MipsBlock;
import backend.component.MipsFunction;
import backend.instruction.MipsInstruction;
import backend.operand.MipsReg;

import java.util.HashMap;
import java.util.HashSet;

public class BlockActive {
    private HashSet<MipsReg> liveUse = new HashSet<>();
    private HashSet<MipsReg> liveDef = new HashSet<>();
    private HashSet<MipsReg> liveIn = new HashSet<>();
    private HashSet<MipsReg> liveOut = new HashSet<>();

    public static HashMap<MipsBlock, BlockActive> activeAnalyze(MipsFunction function){
        HashMap<MipsBlock, BlockActive> analyzeMap = new HashMap<>();

        //初始化
        for(MipsBlock block : function.getBlocks()){
            BlockActive analyzeAnalze = new BlockActive();
            analyzeMap.put(block, analyzeAnalze);
            for(MipsInstruction instruction : block.getInstructions()){
                //计算use和def
                for(MipsReg reg : instruction.getUseRegs()){
                    if(!analyzeAnalze.liveDef.contains(reg) && reg.needColor()){
                        analyzeAnalze.liveUse.add(reg);
                    }
                }
                for(MipsReg reg : instruction.getDefRegs()){
                    if(reg.needColor()){
                        analyzeAnalze.liveDef.add(reg);
                    }
                }
            }
            analyzeAnalze.liveIn.addAll(analyzeAnalze.liveUse);
        }

        boolean changed = true;

        while(changed){
            changed = false;
            for(MipsBlock block : function.getBlocks()){
                BlockActive analyze = analyzeMap.get(block);
                HashSet<MipsReg> newLiveOut = new HashSet<>();
                for(MipsBlock succ : block.getSuccessors()){
                    BlockActive succAnalyze = analyzeMap.get(succ);
                    analyze.liveOut.addAll(succAnalyze.liveIn);
                }
                //计算in
                if (!newLiveOut.equals(analyze.liveOut)) {
                    changed = true;
                    analyze.liveOut = newLiveOut;
                    analyze.liveIn = new HashSet<>(analyze.liveUse);
                    for(MipsReg reg : analyze.liveOut){
                        if(!analyze.liveDef.contains(reg) ){
                            analyze.liveIn.add(reg);
                        }
                    }
                }
            }
        }
        return analyzeMap;
    }
    public HashSet<MipsReg> getLiveOut() {
        return liveOut;
    }
}
