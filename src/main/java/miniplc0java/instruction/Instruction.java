package miniplc0java.instruction;

import java.util.HashMap;
import java.util.Objects;

public class Instruction {
    private Operation opt;
    long x;
    int y;
    public static HashMap<String, Number> instruction = new HashMap<>();

    static{
        instruction.put("NOP", 0x00);
        instruction.put("PUSH", 0x01);
        instruction.put("POP", 0x02);
        instruction.put("POPN", 0x03);
        instruction.put("DUP", 0x04);
        instruction.put("LOCA", 0x0a);
        instruction.put("ARGA", 0x0b);
        instruction.put("GLOBA", 0x0c);
        instruction.put("LOAD_8", 0x10);
        instruction.put("LOAD_16", 0x11);
        instruction.put("LOAD_32", 0x12);
        instruction.put("LOAD_64", 0x13);
        instruction.put("STORE_8", 0x14);
        instruction.put("STORE_16", 0x15);
        instruction.put("STORE_32", 0x16);
        instruction.put("STORE_64", 0x17);
        instruction.put("ALLOC", 0x18);
        instruction.put("FREE", 0x19);
        instruction.put("STACKALLOC", 0x1a);
        instruction.put("ADD_I", 0x20);
        instruction.put("SUB_I", 0x21);
        instruction.put("MUL_I", 0x22);
        instruction.put("DIV_I", 0x23);
        instruction.put("ADD_F", 0x24);
        instruction.put("SUB_F", 0x25);
        instruction.put("MUL_F", 0x26);
        instruction.put("DIV_F", 0x27);
        instruction.put("DIV_U", 0x28);
        instruction.put("SHL", 0x29);
        instruction.put("SHR", 0x2a);
        instruction.put("AND", 0x2b);
        instruction.put("OR", 0x2c);
        instruction.put("XOR", 0x2d);
        instruction.put("NOT", 0x2e);
        instruction.put("CMP_I", 0x30);
        instruction.put("CMP_F", 0x32);
        instruction.put("CMP_U", 0x31);
        instruction.put("NEG_I", 0x34);
        instruction.put("NEG_F", 0x35);
        instruction.put("SHRL", 0x38);
        instruction.put("SET_LT", 0x39);
        instruction.put("SET_GT", 0x3a);
        instruction.put("BR", 0x41);
        instruction.put("BR_FALSE", 0x42);
        instruction.put("BR_TRUE", 0x43);
        instruction.put("CALL", 0x48);
        instruction.put("RET", 0x49);
        instruction.put("CALLNAME", 0x4a);
        instruction.put("SCAN_I", 0x50);
        instruction.put("SCAN_C", 0x51);
        instruction.put("SCAN_F", 0x52);
        instruction.put("PRINT_I", 0x54);
        instruction.put("PRINT_C", 0x55);
        instruction.put("PRINT_F", 0x56);
        instruction.put("PRINT_S", 0x57);
        instruction.put("PRINTLN", 0x58);
        instruction.put("PANIC", 0xfe);
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
        return instruction.get(String.valueOf(this.opt)).intValue();
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
            case ARGA:
            case LOCA:
            case GLOBA:
            case ALLOC:
            case STACKALLOC:
            case BR:
            case BR_TRUE:
            case BR_FALSE:
            case CALL:
            case CALLNAME:
                return String.format("%s(%s)",this.opt,this.x);
            case POP:
            case STORE_64:
            case LOAD_64:
            case ADD_I:
            case SUB_I:
            case MUL_I:
            case DIV_I:
            case CMP_I:
            case NEG_I:
            case SET_GT:
            case SET_LT:
            case NOT:
            case SCAN_C:
            case SCAN_I:
            case PRINT_I:
            case PRINT_C:
            case PRINT_S:
            case PRINTLN:
            case RET:
                return String.format("%s",this.opt);
//            case ARGA:
//                return String.format("%s(%s)",this.opt,this.x);
//            case LOCA:
//                return String.format("%s(%s)",this.opt,this.x);
//            case GLOBA:
//                return String.format("%s(%s)",this.opt,this.x);
//            case LOAD_64:
//                return String.format("%s",this.opt);
//            case STORE_64:
//                return String.format("%s",this.opt);
//            case ALLOC:
//                return String.format("%s(%s)",this.opt,this.x);
//            case STACKALLOC:
//                return String.format("%s(%s)",this.opt,this.x);
//            case ADD_I:
//                return String.format("%s",this.opt);
//            case SUB_I:
//                return String.format("%s",this.opt);
//            case MUL_I:
//                return String.format("%s",this.opt);
//            case DIV_I:
//                return String.format("%s",this.opt);
//            case CMP_I:
//                return String.format("%s",this.opt);
//            case NEG_I:
//                return String.format("%s",this.opt);
//            case SET_LT:
//                return String.format("%s",this.opt);
//            case SET_GT:
//                return String.format("%s",this.opt);
//            case NOT:
//                return String.format("%s",this.opt);
//            case BR:
//                return String.format("%s(%s)",this.opt,this.x);
//            case BR_TRUE:
//                return String.format("%s(%s)",this.opt,this.x);
//            case BR_FALSE:
//                return String.format("%s(%s)",this.opt,this.x);
//            case CALL:
//                return String.format("%s(%s)",this.opt,this.x);
//            case CALLNAME:
//                return String.format("%s(%s)",this.opt,this.x);
//            case SCAN_C:
//                return String.format("%s",this.opt);
//            case SCAN_I:
//                return String.format("%s",this.opt);
//            case PRINT_I:
//                return String.format("%s",this.opt);
//            case PRINT_C:
//                return String.format("%s",this.opt);
//            case PRINT_S:
//                return String.format("%s",this.opt);
//            case PRINTLN:
//                return String.format("%s",this.opt);
//            case RET:
//                return String.format("%s",this.opt);
            default:
                return "panic";
        }
    }
}
