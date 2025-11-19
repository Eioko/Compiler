package midend.ir.instruction;

import midend.ir.type.DataType;
import midend.ir.type.ValueType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class Zext extends Instruction {
    private Value from;
    private DataType toType;
    public Zext(int nameNum, Value from, DataType toType, BasicBlock parent) {
        super("%v"+nameNum, toType, parent, from);
        this.from = from;
        this.toType = toType;
    }
    public Value getFrom() {
        return from;
    }
    public DataType getToType() {
        return toType;
    }
    @Override
    public String toString() {
        return getName() + " = zext " +
                from.getValueType().toString() + " " + from.getName() +
                " to " + toType.toString();
    }
}

