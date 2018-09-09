package mjava.op.basic;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.util.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Combine IAR and SAR
 * 字符串类型的变(常)量值变为两个字符串的和; String s; method(s)->method(s+s);
 * 整数类型的变(常)量;int a;  method(a);  method(0)
 */
/**
 * Created by user on 2018/5/5.
 * @author Jian Liu
 */
public class ARGR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(ARGR.class);
    public ARGR(CompilationUnit comp_unit) {
        super(comp_unit);
    }

    protected  void generateMutants(MethodDeclaration p){
        p.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodCallExpr me, Object arg){
                super.visit(me,arg);
                if(if_line != -1 && me.getBegin().get().line > if_line){
                    return;
                }
                if(isContainSpecificMethod(me.toString())){
                    return;
                }
                NodeList<Expression> exList =  me.getArguments();
                for(int i =0;i<exList.size();i++){
                    Expression ex = exList.get(i);
                    try{
                        if(ex.isMethodCallExpr()|| ex.isNameExpr() || ex.isArrayAccessExpr()){
                            ResolvedType t = MutantsGenerator.getJavaParserFacade().getType(ex);
                            if(t.describe().equals("int")|| t.describe().equals("java.lang.Long")){
                                genArgRepMutantGen(me,ex,i);
                            }
                            else if((ex.isNameExpr() || ex.isArrayAccessExpr()) && t.describe().equals("java.lang.String")){
                                //StrArgRepMutantGen(me,ex,i);
                                StrRepMutantGen(me,i);
                            }
                        }
                    }
                    catch (Exception e){
                        //System.err.println(ex.toString()+" : Unsolved Symbol Exception!!!Ignore it...");
                    }
                }
            }
        }, null);
    }

    /**
     * @param me
     * @param pos
     */
    private void StrRepMutantGen(MethodCallExpr me,int pos){
        MethodCallExpr mutant = me.clone();
        mutant.setArgument(pos,new StringLiteralExpr(""));
        outputToFile(me,mutant);
    }

    private void StrArgRepMutantGen(MethodCallExpr me,Expression ex,int pos){
        //System.out.println(ex.toString());
        MethodCallExpr mutant = me.clone();
        //BinaryExpr binaryExpr = new BinaryExpr(ex,ex, BinaryExpr.Operator.PLUS);
        String name = ex.toString();
        NameExpr nameExpr = new NameExpr();
        nameExpr.setName(name+".substring(0,"+name+".length()-1)");
        mutant.setArgument(pos,nameExpr);
        outputToFile(me,mutant);
    }

    private void genArgRepMutantGen(MethodCallExpr me,Expression ex,int pos){
        //原数据替换为0
        MethodCallExpr mutantZero = me.clone();
        IntegerLiteralExpr literalExpr = new IntegerLiteralExpr(0);
        mutantZero.setArgument(pos,literalExpr);
        outputToFile(me,mutantZero);

        //原数据替换为原数据+原数据
        MethodCallExpr mutantPlus = me.clone();
        //IntegerLiteralExpr literalExpr1 = new IntegerLiteralExpr(1);
        BinaryExpr binaryExpr = new BinaryExpr(ex,ex, BinaryExpr.Operator.PLUS);
        mutantPlus.setArgument(pos,binaryExpr);
        outputToFile(me,mutantPlus);

        //原数据替换为原数据*原数据
        MethodCallExpr mutantMulti = me.clone();
        //IntegerLiteralExpr literalExpr2 = new IntegerLiteralExpr(1);
        BinaryExpr binaryExpr2 = new BinaryExpr(ex,ex, BinaryExpr.Operator.MULTIPLY);
        mutantMulti.setArgument(pos,binaryExpr2);
        outputToFile(me,mutantMulti);
    }

    public boolean isContainSpecificMethod(String body){
        if(body.contains("System.out.print") || body.contains("Log.") || body.contains("com.mijack.Xlog")
                || body.contains("System.err.print")){
            return true;
        }
        return false;
    }

    /**
     * Output ARGR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, MethodCallExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("ARGR");
        String mutant_dir = getMuantID("ARGR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            ARGR_Writer writer = new ARGR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(comp_unit);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("ARGR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
