package samples;


import edu.ecnu.sqslab.mjava.TraditionalMutantsGenerator;

import java.io.File;

/**
 * Created by user on 2018/5/5.
 */
public class ParserTest {

     public static void  main(String args[]){
        new TraditionalMutantsGenerator(new File("E:\\uJava\\src\\Test.java")).generateParseTree();
    }
}
