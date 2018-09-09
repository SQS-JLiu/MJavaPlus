package mjava.op.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.ecnu.sqslab.mjava.MutationSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;
import java.util.regex.Pattern;

/**
 * Created by user on 2018/5/8.
 * @author  Jian Liu
 */
public class Mutator extends VoidVisitorAdapter<Object> {
    public int num = 0;
    protected CompilationUnit comp_unit;

    public Mutator( CompilationUnit comp_unit )
    {
        this.comp_unit = comp_unit;
    }

    public PrintWriter getPrintWriter(String f_name) throws IOException
    {
        File outfile = new File(f_name);
        FileWriter fout = new FileWriter( outfile );
        PrintWriter out = new PrintWriter( fout );
        return out;
    }

    /**
     * Return file name
     * @return
     */
    public String getSourceName()
    {
        // make directory for the mutant
        String dir_name = MutationSystem.MUTANT_PATH + "/" + getClassName() + "_" + this.num;
        File f = new File(dir_name);
        f.mkdir();

        // return file name
        String name;
        name = dir_name + "/" +  MutationSystem.CLASS_NAME + ".java";
        return name;
    }

    //-----------------
    /**
     * Return a class name
     */
    public String getClassName()
    {
        Class cc = this.getClass();
        return exclude(cc.getName(),cc.getPackage().getName());
    }

    /**
     * Remove a portion of string from a specific position
     * @param a
     * @param b
     * @return
     */
    public String exclude(String a, String b)
    {
        return a.substring(b.length()+1,a.length());
    }

    /**
     * Return an ID of a mutant
     * @return
     */
    public String getMuantID()
    {
        String str = getClassName()+"_"+this.num;
        return str;
    }

    public String getConstructorSignature(ConstructorDeclaration p){
        String str = p.getName() +"(";
        NodeList<Parameter> pars = p.getParameters();
        str += getParameterString(pars);
        str += ")";
        return String.valueOf(p.getBegin().get().line)+"_"+str;
    }

    public String getMethodSignature(MethodDeclaration p){
        //remover the generics in the return type
        String temp = p.getType().asString();
        if(temp.indexOf("<") != -1 && temp.indexOf(">")!= -1){
            temp = temp.substring(0, temp.indexOf("<")) + temp.substring(temp.lastIndexOf(">") + 1, temp.length());
        }
        String str = temp + "_" + p.getName() + "(";
        NodeList<Parameter> pars = p.getParameters();
        str += getParameterString(pars);
        str += ")";
        return String.valueOf(p.getBegin().get().line)+"_"+str;
    }
    String getParameterString(NodeList<Parameter> pars){
        String str = "";
        //the for loop goes through each parameter of a method and return them in a String, separated by comma
        for (int i = 0; i < pars.size(); i++)
        {
            //because generics in introduced, the original code does not work anymore
            //the code below applies the cheapest solution: ignore generics by removing the contents between '<' and '>'
            String tempParameter = pars.get(i).getTypeAsString();
            if(tempParameter.indexOf("<") >=0 && tempParameter.indexOf(">") >=0){
                tempParameter = tempParameter.substring(0, tempParameter.indexOf("<")) + tempParameter.substring(tempParameter.lastIndexOf(">") + 1, tempParameter.length());
                str += tempParameter;
            }
            else{
                str += tempParameter;
            }

            if (i != (pars.size()-1))
                str += ",";
        }
        return str;
    }

    public static boolean isContainThread(String bodyStr){
        Pattern threadType = Pattern.compile("public\\s*void\\s*run\\s*\\(\\s*\\)\\s*\\{");
        Pattern asyncTaskType = Pattern.compile("protected\\s*.*\\s*doInBackground\\s*\\(");
        Pattern asyncTaskType2 = Pattern.compile("public\\s*.*\\s*doInBackground\\s*\\(");
        Pattern handlerType = Pattern.compile("public\\s*void\\s*handleMessage\\s*\\(\\s*Message");
        if(threadType.matcher(bodyStr).find()){
            return true;
        }
        else if(asyncTaskType.matcher(bodyStr).find()){
            return true;
        }
        else if(asyncTaskType2.matcher(bodyStr).find()){
            return true;
        }
        else if(handlerType.matcher(bodyStr).find()){
            return true;
        }
        return false;
    }


