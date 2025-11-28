package midend.ir.value;

import midend.ir.type.DataType;

public class Argument extends Value {
    int id;
    public Argument(int id, DataType type, Function parent) {
        super("%a"+id, type, parent);
        this.id = id;
    }
    public int getArgId(){
        return id;
    }
}
