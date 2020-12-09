package miniplc0java.tokenizer;
import miniplc0java.analyser.SymbolEntry;
import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;
import java.util.*;
import java.util.ArrayList;

public class Function {
    private String name;
    private String type;
    private ArrayList<Token> params_list = new ArrayList<>();
    private int begin;//函数操作开始位置（instruction中的位置）
    private int end;//函数返回位置（instruction中的位置）
    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();
    int nextOffset = 0;

    public Function(String name, String type, ArrayList<Token> params_list, int begin, int end) {
        this.name = name;
        this.type = type;
        this.params_list = params_list;
        this.begin = begin;
        this.end = end;
    }

    private void addSymbol(String name,String type, boolean isInitialized, boolean isConstant, Pos curPos) throws AnalyzeError {
        if (this.symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            this.symbolTable.put(name, new SymbolEntry(name, type, isConstant, isInitialized, getNextVariableOffset()));
        }
    }

    private void removeSymbol(String name,Pos curPos) throws AnalyzeError{//删除某个元素
        if(this.symbolTable.get(name)==null){
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        }
        else{
            this.symbolTable.remove(name);
        }
    }

    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    private void declareSymbol(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        }
        else {
            entry.setInitialized(true);
        }
    }

    private int getOffset(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }

    private boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isConstant();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Token> getParams_list() {
        return params_list;
    }

    public void setParams_list(ArrayList<Token> params_list) {
        this.params_list = params_list;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
