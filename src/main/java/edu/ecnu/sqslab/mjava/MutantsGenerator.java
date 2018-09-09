package edu.ecnu.sqslab.mjava;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import mjava.gui.main.MutantsGenPanel;
import mjava.op.util.Mutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Created by user on 2018/5/5.
 * @author Jian Liu
 */
public abstract class MutantsGenerator {
    private static Logger logger  = LoggerFactory.getLogger(MutantsGenerator.class);

    protected File original_file;
    private String[] operators = null;
    CompilationUnit comp_unit = null;
    static JavaParserFacade  javaParserFacade = null;
    public static CombinedTypeSolver combinedTypeSolver;

    public MutantsGenerator(File f) {
        this.original_file = f;
    }

    public MutantsGenerator(File f, String[] operator_list) {
        this(f);
        operators = operator_list;
    }

    public boolean makeMutants(){
        logger.info("-------------------------------------------------------\n");
        logger.info("* Generating parse tree. \n");
        generateParseTree();
        logger.info("..done. \n");
        if(MutationSystem.ThreadFile){
            if(comp_unit == null){
                System.out.println(original_file+", not a file, is Skipped !!!");
                logger.info(original_file+", not a file, is Skipped !!!");
                return false;
            }
            String fileStr = comp_unit.toString();
            if(Mutator.isContainThread(fileStr)){
                //do nothing.
                MutantsGenPanel.setMutationSystemPathFor(original_file.getName());
            }
            else{
                System.out.println("No threads,"+original_file+" is Skipped !!!");
                logger.info("No threads,"+original_file+" is Skipped !!!");
                return false;
            }
        }
        else {
            MutantsGenPanel.setMutationSystemPathFor(original_file.getName());
        }
        logger.info("* Generating Mutants \n");
        genMutants();
        logger.info("..done.\n");
        logger.info("* Arranging original soure code. \\n");
        arrangeOriginal();
        logger.info("..done. \\n");
        return true;
    }

    /**
     * Arrange the original source file into an appropriate directory
     */
    private void arrangeOriginal() {
        if (comp_unit == null) {
            System.err.println(original_file + " is skipped.");
            return;
        }
        File outfile = null;
        try {
                outfile = new File(MutationSystem.ORIGINAL_PATH, MutationSystem.CLASS_NAME + ".java");
                FileWriter fout = new FileWriter(outfile);
                PrintWriter out = new PrintWriter(fout);
                out.println(comp_unit.toString());
                out.flush();
                out.close();
            }
            catch (IOException e) {
                System.err.println("arrangeOriginal: fails to create " + outfile);
            }
    }

    public void generateParseTree(){
        try{
            //initCompilationEnv();
            comp_unit = JavaParser.parse(new java.io.FileInputStream(original_file));
//            System.out.println(comp_unit.toString());
        }
        catch(java.io.FileNotFoundException e){
            logger.error("File " + original_file + " not found."+e);
            return;
        }
//        String pubClass_name = getPublicClass(comp_unit);
//        if (pubClass_name == null) {
//            int len = original_file.getName().length();
//            pubClass_name = original_file.getName().substring(0, len - 5);
//        }
        //System.out.println("pubClass_name :"+pubClass_name);
    }

    abstract void genMutants();

    public String getPublicClass(CompilationUnit comp_unit){
        List<ClassOrInterfaceDeclaration> name = comp_unit.getNodesByType(ClassOrInterfaceDeclaration.class);
        for(ClassOrInterfaceDeclaration n : name){
            if(n.isPublic()){
                return n.getNameAsString();
            }
        }
        return null;
    }

    /**
     * Determine whether a string contain a certain operator
     *
     * @param list
     * @param item
     * @return true if a string contain the operator, false otherwise
     */
    protected boolean hasOperator(String[] list, String item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(item))
                return true;
        }
        return false;
    }

    public static boolean initCompilationEnv(){
        try{
            //Set up a minimal type solver  (Solver can not mutate android file)
            combinedTypeSolver = new CombinedTypeSolver();
            File fileList = new File(MutationSystem.CLASS_PATH);
            fileList.listFiles();
            for(File file: fileList.listFiles()){
                if(file.getName().endsWith(".jar")){
                    combinedTypeSolver.add(new JarTypeSolver(file));
                }
            }
            JavaParserTypeSolver jTypeSolver = new JavaParserTypeSolver(MutationSystem.SRC_PATH);
            ReflectionTypeSolver rTypeSolver = new ReflectionTypeSolver();
            combinedTypeSolver.add(jTypeSolver);
            combinedTypeSolver.add(rTypeSolver);
            // Configure JavaParser to use type resolution
            JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
            JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
            javaParserFacade = JavaParserFacade.get(combinedTypeSolver);
        }
        catch (IOException ioe){
            logger.error("Loading jar failed ."+ioe);
            return false;
        }
       return true;
    }

    public static JavaParserFacade getJavaParserFacade(){
        return javaParserFacade;
    }

    public boolean clearEmptyTraditionalMutantsDir(){
        File file = new File(MutationSystem.MUTANT_HOME);
        for(File f : file.listFiles()){
            if(f.isDirectory()){
                File tempFile = new File(f.getPath()+File.separator+MutationSystem.TM_DIR_NAME);
                if(tempFile.isDirectory()){
                    for(File f2 : tempFile.listFiles()){
                        if(f2.isDirectory() && f2.listFiles().length == 0){
                            f2.delete();
                        }
                    }
                }
            }
        }
        return true;
    }
}
