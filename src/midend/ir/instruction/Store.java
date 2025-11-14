package midend.ir.instruction;

import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class Store extends Instruction {
    public Store(BasicBlock parent, Value value, Value addr)
    {
        super("", new VoidType(), parent, value, addr);
    }
    public String toString()
    {
        return "store " + getUsedValue(0).getValueType() + " " + getUsedValue(0).getName() + ", " +
                getUsedValue(1).getValueType() + " " + getUsedValue(1).getName();
    }
}
