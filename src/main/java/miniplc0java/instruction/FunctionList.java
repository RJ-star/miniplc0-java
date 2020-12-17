package miniplc0java.instruction;
import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FunctionList {
    public static HashMap<String, String> standardFunction = new HashMap<>();
    static {
        standardFunction.put("getint","int");
        standardFunction.put("getdpuble","double");
        standardFunction.put("getchar","char");
        standardFunction.put("putint","void");
        standardFunction.put("putdouble","void");
        standardFunction.put("putchar","void");
        standardFunction.put("putstr","void");
        standardFunction.put("putln","void");
    }
    public String name;
    public String type;
    public int returnSlots = 0;
    public boolean isReturned;
    public int paramsSum = 0;
    public int localSum = 0;
    public ArrayList<FunctionParams> paramsList = new ArrayList<>();
    public ArrayList<Instruction> instructionsList = new ArrayList<>();

    public static void setStandardFunction(HashMap<String, String> standardFunction) {
        FunctionList.standardFunction = standardFunction;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {

        this.type = type;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public void setParamsSum(int paramsSum) {
        this.paramsSum = paramsSum;
    }

    public void setParamsList(ArrayList<FunctionParams> paramsList) {
        this.paramsList = paramsList;
    }

    public void setInstructionsList(ArrayList<Instruction> instructionsList) {
        this.instructionsList = instructionsList;
    }

    public FunctionList(String name) {
        this.name = name;
    }

    public int getNextLocalOffset() {
        return this.localSum-1;
    }

    public void addParam(String name, String type, boolean isConstant, Pos curPos) throws AnalyzeError {
        for (int i=0; i<paramsList.size(); i++) {
            if (paramsList.get(i).getName().equals(name)) {
                throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
            }
        }
        paramsList.add(new FunctionParams(name, type, isConstant));
        paramsSum++;
    }

    public void InParamsList(String name, Pos curPos) throws AnalyzeError {
        for (int i=0; i<paramsList.size(); i++) {
            if (paramsList.get(i).getName().equals(name)) {
                throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
            }
        }
    }

    public void setIsReturned(String type, Pos curPos) throws AnalyzeError{
        if(!type.equals(this.type)){
            throw new AnalyzeError(ErrorCode.ExpectedToken, curPos);
        }
        this.isReturned = true;
    }

    public void checkParams(ArrayList<String> list, Pos curPos) throws AnalyzeError {
        if (paramsSum!=list.size()) {
            throw new AnalyzeError(ErrorCode.ExpectedToken, curPos);
        }
        for (int i=0; i<list.size(); i++) {
            if (!list.get(i).equals(paramsList.get(i).getType())) {
                throw new AnalyzeError(ErrorCode.ExpectedToken, curPos);
            }
        }
    }

    public static HashMap<String, String> getStandardFunction() {
        return standardFunction;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getReturnSlots() {
        return returnSlots;
    }

    public void setReturnSlots(int returnSlots) {
        this.returnSlots = returnSlots;
    }

    public int getLocalSum() {
        return localSum;
    }

    public void setLocalSum(int localSum) {
        this.localSum = localSum;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturn(String ty){
        if(!ty.equals("void"))
            this.returnSlots = 1;
        this.type = ty;
    }

    public int getParamsSum() {
        return paramsSum;
    }

    public void returnFn(String ty, Pos curPos) throws AnalyzeError{
        if(!ty.equals(this.type)){
            throw new AnalyzeError(ErrorCode.ExpectedToken, curPos);
        }
        this.isReturned = true;
    }

    public ArrayList<FunctionParams> getParamsList() {
        return paramsList;
    }

    public ArrayList<Instruction> getInstructionsList() {
        return instructionsList;
    }

    public void addInstruction(Instruction instruction) {
        instructionsList.add(instruction);
    }

    public int getOffset(String name) {
        for (int i=0; i<paramsList.size(); i++) {
            if (paramsList.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public int getFnNumber(){
        return Intermediate.getIntermediate().getFnNumber(this.name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("fn [").append(Intermediate.getIntermediate().getFnNumber(this.name)).
                append("] ").append(localSum).append(" ").append(paramsSum).append(" -> ").
                append(returnSlots).append(" {\n");

        int xh=0;
        for(Instruction i : instructionsList){
            sb.append(xh+": ");
            sb.append(i).append("\n");
            xh++;
        }
        sb.append("}\n");

        return sb.toString();
    }
}
