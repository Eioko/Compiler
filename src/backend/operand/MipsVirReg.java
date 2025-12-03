package backend.operand;

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

}
