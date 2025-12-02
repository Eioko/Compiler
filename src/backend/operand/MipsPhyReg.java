package backend.operand;

import java.util.ArrayList;

public class MipsPhyReg extends MipsReg {
    public enum Register {
        ZERO ("zero"),
        AT ("at"),
        V0 ("v0"),
        V1 ("v1"),
        A0 ("a0"),
        A1 ("a1"),
        A2 ("a2"),
        A3 ("a3"),
        T0 ("t0"),
        T1 ("t1"),
        T2 ("t2"),
        T3 ("t3"),
        T4 ("t4"),
        T5 ("t5"),
        T6 ("t6"),
        T7 ("t7"),
        S0 ("s0"),
        S1 ("s1"),
        S2 ("s2"),
        S3 ("s3"),
        S4 ("s4"),
        S5 ("s5"),
        S6 ("s6"),
        S7 ("s7"),
        T8 ("t8"),
        T9 ("t9"),
        K0 ("k0"),
        K1 ("k1"),
        GP ("gp"),
        SP ("sp"),
        FP ("fp"),
        RA ("ra");

        private final String name;
        Register (String name) {
            this.name = name;
        }
    }

    public static MipsPhyReg SP = new MipsPhyReg(Register.SP);
    public static MipsPhyReg RA = new MipsPhyReg(Register.RA);
    public static MipsPhyReg V0 = new MipsPhyReg(Register.V0);
    public static MipsPhyReg A0 = new MipsPhyReg(Register.A0);
    public static MipsPhyReg FP = new MipsPhyReg(Register.FP);

    public static final ArrayList <MipsPhyReg> allocatableRegs = new ArrayList<>();
    public static final ArrayList<Integer> allocatableRegIds = new ArrayList<>();
    static {
        allocatableRegs.add(new MipsPhyReg(Register.T0));
        allocatableRegs.add(new MipsPhyReg(Register.T1));
        allocatableRegs.add(new MipsPhyReg(Register.T2));
        allocatableRegs.add(new MipsPhyReg(Register.T3));
        allocatableRegs.add(new MipsPhyReg(Register.T4));
        allocatableRegs.add(new MipsPhyReg(Register.T5));
        allocatableRegs.add(new MipsPhyReg(Register.T6));
        allocatableRegs.add(new MipsPhyReg(Register.T7));
        allocatableRegs.add(new MipsPhyReg(Register.S0));
        allocatableRegs.add(new MipsPhyReg(Register.S1));
        allocatableRegs.add(new MipsPhyReg(Register.S2));
        allocatableRegs.add(new MipsPhyReg(Register.S3));
        allocatableRegs.add(new MipsPhyReg(Register.S4));
        allocatableRegs.add(new MipsPhyReg(Register.S5));
        allocatableRegs.add(new MipsPhyReg(Register.S6));
        allocatableRegs.add(new MipsPhyReg(Register.S7));
        allocatableRegs.add(new MipsPhyReg(Register.T8));
        allocatableRegs.add(new MipsPhyReg(Register.T9));
        allocatableRegs.add(new MipsPhyReg(Register.K0));
        allocatableRegs.add(new MipsPhyReg(Register.K1));

        for (MipsPhyReg reg : allocatableRegs) {
            allocatableRegIds.add(reg.reg.ordinal());
        }
    }

    private final Register reg;
    private boolean isAllocated;
    @Override
    public boolean isPreColored() {
        return !isAllocated;
    }


    public int getIndex() {
        return reg.ordinal();
    }

    public boolean isAllocated() {
        return isAllocated;
    }

    @Override
    public boolean needColor() {
        return !isAllocated;
    }

    public void setAllocated(boolean allocated) {
        isAllocated = allocated;
    }
    public static MipsPhyReg getReg(int idx) {
        return new MipsPhyReg(Register.values()[idx]);
    }
    public MipsPhyReg(Register reg) {
        this.reg = reg;
    }

    public MipsPhyReg(int idx, boolean isAllocated) {
        this.reg = Register.values()[idx];
        this.isAllocated = isAllocated;
    }

    @Override
    public String toString() {
        return "$" + reg.name;
    }
}
