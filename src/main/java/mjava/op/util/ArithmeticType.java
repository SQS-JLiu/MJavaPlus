package mjava.op.util;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import edu.ecnu.sqslab.mjava.MutantsGenerator;

/**
 * Created by user on 2018/5/8.
 * @author Jian Liu
 */
public class ArithmeticType {

    /**
     * Determine whether a given expression is of arithmetic type
     * @param ex
     * @return boolean
     */
    public static boolean isArithmeticType(Expression ex){
        try{
//            String type = MutantsGenerator.getJavaParserFacade().getType(ex).describe();
            String type = ex.calculateResolvedType().describe();
            if(type.contains("int")||type.contains("Long")||type.contains("double")
                    ||type.contains("float")||type.contains("short") ||type.contains("char")
                    || type.contains("byte")){
                return  true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    public static boolean isCompareOperator(BinaryExpr.Operator operator){
        if(operator.equals(BinaryExpr.Operator.EQUALS) ||
                operator.equals(BinaryExpr.Operator.NOT_EQUALS) ||
                operator.equals(BinaryExpr.Operator.LESS) ||
                operator.equals(BinaryExpr.Operator.LESS_EQUALS) ||
                operator.equals(BinaryExpr.Operator.GREATER) ||
                operator.equals(BinaryExpr.Operator.GREATER_EQUALS)){
                return true;
        }
        return false;
    }

    public  static boolean isLogicalOperator(BinaryExpr.Operator operator){
        if(operator.equals(BinaryExpr.Operator.AND) ||
                operator.equals(BinaryExpr.Operator.OR) ||
                operator.equals(BinaryExpr.Operator.BINARY_AND) ||
                operator.equals(BinaryExpr.Operator.BINARY_OR) ||
                operator.equals(BinaryExpr.Operator.XOR)){
            return true;
        }
        return false;
    }
}
