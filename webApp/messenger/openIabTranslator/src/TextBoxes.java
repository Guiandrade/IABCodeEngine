
import com.intellij.codeInsight.completion.AllClassesGetter;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.undo.DocumentReferenceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FileBasedIndex;
import com.sun.xml.internal.bind.v2.util.XmlFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static com.intellij.psi.PsiElementFactory.SERVICE.getInstance;


public class TextBoxes extends AnAction {
    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.

    //Old Values
    private final String oldPermission = "<uses-permission android:name=\"com.android.vending.BILLING\" />";
    private final String oldPackage = "package com.android.vending.billing;";
    private final String oldBillingImport  = "import com.android.vending.billing.IInAppBillingService;";
    private final String oldIntent="Intent serviceIntent = new Intent(\"com.android.vending.billing.InAppBillingService.BIND\");";
    private final String oldSetPackage="serviceIntent.setPackage(\"com.android.vending\");";
    private final String fieldName="mService";

    //New Values
    private final String newPermission = "<uses-permission android:name=\"org.onepf.openiab.permission.BILLING\" />";
    private final String newPackage = "org.onepf.oms";
    private final String newBillingImport= "org.onepf.oms.IOpenInAppBillingService";
    private final String newIntent="Intent serviceIntent = new Intent(\"org.onepf.oms.billing.BIND\");";
    private final String newSetPackage="\nserviceIntent.setPackage(\"cm.aptoide.pt\");";
    private final String newFieldValue="IOpenInAppBillingService mService;";


    private List<PsiField> _fieldsToChange = new ArrayList<PsiField>();
    private List<PsiMethod> _methodsToChange = new ArrayList<PsiMethod>();
    private HashMap<PsiElement,PsiJavaFile> _mapToGetFile= new HashMap<PsiElement,PsiJavaFile>();
    private Project _project;
    private PsiJavaFile _javaFile;

    private PsiFile _xmlFileToBeChanged;

    public TextBoxes() {
        // Set the menu item name.
        super("Text _Boxes");
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }


    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        setProject(project);
        changeXml();


