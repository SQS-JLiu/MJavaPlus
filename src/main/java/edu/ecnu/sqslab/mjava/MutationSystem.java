package edu.ecnu.sqslab.mjava;

import mjava.util.DirFileFilter;
import mjava.util.ExtensionFilter;
import mjava.util.InheritanceINFO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

/**
 * Created by user on 2018/5/5.
 */
public class MutationSystem {
    public static final int INTERFACE = 0;
    public static final int ABSTRACT = 1;
    public static final int GUI = 2;
    public static final int MAIN = 3;
    public static final int MAIN_ONLY = 4;
    public static final int NORMAL = 5;
    public static final int APPLET = 6;

    public static final int CM = 0; // Class Mutation Operator
    public static final int TM = 1; // Traditional Mutation Operator
    public static final int EM = 2; // Exceptional Mutation Operator


    /** home path where inputs and output of mjava system are located*/
    public static String SYSTEM_HOME = System.getProperty("user.dir");

    /** path of Java source files which mutation is applied to  */
    public static String SRC_PATH = SYSTEM_HOME + "/src";

    /** path of classes of Java source files at SRC_PATH directory */
    public static String CLASS_PATH = SYSTEM_HOME + "/classes";

    /** home path which mutants are put into */
    public static String MUTANT_HOME = SYSTEM_HOME + "/result";

    /** path which class mutants are put into */
    public static String CLASS_MUTANT_PATH = "";

    /** path which traditional mutants are put into */
    public static String TRADITIONAL_MUTANT_PATH = "";

    /** path which exception-related mutants are put into */
    public static String EXCEPTION_MUTANT_PATH = "";

    /** ??? absolute path for ???*/
    public static String MUTANT_PATH = "";

    /** ??? absolute path for the original Java source*/
    public static String ORIGINAL_PATH = "";

    /** class name without package name that mutation is applied into */
    public static String CLASS_NAME;

    /** path for */
    public static String DIR_NAME;

    /** directory name for class mutants */
    public static String CM_DIR_NAME = "class_mutants";

    /** directory name for exception-related mutants */
    public static String EM_DIR_NAME = "exception_mutants";

    /** directory name for traditional mutants */
    public static String TM_DIR_NAME = "traditional_mutants";

    /** directory name for original class */
    public static String ORIGINAL_DIR_NAME = "original";

    public static String LOG_IDENTIFIER = ":";

    /** List of names of class mutation operators */
    public static String[] cm_operators = {"AMC"};

    /** List of names of traditional mutation operators */
    //public static String[] tm_operators = {"ARGR","SCR","CER","CAR","FLR","ROR","COR"};
    public static String[] tm_operators = {"ARGR","CER","CAR","FLR","UPR","ROR"};

    /** List of names of exception-related mutation operators */
    public static String[] em_operators = {"xxx"};

    /**Default is false to mutate all Java file,
     * Setting True is only mutating java file that have thread calls.*/
    public static boolean ThreadFile = true;


    /**
     * Return type of class.
     * @return type of class ( types: interface, abstract, GUI, main, normal, applet )
     */
    public static int getClassType (String class_name)
    {
        try
        {
            Class c = Class.forName (class_name);
            if (c.isInterface())
                return INTERFACE;

            if (Modifier.isAbstract(c.getModifiers()))
                return ABSTRACT;

            Method[] ms = c.getDeclaredMethods();

            if (ms != null)
            {
                if ( (ms.length == 1) && (ms[0].getName().equals("main")))
                    return MAIN_ONLY;

                for (int i=0; i<ms.length; i++)
                {
                    if (ms[i].getName().equals("main"))
                        return MAIN;
                }
            }

            if (isGUI(c))       return GUI;
            if (isApplet(c))    return APPLET;

            return NORMAL;
        }
        catch(Exception e)  {   return -1;   }
        catch(Error e)  {   return -1;  }
    }


    /** Examine if class <i>c</i> is a GUI class */
    private static boolean isGUI (Class c)
    {
        while (c != null)
        {
            if ( (c.getName().indexOf("java.awt") == 0) ||
                    (c.getName().indexOf("javax.swing") == 0) )
                return true;

            c = c.getSuperclass();
            if (c.getName().indexOf("java.lang") == 0)
                return false;
        }
        return false;
    }


