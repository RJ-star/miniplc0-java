package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.*;
import miniplc0java.tokenizer.*;
import miniplc0java.util.Pos;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
    ArrayList<Instruction> instructions;
    ArrayList<SymbolEntry> localTable = new ArrayList<>();
    Intermediate intermediate = Intermediate.getIntermediate();

    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 符号表 */
    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();

//    HashMap<String, Function> functionTable = new HashMap<>();

    /** 下一个变量的栈偏移 */
    int nextOffset = 0;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
//        this.instructions = new ArrayList<>();
    }

    public Intermediate analyse() throws CompileError {
        analyseProgram();
        return intermediate;
    }

    /**
     * 查看下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     *
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     *
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     *
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    /**
     * 获取下一个变量的栈偏移
     *
     * @return
     */
    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

//    /**
//     * 添加一个符号
//     *
//     * @param name          名字
//     * @param isInitialized 是否已赋值
//     * @param isConstant    是否是常量
//     * @param curPos        当前 token 的位置（报错用）
//     * @throws AnalyzeError 如果重复定义了则抛异常
//     */
    public SymbolEntry checkLocalSymbol(String name, int level) {
        for(int i=localTable.size()-1; i>=0; i--){
            if(localTable.get(i).getName().equals(name) && localTable.get(i).getLevel()<=level && localTable.get(i).getLevel()!=0){
                return localTable.get(i);
            }
        }
        return null;
    }

    public SymbolEntry getSymbol(String name, int level) {
        for (int i=0; i<localTable.size(); i++) {
            if (localTable.get(i).name.equals(name) && localTable.get(i).getLevel()==level) {
                return localTable.get(i);
            }
        }
        return null;
    }

    public SymbolEntry useSymbol(String name, int level, Pos curPos) throws AnalyzeError {
        for(int i=localTable.size()-1; i>=0; i--){
            if(localTable.get(i).getName().equals(name) && localTable.get(i).getLevel() <= level){
                return localTable.get(i);
            }
        }
        throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
    }

    private void addSymbol(String name, boolean isInitialized, boolean isConstant, String type, Pos curPos,int level,int offSet) throws AnalyzeError {
        if (checkLocalSymbol(name, level) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        }
        else {
            this.localTable.add(new SymbolEntry(name, type, isConstant, isInitialized, offSet, level));
        }
    }

