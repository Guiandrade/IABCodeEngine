
import com.intellij.codeInsight.completion.AllClassesGetter;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
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

    IOpenInAppBillingService mService;

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
                            //Change intents
                        }
                        else {
                            if (isField(element)) {
                                PsiField field = (PsiField) element;
                                changeField(javaFile,project,field);

                            } else {
                                super.visitElement(element);
                            }
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

    }

    public void changePackage(PsiJavaFile javaFile, Project project){
        PsiPackageStatement packStatement = javaFile.getPackageStatement();
        String oldPackage = "package com.android.vending.billing;";
        if(packStatement == null){
            // do nothing
            return;
        }

        String packageName = packStatement.getText();

        if (packageName.equals(oldPackage)){
            //Fix suggested to an error that appeared when not using Runnable
            Runnable modificationRunnable= createPackageRunnable(javaFile,project);
            WriteCommandAction.runWriteCommandAction(project, modificationRunnable);
        }

    }

    public void changeImport(PsiJavaFile javaFile, Project project){
        PsiImportList list = javaFile.getImportList();
        PsiImportStatementBase[] imports = list.getAllImportStatements();
        String oldBillingImport  = "import com.android.vending.billing.IInAppBillingService;";

        for (PsiImportStatementBase importStatement: imports){
            String textImport = importStatement.getText();
            if (textImport.equals(oldBillingImport)){
                Runnable modificationRunnable= createImportRunnable(javaFile,project,importStatement);
                WriteCommandAction.runWriteCommandAction(project, modificationRunnable);

            }
        }
    }

    public void changeField(PsiJavaFile javaFile, Project project,PsiField field){
        String fieldName="mService";
        if (field.getName().equals(fieldName)){
            Runnable modificationRunnable= createFieldRunnable(javaFile,project,field);
            WriteCommandAction.runWriteCommandAction(project, modificationRunnable);

        }
    }

    public void addField(PsiJavaFile javaFile, Project project,PsiField field){
        String newFieldValue="IOpenInAppBillingService mService;";
        PsiField newField;
        newField = JavaPsiFacade.getElementFactory(project).createFieldFromText(newFieldValue,javaFile);
        field.replace(newField);
            }


    public void addPackage(PsiJavaFile javaFile,Project project){
        //Adds package name
        String newPackage = "org.onepf.oms";
        javaFile.setPackageName(newPackage);
    }

    public void addImport(PsiJavaFile javaFile, Project project,PsiImportStatementBase importStatement) {
        String newBillingImport= "org.onepf.oms.IOpenInAppBillingService";
        PsiImportStatement newStatement;
        newStatement = JavaPsiFacade.getElementFactory(project).createImportStatementOnDemand(newBillingImport);
        importStatement.replace(newStatement);
    }

    private Runnable createPackageRunnable(PsiJavaFile javaFile,Project project){

        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                addPackage(javaFile,project);
            }
        };
        return aRunnable;
    }

    private Runnable createImportRunnable(PsiJavaFile javaFile,Project project,PsiImportStatementBase importStatement){

        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                addImport(javaFile,project,importStatement);
            }
        };
        return aRunnable;
    }

    private Runnable createFieldRunnable(PsiJavaFile javaFile,Project project,PsiField field){

        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                addField(javaFile,project,field);
            }
        };
        return aRunnable;
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
