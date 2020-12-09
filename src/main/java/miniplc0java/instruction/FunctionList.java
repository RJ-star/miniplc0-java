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
    public boolean isReturned;
    public int paramsSum = 0;
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

    public boolean isReturned() {
        return isReturned;
    }

    public int getParamsSum() {
        return paramsSum;
    }

    public ArrayList<FunctionParams> getParamsList() {
        return paramsList;
    }

    public ArrayList<Instruction> getInstructionsList() {
        return instructionsList;
    }

    public int getOffset(String name) {
        for (int i=0; i<paramsList.size(); i++) {
            if (paramsList.get(i).getName().equals(name)) {
                return i;
            }

        }
        return -1;
    }

//    public boolean checkReturnRoutes(){
//        if(this.getType().equals("void")){
//            if(!this.instructionsList.get(this.instructionsList.size()-1).getOpt().equals(Operation.RET)){
//                instructionsList.add(new Instruction(Operation.RET));
//            }
//            return true;
//        }
//        else {
//            return dfs(0, new HashSet<Integer>());
//        }
//    }
//
//    private boolean dfs(int i, HashSet<Integer> routes){
//        if(i > instructionsList.size()-1){
//            return false;
//        }
//        else if(routes.contains(i)){
//            return true;
//        }
//        else if(instructionsList.get(i).getOpt().equals(Operation.BR_TRUE)){
//            routes.add(i);
//            boolean ret = dfs(i+1, routes);
//            return ret && dfs(i+2, routes);
//        }
//        else if(instructionsList.get(i).getOpt().equals(Operation.BR)){
//            routes.add(i);
//            return dfs(i+instructionsList.get(i).getIntX()+1, routes);
//        }
//        else if(instructionsList.get(i).getOpt().equals(Operation.RET)){
//            return true;
//        }
//        else{
//            routes.add(i);
//            return dfs(i+1, routes);
//        }
//    }

    @Override
    public String toString() {//TODO
        return "FunctionList{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isReturned=" + isReturned +
                ", paramsSum=" + paramsSum +
                ", paramsList=" + paramsList +
                ", instructionsList=" + instructionsList +
                '}';
    }
}
