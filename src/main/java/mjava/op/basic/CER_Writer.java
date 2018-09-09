package mjava.op.basic;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ConditionalExpr;
import mjava.op.util.WriteJavaFile;

import java.io.PrintWriter;

/**
 * Created by user on 2018/5/7.
 * @author  Jian Liu
 */
public class CER_Writer extends TraditionalMutantCodeWriter{
    ConditionalExpr original;
    ConditionalExpr mutant;

    public CER_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void setMutant(ConditionalExpr exp1, ConditionalExpr exp2) {
        original = exp1;
        mutant = exp2;
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
}
