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

    HashMap<String, Function> functionTable = new HashMap<>();

    /** 下一个变量的栈偏移 */
    int nextOffset = 0;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.instructions = new ArrayList<>();
    }

    public List<Instruction> analyse() throws CompileError {
        analyseProgram();
        return instructions;
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

    private void addFunc(String name,Pos curPos, String n1, String type, ArrayList<Token> list, int begin, int end) throws AnalyzeError {
        if (this.functionTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration,curPos);
        }
        else {
            this.functionTable.put(name, new Function(name, type, list, begin, end));
        }
    }

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

    private void analyseAssignStatement() throws CompileError {
        analyseAssign();
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
            declareSymbol(token.getValueString(), level, token.getStartPos());
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
        ArrayList<Integer> point = new ArrayList<>();//需要跳转的点
        ArrayList<Integer> zipper = new ArrayList<>();//跳转点
        ArrayList<Integer> end = new ArrayList<>();
        analyseAssign();
        point.add(instructions.size());
        instructions.add(new Instruction(Operation.BR_TRUE,1));//占坑
        analyseBlockStatement(list, level);
        end.add(instructions.size());
        instructions.add(new Instruction(Operation.BR));//占坑
        while (check(TokenType.ELSE_KW)) {
            next();
            if (check(TokenType.IF_KW)) {
                next();
                zipper.add(instructions.size());
                analyseAssign();
                point.add(instructions.size());
                instructions.add(new Instruction(Operation.BR));//占坑
                analyseBlockStatement(list, level);
                end.add(instructions.size());
                instructions.add(new Instruction(Operation.BR));
            } else {
                zipper.add(instructions.size());
                analyseBlockStatement(list, level);
                if (check(TokenType.ELSE_KW)) {
                    throw new ExpectedTokenError(List.of(TokenType.IF_KW), next());
                }
            }
        }
//        int flag = instructions.size();
//        for (int i=0; i<point.size(); i++) {
//            instructions.set(point.get(i), new Instruction(Operation.JMP, zipper.get(i)));
//        }
//        for (int i=0; i<end.size(); i++) {
//            instructions.set(end.get(i), new Instruction(Operation.JMP, flag));
//        }
    }

    private void analyseWhileStatement(FunctionList list, int level) throws CompileError {
        expect(TokenType.WHILE_KW);
        int start=list.getInstructionsList().size();
        list.addInstruction(new Instruction(Operation.BR, 0));
        analyseAssign();
        list.addInstruction(new Instruction(Operation.BR_TRUE, 1));//成功则跳转
        int zip=list.getInstructionsList().size();
        list.addInstruction(new Instruction(Operation.BR));//失败跳转,填坑
        analyseBlockStatement(list, level);
        list.addInstruction(new Instruction(Operation.BR, start-list.getInstructionsList().size()));//返回开始
        int end=list.getInstructionsList().size();
        list.instructionsList.set(zip,new Instruction(Operation.BR, end-zip-1));
    }

    private void analyseReturnStatement() throws CompileError {
        expect(TokenType.RETURN_KW);
        analyseOperator();
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

    private void analyseEmptyStatement() throws CompileError {
        expect(TokenType.SEMICOLON);
    }

    private void analyseFunction() throws CompileError {
        expect(TokenType.FN_KW);
        Token temp = expect(TokenType.IDENT);
        expect(TokenType.L_PAREN);
        FunctionList list = new FunctionList(temp.getValueString());
        intermediate.addGlobalVar(temp.getValueString(), temp.getStartPos());
        intermediate.addFunction(list);
        while (!check(TokenType.R_PAREN)){
            analyseParam(list);
            if(check(TokenType.COMMA)){
                next();
            } else if (check(TokenType.R_PAREN)) {
                next();
                break;
            } else {
                throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.Uint, TokenType.L_PAREN), next());
            }
        }
        expect(TokenType.ARROW);
        Token return_type = expect(TokenType.Ty);
        list.setType(return_type.getValueString());
        analyseBlockStatement(list, 0);//TODO
        list.addInstruction(new Instruction(Operation.RET));
        //TODO
    }

    private void analyseParam(FunctionList list) throws CompileError {
        Token temp = next();
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
        // 'begin'
        while (!check(TokenType.EOF)) {
            if (check(TokenType.FN_KW)) {
                analyseFunction();
            } else {
                analyseStatementSequence();
            }
        }
        expect(TokenType.EOF);
    }

