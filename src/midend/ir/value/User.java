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

    /**
     * User获取操作数
     * @param index
     * @return
     */
    public Value getUsedValue(int index) {
        return operands.get(index);
    }

    /**
     * User获取操作数数量
     * @return
     */
    public int getNumOfOperands() {
        return operands.size();
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public void dropAllOperands() {
        for (Value operand : operands) {
            operand.dropUser(this);
        }
    }
    public int getNumOps() {
        return operands.size();
    }
    public void setUsedValue(int index, Value newValue) {
        Value oldValue = operands.get(index);
        if (oldValue != null) {
            oldValue.dropUser(this);
        }
        operands.set(index, newValue);
        newValue.addUser(this);
    }
    public ArrayList<Value> getUsedValues() {
        return operands;
    }

}
