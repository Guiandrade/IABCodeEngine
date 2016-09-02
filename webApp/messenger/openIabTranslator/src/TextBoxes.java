/**
 * Created by guilhermeandraade on 10-08-2016.
 */

import com.intellij.codeInsight.completion.AllClassesGetter;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;

import static com.intellij.openapi.ui.Messages.showMessageDialog;


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
        // Explorar metodos para o project
        // Descobrir que plugin ou api usar para fazer parse de Java -> Roaster outra vez ou Java Parser?


        // Translation will be made here
        Processor<PsiClass> processor = new Processor<PsiClass>() {
            @Override
            public boolean process(PsiClass psiClass) {
                // Do work here
                System.out.println(psiClass.getText());
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

/*      Iterador sobre os elementos todos para alterarmos apenas os necessários.

        constructor.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (isMyStatement(element)) {
                    // process statement
                } else {
                    super.visitElement(element);
                }
            }
        });

*/
        String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        showMessageDialog(project, "Hello, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());
        
    }
}

