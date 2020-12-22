package miniplc0java.instruction;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

import java.util.ArrayList;

public class Intermediate {
    public int magic = 0x72303b3e;
    public int version = 0x00000001;
    public String startFn = "_start";
    public ArrayList<GlobalSymbol> gdList = new ArrayList<>();
    public ArrayList<Function> fnList = new ArrayList<>();


    // 函数名和全局变量
    public ArrayList<String> globalSymbol = new ArrayList<>();

    public int getGlobalCounts(){
        return this.globalSymbol.size();
    }

    public int getGlobalVarNum(){
        return this.gdList.size();
    }

    private int globalVarNum = 0;

    private static Intermediate intermediate = new Intermediate();

    public static Intermediate getIntermediate(){
        return intermediate;
    }

    public int getNextGlobalVarOffset(){
        return gdList.size();
    }

    /**
     * 添加一个全局变量到表中
     * @param g
     */
    public void addGlobalVar(GlobalSymbol g){
        gdList.add(g);
    }
    public boolean inGlobalVarList(String name){
        for(GlobalSymbol g: gdList){
            if(g.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    /**
     * 变量不在全局符号表中
     * @param name 变量名
     * @param curPos 位置
     * @throws AnalyzeError 变量重复
     */
    public void notInGlobalSymbol(String name, Pos curPos) throws AnalyzeError {
        if(globalSymbol.indexOf(name) >= 0){
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        }
    }

    /**
     * 添加一个符号到全局符号表，非全局变量
     * @param name 变量名
     * @param curPos 位置
     * @throws AnalyzeError 变量重复
     */
    public void addGlobalSymbol(String name, Pos curPos) throws AnalyzeError{
        notInGlobalSymbol(name, curPos);
        globalSymbol.add(name);
    }

    /**
     * 向全局符号表的倒数第二个位置插入一条符号
     * @param name
     * @param curPos
     * @throws AnalyzeError
     */
    public void addGlobalSymbolToLastPos(String name, Pos curPos) throws AnalyzeError{
        notInGlobalSymbol(name, curPos);
        globalSymbol.add(globalSymbol.size()-2, name);
    }
    // 添加一个全局变量进去
    public void addGlobalVar(String name, Pos curPos) throws AnalyzeError{
        notInGlobalSymbol(name, curPos);
        globalSymbol.add(globalVarNum++, name);
    }

    /**
     * 添加一个函数块
     * @param f 函数块
     */
    public void addFunction(Function f){
        fnList.add(f);
    }



    /**
     * 从函数列表中去掉某个函数
     * @param fnName 函数名
     * @return 成功与否
     */
//    public boolean removeFunction(String fnName){
//        return fnList.removeIf(f -> f.getFnName().equals(fnName));
//    }

    /**
     * 在全局符号表中插入一条库函数的记录，不会插入到fn列表中
     * @param fnName 函数
     * @return 插入的偏移量，callname 返回值即可
     */
    public int insertLibFunctionBefore(String fnName, String libFn){
        int i=0;
        for(String f: globalSymbol){
            if(f.equals(fnName)){
                break;
            }
            i++;
        }
        globalSymbol.add(i, libFn);
        return i;
    }

    /**
     * 获取函数的偏移量，从1开始
     * @param fnName 函数名
     * @return 偏移量
     */
    public int getFnAddress(String fnName) {
        int i=1;
        for(Function f:fnList){
            if(f.getName().equals(fnName)){
                return i;
            }
            i++;
        }
        return -1;
    }

    public int getFnNumber(String fnName) {
        int i=0;
        for(String s: globalSymbol){
            if(s.equals(fnName)){
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * 获取函数
     * @param fnName
     * @param curPos
     * @return
     * @throws AnalyzeError
     */
    public Function getFn(String fnName, Pos curPos) throws AnalyzeError{
        for(Function f:fnList){
            if(f.getName().equals(fnName)){
                return f;
            }
        }
        throw new AnalyzeError(ErrorCode.ExpectedToken, curPos);
    }

    /**
     * 获取全局符号表中某个符号的位置，不存在则返回-1
     * @param name 符号
     * @return 位置
     */
    public int getSymbolAddress(String name){
        return globalSymbol.indexOf(name);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        for(GlobalSymbol g:gdList){
            sb.append(g.getName()).append('\n');
        }

        /*
        for(FnInstruct f : fnList){
            sb.append(f.getFnName()).append('\n');
        }*/



        for(int i=this.getGlobalVarNum(); i<this.globalSymbol.size(); i++){
            sb.append(this.globalSymbol.get(i)).append('\n');
        }
/*
        for(String s:globalSymbol){
            sb.append(s).append("\n");
        }
*/

        sb.append("\n");
        for(Function f : fnList){
            if(f.getName().equals("_start")){
                sb.append(f).append('\n');
            }
        }
        for(Function f : fnList){
            if(!f.getName().equals("_start") ){
                sb.append(f).append('\n');
            }
        }

        return sb.toString();
    }
}