    /** Examine if class <i>c</i> is an applet class */
    private static boolean isApplet(Class c)
    {
        while (c != null)
        {
            if ( c.getName().indexOf("java.applet")==0)
                return true;

            c = c.getSuperclass();
            if (c.getName().indexOf("java.lang")==0)
                return false;
        }
        return false;
    }


    /**  Inheritance Informations  */
    public static InheritanceINFO[] classInfo = null;


    /** Clear arranged original file made before*/
    private static void clearPreviousOriginalFiles()
    {
        File original_class_dir = new File(MutationSystem.ORIGINAL_PATH);
        int i;
        File[] old_files = original_class_dir.listFiles();

        if (old_files == null)        return;

        for (i=0; i<old_files.length; i++)
        {
            old_files[i].delete();
        }
    }


    /* Clear mutants generated from previous run (in class_mutants folder)*/
    static void clearPreviousMutants(String path)
    {
        File mutant_classes_dir = new File(path);
        int i;
        // delete previous mutant files
        File[] old_files = mutant_classes_dir.listFiles();

        if (old_files==null)         return;

        for (i=0; i<old_files.length; i++)
        {
            if (old_files[i].isDirectory())
            {
                File[] old_mutants = old_files[i].listFiles();
                for (int j=0; j<old_mutants.length; j++)
                {
                    old_mutants[j].delete();
                }
            }
            old_files[i].delete();
        }
    }


    /* Clear mutants generated from previous run (in traditional_mutants folder)*/
    static void clearPreviousTraditionalMutants(String path)
    {
        File traditional_mutant_dir = new File(path);
        int i;
        // delete previous mutant files
        File[] methods = traditional_mutant_dir.listFiles();

        if (methods == null)
            return;

        for (i=0; i<methods.length; i++)
        {
            if (methods[i].isDirectory())
            {
                File[] mutant_dir = methods[i].listFiles();

                for (int j=0; j<mutant_dir.length; j++)
                {
                    if (mutant_dir[j].isDirectory())
                    {
                        File[] old_mutants = mutant_dir[j].listFiles();
                        for (int k=0; k<old_mutants.length; k++)
                        {
                            old_mutants[k].delete();
                        }
                    }
                    mutant_dir[j].delete();
                }
            }
            methods[i].delete();
        }
    }

    /** Delete all traditional mutants generated before */
    public static void clearPreviousTraditionalMutants()
    {
        clearPreviousTraditionalMutants(MutationSystem.TRADITIONAL_MUTANT_PATH);
    }

    /** Delete all class mutants generated before */
    public static void clearPreviousClassMutants()
    {
        clearPreviousMutants(MutationSystem.CLASS_MUTANT_PATH);
    }

    /** Delete all mutants generated before */
    public static void clearPreviousMutants()
    {
        clearPreviousOriginalFiles();
        clearPreviousClassMutants();
        clearPreviousTraditionalMutants();
    }

    /* Set up target files (stored in src folder) to be tested */
    public static Vector getNewTragetFiles()
    {
        Vector targetFiles = new Vector();
        getJavacArgForDir (MutationSystem.SRC_PATH, "", targetFiles);
        return targetFiles;
    }

    protected static String getJavacArgForDir (String dir, String str, Vector targetFiles)
    {
        String result = str;
        String temp = "";

        File dirF = new File(dir);
        File[] javaF = dirF.listFiles (new ExtensionFilter("java"));
        if (javaF.length > 0)
        {
            result = result + dir + "/*.java ";

            for (int k=0; k<javaF.length; k++)
            {
                temp = javaF[k].getAbsolutePath();
                temp.replace('\\', '/');
                targetFiles.add(temp.substring(MutationSystem.SRC_PATH.length()+1, temp.length()));
            }
        }

        File[] sub_dir = dirF.listFiles (new DirFileFilter());
        if (sub_dir.length == 0)    return result;

        for (int i=0; i<sub_dir.length; i++)
        {
            result = getJavacArgForDir(sub_dir[i].getAbsolutePath(), result, targetFiles);
        }
        return result;
    }

