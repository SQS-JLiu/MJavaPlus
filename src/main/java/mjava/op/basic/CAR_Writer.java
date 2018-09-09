package mjava.op.basic;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import mjava.op.util.WriteJavaFile;

import java.io.PrintWriter;

/**
 * Created by user on 2018/5/7.
 * @author Jian Liu
 */
public class CAR_Writer extends TraditionalMutantCodeWriter {
    MethodCallExpr original;
    NameExpr mutant;
    ObjectCreationExpr originalObj;
    NameExpr mutantObj;

    public CAR_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     */
    public void setMutant(MethodCallExpr exp1, NameExpr exp2) {
        original = exp1;
        mutant = exp2;
    }

    public void setMutant(ObjectCreationExpr exp1, NameExpr exp2) {
        originalObj = exp1;
        mutantObj = exp2;
    }

    /**
     * Log mutated line
     */
    public void writeFile( CompilationUnit comp_unit )
    {
        new WriteJavaFile(comp_unit,out).writeFile(original,mutant);
        mutated_start = line_num =original.getBegin().get().line;
        String log_str =original.toString()+ "  =>  " +mutant.toString();
        writeLog(removeNewline(log_str));
    }

    /**
     * Log mutated line
     */
    public void writeFile2( CompilationUnit comp_unit )
    {
        new WriteJavaFile(comp_unit,out).writeFile(originalObj,mutantObj);
        mutated_start = line_num =originalObj.getBegin().get().line;
        String log_str =originalObj.toString()+ "  =>  " +mutantObj.toString();
        writeLog(removeNewline(log_str));
    }
}
