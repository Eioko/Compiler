package backend.operand;

public class MipsLabel extends MipsOperand {
    private String name;

    public MipsLabel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
