package mjava.op.util;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.ecnu.sqslab.mjava.MutationSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by user on 2018/5/5.
 * @author Jian Liu
 */
public abstract class MethodLevelMutator extends Mutator {
    final String ClassTag = "MethodLevelMutator";
    protected  String currentMethodSignature = null;
    protected  HashMap<String, ArrayList<Integer>> variableLineNumMap;
    protected int if_line = -1;

    public MethodLevelMutator(CompilationUnit compileUnit){
        super(compileUnit);
    }

    /**
     * Retrieve the source's file name
     */
    public String getSourceName(String op_name) {
        // make directory for the mutant
        String dir_name = MutationSystem.MUTANT_PATH + "/" + currentMethodSignature + "/" + op_name + "_" + this.num;
        File f = new File(dir_name);
        f.mkdir();

        // return file name
        String name;
        name = dir_name + "/" +  MutationSystem.CLASS_NAME + ".java";
        return name;
    }

    /**
     * Return an ID of a given operator name
     * @param op_name
     * @return
     */
    public String getMuantID(String op_name)
    {
        String str = op_name + "_" + this.num;
        return str;
    }

    public PrintWriter getPrintWriter(String f_name) throws IOException
    {
        File outfile = new File(f_name);
        FileWriter fout = new FileWriter( outfile );
        PrintWriter out = new PrintWriter( fout );
        return out;
    }

    protected abstract void generateMutants(MethodDeclaration p);

    public void visit(MethodDeclaration p, Object obj)
    {
        if(MutationSystem.ThreadFile){
            if(isThreadMethodDeclaration(p)){
                //System.out.println("========"+p.getNameAsString());
                currentMethodSignature = getMethodSignature(p);
                //获取线程执行函数里的第一个条件语句的行数，为了只对条件语句之前的代码语句应用变异算子,减少无效变异体
                NodeList<Statement> stateList = p.getBody().get().getStatements();
                if_line = -1;
                for (Statement stat:stateList){
                    if(stat.isIfStmt() || stat.isSwitchStmt()){
                        if_line = stat.getBegin().get().line;
                        break;
                    }
                }
                generateMutants(p);
            }
            else {
                currentMethodSignature = null;
            }
        }
        else {
            currentMethodSignature = getMethodSignature(p);
            generateMutants(p);
        }
        super.visit(p,obj);
    }

    public void visit(ConstructorDeclaration p, Object obj)
    {
        if(MutationSystem.ThreadFile){
            currentMethodSignature = null;
        }
        else {
            currentMethodSignature = getConstructorSignature(p);
        }
        super.visit(p,obj);
    }

    private void getLocalVariableList(MethodDeclaration p){
        p.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(VariableDeclarator n, Object arg){
                //TODO: check if this var was declared above it, as a local var to the func. if yes, return
                ArrayList<Integer> setOfLineNum;
                if(variableLineNumMap == null){
                    System.err.println(ClassTag+" Error: variableLineNumMap is null");
                    return;
                }
                //System.out.println(n.getBegin().get().line+" NameExpr " + n.getNameAsString());
                if (!variableLineNumMap.containsKey(n.getNameAsString()))
                {
                    setOfLineNum = new ArrayList<Integer>();
                    setOfLineNum.add(n.getBegin().get().line);
                    variableLineNumMap.put(n.getNameAsString(), setOfLineNum);
                } else {
                    setOfLineNum = variableLineNumMap.get(n.getNameAsString());
                    setOfLineNum.add(n.getBegin().get().line);
                    variableLineNumMap.put(n.getNameAsString(), setOfLineNum);
                }
                super.visit(n,arg);
            }
        },null);
    }

    protected  boolean isLocalVarInvok(MethodCallExpr me){
        if(me.toString().contains(".")){
            if (me.getChildNodes().size() > 1){
                if(variableLineNumMap.containsKey(me.getChildNodes().get(0).toString())){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isContainOutsideVar(Expression exp){
        List<Node> nodelist =  exp.getChildNodes();
        for(int i=0;i < nodelist.size();i++ ) {
            Node node = nodelist.get(i);
            if(!variableLineNumMap.containsKey(node.toString())){
                return true;
            }
        }
        return false;
    }
}
