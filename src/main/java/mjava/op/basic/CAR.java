package mjava.op.basic;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
import mjava.op.util.MethodLevelMutator;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 数组值替换; int[] a; method(a); a[0]=0;method(a)/a[a.length/2]=0;method(a)/a[a.length-1]=0;method(a);
 */
 /**
 * Created by user on 2018/5/7.
 * @author Jian Liu
 */
public class CAR extends MethodLevelMutator {

    public CAR(CompilationUnit comp_unit) {
        super(comp_unit);
    }

     protected  void generateMutants(MethodDeclaration p){
        //1.MethodCallExpr 方法调用表达式，入参为数组引用时
         p.accept(new VoidVisitorAdapter<Object>() {
             @Override
             public void visit(MethodCallExpr m, Object obj){
                 super.visit(m,obj);
                 if(if_line != -1 && m.getBegin().get().line > if_line){
                     return;
                 }
                 NodeList<Expression> expList = m.getArguments();
                 for (int i = 0; i < expList.size(); i++) {
                     Expression e = expList.get(i);
                     try{
                         //ResolvedType t = MutantsGenerator.getJavaParserFacade().getType(e);
                         ResolvedType t = e.calculateResolvedType();
                         if (t.isArray() && e.isNameExpr()) {
                             //int short long float double
                             if(t.describe().equals("int")||t.describe().equals("java.lang.Long")){
                                 insertMutant(m,e,0);
                                 insertMutant2(m,e,0);
                                 insertMutant3(m,e,0);
                             }else if(t.describe().equals("char")||t.describe().equals("char[]")) { //char
                                 insertMutant(m,e,1);
                                 insertMutant2(m,e,1);
                                 insertMutant3(m,e,1);
                             }
                         }
                     } catch (Exception uex){
                         // System.err.println(e.toString()+" ,Unsolved Symbol Exception, ignore...");
                     }
                 }
             }
         },null);
         //2.ObjectCreationExpr  对象创建表达式，入参为数组引用时
         p.accept(new VoidVisitorAdapter<Object>() {
             @Override
             public void visit( ObjectCreationExpr m, Object obj) {
                 super.visit(m, obj);
                 if(if_line != -1 && m.getBegin().get().line > if_line){
                     return;
                 }
                 NodeList<Expression> expList = m.getArguments();
                 for (int i = 0; i < expList.size(); i++) {
                     Expression e = expList.get(i);
                     try{
                         ResolvedType t = e.calculateResolvedType();
                         if (t.isArray() && e.isNameExpr()) {
                             //int short long float double
                             if(t.describe().equals("int")||t.describe().equals("java.lang.Long")){
                                 insertMutant(m,e,0);
                                 insertMutant2(m,e,0);
                                 insertMutant3(m,e,0);
                             }else if(t.describe().equals("char")||t.describe().equals("char[]")) { //char
                                 insertMutant(m,e,1);
                                 insertMutant2(m,e,1);
                                 insertMutant3(m,e,1);
                             }
                         }
                     } catch (Exception uex){
                     // System.err.println(e.toString()+" ,Unsolved Symbol Exception, ignore...");
                    }
                 }
             }
         },null);
     }
     //1.MethodCallExpr
     private void insertMutant(MethodCallExpr m, Expression exp, int flag) {
        //变异数组第一个值为0
        ArrayAccessExpr aaExpr = new ArrayAccessExpr();
        aaExpr.setName(exp);
        aaExpr.setIndex(new IntegerLiteralExpr(0));
        AssignExpr assignExpr;
        if(flag == 0){
            assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
        }else {
            assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
        }
        NameExpr nameExpr = new NameExpr();
        nameExpr.setName(assignExpr.toString()+";" + m.toString());
        outputToFile(m, nameExpr);
     }

