package mjava.op.classop;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import mjava.op.util.Mutator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;

/**
 * 修改权限修饰符
 * Created by user on 2018/5/8.
 * @author Jian Liu
 */
public class AMC extends Mutator{

    public AMC(CompilationUnit compilationUnit){
        super(compilationUnit);
    }

    public void visit(MethodDeclaration m,Object obj){
        genMutants(m,1,m.getModifiers());
        //System.out.println(m.getModifiers().toString());
        super.visit(m,obj);
    }

    public void visit(FieldDeclaration f,Object obj){
        genMutants(f,2,f.getModifiers());
        super.visit(f,obj);
    }

    private void genMutants(Node node,int type ,EnumSet<Modifier> modifiersSet){
       if(modifiersSet.contains(Modifier.PUBLIC)){
           changeModifier(node,type,modifiersSet,Modifier.PUBLIC,Modifier.PRIVATE);
           changeModifier(node,type,modifiersSet,Modifier.PUBLIC,Modifier.PROTECTED);
           changeModifier(node,type,modifiersSet,Modifier.PUBLIC,null);
       }
       else if(modifiersSet.contains(Modifier.PRIVATE)){
           changeModifier(node,type,modifiersSet,Modifier.PRIVATE,Modifier.PUBLIC);
           changeModifier(node,type,modifiersSet,Modifier.PRIVATE,Modifier.PROTECTED);
           changeModifier(node,type,modifiersSet,Modifier.PRIVATE,null);
       }
       else if(modifiersSet.contains(Modifier.PROTECTED)){
           changeModifier(node,type,modifiersSet,Modifier.PROTECTED,Modifier.PUBLIC);
           changeModifier(node,type,modifiersSet,Modifier.PROTECTED,Modifier.PRIVATE);
           changeModifier(node,type,modifiersSet,Modifier.PROTECTED,null);
       }
       else{
           changeModifier(node,type,modifiersSet,null,Modifier.PUBLIC);
           changeModifier(node,type,modifiersSet,null,Modifier.PROTECTED);
           changeModifier(node,type,modifiersSet,null,Modifier.PRIVATE);
       }
    }

    private void changeModifier(Node node,int type, EnumSet<Modifier> modifiersSet,Modifier exist_mod,Modifier rep_mod){
        Node muttan_node = node.clone();
        EnumSet<Modifier> mutant = modifiersSet.clone();
        if(type == 1){
            ((MethodDeclaration)muttan_node).setModifiers(mutant);
        }
        else{
            ((FieldDeclaration)muttan_node).setModifiers(mutant);
        }
        if(exist_mod == null){
            mutant.add(rep_mod);
        }
        else if(rep_mod == null){
            mutant.remove(exist_mod);
        }
        else {
           for(Modifier m: modifiersSet){
               if(m.equals(exist_mod)){
                   mutant.remove(exist_mod);
                   mutant.add(rep_mod);
                   break;
               }
           }
        }
        outputToFile(node,muttan_node);
    }
    /**
     * Output AMC mutants to files
     * @param original
     * @param mutant
     */
    public void outputToFile(Node original, Node mutant)
    {
        if (comp_unit == null)
            return;
        String f_name;
        num++;
        f_name = getSourceName();
        String mutant_dir = getMuantID();
        try
        {
            PrintWriter out = getPrintWriter(f_name);
            AMC_Writer writer = new AMC_Writer( mutant_dir, out );
            writer.setMutant(original,mutant);
            writer.writeFile(comp_unit);
            out.flush();
            out.close();
        } catch ( IOException e )
        {
            System.err.println( "fails to create " + f_name );
        }
    }

}
