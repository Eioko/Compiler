package midend.ir.instruction;

import midend.ir.type.DataType;
import midend.ir.value.BasicBlock;
import midend.ir.value.User;
import midend.ir.value.Value;

public class Instruction extends User {
    /**
     * @param name
     * @param dataType 指令的返回值类型
     * @param parent
     * @param ops
     */
    public Instruction(String name, DataType dataType, BasicBlock parent, Value... ops) {
        super(name, dataType, parent, ops);
    }

    public void toMips(BasicBlock block, Value function) {}
}
