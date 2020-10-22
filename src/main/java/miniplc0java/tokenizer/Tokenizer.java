package miniplc0java.tokenizer;

import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {//是否结束
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {//下一个是否为数字
            return lexUInt();
        } else if (Character.isAlphabetic(peek)) {//是否为字母
            return lexIdentOrKeyword();
        } else {
            return lexOperatorOrUnknown();
        }
    }

    private Token lexUInt() throws TokenizeError {//判断整数
        // 请填空：
        // 直到查看下一个字符不是数字为止:

        Pos flag=it.previousPos();
        StringBuffer uint=new StringBuffer("");
        while(Character.isDigit(it.peekChar())){
            uint.append(it.nextChar());
        }
        // -- 前进一个字符，并存储这个字符
        //
        // 解析存储的字符串为无符号整数
        String uint1=new String(uint);
        int a=Integer.parseInt(uint1);
        // 解析成功则返回无符号整数类型的token，否则返回编译错误
        return new Token(TokenType.Uint,a,flag,it.currentPos());
        //
        // Token 的 Value 应填写数字的值
        //throw new Error("Not implemented");
    }

    private Token lexIdentOrKeyword() throws TokenizeError {//判断为标识符或关键字
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        Pos flag=it.previousPos();
        StringBuffer x=new StringBuffer("");
        // -- 前进一个字符，并存储这个字符
        while(Character.isAlphabetic(it.peekChar())||Character.isDigit(it.peekChar())){
            x.append(it.nextChar());
        }
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        String a=new String(x);
        if(a.equals("begin")){
            return new Token(TokenType.Begin,"begin",flag,it.currentPos());
        }
        else if(a.equals("end")){
            return new Token(TokenType.End,"end",flag,it.currentPos());
        }
        else if(a.equals("var")){
            return new Token(TokenType.Var,"var",flag,it.currentPos());
        }
        else if(a.equals("const")){
            return new Token(TokenType.Const,"const",flag,it.currentPos());
        }
        else if(a.equals("print")){
            return new Token(TokenType.Print,"print",flag,it.currentPos());
        }
        else{
            return new Token(TokenType.Ident,a,flag,it.currentPos());
        }
        //
        // Token 的 Value 应填写标识符或关键字的字符串
        //throw new Error("Not implemented");
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {//返回
        switch (it.nextChar()) {//读入下一个
            case '+':
                return new Token(TokenType.Plus, '+', it.previousPos(), it.currentPos());

            case '-':
                // 填入返回语句
                return new Token(TokenType.Minus, '-', it.previousPos(), it.currentPos());

            case '*':
                // 填入返回语句
                return new Token(TokenType.Mult, '*', it.previousPos(), it.currentPos());

            case '/':
                // 填入返回语句
                return new Token(TokenType.Div, '/', it.previousPos(), it.currentPos());

            // 填入更多状态和返回语句
            case '=':
                return new Token(TokenType.Equal, '=', it.previousPos(), it.currentPos());

            case ';':
                // 填入返回语句
                return new Token(TokenType.Semicolon, ';', it.previousPos(), it.currentPos());

            case '(':
                // 填入返回语句
                return new Token(TokenType.LParen, '(', it.previousPos(), it.currentPos());

            case ')':
                // 填入返回语句
                return new Token(TokenType.RParen, ')', it.previousPos(), it.currentPos());

            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
