package backend.operand;

public class MipsVirReg extends MipsReg {
    private final int regId;

    public MipsVirReg(int regId) {
        this.regId = regId;
    }

    public int getRegId() {
        return regId;
    }

    @Override
    public boolean needColor() {
        return true;
    }

}
