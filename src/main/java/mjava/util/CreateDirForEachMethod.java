package mjava.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import edu.ecnu.sqslab.mjava.MutationSystem;
import mjava.op.util.MultiThreadTriggerLog;
import mjava.op.util.Mutator;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by user on 2018/5/5.
 * @author  Jian Liu
 */
public class CreateDirForEachMethod extends Mutator {
    PrintWriter out = null;
    ClassOrInterfaceDeclaration cls;
    public CreateDirForEachMethod(ClassOrInterfaceDeclaration cls , PrintWriter out, CompilationUnit comp_unit){
        super(comp_unit);
        this.out = out;
        this.cls = cls;
    }

    public void  startMakeDir(){
        List<MethodDeclaration> methodList =  cls.getMethods();
        for(MethodDeclaration m: methodList){
            createDirectory(m);
        }
    }

    void createDirectory(String dir_name)
    {
        out.println(dir_name);
        String absolute_dir_path = MutationSystem.MUTANT_PATH + "/" + dir_name;
        File dirF = new File(absolute_dir_path);
        dirF.mkdir();
    }

    void createDirectory(MethodDeclaration m){
        createDirectory(getMethodSignature(m));
    }

    void createDirectory(ConstructorDeclaration c){
        createDirectory(getConstructorSignature(c));
    }


    //多线程触发记录
    public void old_visit(ConstructorDeclaration p,Object object)
    {
        if(MutationSystem.ThreadFile){
            String bodyStr = p.getBody().toString();
            if(isContainThreadCall(bodyStr)){
                String name = getConstructorSignature(p);
                MultiThreadTriggerLog.writeList(name);
                createDirectory(name);
            }
        }
        else {
            createDirectory(getConstructorSignature(p));
        }
        super.visit(p,object);
    }

    //多线程触发记录
    public void old_visit(MethodDeclaration p,Object object)
    {
        if(MutationSystem.ThreadFile){
            String bodyStr = p.getBody().toString();
            if(isContainThreadCall(bodyStr)){
                String name = getMethodSignature(p);
                MultiThreadTriggerLog.writeList(name);
                createDirectory(name);
            }
        }
        else{
            createDirectory(getMethodSignature(p));
        }
        super.visit(p,object);
    }

    public void visit(ConstructorDeclaration p,Object object)
    {
        if(MutationSystem.ThreadFile){
        }
        else {
            createDirectory(getConstructorSignature(p));
        }
        super.visit(p,object);
    }

    public void visit(MethodDeclaration p,Object object)
    {
        if(MutationSystem.ThreadFile){
            if(isThreadMethodDeclaration(p)){
                String name = getMethodSignature(p);
                createDirectory(name);
            }
        }
        else{
            createDirectory(getMethodSignature(p));
        }
        super.visit(p,object);
    }
}