//    private void addFunc(String name,Pos curPos, String n1, String type, ArrayList<Token> list, int begin, int end) throws AnalyzeError {
//        if (this.functionTable.get(name) != null) {
//            throw new AnalyzeError(ErrorCode.DuplicateDeclaration,curPos);
//        }
//        else {
//            this.functionTable.put(name, new Function(name, type, list, begin, end));
//        }
//    }

    private void pop(int level) {
        localTable.removeIf(s->s.getLevel()==level);
    }

    /**
     * 设置符号为已赋值
     *
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void declareSymbol(String name, int level, Pos curPos) throws AnalyzeError {
        var entry = getSymbol(name, level);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            entry.setInitialized(true);
        }
    }

    /**
     * 获取变量在栈上的偏移
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 栈偏移
     * @throws AnalyzeError
     */
    private int getOffset(String name, int level, Pos curPos) throws AnalyzeError {//TODO 没用
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }

    private int getLevelOffset(int level) {
        int ans=0;
        if (level == 0) {
            for (SymbolEntry s: localTable) {
                if (s.getLevel() == 0) {
                    ans++;
                }
            }
            return ans;
        } else {
            for (SymbolEntry s: localTable) {
                if (s.getLevel() <= level && s.getLevel()!=0) {
                    ans++;
                }
            }
            return  ans;
        }
    }

    /**
     * 获取变量是否是常量
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 是否为常量
     * @throws AnalyzeError
     */
    private boolean isConstant(FunctionList list, String name, int level, Pos curPos) throws AnalyzeError {//TODO
        SymbolEntry sy;
        int offset;
        if ((sy=checkLocalSymbol(name, level))!=null) {//查找局部变量
            return sy.isConstant();
        } else if ((offset=(list.getOffset(name))) >= 0) {// 查找函数参数表
            return list.paramsList.get(offset).isConstant();
        } else {// 查找变量表
            return useSymbol(name, 0, curPos).isConstant();
        }
    }

    private boolean isInitialized(FunctionList list, String name, int level, Pos curPos) throws AnalyzeError {
        SymbolEntry sy;
        int offset;
        if ((sy=checkLocalSymbol(name, level))!=null) {//查找局部变量
            return sy.isInitialized;
        } else if ((offset=(list.getOffset(name))) >= 0) {// 查找函数参数表
            return true;
        } else {// 查找变量表
            return useSymbol(name, 0, curPos).isInitialized;
        }
    }

    private void analyseStatement(FunctionList list, int level) throws CompileError {
        if (check(TokenType.LET_KW)) {
            analyseLetStatement(list, level);
        } else if (check(TokenType.IF_KW)) {
            analyseIfStatement(list, level);
        } else if (check(TokenType.CONST_KW)) {
            analyseConstStatement(list, level);
        } else if (check(TokenType.WHILE_KW)) {
            analyseWhileStatement(list, level);
        } else if (check(TokenType.RETURN_KW)) {
            analyseReturnStatement(list, level);
        } else if (check(TokenType.L_BRACE)) {
            analyseBlockStatement(list, level);
        } else if (check(TokenType.SEMICOLON)) {
            analyseEmptyStatement(list, level);
        } else {
            analyseAssignStatement(list, level);
        }
    }

    private void analyseAssignStatement(FunctionList list, int level) throws CompileError {
        analyseAssign(list, level);
        expect(TokenType.SEMICOLON);
    }

    private void analyseLetStatement(FunctionList list, int level) throws CompileError {
        expect(TokenType.LET_KW);
        Token token = expect(TokenType.IDENT);
        String name=token.getValueString();
        expect(TokenType.COLON);
        Token temp = expect(TokenType.Ty);
        int offset;
        if (level == 0) {//全局变量
            intermediate.addGlobalVar(name, token.getStartPos());
            intermediate.addGlobalVar(new GlobalSymbol(token.getValueString(), false));
            offset = getLevelOffset(level);
        } else {
            if (level == 1) {//传进来的参数不能在第一层再次定义
                list.InParamsList(name, token.getStartPos());
            }
            list.localSum++;
            offset=list.getLocalSum()-1;
        }
        addSymbol(name, false, false, temp.getValueString(), token.getStartPos(), level, offset);
        if (check(TokenType.ASSIGN)) {
            next();
            if (level == 0) {
                offset=Intermediate.getIntermediate().getNextGlobalVarOffset();
                list.addInstruction(new Instruction(Operation.GLOBA, offset-1, 4));
            } else {
                offset=list.getNextLocalOffset();
                list.addInstruction(new Instruction(Operation.LOCA, offset, 4));
            }
            analyseAssign(list, level);
            list.addInstruction(new Instruction(Operation.STORE_64));
            declareSymbol(name, level, token.getStartPos());
        }
        expect(TokenType.SEMICOLON);
    }

    private void analyseConstStatement(FunctionList list, int level) throws CompileError {
        expect(TokenType.CONST_KW);
        Token token = expect(TokenType.IDENT);
        String name = token.getValueString();
        expect(TokenType.COLON);
        Token temp = expect(TokenType.Ty);
        int offset;
        if (level == 0) {//全局变量
            intermediate.addGlobalVar(token.getValueString(), token.getStartPos());
            intermediate.addGlobalVar(new GlobalSymbol(token.getValueString(), false));
            offset = getLevelOffset(level);
        } else {
            if (level == 1) {
                list.InParamsList(name, token.getStartPos());
            }
            list.localSum++;
            offset=list.getLocalSum()-1;
        }
        addSymbol(name, false, true, temp.getValueString(), token.getStartPos(), level, offset);
        if (check(TokenType.ASSIGN)) {
            next();
            if (level == 0) {
                offset=Intermediate.getIntermediate().getNextGlobalVarOffset();
                list.addInstruction(new Instruction(Operation.GLOBA, offset-1, 4));
            } else {
                offset=list.getNextLocalOffset();
                list.addInstruction(new Instruction(Operation.LOCA, offset, 4));
            }
            analyseAssign(list, level);
            list.addInstruction(new Instruction(Operation.STORE_64));
            declareSymbol(token.getValueString(), level, token.getStartPos());
        } else {
            throw new ExpectedTokenError(List.of(TokenType.ASSIGN), next());
        }
        expect(TokenType.SEMICOLON);
    }

    private void analyseIfStatement(FunctionList list, int level) throws CompileError {//TODO
        expect(TokenType.IF_KW);
        analyseAssign(list, level);
        ArrayList<Integer> add=new ArrayList<Integer>();
        ArrayList<Integer> nextAdd=new ArrayList<Integer>();
        ArrayList<Integer> end=new ArrayList<Integer>();
        int end1;
        int k1=0;
        list.addInstruction(new Instruction(Operation.BR_TRUE,1,4));
        add.add(list.getInstructionsList().size());//需要修改跳转地址的位置
        list.addInstruction(new Instruction(Operation.BR,0,4));
        analyseBlockStatement(list,level);
        if(check(TokenType.ELSE_KW )){
            next();
            end.add(list.getInstructionsList().size());//跳出if else语句需要修改的跳转地址位置
            list.addInstruction(new Instruction(Operation.BR,0,4));
            while (check(TokenType.IF_KW)){
                next();
                nextAdd.add(list.getInstructionsList().size());//回填地址
                analyseAssign(list, level);
                list.addInstruction(new Instruction(Operation.BR_TRUE,1,4));
                add.add(list.getInstructionsList().size());//需要修改跳转地址的位置
                list.addInstruction(new Instruction(Operation.BR,0,4));
                analyseBlockStatement(list, level);
                end.add(list.getInstructionsList().size());
                list.addInstruction(new Instruction(Operation.BR,0,4));
                if (!check(TokenType.ELSE_KW)){
                    k1=1;
                    break;
                }
                else{
                    next();
                }
            }
            nextAdd.add(list.getInstructionsList().size());
            if(k1==0){
                analyseBlockStatement(list, level);
            }
            end1=list.getInstructionsList().size();//结束地址
            for(int i=0;i<add.size();i++){
                list.instructionsList.set(add.get(i),new Instruction(Operation.BR,nextAdd.get(i)-add.get(i)-1,4));
            }
            for(int i=0;i<end.size();i++){
                list.instructionsList.set(end.get(i),new Instruction(Operation.BR,end1-end.get(i)-1,4));
            }
        }
        else{
            list.instructionsList.set(add.get(0),new Instruction(Operation.BR,list.getInstructionsList().size()-add.get(0)-1,4));
        }
    }

    private void analyseWhileStatement(FunctionList list, int level) throws CompileError {
        expect(TokenType.WHILE_KW);
        int begin = list.getInstructionsList().size();
        list.addInstruction(new Instruction(Operation.BR, 0, 4));
        analyseAssign(list, level);
        list.addInstruction(new Instruction(Operation.BR_TRUE, 1, 4));
        int add=list.getInstructionsList().size();
        list.addInstruction(new Instruction(Operation.BR, 0 ,4));
        analyseBlockStatement(list, level);
        list.addInstruction(new Instruction(Operation.BR, begin-list.getInstructionsList().size(), 4));
        int end=list.getInstructionsList().size();
        list.getInstructionsList().set(add, new Instruction(Operation.BR, end-add-1, 4));
    }

    private void analyseReturnStatement(FunctionList list, int level) throws CompileError {
        expect(TokenType.RETURN_KW);
        if (!list.getType().equals("void")) {
            list.addInstruction(new Instruction(Operation.ARGA, 0, 4));
        }
        String type="void";
        if(!check(TokenType.SEMICOLON)) {
            type = analyseAssign(list, level);
            list.addInstruction(new Instruction(Operation.STORE_64));
        }
        if (!list.getType().equals(type)) {
            throw new AnalyzeError(ErrorCode.ExpectedToken, new Pos(0,0));
        }
        list.isReturned = true;
        list.addInstruction(new Instruction(Operation.RET));
        expect(TokenType.SEMICOLON);
    }

    private void analyseBlockStatement(FunctionList list, int level) throws CompileError {
        level++;
        expect(TokenType.L_BRACE);
        while (!check(TokenType.R_BRACE)) {
            analyseStatement(list, level);
        }
        pop(level);
        expect(TokenType.R_BRACE);
    }

    private void analyseEmptyStatement(FunctionList list, int level) throws CompileError {
        expect(TokenType.SEMICOLON);
    }

    private void analyseFunction() throws CompileError {
        expect(TokenType.FN_KW);
        Token temp = expect(TokenType.IDENT);
        expect(TokenType.L_PAREN);
        FunctionList list = new FunctionList(temp.getValueString());
        intermediate.addGlobalSymbol(temp.getValueString(), temp.getStartPos());
        intermediate.addFunction(list);
        while (!check(TokenType.R_PAREN)){
            analyseParam(list);
            if(check(TokenType.COMMA)){
                next();
            } else {
                break;
            }
        }
        expect(TokenType.R_PAREN);
        expect(TokenType.ARROW);
        Token return_type = expect(TokenType.Ty);
        list.setReturn(return_type.getValueString());
        analyseBlockStatement(list, 0);
        if(!list.isReturned()){
            list.addInstruction(new Instruction(Operation.RET));
            list.returnFn("void",temp.getStartPos());
        }
    }

    private void analyseParam(FunctionList list) throws CompileError {
        Token temp = peek();
        if (check(TokenType.CONST_KW)) {
            temp=next();
        }
        Token token = expect(TokenType.IDENT);
        expect(TokenType.COLON);
        Token return_type = expect(TokenType.Ty);
        if (temp.getValueString().equals("const")) {
            list.addParam(token.getValueString(), return_type.getValueString(), true, token.getStartPos());
        } else {
            list.addParam(token.getValueString(), return_type.getValueString(), false, token.getStartPos());
        }
    }

    /**
     * <程序> ::= 'begin'<主过程>'end'
     */

    private void analyseProgram() throws CompileError {
        // 示例函数，示例如何调用子程序
        FunctionList list = new FunctionList("_start");
        while (!check(TokenType.EOF)) {
            if (check(TokenType.FN_KW)) {
                analyseFunction();
            } else {
                if (check(TokenType.LET_KW)) {
                    analyseLetStatement(list, 0);
                } else if (check(TokenType.CONST_KW)) {
                    analyseConstStatement(list, 0);
                }
            }
        }
        Intermediate.getIntermediate().addFunction(list);
        FunctionList temp=intermediate.getFn("main",peek().getStartPos());
        list.addInstruction(new Instruction(Operation.STACKALLOC, temp.getReturnSlots(), 4));
        int begin=intermediate.getFnAddress("main");
        list.addInstruction(new Instruction(Operation.CALL, begin, 4));
        intermediate.addGlobalSymbol("_start", peek().getStartPos());
        expect(TokenType.EOF);
    }

    private String analyseAssign(FunctionList list, int level) throws CompileError {
        Token temp = peek();
        String type=analyseOperator(list, level);
        String type_temp;
        if (check(TokenType.ASSIGN)) {
            if (temp.getTokenType() == TokenType.IDENT) {
                list.instructionsList.remove(list.instructionsList.size()-1);
                if (isConstant(list, temp.getValueString(), level, temp.getStartPos())) {
                    throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.Uint, TokenType.L_PAREN), next());
                }
                next();
                type_temp=analyseOperator(list, level);
                if (!type_temp.equals(type)) {
                    throw new AnalyzeError(ErrorCode.InvalidAssignment, temp.getStartPos());
                }
                list.instructionsList.add(new Instruction(Operation.STORE_64));
            } else {
                throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.Uint, TokenType.L_PAREN), next());
            }
            return "void";
        }
        return type;
