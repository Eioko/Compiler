package midend.ir.value;

import midend.ir.IrModule;
import midend.ir.type.DataType;
import midend.ir.type.ValueType;

import java.util.ArrayList;

public class Declare extends Value{

    ArrayList<DataType> paramTypes = new ArrayList<>();
    /**
     *
     * @param name
     * @param returnType 函数的返回值类型
     */
    public Declare(String name, DataType returnType, ArrayList<DataType> paramTypes) {
        super("@"+name, returnType, IrModule.getInstance());
        this.paramTypes = paramTypes;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("declare ").append(getValueType().toString()).append(" ").append(getName()).append("(");
        for (int i = 0; i < paramTypes.size(); i++) {
            res.append(paramTypes.get(i).toString());
            if (i != paramTypes.size() - 1) {
                res.append(", ");
            }
        }
        res.append(")");
        return res.toString();
    }
}
