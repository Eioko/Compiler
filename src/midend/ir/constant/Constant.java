package midend.ir.constant;

import midend.ir.type.ValueType;
import midend.ir.value.User;
import midend.ir.value.Value;

public class Constant extends User {
    public Constant(ValueType valueType) {
        super(null, valueType , null);
    }
    public Constant(ValueType valueType, Value... values)
    {
        super(null, valueType, null, values);
    }
}