//    private void analyseMain() throws CompileError {
////        throw new Error("Not implemented");
////        while (!check(TokenType.End)) {
////            if (check(TokenType.Const)) {
////                analyseConstantDeclaration();
////            } else if (check(TokenType.Var)) {
////                analyseVariableDeclaration();
////            } else if (check(TokenType.Ident)) {
////                analyseStatementSequence();
////            } else {
////                break;
////            }
////        }
////        analyseConstantDeclaration();
////        analyseVariableDeclaration();
//        analyseStatementSequence();
//    }

//    private void analyseConstantDeclaration() throws CompileError {
//        // 示例函数，示例如何解析常量声明
//        // 如果下一个 token 是 const 就继续
//        while (nextIf(TokenType.Const) != null) {
//            // 变量名
//            var nameToken = expect(TokenType.Ident);
//
//            // 等于号
//            expect(TokenType.Equal);
//
//            // 常表达式
//            analyseConstantExpression();
//
//            // 分号
//            expect(TokenType.Semicolon);
//            addSymbol(nameToken.getValueString(),true,true,nameToken.getStartPos());
//        }
//    }
//
//    private void analyseVariableDeclaration() throws CompileError {//变量声明
////        throw new Error("Not implemented");
//        while (nextIf(TokenType.Var) != null) {
//            var nameToken = expect(TokenType.Ident);
//            addSymbol(nameToken.getValueString(),false,false,nameToken.getStartPos());
//            if (check(TokenType.Equal)) {
//                next();
//                analyseExpression();
//                declareSymbol(nameToken.getValueString(),nameToken.getStartPos());
//            } else {
//                instructions.add(new Instruction(Operation.LIT,0));
//            }
//            expect(TokenType.Semicolon);
//        }
//    }

//    private void analyseStatementSequence() throws CompileError {//语句序列
////        throw new Error("Not implemented");
//        while (!check(TokenType.EOF)) {
//            analyseStatement();
//        }
//    }

//    private void analyseStatement() throws CompileError {//语句
////        throw new Error("Not implemented");
//        if (check(TokenType.Ident)) {
//            analyseAssignmentStatement();
//        } else if (check(TokenType.Print)) {
//            analyseOutputStatement();
//        } else if (check(TokenType.Semicolon)) {
//            next();
//        }
//    }

//    private void analyseConstantExpression() throws CompileError {//常表达式语句
////        throw new Error("Not implemented");
//        boolean negate;
//        if (nextIf(TokenType.Minus) != null) {
//            negate = true;
//            // 计算结果需要被 0 减
//            instructions.add(new Instruction(Operation.LIT, 0));
//        } else {
//            nextIf(TokenType.Plus);
//            negate = false;
//        }
//        instructions.add(new Instruction(Operation.LIT,Integer.parseInt(next().getValueString())));
//
//        if (negate) {
//            instructions.add(new Instruction(Operation.SUB));
//        }
//    }

    private void analyseAssign() throws CompileError {
        Token temp = peek();
        analyseOperator();
        if (check(TokenType.ASSIGN)) {
            if (temp.getTokenType() == TokenType.IDENT) {
                instructions.remove(instructions.size()-1);
                if (isConstant(temp.getValueString(), temp.getStartPos())) {
                    throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.Uint, TokenType.L_PAREN), next());
                }
                next();
                analyseOperator();
                instructions.add(new Instruction((Operation.STORE_64)));
            } else {
                throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.Uint, TokenType.L_PAREN), next());
            }
        }
    }

    private void analyseOperator() throws CompileError {//运算符表达式
        analyseExpression();
        if (check(TokenType.EQ) || check(TokenType.NEQ) || check(TokenType.LT) || check(TokenType.GT) || check(TokenType.LE) || check(TokenType.GE)){
            Token temp = next();
            analyseExpression();
            if(temp.getTokenType() == TokenType.EQ){
                instructions.add(new Instruction(Operation.NOT));
            }
            else if(temp.getTokenType() == TokenType.NEQ){
//                instructions.add(new Instruction(Operation.NOT));
            }
            else if(temp.getTokenType() == TokenType.LT){
                instructions.add(new Instruction(Operation.SET_LT));
            }
            else if(temp.getTokenType() == TokenType.GT){
                instructions.add(new Instruction(Operation.SET_GT));
            }
            else if(temp.getTokenType() == TokenType.LE){
                instructions.add(new Instruction(Operation.SET_GT));
                instructions.add(new Instruction(Operation.NOT));
            }
            else if(temp.getTokenType() == TokenType.GE){
                instructions.add(new Instruction(Operation.SET_LT));
                instructions.add(new Instruction(Operation.NOT));
            }
        }
    }

    private void analyseExpression() throws CompileError {//表达式
//        throw new Error("Not implemented");
        analyseItem();
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            if (nextIf(TokenType.PLUS) != null) {
                analyseItem();
                instructions.add(new Instruction(Operation.ADD_I));
            } else if (nextIf(TokenType.MINUS) != null) {
                analyseItem();
                instructions.add(new Instruction(Operation.SUB_I));
            } else
                throw new ExpectedTokenError(List.of(TokenType.PLUS,TokenType.MINUS), next());
        }
    }