    /** Return list of class names = class name of <i>dir</i> directory + <i>result</i>*/
    public static String[] getAllClassNames(String[] result,String dir)
    {
        String[] classes;
        String temp;
        File dirF = new File(dir);

        File[] classF = dirF.listFiles (new ExtensionFilter("class"));
        if (classF != null)
        {
            classes = new String[classF.length];
            for(int k=0; k<classF.length; k++)
            {
                temp = classF[k].getAbsolutePath();
                classes[k] = temp.substring(MutationSystem.CLASS_PATH.length()+1, temp.length()-".class".length());
                classes[k] = classes[k].replace('\\', '.');
                classes[k] = classes[k].replace('/', '.');
            }
            result = addClassNames(result, classes);
        }

        File[] sub_dir = dirF.listFiles(new DirFileFilter());
        if (sub_dir == null)       return result;

        for (int i=0; i<sub_dir.length; i++)
        {
            result = getAllClassNames(result,sub_dir[i].getAbsolutePath());
        }
        return result;
    }


    /** Return combine list of <i> list1 </i> and <i> list2</i> lists.
     *  @param list1 String list
     *  @param list2 String list
     *  @return combined list of list1 and list2*/
    private static final String[] addClassNames(String[] list1, String[] list2)
    {
        if (list1 == null)
            list1 = list2;
        else
        {
            int num = list1.length;
            String[] temp = new String[num];

            for (int i=0; i<temp.length; i++)
            {
                temp[i] = list1[i];
            }

            num = num + list2.length;
            list1 = new String[num];

            for (int i=0; i<temp.length; i++)
            {
                list1[i] = temp[i];
            }

            for (int i=temp.length; i<num; i++)
            {
                list1[i] = list2[i-temp.length];
            }
        }
        return list1;
    }

    /** Return inheritance information for give class <br>
     *  @param class_name name of class
     *  @return inheritance information
     */
    public static InheritanceINFO getInheritanceInfo(String class_name)
    {
        for (int i=0; i<classInfo.length; i++)
        {
            if (classInfo[i].getClassName().equals(class_name))
                return classInfo[i];
        }
        return null;
    }

    /** Recognize inheritance relation of all classes located at CLASS_PATH directory. <br>
     *  <b>* CAUTION: </b> this function should be called before
     *  using <i>MutantsGenerator</i> or sub-classes of <i>MutantsGenerator</i>.
     */
    public static void recordInheritanceRelation() throws Exception
    {
        String[] classes = null;
        classes = MutationSystem.getAllClassNames(classes, MutationSystem.CLASS_PATH);

        if (classes == null)
        {
            System.err.println("[ERROR] There are no classes to mutate.");
            System.err.println(" Please check the directory  " + MutationSystem.CLASS_PATH + " and be sure that MJava_HOME is set correctly (without a trailing slash) in mjava.config.");
            Runtime.getRuntime().exit(0);
        }
        classInfo = new InheritanceINFO[classes.length];

        boolean[] bad = new boolean[classes.length];

        for (int i = 0; i < classes.length; i++)
        {
            bad[i] = false;
            try
            {
                String classpath = System.getProperty("java.class.path");
                //add the class path dynamically
                addURL(MutationSystem.CLASS_PATH);
                //create a new class from the class name
                Class c = Class.forName(classes[i]);
                //create the parent class of the class above
                Class parent = c.getSuperclass();

                if ( (parent == null) || (parent.getName().equals("java.lang.Object")) )
                {
                    classInfo[i] = new InheritanceINFO(classes[i], "");
                }
                else
                {
                    classInfo[i] = new InheritanceINFO(classes[i], parent.getName());
                }
            } catch (ClassNotFoundException e)
            {
                System.err.println("[ERROR] Can't find the class: " + classes[i]);
                System.err.println("Please check that the compiled class for the code you want to mutate is in the classes/ directory. Also check that the MuJava_HOME variable in mujava.config does not end with a trailing slash. " );
                bad[i] = true;
                classInfo[i] = new InheritanceINFO(classes[i], "");
                Runtime.getRuntime().exit(0);
            } catch (Error er)
            {
                // Sometimes error occurred. However, I can't solve..
                // To muJava users: try do your best to solve it. ^^;
                System.out.println ("[ERROR] for class " + classes[i] + " => "+ er.getMessage() );
                bad[i] = true;
                classInfo[i] = new InheritanceINFO(classes[i], "");
            }
        }

        for (int i=0; i<classes.length; i++)
        {
            if (bad[i])
                continue;

            String parent_name = classInfo[i].getParentName();

            for (int j=0; j<classes.length; j++)
            {
                if (bad[j])
                    continue;

                if (classInfo[j].getClassName().equals(parent_name))
                {
                    classInfo[i].setParent(classInfo[j]);
                    classInfo[j].addChild(classInfo[i]);
                    break;
                }
            }
        }
    }

