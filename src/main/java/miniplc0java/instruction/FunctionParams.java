package miniplc0java.instruction;

public class FunctionParams {
    private String name;
    private String type;
    private boolean isConstant;

    public FunctionParams(String name, String type, boolean isConstant) {
        this.name = name;
        this.type = type;
        this.isConstant = isConstant;
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

    public boolean isConstant() {
        return isConstant;
    }

    public void setConstant(boolean constant) {
        isConstant = constant;
    }
}
