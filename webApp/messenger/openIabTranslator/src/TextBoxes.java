/**
 * Created by Guilherme Andrade on 10-08-2016.
 */

import com.intellij.codeInsight.completion.AllClassesGetter;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;




public class TextBoxes extends AnAction {
    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.
    public TextBoxes() {
        // Set the menu item name.
        super("Text _Boxes");
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }





    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

        // Translation will be made here
        Processor<PsiClass> processor = new Processor<PsiClass>() {
            @Override
            public boolean process(PsiClass psiClass) {

                PsiJavaFile javaFile;
                javaFile = (PsiJavaFile) psiClass.getContainingFile();

                // Import
                changeImport(javaFile,project);

                //Package
                changePackage(javaFile,project);


                //Fields and Methods
                psiClass.accept(new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        if (isMethod(element)) {
                            //System.out.println("MÉTODO \n"+element.getText());
                        }
                        else if (isField(element)){
                            System.out.println("FIELD \n"+element.getText());
                        }

                        else {
                            super.visitElement(element);
                        }
                    }
                });
                return true;
            }
        };

        // Get all JavaClasses on project folder
        AllClassesGetter.processJavaClasses(
                new PlainPrefixMatcher(""),
                project,
                GlobalSearchScope.projectScope(project),
                processor
        );

/*
        String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        showMessageDialog(project, "Hello, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());
*/

        
    }

    public void changePackage(PsiJavaFile javaFile, Project project){
        PsiPackageStatement packStatement = javaFile.getPackageStatement();
        PsiPackageStatement newStatement;
        String oldPackage = "package com.android.vending.billing;";
        String newPackage = "org.onepf.oms";

        if(packStatement == null){

            newStatement = JavaPsiFacade.getElementFactory(project).createPackageStatement(newPackage);
            javaFile.add(newStatement);
            return;

        }

        String packageName = packStatement.getText();

        if (packageName.equals(oldPackage)){
            newStatement = JavaPsiFacade.getElementFactory(project).createPackageStatement(newPackage);
            packStatement.replace(newStatement);
        }

    }

    public void changeImport(PsiJavaFile javaFile, Project project){
        PsiImportList list = javaFile.getImportList();
        PsiImportStatementBase[] imports = list.getAllImportStatements();
        String oldBillingImport  = "import com.android.vending.billing.IInAppBillingService;";
        String newBillingImport= "import org.onepf.oms.IOpenInAppBillingService;";
        for (PsiImportStatementBase importStatement: imports){

            String textImport = importStatement.getText();
            if (textImport.equals(oldBillingImport)){
                PsiImportStatement newStatement;
                newStatement = JavaPsiFacade.getElementFactory(project).createImportStatementOnDemand(newBillingImport);
                importStatement.replace(newStatement);
            }
        }
    }

    public boolean isMethod(PsiElement element){
        if (element instanceof PsiMethod){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isField(PsiElement element){
        if (element instanceof PsiField){
            return true;
        }
        else{
            return false;
        }
    }

    public void testarAlterar(){
        String oldIntent="Intent serviceIntent=new Intent(\"com.android.vending.billing.InAppBillingService.BIND\");";
        String oldSetPackage="serviceIntent.setPackage(\"com.android.vending\");";
        String newIntent="Intent serviceIntent = new Intent(\"org.onepf.oms.billing.BIND\");";
        String newSetPackage="\r\nserviceIntent.setPackage(\"cm.aptoide.pt\");";
    }

}