        // Translation will be made here
        Processor<PsiClass> processor = new Processor<PsiClass>() {
            @Override
            public boolean process(PsiClass psiClass) {

                PsiJavaFile javaFile;
                javaFile = (PsiJavaFile) psiClass.getContainingFile();
                setJavaFile(javaFile);

                // Import
                changeImport();


                //Fields and Methods
                psiClass.accept(new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        //Elements need to be saved and changed after iteration


                        if (isMethod(element)) {
                            //Methods
                            PsiMethod method = (PsiMethod) element;
                            PsiCodeBlock body = method.getBody();
                            if (body != null){
                                String text = body.getText();
                                if(text.contains(oldIntent) && text.contains(oldSetPackage)){
                                    getMethodsToChange().add(method);
                                    getHashMap().put(method,getJavaFile());
                                }
                            }

                        }
                        else {
                            if (isField(element)) {
                                //Fields
                                PsiField field = (PsiField) element;
                                String fieldType = field.getType().getCanonicalText();
                                String newFieldType = "IOpenInAppBillingService";
                                if (field.getName().equals(fieldName) && !fieldType.equals(newFieldType)) {
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

    private void changeXml() {

        Collection<VirtualFile> xmlFiles = FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME,
                XmlFileType.INSTANCE,
                GlobalSearchScope.allScope(getProject()));

        for (VirtualFile file : xmlFiles) {
            XmlFile xmlFile = (XmlFile) PsiManager.getInstance(getProject()).findFile(file);
            if (xmlFile != null) {
                if (xmlFile.getText().contains(oldPermission)){
                    setXmlFileToBeChanged(xmlFile);
                }
            }
        }

        if (getXmlFileToBeChanged()!= null){
            Runnable modificationRunnable= createPermissionRunnable();
            WriteCommandAction.runWriteCommandAction(getProject(), modificationRunnable);

        }

    }


    public void changeElements(){
        //Modifies methods and fields

        //Methods
        for(PsiMethod method : getMethodsToChange()){
            PsiJavaFile javaFile = getHashMap().get(method);
            changeMethod(javaFile,method);
        }

        //Fields
        for(PsiField field : getFieldsToChange()){
            PsiJavaFile javaFile = getHashMap().get(field);
            changeField(javaFile,field);
        }
    }


    public void changePackage(){
        PsiPackageStatement packStatement = getJavaFile().getPackageStatement();
        if(packStatement == null){
            // do nothing
            return;
        }

        String packageName = packStatement.getText();

        if (packageName.equals(oldPackage)){
            //Fix suggested to an error that appeared when not using Runnable
            Runnable modificationRunnable= createPackageRunnable();
            WriteCommandAction.runWriteCommandAction(getProject(), modificationRunnable);
        }

    }

    public void changeImport(){
        PsiImportList list = getJavaFile().getImportList();
        PsiImportStatementBase[] imports = list.getAllImportStatements();

        for (PsiImportStatementBase importStatement: imports){
            String textImport = importStatement.getText();
            if (textImport.equals(oldBillingImport)){
                Runnable modificationRunnable= createImportRunnable(importStatement);
                WriteCommandAction.runWriteCommandAction(getProject(), modificationRunnable);

            }
        }
    }

    public void changeField(PsiJavaFile javaFile,PsiField field){
            Runnable modificationRunnable= createFieldRunnable(javaFile,field);
            WriteCommandAction.runWriteCommandAction(getProject(), modificationRunnable);
    }

    public void changeMethod(PsiJavaFile javaFile,PsiMethod method){
        Runnable modificationRunnable= createMethodRunnable(javaFile,method);
        WriteCommandAction.runWriteCommandAction(getProject(), modificationRunnable);

    }

    private void addPermission() {
        //Sets correct permission on XML Document.
        String newText = getXmlFileToBeChanged().getText();
        newText = newText.replace(oldPermission,newPermission);
        Document doc = PsiDocumentManager.getInstance(getProject()).getDocument(getXmlFileToBeChanged().getContainingFile());
        doc.setText(newText);
    }

    public void addField(PsiJavaFile javaFile,PsiField field){
        PsiField newField;
        newField = JavaPsiFacade.getElementFactory(getProject()).createFieldFromText(newFieldValue,javaFile);
        field.replace(newField);
            }


    public void addPackage(){
        //Adds package name
        getJavaFile().setPackageName(newPackage);
    }

    public void addImport(PsiImportStatementBase importStatement) {
        PsiImportStatement newStatement;
        newStatement = JavaPsiFacade.getElementFactory(getProject()).createImportStatementOnDemand(newBillingImport);
        importStatement.replace(newStatement);
    }

    public void addMethod(PsiJavaFile javaFile,PsiMethod method){
        PsiCodeBlock body = method.getBody();
        String bodyText = body.getText();

        //Replace intent and setPackage
        bodyText = bodyText.replace(oldIntent,newIntent);
        bodyText = bodyText.replace(oldSetPackage,newSetPackage);

        //Replace body
        PsiCodeBlock newBody = JavaPsiFacade.getElementFactory(getProject()).createCodeBlockFromText(bodyText,javaFile);
        body.replace(newBody);
    }

    private Runnable createPermissionRunnable() {
        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                addPermission();
            }
        };
        return aRunnable;
    }


    private Runnable createPackageRunnable(){

        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                addPackage();
            }
        };
        return aRunnable;
    }

    private Runnable createImportRunnable(PsiImportStatementBase importStatement){

        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                addImport(importStatement);
            }
        };
        return aRunnable;
    }

    private Runnable createFieldRunnable(PsiJavaFile javaFile, PsiField field){

        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                addField(javaFile,field);
            }
        };
        return aRunnable;
    }

    private Runnable createMethodRunnable(PsiJavaFile javaFile,PsiMethod method){

        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                addMethod(javaFile,method);
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

    public PsiElement getXmlFileToBeChanged() {
        return _xmlFileToBeChanged;
    }

    public void setXmlFileToBeChanged(PsiFile _xmlElementToBeChanged) {
        this._xmlFileToBeChanged = _xmlElementToBeChanged;
    }


}