    public static boolean isContainThreadCall(String bodyStr){
        //.start()   .runOnUiThread(     .execute(   .executeOnExecutor(
        //.post(     .postDelayed(       .postAtTime(   .sendMessage(
        //.sendMessageDelayed(    .sendEmptyMessage(    .sendMessageAtTime(
        Pattern startP = Pattern.compile("\\s*\\.\\s*start\\s*\\(\\s*\\)\\s*;");
        Pattern runOnUIP = Pattern.compile("\\s*\\.\\s*runOnUiThread\\s*\\(");
        Pattern executeP = Pattern.compile("\\s*\\.\\s*execute\\s*\\(");
        Pattern executeOnExP = Pattern.compile("\\s*\\.\\s*executeOnExecutor\\s*\\(");
        Pattern postP = Pattern.compile("\\s*\\.\\s*post\\s*\\(");
        Pattern postDelayedP = Pattern.compile("\\s*\\.\\s*postDelayed\\s*\\(");
        Pattern postAtTimeP = Pattern.compile("\\s*\\.\\s*postAtTime\\s*\\(");
        Pattern sendMsgP = Pattern.compile("\\s*\\.\\s*sendMessage\\s*\\(");
        Pattern sendMsgDelayP = Pattern.compile("\\s*\\.\\s*sendMessageDelayed\\s*\\(");
        Pattern sendEmptyMsgP = Pattern.compile("\\s*\\.\\s*sendEmptyMessage\\s*\\(");
        Pattern sendMsgAtTimeP = Pattern.compile("\\s*\\.\\s*sendMessageAtTime\\s*\\(");

        if(startP.matcher(bodyStr).find()){
            MultiThreadTriggerLog.setType(MultiThreadTriggerLog.Type.THREAD);
            return true;
        }
        else if(executeP.matcher(bodyStr).find() || executeOnExP.matcher(bodyStr).find()){
            MultiThreadTriggerLog.setType(MultiThreadTriggerLog.Type.ASYNCTASK);
            return true;
        }
        else if(runOnUIP.matcher(bodyStr).find()){
            MultiThreadTriggerLog.setType(MultiThreadTriggerLog.Type.RUNONUITHREAD);
            return true;
        }
        else if(postP.matcher(bodyStr).find() || postDelayedP.matcher(bodyStr).find() ||
                postAtTimeP.matcher(bodyStr).find() || sendMsgP.matcher(bodyStr).find() ||
                sendMsgDelayP.matcher(bodyStr).find() || sendEmptyMsgP.matcher(bodyStr).find()
                || sendMsgAtTimeP.matcher(bodyStr).find()){
            MultiThreadTriggerLog.setType(MultiThreadTriggerLog.Type.HANDLERTYPE);
            return true;
        }
        return false;
    }


    public static boolean isThreadMethodDeclaration(MethodDeclaration p){
        String name = p.getNameAsString();
        EnumSet<Modifier> modifiers = p.getModifiers();
        if("run".equals(name) && modifiers.contains(Modifier.PUBLIC) && p.getType().isVoidType()
                && p.getParameters().size() == 0){
            return true;
        }
        else if("doInBackground".equals(name)&&(modifiers.contains(Modifier.PROTECTED)||modifiers.contains(Modifier.PUBLIC))){
            return true;
        }
        else if("handleMessage".equals(name)&& modifiers.contains(Modifier.PUBLIC)
                && p.getType().isVoidType()){
            return true;
        }
        return false;
    }
}
