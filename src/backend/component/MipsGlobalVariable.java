package backend.component;

import java.util.ArrayList;

public class MipsGlobalVariable {
    private final String name;
    private final boolean isInit;
    private final boolean isStr;
    private final int size;
    private final ArrayList<Integer> elements;
    private final String content;

    public MipsGlobalVariable(String name, ArrayList<Integer> elements) {
        this.name = name.substring(1);
        this.isInit = true;
        this.isStr = false;
        this.size = 4 * elements.size();
        this.elements = elements;
        this.content = null;
    }

    public MipsGlobalVariable(String name, int size) {
        this.name = name.substring(1);
        this.isInit = false;
        this.isStr = false;
        this.size = 4 * size;
        this.elements = null;
        this.content = null;
    }

    public MipsGlobalVariable(String name, String content) {
        this.name = name.substring(1);
        this.isInit = true;
        this.isStr = true;
        this.size = content.length() + 1;
        this.elements = null;
        this.content = content;
    }

    public boolean isInit() {
        return isInit;
    }
    public boolean isStr() {
        return isStr;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":").append("\n");
        if (isInit) {
            if (isStr) {
                sb.append("\t.asciiz\t").append("\"").append(content).append("\"").append("\n");
            } else {
                for (Integer element : elements) {
                    sb.append("\t.word\t").append(element).append("\n");
                }
            }
        } else {
            sb.append("\t.space\t").append(size).append("\n");
        }
        return sb.toString();
    }
}