//        expect(TokenType.SEMICOLON);
    }


    private String analyseOperator(FunctionList list, int level) throws CompileError {//运算符表达式
        String type=analyseExpression(list, level);
        if (check(TokenType.EQ) || check(TokenType.NEQ) || check(TokenType.LT) || check(TokenType.GT) || check(TokenType.LE) || check(TokenType.GE)){
            Token temp = next();
            String type_temp=analyseExpression(list, level);
            if (!type.equals(type_temp)) {
                throw new AnalyzeError(ErrorCode.InvalidAssignment, temp.getStartPos());
            }
            list.instructionsList.add(new Instruction(Operation.CMP_I));
            if(temp.getTokenType() == TokenType.EQ){
                list.instructionsList.add(new Instruction(Operation.NOT));
            } else if(temp.getTokenType() == TokenType.NEQ){
            } else if(temp.getTokenType() == TokenType.LT){
                list.instructionsList.add(new Instruction(Operation.SET_LT));
            } else if(temp.getTokenType() == TokenType.GT){
                list.instructionsList.add(new Instruction(Operation.SET_GT));
            } else if(temp.getTokenType() == TokenType.LE){
                list.instructionsList.add(new Instruction(Operation.SET_GT));
                list.instructionsList.add(new Instruction(Operation.NOT));
            } else if(temp.getTokenType() == TokenType.GE){
                list.instructionsList.add(new Instruction(Operation.SET_LT));
                list.instructionsList.add(new Instruction(Operation.NOT));
            }
        }
        return type;
    }

    private String analyseExpression(FunctionList list, int level) throws CompileError {//表达式
        String type=analyseItem(list, level);
        String type_temp;
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
//            Token temp=next();
            if (nextIf(TokenType.PLUS) != null) {
                type_temp=analyseItem(list, level);
                if (!type.equals(type_temp)) {
                    throw new AnalyzeError(ErrorCode.InvalidAssignment, new Pos(0,0));
                }
                list.instructionsList.add(new Instruction(Operation.ADD_I));
            } else if (nextIf(TokenType.MINUS) != null) {
                type_temp=analyseItem(list, level);
                if (!type.equals(type_temp)) {
                    throw new AnalyzeError(ErrorCode.InvalidAssignment, new Pos(0,0));
                }
                list.instructionsList.add(new Instruction(Operation.SUB_I));
            } else
                throw new ExpectedTokenError(List.of(TokenType.PLUS,TokenType.MINUS), next());
        }
        return type;
    }

    private String analyseItem(FunctionList list, int level) throws CompileError {//项
        String type=analyseFactor(list, level);
        String type_temp;
        while (check(TokenType.MUL) || check(TokenType.DIV)) {
//            Token temp=next();
            if (nextIf(TokenType.MUL) != null) {
                type_temp=analyseFactor(list, level);
                if (!type.equals(type_temp)) {
                    throw new AnalyzeError(ErrorCode.InvalidAssignment, new Pos(0,0));
                }
                list.instructionsList.add(new Instruction(Operation.MUL_I));
            } else if (nextIf(TokenType.DIV) != null) {
                type_temp=analyseFactor(list, level);
                if (!type.equals(type_temp)) {
                    throw new AnalyzeError(ErrorCode.InvalidAssignment, new Pos(0,0));
                }
                list.instructionsList.add(new Instruction(Operation.DIV_I));
            } else
                throw new ExpectedTokenError(List.of(TokenType.MUL, TokenType.DIV), next());
        }
        return type;
    }

    private String analyseFactor(FunctionList list, int level) throws CompileError {//因子
        String type="void";
        boolean negate = false;
        while (check(TokenType.MINUS)) {
            next();
            negate=!negate;
        }
        if (check(TokenType.PLUS)) {
            next();
            negate=false;
        }

        if (check(TokenType.IDENT)) {
            // 调用相应的处理函数
            Token temp = next();
            if(check(TokenType.L_PAREN)){
                if (FunctionList.standardFunction.get(temp.getValueString()) != null) {
                    next();
                    int offSet = intermediate.insertLibFunctionBefore(list.getName(), temp.getValueString());
                    switch (temp.getValueString()){
                        case "getdouble":
                            list.addInstruction(new Instruction(Operation.STACKALLOC, 1, 4));
                            break;
                        case "getint":
                        case "getchar":
                            type="int";
                            list.addInstruction(new Instruction(Operation.STACKALLOC, 1, 4));
                            break;
                        case "putstr":
                            list.addInstruction(new Instruction(Operation.STACKALLOC, 0, 4));
                            if (check(TokenType.R_PAREN)) {
                                throw new AnalyzeError(ErrorCode.ExpectedToken, temp.getStartPos());
                            }
                            Token t=expect(TokenType.Str);
                            intermediate.addGlobalSymbolToLastPos(t.getValueString(), t.getStartPos());
                            offSet=intermediate.getSymbolAddress(t.getValueString());
                            list.addInstruction(new Instruction(Operation.PUSH, offSet, 8));
                            offSet=intermediate.getSymbolAddress("putstr");
                            break;
                        case "putln":
                            list.addInstruction(new Instruction(Operation.STACKALLOC, 0, 4));
                            break;
                        default:
                            if (check(TokenType.R_PAREN)) {
                                throw new AnalyzeError(ErrorCode.ExpectedToken, temp.getStartPos());
                            }
                            list.addInstruction(new Instruction(Operation.STACKALLOC, 0, 4));
                            if (temp.getValueString().equals("putint")||temp.getValueString().equals("putchar")) {
                                if (!analyseAssign(list, level).equals("int")) {
                                    throw new AnalyzeError(ErrorCode.InvalidAssignment, temp.getStartPos());
                                }
                            }
                            break;
                    }
                    expect(TokenType.R_PAREN);
                    list.addInstruction(new Instruction(Operation.CALLNAME,offSet,4));
                } else {//TODO修改
                    FunctionList calledFunc = intermediate.getFn(temp.getValueString(), temp.getStartPos());
                    int flag = 0;
                    if (calledFunc.isReturned) {
                        flag = 1;
                    }
                    list.addInstruction(new Instruction(Operation.STACKALLOC, flag, 4));
                    next();
                    ArrayList<String> paramType = new ArrayList<>();
                    if (check(TokenType.R_PAREN)) {
                        expect(TokenType.R_PAREN);
                    } else {
                        paramType.add(analyseAssign(list, level));
                        while (check(TokenType.COMMA)) {
                            next();
                            paramType.add(analyseAssign(list, level));
                        }
                        expect(TokenType.R_PAREN);
                    }
                    calledFunc.checkParams(paramType, temp.getStartPos());
                    list.addInstruction(new Instruction(Operation.CALL, intermediate.getFnAddress(calledFunc.getName()), 4));
                    type=calledFunc.getType();
                }
            } else {
                if (checkLocalSymbol(temp.getValueString(), level) != null) {
                    type=checkLocalSymbol(temp.getValueString(), level).type;
                    list.instructionsList.add(new Instruction(Operation.LOCA, checkLocalSymbol(temp.getValueString(), level).getStackOffset(), 4));

                } else if (list.getOffset(temp.getValueString()) != -1) {
                    type=list.paramsList.get(list.getOffset(temp.getValueString())).getType();
                    if (list.returnSlots>0) {
                        list.instructionsList.add(new Instruction(Operation.ARGA, list.getOffset(temp.getValueString())+1, 4));
                    } else {
                        list.instructionsList.add(new Instruction(Operation.ARGA, list.getOffset(temp.getValueString()), 4));
                    }


                } else {
                    type=useSymbol(temp.getValueString(), 0, temp.getStartPos()).type;
                    list.instructionsList.add(new Instruction(Operation.GLOBA, useSymbol(temp.getValueString(), 0, temp.getStartPos()).getStackOffset(), 4));
                }
                list.instructionsList.add(new Instruction(Operation.LOAD_64));
            }
        } else if (check(TokenType.Uint)) {
            // 调用相应的处理函数
            type="int";
            list.instructionsList.add(new Instruction(Operation.PUSH,Long.parseLong(next().getValueString()), 8));
        } else if (check(TokenType.Char)) {
            type="int";
            list.instructionsList.add(new Instruction(Operation.PUSH, (int)(next().getValue()), 8));
        } else if (check(TokenType.L_PAREN)) {
            // 调用相应的处理函数
            expect(TokenType.L_PAREN);
            type=analyseAssign(list, level);
            expect(TokenType.R_PAREN);
        } else {
            // 都不是，摸了
            throw new ExpectedTokenError(List.of(TokenType.IDENT), next());
        }

        if (negate) {
            list.instructionsList.add(new Instruction(Operation.NEG_I));
        }
        return type;
    }


}
