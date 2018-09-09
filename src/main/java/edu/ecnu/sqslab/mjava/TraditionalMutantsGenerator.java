package edu.ecnu.sqslab.mjava;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import mjava.op.basic.*;
import mjava.op.util.CodeChangeLog;
import mjava.util.CreateDirForEachMethod;
import mjava.op.util.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by user on 2018/5/5.
 * @author Jian Liu
 */
public class TraditionalMutantsGenerator extends MutantsGenerator {
    private final static  Logger logger = LoggerFactory.getLogger(TraditionalMutantsGenerator.class);
    private String[] traditionalOp;

    public TraditionalMutantsGenerator(File f)
    {
        super(f);
        traditionalOp = MutationSystem.tm_operators;
    }
    public TraditionalMutantsGenerator(File f, String[] tOP)
    {
        super(f);
        traditionalOp = tOP;
    }
    void genMutants(){
        if (comp_unit == null){
            logger.error("genMutants: "+ original_file + " is skipped.");
            System.out.println("genMutants: "+ original_file + " is skipped.");
            return;
        }
        List<ClassOrInterfaceDeclaration> clsList = comp_unit.getNodesByType(ClassOrInterfaceDeclaration.class);
        if(clsList == null || clsList.size() == 0){
            return;
        }
        if(traditionalOp != null && traditionalOp.length > 0){
            logger.info("* Generating traditional mutants.");
            MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
            MutationSystem.clearPreviousTraditionalMutants();
            CodeChangeLog.openLogFile();
            //MultiThreadTriggerLog.clearList();
            genTraditionalMutants();
            //MultiThreadTriggerLog.writeAllLog(MutationSystem.CLASS_NAME);
            CodeChangeLog.closeLogFile();
        }
    }
    void genTraditionalMutants(){
        List<ClassOrInterfaceDeclaration> clsList = comp_unit.getNodesByType(ClassOrInterfaceDeclaration.class);
        for(ClassOrInterfaceDeclaration cls : clsList){
            String tempName = cls.getNameAsString();
            //take care of the case for generics
            if(tempName.indexOf("<") != -1 && tempName.indexOf(">")!= -1)
                tempName = tempName.substring(0, tempName.indexOf("<")) + tempName.substring(tempName.lastIndexOf(">") + 1, tempName.length());

            if (tempName.equals(MutationSystem.CLASS_NAME))
            {
                //generate a list of methods from the original java class
                MethodLevelMutator  mutant_op;
                try{
                    File f = new File(MutationSystem.MUTANT_PATH, "method_list");
                    FileOutputStream fout = new FileOutputStream(f);
                    PrintWriter out = new PrintWriter(fout);
                    CreateDirForEachMethod  creatDir = new CreateDirForEachMethod(cls, out,comp_unit);
                    //creatDir.startMakeDir();
                    comp_unit.accept(creatDir,null);
                    out.flush();
                    out.close();
                }
                catch(FileNotFoundException fnf){
                        logger.error("Error in writing method list.");
                        System.out.println(fnf.toString());
                    return;
                }
                if (hasOperator (traditionalOp, "ARGR") )
                {
                    System.out.println("  Applying ARGR ... ... ");
                    mutant_op = new ARGR(comp_unit);
                    comp_unit.accept(mutant_op,null);
                    System.out.println("ARGR are handled.");
                }
                if (hasOperator (traditionalOp, "SCR") )
                {
                    System.out.println("  Applying SCR ... ... ");
                    mutant_op = new SCR(comp_unit);
                    comp_unit.accept(mutant_op,null);
                    System.out.println("SCR are handled.");
                }
                if (hasOperator (traditionalOp, "CER") )
                {
                    System.out.println("  Applying CER ... ... ");
                    mutant_op = new CER(comp_unit);
                    comp_unit.accept(mutant_op,null);
                    System.out.println("CER are handled.");
                }
                if (hasOperator (traditionalOp, "CAR") )
                {
                    System.out.println("  Applying CAR ... ... ");
                    mutant_op = new CAR(comp_unit);
                    comp_unit.accept(mutant_op,null);
                    System.out.println("CAR are handled.");
                }
                if (hasOperator (traditionalOp, "FLR") )
                {
                    System.out.println("  Applying FLR ... ... ");
                    mutant_op = new FLR(comp_unit);
                    comp_unit.accept(mutant_op,null);
                    System.out.println("FLR are handled.");
                }
                if (hasOperator (traditionalOp, "COR") )
                {
                    System.out.println("  Applying COR ... ... ");
                    mutant_op = new COR(comp_unit);
                    comp_unit.accept(mutant_op,null);
                    System.out.println("COR are handled.");
                }
                if (hasOperator (traditionalOp, "ROR") )
                {
                    System.out.println("  Applying ROR ... ... ");
                    mutant_op = new ROR(comp_unit);
                    comp_unit.accept(mutant_op,null);
                    System.out.println("ROR are handled.");
                }
                if (hasOperator (traditionalOp, "UPR") )
                {
                    System.out.println("  Applying UPR ... ... ");
                    mutant_op = new UPR(comp_unit);
                    comp_unit.accept(mutant_op,null);
                    System.out.println("UPR are handled.");
                }
            }
        }
    }
}
