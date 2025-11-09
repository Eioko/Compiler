package midend.ir.type;

import midend.ir.value.Argument;

import java.util.ArrayList;

public class FunctionType extends ValueType {
    private final ArrayList<DataType> arguments = new ArrayList<>();
    private final DataType returnType;
    public FunctionType(DataType returnType, ArrayList<DataType> arguments) {
        this.returnType = returnType;
        this.arguments.addAll(arguments);
    }
    public DataType getReturnType() {
        return returnType;
    }
    public ArrayList<DataType> getArguments() {
        return arguments;
    }
}
