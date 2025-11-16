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
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i).toString());
            if (i != elements.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String getName(){
        return toString();
    }
    public Constant getElementAt(int index){
        return elements.get(index);
    }
}
