package mjava.op.basic;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.util.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Unary plus replacement   一元运算加替换  i++ -->  i+=2
 * @author jian liu
 */
public class UPR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(UPR.class);
    public UPR(CompilationUnit comp_unit) {
        super(comp_unit);
    }

    protected  void generateMutants(MethodDeclaration p){
        p.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(UnaryExpr m, Object obj){
                super.visit(m,obj);
                if(if_line != -1 && m.getBegin().get().line > if_line){
                    return;
                }
                try{
                    if(m.getOperator().equals(UnaryExpr.Operator.POSTFIX_INCREMENT) ||
                            m.getOperator().equals(UnaryExpr.Operator.PREFIX_INCREMENT)){
                        genMutants(m);
                    }
                } catch (Exception e){
                    System.err.println("UPR: No value present!!!");
                }
            }
        },null);
    }

    private void genMutants(UnaryExpr expr){
        AssignExpr mutant;
        mutant = new AssignExpr(expr.getExpression(),new IntegerLiteralExpr("2"),AssignExpr.Operator.PLUS);
        outputToFile(expr,mutant);
    }

    /**
     * Output UPR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(UnaryExpr original, AssignExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("UPR");
        String mutant_dir = getMuantID("UPR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            UPR_Writer writer = new UPR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(comp_unit);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("UPR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
