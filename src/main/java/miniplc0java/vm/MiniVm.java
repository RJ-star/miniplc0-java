package miniplc0java.vm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import miniplc0java.instruction.Instruction;

public class MiniVm {
    private List<Instruction> instructions;
    private PrintStream out;

    /**
     * @param instructions
     * @param out
     */
    public MiniVm(List<Instruction> instructions, PrintStream out) {
        this.instructions = instructions;
        this.out = out;
    }

    public MiniVm(List<Instruction> instructions) {
        this.instructions = instructions;
        this.out = System.out;
    }

    private ArrayList<Integer> stack = new ArrayList<>();

    private int ip;

    public void Run() {
        ip = 0;
        while (ip < instructions.size()) {
            var inst = instructions.get(ip);
            RunStep(inst);
            ip++;
        }
    }

    private Integer pop() {
        var val = this.stack.get(this.stack.size() - 1);
        this.stack.remove(this.stack.size() - 1);
        return val;
    }

    private void push(Integer i) {
        this.stack.add(i);
    }

    private void RunStep(Instruction inst) {
        switch (inst.getOpt()) {
            case PUSH:
            case POP:
            case LOCA:
            case GLOBA:
            case LOAD_64:
            case STORE_64:
            case ALLOC:
            case STACKALLOC:
            case ADD_I:
            case SUB_I:
            case MUL_I:
            case DIV_I:
            case CMP_I:
            case NEG_I:
            case SET_LT:
            case SET_GT:
            case NOT:
            case BR:
            case BR_TRUE:
            case BR_FALSE:
            case CALL:
            case CALLNAME:
            case SCAN_C:
            case SCAN_I:
            case PRINT_I:
            case PRINT_C:
            case PRINT_S:
            case PRINTLN:
            default:
                break;
        }
    }
}
