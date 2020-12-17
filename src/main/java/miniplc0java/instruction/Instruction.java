package miniplc0java.instruction;

import java.util.HashMap;
import java.util.Objects;

public class Instruction {
    private Operation opt;
    long x;
    int y;
    public static HashMap<String, Number> INSTRUCT = new HashMap<>();
    static{
        INSTRUCT.put("NOP", 0x00);INSTRUCT.put("PUSH", 0x01);
        INSTRUCT.put("POP", 0x02);INSTRUCT.put("POPN", 0x03);
        INSTRUCT.put("DUP", 0x04);INSTRUCT.put("LOCA", 0x0a);
        INSTRUCT.put("ARGA", 0x0b);INSTRUCT.put("GLOBA", 0x0c);
        INSTRUCT.put("LOAD_8", 0x10);INSTRUCT.put("LOAD_16", 0x11);
        INSTRUCT.put("LOAD_32", 0x12);INSTRUCT.put("LOAD_64", 0x13);
        INSTRUCT.put("STORE_8", 0x14);INSTRUCT.put("STORE_16", 0x15);
        INSTRUCT.put("STORE_32", 0x16);INSTRUCT.put("STORE_64", 0x17);
        INSTRUCT.put("ALLOC", 0x18);INSTRUCT.put("FREE", 0x19);
        INSTRUCT.put("STACKALLOC", 0x1a);INSTRUCT.put("ADD_I", 0x20);
        INSTRUCT.put("SUB_I", 0x21);INSTRUCT.put("MUL_I", 0x22);
        INSTRUCT.put("DIV_I", 0x23);INSTRUCT.put("ADD_F", 0x24);
        INSTRUCT.put("SUB_F", 0x25);INSTRUCT.put("MUL_F", 0x26);
        INSTRUCT.put("DIV_F", 0x27);INSTRUCT.put("DIV_U", 0x28);
        INSTRUCT.put("SHL", 0x29);INSTRUCT.put("SHR", 0x2a);
        INSTRUCT.put("AND", 0x2b);INSTRUCT.put("OR", 0x2c);
        INSTRUCT.put("XOR", 0x2d);INSTRUCT.put("NOT", 0x2e);
        INSTRUCT.put("CMP_I", 0x30);
        INSTRUCT.put("CMP_F", 0x32);INSTRUCT.put("CMP_U", 0x31);
        INSTRUCT.put("NEG_I", 0x34);INSTRUCT.put("NEG_F", 0x35);
        INSTRUCT.put("SHRL", 0x38);INSTRUCT.put("SET_LT", 0x39);
        INSTRUCT.put("SET_GT", 0x3a);INSTRUCT.put("BR", 0x41);
        INSTRUCT.put("BR_FALSE", 0x42);INSTRUCT.put("BR_TRUE", 0x43);
        INSTRUCT.put("CALL", 0x48);INSTRUCT.put("RET", 0x49);
        INSTRUCT.put("CALLNAME", 0x4a);INSTRUCT.put("SCAN_I", 0x50);
        INSTRUCT.put("SCAN_C", 0x51);INSTRUCT.put("SCAN_F", 0x52);
        INSTRUCT.put("PRINT_I", 0x54);INSTRUCT.put("PRINT_C", 0x55);
        INSTRUCT.put("PRINT_F", 0x56);INSTRUCT.put("PRINT_S", 0x57);
        INSTRUCT.put("PRINTLN", 0x58);INSTRUCT.put("PANIC", 0xfe);
    }

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = 0;
        this.y = 0;
    }

    public Instruction(Operation opt, long x) {
        this.opt = opt;
        this.x = x;
        this.y = 0;
    }

    public Instruction(Operation opt, long x, int y) {
        this.opt = opt;
        this.x = x;
        this.y = y;
    }

    //    public Instruction() {
//        this.opt = Operation.LIT;
//        this.x = 0;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return opt == that.opt && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opt, x);
    }

    public Operation getOpt() {
        return opt;
    }

    public int getOptValue(){
        return INSTRUCT.get(String.valueOf(this.opt)).intValue();
    }

    public void setOpt(Operation opt) {
        this.opt = opt;
    }

    public long getX() {
        return x;
    }

    public int getIntX() {
        return (int)x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean hasX() {
        switch (this.opt) {
            case CALL:
            case PUSH:
            case GLOBA:
            case LOCA:
            case STACKALLOC:
            case BR_TRUE:
            case BR:
            case BR_FALSE:
            case ARGA:
            case CALLNAME:
                return true;
            case POP:
            case NOT:
            case RET:
            case ADD_F:
            case ADD_I:
            case CMP_F:
            case CMP_I:
            case DIV_F:
            case DIV_I:
            case MUL_F:
            case MUL_I:
            case NEG_F:
            case NEG_I:
            case SUB_F:
            case SUB_I:
            case STORE_64:
            case SET_GT:
            case SET_LT:
            case PRINT_C:
            case PRINT_F:
            case PRINT_I:
            case SCAN_C:
            case PRINT_S:
            case SCAN_F:
            case PRINTLN:
            case SCAN_I:
            case LOAD_64:
            case PANIC:
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        switch (this.opt) {
            case PUSH:
                return String.format("%s(%s)",this.opt,this.x);
            case POP:
                return String.format("%s",this.opt);
            case ARGA:
                return String.format("%s(%s)",this.opt,this.x);
            case LOCA:
                return String.format("%s(%s)",this.opt,this.x);
            case GLOBA:
                return String.format("%s(%s)",this.opt,this.x);
            case LOAD_64:
                return String.format("%s(%s)",this.opt,this.x);
            case STORE_64:
                return String.format("%s",this.opt);
            case ALLOC:
                return String.format("%s(%s)",this.opt,this.x);
            case STACKALLOC:
                return String.format("%s(%s)",this.opt,this.x);
            case ADD_I:
                return String.format("%s",this.opt);
            case SUB_I:
                return String.format("%s",this.opt);
            case MUL_I:
                return String.format("%s",this.opt);
            case DIV_I:
                return String.format("%s",this.opt);
            case CMP_I:
                return String.format("%s",this.opt);
            case NEG_I:
                return String.format("%s",this.opt);
            case SET_LT:
                return String.format("%s",this.opt);
            case SET_GT:
                return String.format("%s",this.opt);
            case NOT:
                return String.format("%s",this.opt);
            case BR:
                return String.format("%s(%s)",this.opt,this.x);
            case BR_TRUE:
                return String.format("%s(%s)",this.opt,this.x);
            case BR_FALSE:
                return String.format("%s(%s)",this.opt,this.x);
            case CALL:
                return String.format("%s(%s)",this.opt,this.x);
            case CALLNAME:
                return String.format("%s(%s)",this.opt,this.x);
            case SCAN_C:
                return String.format("%s",this.opt);
            case SCAN_I:
                return String.format("%s",this.opt);
            case PRINT_I:
                return String.format("%s",this.opt);
            case PRINT_C:
                return String.format("%s",this.opt);
            case PRINT_S:
                return String.format("%s",this.opt);
            case PRINTLN:
                return String.format("%s",this.opt);
            case RET:
                return String.format("%s",this.opt);
            default:
                return "panic";
        }
    }
}
