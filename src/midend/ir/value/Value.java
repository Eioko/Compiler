package midend.ir.value;

import midend.ir.type.ValueType;

import java.util.ArrayList;

public class Value {
    protected String name;
    protected ValueType valueType;
    private Value parent;

    protected final ArrayList<User> users;
    public Value(String name, ValueType valueType, Value parent) {
        this.name = name;
        this.valueType = valueType;
        users = new ArrayList<>();
        this.parent = parent;
    }

    public String getName() {
        return name;
    }
    public ValueType getValueType() {
        return valueType;
    }
    public void addUser(User user) {
        users.add(user);
    }
    public Value getParent() {
        return parent;
    }
}
