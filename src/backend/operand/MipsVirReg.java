package backend.operand;

import java.util.Objects;

public class MipsVirReg extends MipsReg {
    private static int regId = 0;
    private final int id;
    public MipsVirReg() {
        this.id = regId;
        regId++;
    }

    public int getRegId() {
        return id;
    }

    @Override
    public boolean needColor() {
        return true;
    }

    public String toString() {
        return "vreg" + id;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MipsVirReg that = (MipsVirReg) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
