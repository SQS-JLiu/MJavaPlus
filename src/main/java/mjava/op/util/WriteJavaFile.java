package mjava.op.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;

import java.io.PrintWriter;
import java.util.List;

/**
 * Created by user on 2018/5/6.
 * @author Jian Liu
 */
public class WriteJavaFile {
    CompilationUnit comp_unit;
    PrintWriter out;

    public WriteJavaFile(){
    }

    public WriteJavaFile(CompilationUnit comp_unit, PrintWriter out){
        this.comp_unit = comp_unit.clone();
        this.out = out;
    }

    public  void setComp_unit(CompilationUnit comp_unit){
        this.comp_unit = comp_unit.clone();
    }

    public void setOut(PrintWriter out){
        this.out = out;
    }

    public  boolean  writeFile(MethodCallExpr original, MethodCallExpr mutant){
        if(comp_unit == null){
            return false;
        }
        List<MethodCallExpr> meList= comp_unit.getNodesByType(MethodCallExpr.class);
        for(MethodCallExpr mecall : meList){
            if(mecall.equals(original) && isEqualLine(mecall,original)){
                mecall.replace(mutant);
                out.println(comp_unit.toString());
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public  boolean  writeFile(ConditionalExpr original, ConditionalExpr mutant){
        if(comp_unit == null){
            return false;
        }
        List<ConditionalExpr> meList= comp_unit.getNodesByType(ConditionalExpr.class);
        for(ConditionalExpr conditionalExpr : meList){
            if(conditionalExpr.equals(original) && isEqualLine(conditionalExpr,original)){
                conditionalExpr.replace(mutant);
                out.println(comp_unit.toString());
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public  boolean  writeFile(ForStmt original, ForStmt mutant){
        if(comp_unit == null){
            return false;
        }
        List<ForStmt> meList= comp_unit.getNodesByType(ForStmt.class);
        for(ForStmt forStmt : meList){
            if(forStmt.equals(original) && isEqualLine(forStmt,original)){
                forStmt.replace(mutant);
                out.println(comp_unit.toString());
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public  boolean  writeFile(BinaryExpr original, BinaryExpr mutant){
        if(comp_unit == null){
            return false;
        }
        List<BinaryExpr> meList= comp_unit.getNodesByType(BinaryExpr.class);
        for(BinaryExpr binaryExpr : meList){
            if(binaryExpr.equals(original) && isEqualLine(binaryExpr,original)){
                binaryExpr.replace(mutant);
                out.println(comp_unit.toString());
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public  boolean  writeNode2File(Node original, Node mutant){
        if(comp_unit == null){
            return false;
        }
        List<Node> meList= comp_unit.getNodesByType(Node.class);
        for(Node mode : meList){
            if(mode.equals(original) && isEqualLine(mode,original)){
                mode.replace(mutant);
                out.println(comp_unit.toString());
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public  boolean  writeFile(MethodCallExpr original, NameExpr mutant){
        if(comp_unit == null){
            return false;
        }
        List<MethodCallExpr> meList= comp_unit.getNodesByType(MethodCallExpr.class);
        for(MethodCallExpr mecall : meList){
            if(mecall.equals(original) && isEqualLine(mecall,original)){
                mecall.replace(mutant);
                out.println(comp_unit.toString());
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public  boolean  writeFile(ObjectCreationExpr original, NameExpr mutant){
        if(comp_unit == null){
            return false;
        }
        List<ObjectCreationExpr> meList= comp_unit.getNodesByType(ObjectCreationExpr.class);
        for(ObjectCreationExpr mecall : meList){
            if(mecall.equals(original) && isEqualLine(mecall,original)){
                mecall.replace(mutant);
                out.println(comp_unit.toString());
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public  boolean  writeFile(MethodCallExpr original, BlockStmt mutant){
        if(comp_unit == null){
            return false;
        }
        List<MethodCallExpr> meList= comp_unit.getNodesByType(MethodCallExpr.class);
        for(MethodCallExpr mecall : meList){
            if(mecall.equals(original) && isEqualLine(mecall,original)){
                mecall.replace(mutant);
                out.println(comp_unit.toString());
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public  boolean  writeFile(UnaryExpr original, AssignExpr mutant){
        if(comp_unit == null){
            return false;
        }
        List<UnaryExpr> meList= comp_unit.getNodesByType(UnaryExpr.class);
        for(UnaryExpr expr : meList){
            if(expr.equals(original) && isEqualLine(expr,original)){
                expr.replace(mutant);
                out.println(comp_unit.toString());
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean isEqualLine(Node node1,Node node2){
        if(node1.getBegin().get().line == node2.getBegin().get().line){
            return true;
        }
        return false;
    }
}
