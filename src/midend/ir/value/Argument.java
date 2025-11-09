package midend.ir.value;

import midend.ir.type.DataType;

public class Argument extends Value {
    public Argument(int id, DataType type, Function parent) {
        super("%a"+id, type, parent);
    }
}
