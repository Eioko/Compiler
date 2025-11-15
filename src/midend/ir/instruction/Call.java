package midend.ir.instruction;

import midend.ir.type.DataType;
import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import java.util.ArrayList;

public class Call extends Instruction {
    /**
     * 有返回值调用
     * @param nameNum
     * @param function
     * @param parent
     * @param args
     */
    public Call(int nameNum, Function function, BasicBlock parent, ArrayList<Value> args) {
        super("%v" + nameNum, function.getReturnType(),
                parent, new ArrayList<Value>(){
                    {
                        add(function);
                        addAll(args);
                    }
                }.toArray(new Value[0]));
    }

    /**
     * 无返回值调用
     * @param function
     * @param parent
     */
    public Call(Function function, BasicBlock parent, ArrayList<Value> args) {
        super("" , function.getReturnType(), parent, new ArrayList<Value>(){
            {
                add(function);
                addAll(args);
            }
        }.toArray(new Value[0]));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!(this.getValueType() instanceof VoidType)) {
            sb.append(this.getName()).append(" = ");
        }
        sb.append("call ").append(this.getValueType().toString()).append(" ");
        sb.append(this.getUsedValue(0).getName()).append("(");
        for (int i = 1; i < this.getNumOfOperands(); i++) {
            sb.append(this.getUsedValue(i).getValueType().toString()).append(" ")
                    .append(this.getUsedValue(i).getName());
            if (i != this.getNumOfOperands() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
