package miniplc0java.instruction;

import java.io.FileOutputStream;

public class WriteFile {
    public static void writeO0File(Intermediate midCode, String outFileName){
        try{
            FileOutputStream content = new FileOutputStream(outFileName);
            content.write(getByteValue(midCode.magic, 4));
            content.write(getByteValue(midCode.version, 4));
            content.write(getByteValue(midCode.getGlobalCounts(), 4));

            for(int i=0; i<midCode.gdList.size(); i++){
                if(midCode.gdList.get(i).isConstant()){
                    content.write(getByteValue(1, 1));
                }
                else{
                    content.write(getByteValue(0, 1));
                }
                content.write(getByteValue(8, 4));
                content.write(getByteValue(0, 8));
            }
            for(int i=midCode.getGlobalVarNum(); i<midCode.globalSymbol.size(); i++){
                content.write(getByteValue(1, 1));
                content.write(getByteValue(midCode.globalSymbol.get(i).length(), 4));
                content.write(getByteValue(midCode.globalSymbol.get(i)));
            }

            content.write(getByteValue(midCode.fnList.size(), 4));

            for(FunctionList f: midCode.fnList){
                if(f.getName().equals("_start")) {
                    content.write(getByteValue(f.getFnNumber(), 4));
                    content.write(getByteValue(!f.isReturned ?0:1, 4));
                    content.write(getByteValue(f.getParamsSum(), 4));
                    content.write(getByteValue(f.getLocalSum(), 4));

                    content.write(getByteValue(f.getInstructionsList().size(), 4));

                    for (Instruction i : f.getInstructionsList()) {
                        content.write(getByteValue(i.getOptValue(), 1));
                        if (i.hasX())
                            content.write(getByteValue(i.getX(), (int)i.getY()));
                    }
                }
            }
            for(FunctionList f: midCode.fnList){
                if(!f.getName().equals("_start")) {
                    content.write(getByteValue(f.getFnNumber(), 4));
                    content.write(getByteValue(!f.isReturned ?0:1, 4));
                    content.write(getByteValue(f.getParamsSum(), 4));
                    content.write(getByteValue(f.getLocalSum(), 4));

                    content.write(getByteValue(f.getInstructionsList().size(), 4));

                    for (Instruction i : f.getInstructionsList()) {
                        content.write(getByteValue(i.getOptValue(), 1));
                        if (i.hasX())
                            content.write(getByteValue(i.getX(), (int)i.getY()));
                    }
                }
            }
            content.close();

        } catch(Exception ignored){}

    }

    public static byte[] getByteValue(long i, int size){
        if(size == 8){
            return new byte[]{(byte)((i >> 56) & 0xFF), (byte)((i >> 48) & 0xFF), (byte)((i >> 40) & 0xFF), (byte)((i >> 32) & 0xFF),
                    (byte)((i >> 24) & 0xFF),(byte)((i >> 16) & 0xFF),(byte)((i >> 8) & 0xFF),(byte)(i & 0xFF)};
        }
        else if(size == 4){
            i = (int) i;
            return new byte[]{(byte)((i >> 24) & 0xFF),(byte)((i >> 16) & 0xFF),(byte)((i >> 8) & 0xFF),(byte)(i & 0xFF)};
        }
        else{
            return new byte[]{(byte)(i & 0xFF)};
        }
    }

    public static byte[] getByteValue(String s){
        return s.getBytes();
    }
}