     //2.MethodCallExpr
     private void insertMutant2(MethodCallExpr m, Expression exp, int flag){
         //变异数组中间的值
         ArrayAccessExpr aaExpr = new ArrayAccessExpr();
         aaExpr.setName(exp);
         aaExpr.setIndex(new NameExpr(exp.toString()+".length - 1"));
         AssignExpr assignExpr;
         if(flag == 0){
             assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
         }else {
             assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
         }
         NameExpr nameExpr = new NameExpr();
         nameExpr.setName(assignExpr.toString()+";" + m.toString());
         outputToFile(m, nameExpr);
     }

     //3.MethodCallExpr
     private void insertMutant3(MethodCallExpr m, Expression exp, int flag){
         //变异数组最后一个值为0
         ArrayAccessExpr aaExpr = new ArrayAccessExpr();
         aaExpr.setName(exp);
         aaExpr.setIndex(new NameExpr(exp.toString()+".length / 2"));
         AssignExpr assignExpr;
         if(flag == 0){
             assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
         }else {
             assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
         }
         NameExpr nameExpr = new NameExpr();
         nameExpr.setName(assignExpr.toString()+";" + m.toString());
         outputToFile(m, nameExpr);
     }

     //1.ObjectCreationExpr
     private void insertMutant(ObjectCreationExpr m, Expression exp, int flag) {
         //变异数组第一个值为0
         ArrayAccessExpr aaExpr = new ArrayAccessExpr();
         aaExpr.setName(exp);
         aaExpr.setIndex(new IntegerLiteralExpr(0));
         AssignExpr assignExpr;
         if(flag == 0){
             assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
         }else {
             assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
         }
         NameExpr nameExpr = new NameExpr();
         nameExpr.setName(assignExpr.toString()+"; \n" + m.toString());
         outputToFile(m, nameExpr);
     }

     //2.ObjectCreationExpr
     private void insertMutant2(ObjectCreationExpr m, Expression exp, int flag){
         //变异数组中间的值
         ArrayAccessExpr aaExpr = new ArrayAccessExpr();
         aaExpr.setName(exp);
         aaExpr.setIndex(new NameExpr(exp.toString()+".length - 1"));
         AssignExpr assignExpr;
         if(flag == 0){
             assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
         }else {
             assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
         }
         NameExpr nameExpr = new NameExpr();
         nameExpr.setName(assignExpr.toString()+"; \n" + m.toString());
         outputToFile(m, nameExpr);
     }

     //2.ObjectCreationExpr
     private void insertMutant3(ObjectCreationExpr m, Expression exp, int flag){
         //变异数组最后一个值为0
         ArrayAccessExpr aaExpr = new ArrayAccessExpr();
         aaExpr.setName(exp);
         aaExpr.setIndex(new NameExpr(exp.toString()+".length / 2"));
         AssignExpr assignExpr;
         if(flag == 0){
             assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
         }else {
             assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
         }
         NameExpr nameExpr = new NameExpr();
         nameExpr.setName(assignExpr.toString()+"; \n" + m.toString());
         outputToFile(m, nameExpr);
     }


    /**
     * Output CAR mutants to files (MethodCallExpr)
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, NameExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("CAR");
        String mutant_dir = getMuantID("CAR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            CAR_Writer writer = new CAR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(comp_unit);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("CAR: Fails to create " + f_name);
        }
    }

     /**
      * Output CAR mutants to files (ObjectCreationExpr)
      *
      * @param original
      * @param mutant
      */
     public void outputToFile(ObjectCreationExpr original, NameExpr mutant) {
         if (comp_unit == null || currentMethodSignature == null){
             return;
         }
         num++;
         String f_name = getSourceName("CAR");
         String mutant_dir = getMuantID("CAR");
         try {
             PrintWriter out = getPrintWriter(f_name);
             CAR_Writer writer = new CAR_Writer(mutant_dir, out);
             writer.setMutant(original, mutant);
             writer.setMethodSignature(currentMethodSignature);
             writer.writeFile2(comp_unit);
             out.flush();
             out.close();
         }
         catch (IOException e) {
             System.err.println("CAR: Fails to create " + f_name);
         }
     }
}
