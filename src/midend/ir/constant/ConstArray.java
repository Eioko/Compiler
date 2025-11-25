package midend.ir.constant;

import midend.ir.type.ArrayType;
import midend.ir.value.Value;

import java.util.ArrayList;

public class ConstArray extends Constant{
    ArrayList<Constant> elements;
    ArrayList<Constant> allElements = new ArrayList<>();

    public ConstArray (ArrayList<Constant> elements){
        super(new ArrayType(elements.size()), elements.toArray(new Value[0]));
        this.elements = elements;
        allElements.addAll(elements);
    }

    /**
     * 全局数组初始化需要长度信息
     * @param elements
     * @param len
     */
    public ConstArray (ArrayList<Constant> elements, int len){
        super(new ArrayType(len), elements.toArray(new Value[0]));
        this.elements = elements;
        allElements.addAll(elements);
        for(int i=elements.size(); i<len; i++){
            allElements.add(ConstInt.ZERO);
        }
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < allElements.size(); i++) {
            sb.append(allElements.get(i).getValueType().toString());
            sb.append(" ");
            sb.append(allElements.get(i).toString());
            if (i != allElements.size() - 1) {
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
    public ArrayList<Integer> toIntList(){
        ArrayList<Integer> intList = new ArrayList<>();
        for(Constant constVal : allElements){
            ConstInt constInt = (ConstInt) constVal;
            intList.add(constInt.getNumber());
        }
        return intList;
    }
}