    /**
     * add the class path dynamically
     * @param classPath
     * @throws Exception
     */

    public static void addURL(String classPath) throws Exception {
        Method addClass = null;
        ClassLoader cl = null;
        File f = null;

        addClass = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
        addClass.setAccessible(true);
        f = new File(classPath);
        cl = ClassLoader.getSystemClassLoader();
        addClass.invoke(cl, new Object[] { f.toURL() });
    }

    /** Re-setting MJava structure for give class name <br>*/
    public static void setJMutationPaths(String whole_class_name)
    {
        int temp_start = whole_class_name.lastIndexOf(".") + 1;
        if (temp_start < 0)
        {
            temp_start=0;
        }

        int temp_end = whole_class_name.length();
        MutationSystem.CLASS_NAME = whole_class_name.substring(temp_start, temp_end);
        MutationSystem.DIR_NAME   = whole_class_name;
        MutationSystem.ORIGINAL_PATH = MutationSystem.MUTANT_HOME
                + "/" + whole_class_name + "/" + MutationSystem.ORIGINAL_DIR_NAME;
        MutationSystem.CLASS_MUTANT_PATH = MutationSystem.MUTANT_HOME
                + "/" + whole_class_name + "/" + MutationSystem.CM_DIR_NAME;
        MutationSystem.TRADITIONAL_MUTANT_PATH = MutationSystem.MUTANT_HOME
                + "/" + whole_class_name + "/" + MutationSystem.TM_DIR_NAME;
        MutationSystem.EXCEPTION_MUTANT_PATH = MutationSystem.MUTANT_HOME
                + "/" + whole_class_name + "/" + MutationSystem.EM_DIR_NAME;

    }

    /** <b> Default mjava system structure setting function </b>
     * <p> Recognize file structure for mutation system based on "mjava.config". </p>
     *  <p> ** CAUTION : this function or `setJMutationStructure(String home_path)' should be called before generating and running mutants. */
    public static void setJMutationStructure()
    {
        try
        {
            File f = new File (MutationSystem.SYSTEM_HOME + "/mjava.config");
            FileReader r = new FileReader(f);
            BufferedReader reader = new BufferedReader(r);
            String str = reader.readLine();
            String home_path = str.substring("MJava_HOME=".length(), str.length());
            SYSTEM_HOME = home_path;
            SRC_PATH = home_path + "/src";
            CLASS_PATH = home_path + "/classes";
            MUTANT_HOME = home_path + "/result";
        } catch (FileNotFoundException e1)
        {
            System.err.println("[ERROR] Can't find mjava.config file");
            e1.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** <p> Recognize file structure for mutation system from not "mjava.config" but from user directly </p>*/
    public static void setJMutationStructure(String home_path)
    {
        SYSTEM_HOME = home_path;
        SRC_PATH = home_path + "/src";
        CLASS_PATH = home_path + "/classes";
        MUTANT_HOME = home_path + "/result";
    }

    /*
     * Lin add for setting sessions
     *
     */
    public static void setJMutationStructureAndSession (String sessionName)
    {
        try
        {
            File f = new File (MutationSystem.SYSTEM_HOME + "/mujava.config");
            FileReader r = new FileReader(f);
            BufferedReader reader = new BufferedReader(r);
            String str = reader.readLine();
            String home_path = str.substring("MJava_HOME=".length(), str.length());
            home_path = home_path+ "/" + sessionName;
            SYSTEM_HOME = home_path;
            SRC_PATH = home_path + "/src";
            CLASS_PATH = home_path + "/classes";
            MUTANT_HOME = home_path + "/result";
        } catch (FileNotFoundException e1)
        {
            System.err.println("[ERROR] Can't find mjava.config file");
            e1.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}