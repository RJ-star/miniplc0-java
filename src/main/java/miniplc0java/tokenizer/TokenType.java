package miniplc0java.tokenizer;

public enum TokenType {
    /** 空 */
    None,
    /** 无符号整数 */
    Uint,

    Double,
    /** 标识符 */
    Ident,
    /** Begin */
    Begin,
    /** End */
    End,
    /** Var */
    Var,

    FN_KW,

    LET_KW,

    AS_KW,

    WHILE_KW,

    IF_KW,

    ELSE_KW,

    RETURN_KW,

    Const,
    /** Const */
    CONST_KW,

    PLUS,

    MINUS,

    MUL,

    DIV,

    ASSIGN,

    EQ,

    NEQ,

    LT,

    GT,

    LE,

    GE,

    L_PAREN,

    R_PAREN,

    L_BRACE,

    R_BRACE,

    ARROW,

    COMMA,

    COLON,

    SEMICOLON,

    IDENT,

    Char,

    Str,

    /** Print */
    Print,
    /** 加号 */
    Plus,
    /** 减号 */
    Minus,
    /** 乘号 */
    Mult,
    /** 除号 */
    Div,
    /** 等号 */
    Equal,
    /** 分号 */
    Semicolon,
    /** 左括号 */
    LParen,
    /** 右括号 */
    RParen,
    /** 文件尾 */
    EOF;

    @Override
    public String toString() {
        switch (this) {
            case FN_KW:
                return "Fn";
            case LET_KW:
                return "Let";
            case CONST_KW:
                return "Const";
            case AS_KW:
                return "As";
            case WHILE_KW:
                return "While";
            case IF_KW:
                return "If";
            case ELSE_KW:
                return "Else";
            case RETURN_KW:
                return "Return";
            case PLUS:
                return "Plus";
            case MINUS:
                return "Minus";
            case MUL:
                return "Mul";
            case DIV:
                return "Div";
            case ASSIGN:
                return "Assign";
            case EQ:
                return "Eq";
            case NEQ:
                return "Neq";
            case LT:
                return "Lt";
            case GT:
                return "Gt";
            case LE:
                return "Le";
            case GE:
                return "Ge";
            case Str:
                return "Str";
            case Char:
                return "Char";
            case Double:
                return "Double";
            case IDENT:
                return "Ident";
            case L_PAREN:
                return "L_paren";
            case R_PAREN:
                return "R_paren";
            case L_BRACE:
                return "L_brace";
            case R_BRACE:
                return "R_brace";
            case ARROW:
                return "Arrow";
            case COMMA:
                return "Comma";
            case COLON:
                return "Colon";
            case SEMICOLON:
                return "Semicolon";
            case None:
                return "NullToken";
            case Begin:
                return "Begin";
            case Const:
                return "Const";
            case Div:
                return "DivisionSign";
            case EOF:
                return "EOF";
            case End:
                return "End";
            case Equal:
                return "EqualSign";
            case Ident:
                return "Identifier";
            case LParen:
                return "LeftBracket";
            case Minus:
                return "MinusSign";
            case Mult:
                return "MultiplicationSign";
            case Plus:
                return "PlusSign";
            case Print:
                return "Print";
            case RParen:
                return "RightBracket";
            case Semicolon:
                return "Semicolon";
            case Uint:
                return "UnsignedInteger";
            case Var:
                return "Var";
            default:
                return "InvalidToken";
        }
    }
}
