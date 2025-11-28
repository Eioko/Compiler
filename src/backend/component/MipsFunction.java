package backend.component;

public class MipsFunction {
    private String name;
    private int stackSize = 0;
    private int allocaSize = 0;
    public MipsFunction(String name) {
        this.name = name;
    }
    public void addAllocaSize(int size) {
        allocaSize += size;
    }
    public int getAllocaSize() {
        return allocaSize;
    }
    public String getName() {
        return name;
    }

}
