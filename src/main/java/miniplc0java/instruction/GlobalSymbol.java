package miniplc0java.instruction;

public class GlobalSymbol {
    private String name;
    private boolean isConstant;

    public GlobalSymbol(String name, boolean isConstant) {
        this.name = name;
        this.isConstant = isConstant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public void setConstant(boolean constant) {
        isConstant = constant;
    }
}
