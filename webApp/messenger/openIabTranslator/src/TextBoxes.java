
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TextBoxes extends AnAction {
    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.



    private List<PsiField> _fieldsToChange = new ArrayList<PsiField>();
    private List<PsiMethod> _methodsToChange = new ArrayList<PsiMethod>();
    private HashMap<PsiElement,PsiJavaFile> _mapToGetFile= new HashMap<PsiElement,PsiJavaFile>();
    private Project _project;
    private PsiJavaFile _javaFile;

    public TextBoxes() {
        // Set the menu item name.
        super("Text _Boxes");
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }


    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        setProject(project);

        // Translation will be made here
        Processor<PsiClass> processor = new Processor<PsiClass>() {
            @Override
            public boolean process(PsiClass psiClass) {

                PsiJavaFile javaFile;
                javaFile = (PsiJavaFile) psiClass.getContainingFile();
                setJavaFile(javaFile);

                // Import
                changeImport(javaFile,project);

                //Package
                changePackage(javaFile,project);


                //Fields and Methods
                psiClass.accept(new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        //Elements need to be saved and changed after iteration
                        String fieldName="mService";
                        String oldIntent="Intent serviceIntent = new Intent(\"com.android.vending.billing.InAppBillingService.BIND\");";
                        String oldSetPackage="serviceIntent.setPackage(\"com.android.vending\");";

                        if (isMethod(element)) {
                            //Methods
                            PsiMethod method = (PsiMethod) element;
                            PsiCodeBlock body = method.getBody();
                            if (body != null){
                                String text = body.getText();
                                if(text.contains(oldIntent) && text.contains(oldSetPackage)){
                                    System.out.println("CHEGA PARA ALTERAR O INTENT E O SETPACKAGE.");
                                    getMethodsToChange().add(method);
                                    getHashMap().put(method,getJavaFile());
                                }
                            }

                        }
                        else {
                            if (isField(element)) {
                                //Fields
                                PsiField field = (PsiField) element;
                                if (field.getName().equals(fieldName)) {
                                    getFieldsToChange().add(field);
                                    getHashMap().put(field,getJavaFile());
                                }

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

        //Changes Elements saved on iteration
        changeElements();

    }

    public void changeElements(){
        //Modifies methods and fields

        //Methods
        for(PsiMethod method : getMethodsToChange()){
            PsiJavaFile javaFile = getHashMap().get(method);
            changeMethod(javaFile,getProject(),method);
        }

        //Fields
        for(PsiField field : getFieldsToChange()){
            PsiJavaFile javaFile = getHashMap().get(field);
            changeField(javaFile,getProject(),field);
        }
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
            Runnable modificationRunnable= createFieldRunnable(javaFile,project,field);
            WriteCommandAction.runWriteCommandAction(project, modificationRunnable);
    }

    public void changeMethod(PsiJavaFile javaFile, Project project,PsiMethod method){
        Runnable modificationRunnable= createMethodRunnable(javaFile,project,method);
        WriteCommandAction.runWriteCommandAction(project, modificationRunnable);

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

    public void addMethod(PsiJavaFile javaFile, Project project,PsiMethod method){
        String oldIntent="Intent serviceIntent=new Intent(\"com.android.vending.billing.InAppBillingService.BIND\");";
        String oldSetPackage="serviceIntent.setPackage(\"com.android.vending\");";
        String newIntent="Intent serviceIntent = new Intent(\"org.onepf.oms.billing.BIND\");";
        String newSetPackage="\r\nserviceIntent.setPackage(\"cm.aptoide.pt\");";
        PsiCodeBlock body = method.getBody();
        String bodyText = body.getText();

        //Replace intent and setPackage
        bodyText = bodyText.replace(oldIntent,newIntent);
        bodyText = bodyText.replace(oldSetPackage,newSetPackage);

        //Replace body
        PsiCodeBlock newBody = JavaPsiFacade.getElementFactory(project).createCodeBlockFromText(bodyText,javaFile);
        body.replace(newBody);
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

    private Runnable createMethodRunnable(PsiJavaFile javaFile,Project project,PsiMethod method){

        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                addMethod(javaFile,project,method);
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

    public List<PsiField> getFieldsToChange() {
        return _fieldsToChange;
    }

    public List<PsiMethod> getMethodsToChange() {
        return _methodsToChange;
    }

    public Project getProject() {
        return _project;
    }

    public void setProject(Project project) {
        this._project = project;
    }


    public PsiJavaFile getJavaFile() {
        return _javaFile;
    }

    public void setJavaFile(PsiJavaFile javaFile) {
        this._javaFile = javaFile;
    }

    public HashMap<PsiElement, PsiJavaFile> getHashMap() {
        return _mapToGetFile;
    }

}
