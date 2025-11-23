package midend.ir.value;

import midend.ir.IrModule;
import midend.ir.type.DataType;

import java.util.ArrayList;

public class Declare extends Value{

    ArrayList<DataType> paramTypes;
    /**
     *
     * @param name 函数名
     * @param returnType 函数的返回值类型
     * @param paramTypes 函数的参数类型列表
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
