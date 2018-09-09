package mjava.gui.main;

import edu.ecnu.sqslab.mjava.MutationSystem;
import javax.swing.*;
import java.awt.event.*;

/**
 * Created by user on 2018/5/5.
 */
public class GenMutantsMain extends JFrame {

    JTabbedPane mutantTabbedPane = new JTabbedPane();

    /** Panel for generating mutants. */
    MutantsGenPanel genPanel;

    /** Panel for viewing details of class mutants. */
    ClassMutantsViewerPanel cvPanel;

    /** Panel for viewing details of traditional mutants.  */
    TraditionalMutantsViewerPanel tvPanel;

    public GenMutantsMain()
    {
        try
        {
            jbInit();
            this.setTitle("MJava");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** <p> Main program for generating mutants (no parameter required for run).</p>
     *  <p>- supporting functions:
     *       (1) selection of Java source files to apply,
     *       (2) selection of mutation operators to apply </p>
     * @throws Exception
     */
    public static void main (String[] args) throws Exception
    {
        System.out.println("The main method starts");
        try {
            MutationSystem.setJMutationStructure();
        }
        catch (NoClassDefFoundError e) {
            System.err.println("[ERROR] Could not find one of the classes necessary to run muJava. Make sure that the .jar file for openjava is in your classpath.");
            System.err.println();
            e.printStackTrace();
            return;
        }
        MutationSystem.recordInheritanceRelation();
        GenMutantsMain main = new GenMutantsMain();
        try {
            main.pack();
        }
        catch (NullPointerException e) {
            System.err.println("[ERROR] An error occurred while initializing muJava. This may have happened because the files used by muJava are in an unexpected state. Try deleting any uncompiled mutants that were generated in the result/ directory, and then re-generate them.");
            System.err.println();
            e.printStackTrace();
            return;
        }
        main.setVisible(true);
    }

    /** <p> Initialize GenMutantsMain </p> */
    private void jbInit() throws Exception
    {
        genPanel = new MutantsGenPanel(this);
        cvPanel = new ClassMutantsViewerPanel();
        tvPanel = new TraditionalMutantsViewerPanel();

        mutantTabbedPane.add("Mutants Generator", genPanel);
        mutantTabbedPane.add("Traditional Mutants Viewer", tvPanel);
        mutantTabbedPane.add("Class Mutants Viewer", cvPanel);
        this.getContentPane().add(mutantTabbedPane);
        this.addWindowListener( new java.awt.event.WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                this_windowClosing(e);
            }
        } );
    }

    void this_windowClosing (WindowEvent e)
    {
        System.exit(0);
    }
}
