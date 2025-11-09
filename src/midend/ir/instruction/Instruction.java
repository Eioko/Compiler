package midend.ir.instruction;

import midend.ir.type.DataType;
import midend.ir.value.BasicBlock;
import midend.ir.value.User;
import midend.ir.value.Value;

public class Instruction extends User {
    public Instruction(String name, DataType dataType, BasicBlock parent, Value... ops) {
        super(name, dataType, parent, ops);
    }
}
