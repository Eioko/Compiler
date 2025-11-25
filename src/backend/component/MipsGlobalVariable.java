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
        this.name = name.substring(2);
        this.isInit = true;
        this.isStr = false;
        this.size = 4 * elements.size();
        this.elements = elements;
        this.content = null;
    }

    public MipsGlobalVariable(String name, int size) {
        this.name = name.substring(2);
        this.isInit = false;
        this.isStr = false;
        this.size = size;
        this.elements = null;
        this.content = null;
    }

    public MipsGlobalVariable(String name, String content) {
        this.name = name.substring(2);
        this.isInit = true;
        this.isStr = true;
        this.size = content.length() + 1;
        this.elements = null;
        this.content = content;
    }
}