//    private void analyseAssignmentStatement() throws CompileError {//赋值语句
////        throw new Error("Not implemented");
//        var nameToken = expect(TokenType.IDENT);
//        expect(TokenType.ASSIGN);
//        analyseFactor();
//        expect(TokenType.Semicolon);
//        instructions.add(new Instruction(Operation.STO,getOffset(nameToken.getValueString(), nameToken.getStartPos())));
//    }

    private void analyseAs() throws CompileError {
        analyseFactor();
//        if (check(TokenType.AS_KW)) {
//            next();
//            Token temp = next();
//            if (temp.getValueString().equals("int")) {
//                instructions.add(new Instruction(Operation.ASDtI));
//            } else if (temp.getValueString().equals("double")) {
//                instructions.add(new Instruction(Operation.ASItD));
//            } else
//                throw new ExpectedTokenError(List.of(TokenType.Ident, TokenType.Uint, TokenType.LParen), next());
//        }
    }

//    private void analyseOutputStatement() throws CompileError {//输出语句
//        expect(TokenType.Print);
//        expect(TokenType.LParen);
//        analyseExpression();
//        expect(TokenType.RParen);
//        expect(TokenType.Semicolon);
//        instructions.add(new Instruction(Operation.WRT));
//    }

    private void analyseItem() throws CompileError {//项
//        throw new Error("Not implemented");
        analyseAs();
        while (check(TokenType.MUL) || check(TokenType.DIV)) {
            if (nextIf(TokenType.MUL) != null) {
                analyseAs();
//                var nameToken = next();
//                int pos = getOffset(nameToken.getValueString(),nameToken.getStartPos());
                instructions.add(new Instruction(Operation.MUL_I));
            } else if (nextIf(TokenType.DIV) != null){
                next();
                analyseAs();
//                var nameToken = next();
//                int pos = getOffset(nameToken.getValueString(),nameToken.getStartPos());
                instructions.add(new Instruction(Operation.DIV_I));
            } else
            throw new ExpectedTokenError(List.of(TokenType.MUL, TokenType.DIV), next());
//            analyseFactor();
        }
    }

    private void analyseFactor() throws CompileError {//因子
        boolean negate;
        if (nextIf(TokenType.MINUS) != null) {
            negate = true;
            // 计算结果需要被 0 减
//            instructions.add(new Instruction(Operation.SUB_I, 0));
        } else {
            nextIf(TokenType.PLUS);
            negate = false;
        }

        if (check(TokenType.IDENT)) {
            // 调用相应的处理函数
//            var nameToken = next();
//            int pos = getOffset(nameToken.getValueString(),nameToken.getStartPos());
//            instructions.add(new Instruction(Operation.LOD,pos));
            Token temp = next();
            if(check(TokenType.L_PAREN)){
                next();
                instructions.add(new Instruction(Operation.STACKALLOC));
                analyseAssign();
                while(check(TokenType.COMMA)){
                    next();
                    analyseAssign();
                }
                expect(TokenType.R_PAREN);
                instructions.add(new Instruction(Operation.CALL, getOffset(temp.getValueString(), temp.getStartPos())));
            }
            instructions.add(new Instruction(Operation.LOAD_64,getOffset(temp.getValueString(),temp.getStartPos())));

        } else if (check(TokenType.Uint)) {
            // 调用相应的处理函数
            instructions.add(new Instruction(Operation.PUSH,Integer.parseInt(next().getValueString())));
        } else if (check(TokenType.Double)) {
            double x=Double.parseDouble(next().getValue().toString());
            instructions.add(new Instruction(Operation.PUSH,new Double(x).longValue()));
        } else if (check(TokenType.L_PAREN)) {
            // 调用相应的处理函数
            expect(TokenType.L_PAREN);
            analyseAssign();
            expect(TokenType.R_PAREN);
        } else {
            // 都不是，摸了
            throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.Uint, TokenType.L_PAREN), next());
        }

        if (negate) {
            instructions.add(new Instruction(Operation.NEG_I));
        }
    }
}
