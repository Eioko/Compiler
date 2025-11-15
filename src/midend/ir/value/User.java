package midend.ir.value;

import midend.ir.type.ValueType;

import java.util.ArrayList;
import java.util.Arrays;

public class User extends Value {

    private final ArrayList<Value> operands = new ArrayList<>();
    public User(String name, ValueType valueType, Value parent) {
        super(name, valueType, parent);
    }

    public User(String name, ValueType valueType, Value parent, Value... operands) {
        super(name, valueType, parent);
        this.operands.addAll(Arrays.asList(operands));
        initUse();
    }

    private void initUse() {
        for (Value value : operands) {
            if (value != null) {
                value.addUser(this);
            }
        }
    }
    public Value getUsedValue(int index) {
        return operands.get(index);
    }


}
