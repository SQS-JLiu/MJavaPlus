package mjava.op.basic;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import mjava.op.util.WriteJavaFile;

import java.io.PrintWriter;

public class UPR_Writer extends TraditionalMutantCodeWriter{
    UnaryExpr original;
    AssignExpr mutant;

    public UPR_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void setMutant(UnaryExpr exp1, AssignExpr exp2) {
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
