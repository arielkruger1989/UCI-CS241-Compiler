package uci.cs241;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ariel
 */
public class Compiler {
    private char in[];
    private int inCnt;
    private int lineCnt;
    private int charCnt;
    private int[] executable;
    private List<String> codeLines = new ArrayList<>();
    private List<Integer> program = new ArrayList<>();
    private boolean[] usedRegs = new boolean[32];
    Map<String, Integer> myMap;

    public Compiler(){
        usedRegs[0] = true;
        myMap = new HashMap<>();
    }

    private int allocateReg(){
        for(int i = 1; i < 28; i++){
            if(!usedRegs[i]){
                usedRegs[i] = true;
                return i;
            }
        }
        error("No Registers available");
        return -1;
    }

    private void deallocateReg(int i){
        usedRegs[i] = false;
    }

    private void addInstruction(int instructionWord){
        program.add(instructionWord);
    }

    private void loadFile(String path){
        inCnt = 0;
        lineCnt = 1;
        charCnt = 1;
        try {
            String str = new String(Files.readAllBytes(Paths.get(path)));
            in = str.toCharArray();

            BufferedReader buff = new BufferedReader(new FileReader(path));
            codeLines = new ArrayList<String>();
            while((str = buff.readLine()) != null){
                codeLines.add(str);
            }

        } catch (IOException ex) {
            System.out.println("could not read file "+path);
            System.exit(-1);
        }
    }

    private char in(){
        return in[inCnt];
    }


    private Result letter(){
        if(Character.isLetter(in[inCnt])){

        }

        return new Result();
    }

    private void cleanSpaces(){
        while(Character.isWhitespace(in[inCnt])){
            if(in[inCnt] == ' '){
                charCnt++;
            }
            else if(in[inCnt] == '\n'){
                lineCnt++;
                charCnt = 1;
            }
            inCnt++;
        }
    }

    private void next(boolean clean){
        next();
        if(clean){
            cleanSpaces();
        }
    }

    private void next(){
        inCnt++;
        charCnt++;
    }

    private Result ident(){
        if(!Character.isLetter(in())){
            error("Expected letter");
        }

        StringBuilder buff = new StringBuilder();
        while(Character.isLetter(in()) || Character.isDigit(in())){
            buff.append(in());
            next();
        }

        Result r = new Result();
        r.type = ResultType.IDENTIFIER;
        r.value = buff.toString();
        return r;
    }

    private String inWord(){
        StringBuilder buff = new StringBuilder();
        int ctn = inCnt;
        while(Character.isLetter(in[ctn]) || Character.isDigit(in[ctn])){
            buff.append(in[ctn]);
            ctn++;
        }
        return buff.toString();
    }

    private Result typeDecl(){
        Result r = ident();
        r.type = ResultType.VAR;
        if(r.value.equals("var")){

        }
        else if(r.value.equals("array")){

        }
        else{
            error("Expected Type Declaration");
        }

        return r;
    }

    private String number(){
        if(!Character.isDigit(in())){
            error("Expected Number");
        }

        StringBuilder buff = new StringBuilder();
        while(Character.isDigit(in())){
            buff.append(in());
            next();
        }

        return buff.toString();
    }

    private Result funcDecl(){
        Result r = new Result();
        return r;
    }

    private void varDecl(){

    }

    private void statSequence(){

    }

    private void computation(){
        Result r = ident();
        if(r.value.equals("main")){
            cleanSpaces();
            if(Character.isLetter(in())){
                String word = inWord();
                if(word.equals("procedure") || word.equals("function")){
                    funcDecl();
                }
                else if(word.equals("var") || word.equals("array")){
                    varDecl();
                }
                else{
                    error("Not recognized keyword "+word);
                };
            }
            else if(in() == '{'){
                next();
                statSequence();
                if(in() == '}'){
                    next();
                    if(in() == '.'){
                        addInstruction(DLX.assemble(49, 0));
                    }
                    else{
                        error("Expected . found "+in());
                    }
                }
                else{
                    error("Expected } found "+in());
                }
            }
            else{
                error("Syntax error");
            }
        }
        else{
            error("Syntax error expected main found "+r.value);
        }
    }

    public int[] compile(String path){
        loadFile(path);
        cleanSpaces();
        String word = inWord();
        if(word.equals("main")){
            computation();
        }
        else{
            error("Expected main found "+word);
        }

        return getExecutable();
    }

    public void print(String str){
        System.out.println(str);
    }

    public void error(String str){
        System.out.println(str+" line:"+lineCnt+" char: "+charCnt+" "+codeLines.get(lineCnt-1));
        System.exit(-1);
    }

    public void printProgram(){
        for(int i = 0; i < executable.length; i++){
            print(DLX.disassemble(executable[i]));
        }
    }

    public int[] getExecutable(){
        Object[] temp = program.toArray();
        executable = new int[temp.length];
        for(int i = 0; i < temp.length; i++){
            executable[i] = (int)temp[i];
        }
        return executable;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Compiler c = new Compiler();
        int[] program = c.compile("tests/big.txt");
        c.printProgram();



    }

}
