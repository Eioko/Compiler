package midend.ir.constant;

import midend.ir.type.ArrayType;

import java.util.ArrayList;

public class ConstArray extends Constant{
    ArrayList<Constant> elements;
    public ConstArray (ArrayList<Constant> elements){
        super(new ArrayType());
        this.elements = elements;
    }
}
