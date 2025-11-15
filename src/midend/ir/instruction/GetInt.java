package midend.ir.instruction;

import midend.ir.type.IntegerType;
import midend.ir.value.BasicBlock;

public class GetInt extends Instruction{
    public GetInt(int nameNum, BasicBlock parent) {
        super("%v"+ nameNum, new IntegerType(), parent);
    }

    public String toString() {
        return this.getName() + " = call i32 @getint()";
    }
}
