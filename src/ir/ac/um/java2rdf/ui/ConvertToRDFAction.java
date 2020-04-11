package ir.ac.um.java2rdf.ui;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.ui.content.Content;
import ir.ac.um.java2rdf.Constants;
import ir.ac.um.java2rdf.JavaConverter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;

public class ConvertToRDFAction extends AnAction {
    private JFileChooser fileChooser;
    private ConsoleView consoleView;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Navigatable navigatable = anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
        if (navigatable != null) {
            Project project = anActionEvent.getProject();
            PsiElement psiElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
            String suggestedName = "output";
            if (psiElement instanceof PsiClassImpl) {
                suggestedName = ((PsiClassImpl) psiElement).getName();
            } else if (psiElement instanceof PsiJavaDirectoryImpl) {
                suggestedName = ((PsiJavaDirectoryImpl) psiElement).getName();
            }
            displayFileChooser(suggestedName, project, psiElement);
        }
    }

    private void displayFileChooser(String suggestedName, Project project, PsiElement psiElement) {
        fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        String[] acceptedExtensions = new String[1];
        final String EXTENSION = "n3";
        acceptedExtensions[0] = EXTENSION;
        fileChooser.setFileFilter(new FileNameExtensionFilter("N3 (." + EXTENSION + ")", acceptedExtensions));
        fileChooser.setSelectedFile(new File(suggestedName + "." + EXTENSION));
        int returnVal = fileChooser.showSaveDialog(null);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                if (!outputFile.getName().toLowerCase().endsWith(EXTENSION)) {
                    File outputFile2 = new File(outputFile.getParentFile().getAbsolutePath(),
                            outputFile.getName() + EXTENSION);
                    outputFile.renameTo(outputFile2);
                    outputFile = outputFile2;
                }
                processSelectedElement(project, psiElement, outputFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processSelectedElement(Project project, PsiElement psiElement, File outputFile) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(Constants.PLUGIN_NAME);
        if (consoleView == null) {
            consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
            Content content = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), Constants.PLUGIN_NAME, true);
            toolWindow.getContentManager().addContent(content);
        }
        toolWindow.show(null);
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runReadAction(new JavaConverter(project, psiElement, outputFile, consoleView));
            }
        });

    }

    @Override
    public void update(AnActionEvent anActionEvent) {
        // Set the availability based on whether a project is open and the selected item is a java file or a java directory
        Project project = anActionEvent.getProject();
        PsiElement psiElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        anActionEvent.getPresentation().setEnabledAndVisible(project != null
                && (psiElement instanceof PsiJavaDirectoryImpl || psiElement instanceof PsiClassImpl));
    }

}
