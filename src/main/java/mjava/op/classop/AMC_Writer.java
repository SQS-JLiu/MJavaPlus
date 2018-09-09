package mjava.op.classop;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import mjava.op.util.MutantCodeWriter;
import mjava.op.util.WriteJavaFile;

import java.io.PrintWriter;

/**
 * Created by user on 2018/5/8.
 */
public class AMC_Writer extends MutantCodeWriter {
    Node original;
    Node mutant;

    public AMC_Writer( String file_name, PrintWriter out )
    {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     * @param original
     * @param mutant
     */
    public void setMutant(Node original, Node mutant)
    {
        this.original = original;
        this.mutant = mutant;
    }

    /**
     * Log mutated line
     */
    public void writeFile( CompilationUnit comp_unit )
    {
        new WriteJavaFile(comp_unit,out).writeNode2File(original,mutant);
        mutated_start = line_num =original.getBegin().get().line;
        String log_str =original.toString()+ "  =>  " +mutant.toString();
        writeLog(removeNewline(log_str));
    }
}
