package backend.operand;

public class Label extends MipsOperand {
    private String name;

    public Label(String name) {
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
