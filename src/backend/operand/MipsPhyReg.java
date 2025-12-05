package backend.operand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

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
    public static MipsPhyReg A1 = new MipsPhyReg(Register.A1);
    public static MipsPhyReg A2 = new MipsPhyReg(Register.A2);
    public static MipsPhyReg A3 = new MipsPhyReg(Register.A3);
    public static MipsPhyReg FP = new MipsPhyReg(Register.FP);

    public static final ArrayList <MipsPhyReg> allocatableRegs = new ArrayList<>();
    public static final ArrayList<Integer> allocatableRegIds = new ArrayList<>();
    public final static HashSet<Integer> calleeSavedRegIndex = new HashSet<>();
    static {
        for (int i = 0; i < 32; i++) {
            if (i != 0 && i != 1 && i != 29) {
                allocatableRegIds.add(i);
            }
        }
        calleeSavedRegIndex.add(3);
        for (int i = 8; i <= 28; i++)
        {
            calleeSavedRegIndex.add(i);
        }
        calleeSavedRegIndex.add(30);
        calleeSavedRegIndex.add(31);
    }

    private final Register reg;
    private boolean isAllocated;
    @Override
    public boolean isPreColored() {
        return !isAllocated;
    }

    public boolean isAllocated() {
        return isAllocated;
    }

    @Override
    public boolean needColor() {
        return true;
    }

    public int getIndex() {
        return reg.ordinal();
    }
    public void setAllocated(boolean allocated) {
        isAllocated = allocated;
    }
    public static MipsPhyReg getReg(int idx) {
        return new MipsPhyReg(Register.values()[idx]);
    }
    public MipsPhyReg(Register reg) {
        this.reg = reg;
        this.isAllocated = false;
    }

    public MipsPhyReg(int idx, boolean isAllocated) {
        this.reg = Register.values()[idx];
        this.isAllocated = isAllocated;
    }

    @Override
    public String toString() {
        return "$" + reg.name;
    }

    /**
     * 优化用
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MipsPhyReg mipsPhyReg = (MipsPhyReg) o;
        return reg.ordinal() == mipsPhyReg.reg.ordinal() && isAllocated == mipsPhyReg.isAllocated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reg.ordinal(), isAllocated);
    }
}
