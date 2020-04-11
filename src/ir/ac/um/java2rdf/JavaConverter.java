package ir.ac.um.java2rdf;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import ir.ac.um.java2rdf.visitor.JavaFileVisitor;

import java.io.*;
import java.util.List;

public class JavaConverter implements Runnable {
    private PsiElement psiElement;
    private Project project;
    private Resource projectResource;
    private InfModel model;
    private int numberOfItemsToBeProcessed;
    private int itemNumber = 0;
    private File outputFile;
    private ConsoleView consoleView;

    public JavaConverter(Project project, PsiElement psiElement, File outputFile, ConsoleView consoleView) {
        this.psiElement = psiElement;
        this.project = project;
        this.outputFile = outputFile;
        this.consoleView = consoleView;
    }

    @Override
    public void run() {
        update(String.format("Conversion phase started.%n"));
        long start = System.currentTimeMillis();
        itemNumber = 0;
        try {
            update(String.format("Estimating number of items to be processed ...%n"));
            numberOfItemsToBeProcessed = count(psiElement);
            model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
            addNamespaces(model);
            convert();
            doInference(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (model != null) {
                model.close();
            }
        }
        long finish = System.currentTimeMillis();
        update(String.format("Conversion phase finished. (total time: %s)%n", getFriendlyTime(finish - start)));
    }

    private void update(String message) {
        consoleView.print(message,
                ConsoleViewContentType.NORMAL_OUTPUT);
    }

    private void process(PsiJavaFile psiJavaFile) {
        itemNumber++;
        update(String.format("[%d / %d] Processing %s ...%n",
                itemNumber, numberOfItemsToBeProcessed, psiJavaFile.getName()));
        psiJavaFile.accept(new JavaFileVisitor(projectResource, model));
    }

    private void processDirectory(PsiDirectory psiDirectory) {
        PsiDirectory[] directories = psiDirectory.getSubdirectories();
        for (PsiDirectory directory : directories) {
            processDirectory(directory);
        }

        PsiFile[] innerFiles = psiDirectory.getFiles();
        for (PsiFile innerFile : innerFiles) {
            if (innerFile instanceof PsiJavaFileImpl) {
                PsiJavaFileImpl psiJavaFile = (PsiJavaFileImpl) innerFile;
                process(psiJavaFile);
            }
        }
    }

    private void processProject() {
        String projectName = project.getName();
        projectResource = Predicates.getResourceForProject(model, projectName);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.PROJECT);
        model.add(projectResource, prop, obj);

        prop = model.createProperty(Predicates.HAS_NAME);
        Literal literalObject = model.createTypedLiteral(projectName, XSDDatatype.XSDstring);
        model.add(projectResource, prop, literalObject);
    }

    private int count(PsiElement element) {
        int count = 0;
        if (element instanceof PsiJavaFileImpl || element instanceof PsiClassImpl) {
            count = 1;
        } else if (element instanceof PsiJavaDirectoryImpl) {
            PsiJavaDirectoryImpl psiJavaDirectory = (PsiJavaDirectoryImpl) element;
            PsiElement[] children = psiJavaDirectory.getChildren();
            for (PsiElement child : children) {
                count += count(child);
            }
        }
        return count;
    }

    private void convert() {
        processProject();

        if (psiElement instanceof PsiJavaFileImpl || psiElement instanceof PsiClassImpl) {
            PsiFile file = psiElement.getContainingFile();
            if (file instanceof PsiJavaFile) {
                PsiJavaFile psiJavaFile = (PsiJavaFile) file;
                process(psiJavaFile);
            }
        } else if (psiElement instanceof PsiJavaDirectoryImpl) {
            PsiJavaDirectoryImpl psiJavaDirectory = (PsiJavaDirectoryImpl) psiElement;
            processDirectory(psiJavaDirectory);
        }
    }

    private void addNamespaces(Model theModel) {
        String[] prefixes = {
                Predicates.RDF_PREFIX, Predicates.RDFS_PREFIX,
                Predicates.XSD_PREFIX, Predicates.JAVA2RDF_PREFIX,
        };
        String[] namespaces = {
                Predicates.RDF_NAMESPACE, Predicates.RDFS_NAMESPACE,
                Predicates.XSD_NAMESPACE, Predicates.JAVA2RDF_NAMESPACE,
        };

        for (int i = 0; i < prefixes.length; i++) {
            theModel.setNsPrefix(prefixes[i], namespaces[i]);
        }
    }

    private void loadOntology() {
        try {
            final PluginId pluginId = PluginId.getId(Constants.PLUGIN_ID);
            final IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
            if (pluginDescriptor != null) {
                final ClassLoader classLoader = pluginDescriptor.getPluginClassLoader();
                model.read(classLoader.getResource("ontology/java2rdf.owl").toURI().toString(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doInference(File outputFile) {
        try {
            update(String.format("Processing inference rules ...%n"));
            List rules = Rule.rulesFromURL(this.getClass().getResource("rules.txt").toURI().toString());
            Reasoner reasoner = new GenericRuleReasoner(rules);
            //loadOntology();
            InfModel inf = ModelFactory.createInfModel(reasoner, model);
            addNamespaces(inf);
            FileWriter writer = new FileWriter(outputFile);
            inf.write(writer, "N3");
            inf.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFriendlyTime(long timeInMilliSeconds) {
        long timeInSeconds = timeInMilliSeconds / 1000;
        if (timeInMilliSeconds < 1000) {
            return String.format("%d milli second%s", timeInMilliSeconds, (timeInMilliSeconds > 1 ? "s" : ""));
        } else if (timeInSeconds < 60) {
            return String.format("%d second%s", timeInSeconds, (timeInSeconds > 1 ? "s" : ""));
        } else if (timeInSeconds == 60) {
            return "1 minute";
        } else if (timeInSeconds < 60 * 60) {
            int minutes = (int) timeInSeconds / 60;
            int seconds = (int) timeInSeconds % 60;
            return String.format("%d minute%s %d second%s", minutes, (minutes > 1 ? "s" : ""), seconds, (seconds > 1 ? "s" : ""));
        } else if (timeInSeconds == 60 * 60) {
            return "1 hour";
        } else {
            int hours = (int) timeInSeconds / (60 * 60);
            int remaining = (int) timeInSeconds % (60 * 60);
            String part2 = "";
            if (remaining > 0) {
                part2 = getFriendlyTime(remaining * 1000);
            }
            return String.format("%d hour%s %s", hours, (hours > 1 ? "s" : ""), part2);
        }
    }

}
