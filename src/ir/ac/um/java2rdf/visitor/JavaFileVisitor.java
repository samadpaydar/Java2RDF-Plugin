package ir.ac.um.java2rdf.visitor;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import com.intellij.psi.*;
import ir.ac.um.java2rdf.Predicates;

public class JavaFileVisitor extends JavaRecursiveElementVisitor {
    private InfModel model;
    private final Resource PROJECT_RESOURCE;
    private final Resource FILE_RESOURCE;

    public JavaFileVisitor(Resource projectResource, InfModel model) {
        this.model = model;
        this.PROJECT_RESOURCE = projectResource;
        this.FILE_RESOURCE = Predicates.createBlankResource(model);
    }

    @Override
    public void visitJavaFile(PsiJavaFile psiJavaFile) {
        super.visitFile(psiJavaFile);

        String path = psiJavaFile.getVirtualFile().getPath();
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.SOURCE_FILE);
        model.add(FILE_RESOURCE, prop, obj);

        Literal literalObject = model.createTypedLiteral(path, XSDDatatype.XSDstring);
        prop = model.createProperty(Predicates.HAS_PATH);
        model.add(FILE_RESOURCE, prop, literalObject);

        prop = model.createProperty(Predicates.HAS_NAME);
        literalObject = model.createTypedLiteral(psiJavaFile.getName(), XSDDatatype.XSDstring);
        model.add(FILE_RESOURCE, prop, literalObject);

        prop = model.createProperty(Predicates.CONTAINS);
        model.add(PROJECT_RESOURCE, prop, FILE_RESOURCE);

        VisitorUtils.processPhysicalLOC(model, FILE_RESOURCE, psiJavaFile.getText());

        PsiClass[] psiClasses = psiJavaFile.getClasses();
        for (PsiClass psiClass : psiClasses) {
            psiClass.accept(new JavaClassVisitor(FILE_RESOURCE, model));
        }
    }

    @Override
    public void visitPackage(PsiPackage aPackage) {
        super.visitElement(aPackage);
        try {
            String nameStr = aPackage.getQualifiedName();
            String subject = Predicates.JAVA2RDF_NAMESPACE + Predicates.prepareNameForResource(nameStr);
            Resource packageResource = model.createResource(subject);
            Property prop = model.createProperty(Predicates.RDF_TYPE);
            Resource obj = model.createResource(Predicates.PACKAGE);
            model.add(packageResource, prop, obj);

            prop = model.createProperty(Predicates.HAS_NAME);
            Literal literalObject = model.createTypedLiteral(aPackage.getQualifiedName(), XSDDatatype.XSDstring);
            model.add(packageResource, prop, literalObject);

            prop = model.createProperty(Predicates.HAS_PACKAGE_DECLARATION);
            model.add(FILE_RESOURCE, prop, packageResource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visitImportList(PsiImportList list) {
        super.visitElement(list);
        PsiImportStatementBase[] importStatements = list.getAllImportStatements();
        int importCount = importStatements.length;
        Property prop = model.createProperty(Predicates.HAS_IMPORT_DECLARATION_COUNT);
        Literal literalObject = model.createTypedLiteral(importCount, XSDDatatype.XSDnonNegativeInteger);
        model.add(FILE_RESOURCE, prop, literalObject);
        for (PsiImportStatementBase importStatement : importStatements) {
            Resource importResource = Predicates.createBlankResource(model);
            prop = model.createProperty(Predicates.RDF_TYPE);
            Resource obj = model.createResource(Predicates.IMPORT);
            model.add(importResource, prop, obj);

            prop = model.createProperty(Predicates.IS_SINGLE_TYPE_IMPORT);
            literalObject = model.createTypedLiteral(!(importStatement.isOnDemand()), XSDDatatype.XSDboolean);
            model.add(importResource, prop, literalObject);

            boolean isStatic = (importStatement instanceof PsiImportStaticStatement);
            prop = model.createProperty(Predicates.IS_STATIC_IMPORT);
            literalObject = model.createTypedLiteral(isStatic, XSDDatatype.XSDboolean);
            model.add(importResource, prop, literalObject);

            prop = model.createProperty(Predicates.IMPORTS);
            String text = importStatement.getImportReference().getQualifiedName();
            text = Predicates.prepareNameForResource(text);
            obj = model.createResource(Predicates.JAVA2RDF_NAMESPACE
                    + text);
            model.add(importResource, prop, obj);

            prop = model.createProperty(Predicates.HAS_IMPORT_DECLARATION);
            model.add(FILE_RESOURCE, prop, importResource);
        }
    }


}
