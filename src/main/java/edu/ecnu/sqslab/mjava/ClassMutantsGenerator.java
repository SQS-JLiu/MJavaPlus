/**
 * Copyright (C) 2018  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
 
package edu.ecnu.sqslab.mjava;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import mjava.op.classop.AMC;
import mjava.op.util.CodeChangeLog;
import mjava.op.util.Mutator;

import java.io.File;
import java.util.List;

/**
 * <p>Generate class mutants according to selected
 *            class mutation operator(s) from gui.GenMutantsMain.
 *            The original version is loaded, mutated, and compiled.
 *            Outputs (mutated source and class files) are in the
 *            class-mutants folder. </p>
 *
 * <p> Currently available class mutation operators:
 *          (1) AMC: Access modifier change,
 * </p>
 * @author Jian Liu
 * @version 1.0
*/

public class ClassMutantsGenerator  extends MutantsGenerator
{
   String[] classOp;

   public ClassMutantsGenerator (File f) 
   {
      super(f);
      classOp = MutationSystem.cm_operators;
   }

   public ClassMutantsGenerator (File f, String[] cOP)
   {
      super(f);
      classOp = cOP;
   }
   
   /** 
    * Verify if the target Java source and class files exist, 
    * generate class mutants
    */
   void genMutants()
   {
      if (comp_unit == null)
      {
         System.err.println(original_file + " is skipped.");
         return;
      }
     List<ClassOrInterfaceDeclaration> cdecls = comp_unit.getNodesByType(ClassOrInterfaceDeclaration.class);
      
      if (cdecls == null || cdecls.size() == 0)    
         return;

      if (classOp != null && classOp.length > 0)
      {
         System.out.println("* Generating class mutants");
         MutationSystem.clearPreviousClassMutants();
         MutationSystem.MUTANT_PATH = MutationSystem.CLASS_MUTANT_PATH;
         CodeChangeLog.openLogFile();
         genClassMutants(cdecls);
         CodeChangeLog.closeLogFile();
      }
   }
 
   /**
    * Apply selected class mutation operators
    */
   void genClassMutants (List<ClassOrInterfaceDeclaration> cdecls)
   {
//      System.out.println("No Class Mutation operators.");
      for (int j=0; j<cdecls.size(); ++j) {
            ClassOrInterfaceDeclaration cdecl = cdecls.get(j);
         if (cdecl.getNameAsString().equals(MutationSystem.CLASS_NAME)){
            Mutator mutant_op;
            if (hasOperator(classOp, "AMC"))
            {
               System.out.println("Applying AMC ... ... ");
               mutant_op = new AMC(comp_unit);
               comp_unit.accept(mutant_op,null);
               System.out.println("AMC are handled.");
            }
         }
      }
   }
}