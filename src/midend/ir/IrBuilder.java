package midend.ir;


import midend.ir.constant.ConstArray;
import midend.ir.constant.ConstString;
import midend.ir.constant.Constant;
import midend.ir.instruction.*;
import midend.ir.type.DataType;
import midend.ir.type.FunctionType;
import midend.ir.type.ValueType;
import midend.ir.type.VoidType;
import midend.ir.value.*;

import java.util.ArrayList;


public class IrBuilder {
    private static final IrBuilder irBuilder = new IrBuilder();
    private IrBuilder() {}
    public static IrBuilder getInstance() {
        return irBuilder;
    }

    public final IrModule irModule = IrModule.getInstance();

    private static int nameNumCount = 0;

    private static int strNumCount = 0;

    public GlobalVariable buildGlobalVariable(String name, Constant initVal, boolean isConst) {
        GlobalVariable globalVariable = new GlobalVariable(name, initVal, isConst);
        irModule.addGlobalVariable(globalVariable);
        return globalVariable;
    }
    public Alloca buildConstAlloca(ValueType allocatedType, BasicBlock parent, ConstArray initVal) {
        int nameNum = nameNumCount++;
        BasicBlock realParent = parent.getParent().getHeadBlock();
        Alloca ans = new Alloca(nameNum, allocatedType, realParent, initVal);
        realParent.insertHead(ans);
        return ans;
    }
    public Alloca buildConstAlloca(ValueType allocatedType, BasicBlock parent, Constant initVal) {
        int nameNum = nameNumCount++;
        BasicBlock realParent = parent.getParent().getHeadBlock();
        Alloca ans = new Alloca(nameNum, allocatedType, realParent, initVal);
        realParent.insertHead(ans);
        return ans;
    }

    public Alloca buildAlloca(ValueType allocatedType, BasicBlock parent) {
        int nameNum = nameNumCount++;
        BasicBlock realParent = parent.getParent().getHeadBlock();
        Alloca ans = new Alloca(nameNum, allocatedType, realParent);
        realParent.insertHead(ans);
        return ans;
    }

    /**
     * @param parent 基本块
     * @param base 基地址（是一个指针）
     * @param indices 变长索引
     * @return 一个新的指针
     */
    public GEP buildGEP(BasicBlock parent, Value base, Value... indices) {
        int nameNum = nameNumCount++;
        GEP ans;
        if (indices.length == 1) {
            ans = new GEP(nameNum, parent, base, indices[0]);
        }
        else {
            ans = new GEP(nameNum, parent, base, indices[0], indices[1]);
        }
        parent.insertTail(ans);
        return ans;
    }
    /**
     * @param parent 基本块
     * @param content 存储内容
     * @param addr 地址
     */
    public void buildStore(BasicBlock parent, Value content, Value addr) {
        Store ans = new Store(parent, content, addr);
        parent.insertTail(ans);
    }
    public Call buildCall(BasicBlock parent, Function function, ArrayList<Value> args) {
        if(function.getReturnType() instanceof VoidType){
            Call call = new Call(function, parent, args);
            parent.insertTail(call);
            return call;
        }else{
            int nameNum = nameNumCount++;
            Call call = new Call(nameNum,  function, parent, args);
            parent.insertTail(call);
            return call;
        }
    }
    public Function buildFunction(String name, DataType returnType, ArrayList<DataType> paramTypes) {
        FunctionType functionType = new FunctionType(returnType, paramTypes);
        Function function;
        if(name.equals("main")){
            function = new Function(name, functionType, true);
        }else{
            function = new Function(name, functionType);
        }
        irModule.addFunction(function);
        return function;
    }

    public BasicBlock buildBasicBlock(Function function) {
        int nameNum = nameNumCount++;
        BasicBlock basicBlock = new BasicBlock(nameNum, function);
        function.addBasicBlock(basicBlock);
        return basicBlock;
    }

    public Add buildAdd(BasicBlock basicBlock, Value op1, Value op2) {
        int nameNum = nameNumCount++;
        Add add = new Add(nameNum, basicBlock, op1, op2);
        basicBlock.insertTail(add);
        return add;
    }

    public Sub buildSub(BasicBlock basicBlock, Value op1, Value op2) {
        int nameNum = nameNumCount++;
        Sub sub = new Sub(nameNum, basicBlock, op1, op2);
        basicBlock.insertTail(sub);
        return sub;
    }

    public Mul buildMul(BasicBlock basicBlock, Value op1, Value op2) {
        int nameNum = nameNumCount++;
        Mul mul = new Mul(nameNum, basicBlock, op1, op2);
        basicBlock.insertTail(mul);
        return mul;
    }

    public Div buildDiv(BasicBlock basicBlock, Value op1, Value op2) {
        int nameNum = nameNumCount++;
        Div div = new Div(nameNum, basicBlock, op1, op2);
        basicBlock.insertTail(div);
        return div;
    }

    public Mod buildMod(BasicBlock basicBlock, Value op1, Value op2) {
        int nameNum = nameNumCount++;
        Mod mod = new Mod(nameNum, basicBlock, op1, op2);
        basicBlock.insertTail(mod);
        return mod;
    }

    public Load buildLoad(BasicBlock parent, Value pointerValue) {
        int nameNum = nameNumCount++;
        Load load = new Load(nameNum, pointerValue, parent);
        parent.insertTail(load);
        return load;
    }

    public Declare buildDeclare(String name, DataType returnType, ArrayList<DataType> paramTypes) {
        Declare declare = new Declare(name, returnType, paramTypes);
        irModule.addDeclare(declare);
        return declare;
    }

    public Ret buildReturn(BasicBlock parent, Value... returnValue) {
        Ret ret;
        if(returnValue.length == 0){
            ret = new Ret(parent);

        }else{
            ret = new Ret(nameNumCount++, parent, returnValue[0]);
        }
        parent.insertTail(ret);
        return ret;
    }

    public GlobalVariable buildGlobalString(String str) {
        String name = "@.str." + strNumCount++;
        ConstString constString = new ConstString(str);
        GlobalVariable globalString = new GlobalVariable(name, constString, true);
        irModule.addGlobalVariable(globalString);
        return globalString;
    }

    public PutStr buildPutStr(BasicBlock parent, Value strAddr) {
        PutStr putStr = new PutStr(parent, strAddr);
        parent.insertTail(putStr);
        return putStr;
    }

    public PutInt buildPutInt(BasicBlock parent, Value intValue) {
        PutInt putInt = new PutInt(parent, intValue);
        parent.insertTail(putInt);
        return putInt;
    }

    public GetInt buildGetInt(BasicBlock parent) {
        int nameNum = nameNumCount++;
        GetInt getInt = new GetInt(nameNum, parent);
        parent.insertTail(getInt);
        return getInt;
    }
}
