package backend.operand;

public class MipsLabel extends MipsOperand {
    private String name;

    public MipsLabel(String name) {
        if(!name.startsWith("b")){
            this.name = name.substring(1);
        }else{
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
