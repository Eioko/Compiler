package midend.ir.constant;

import midend.ir.type.ArrayType;
import midend.ir.value.Value;

import java.util.ArrayList;

public class ConstArray extends Constant{
    ArrayList<Constant> elements;
    public ConstArray (ArrayList<Constant> elements){
        super(new ArrayType(elements.size()), elements.toArray(new Value[0]));
        this.elements = elements;
    }
}
