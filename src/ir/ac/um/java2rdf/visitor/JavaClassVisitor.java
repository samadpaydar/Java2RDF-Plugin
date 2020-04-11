package ir.ac.um.java2rdf.visitor;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import com.intellij.psi.*;
import ir.ac.um.java2rdf.Predicates;

public class JavaClassVisitor extends JavaRecursiveElementVisitor {
    private InfModel model;
    private final Resource FILE_RESOURCE;

    public JavaClassVisitor(Resource fileResource, InfModel model) {
        this.model = model;
        this.FILE_RESOURCE = fileResource;
    }

    @Override
    public void visitClass(PsiClass aClass) {
        super.visitClass(aClass);
        String name = aClass.getQualifiedName();
        if (name == null) {
            //the class is anonymous class, it will be processed somewhere else
            return;
        }
        boolean isInterface = aClass.isInterface();
        boolean isEnum = aClass.isEnum();
        String simpleName = aClass.getQualifiedName();
        String subject = Predicates.JAVA2RDF_NAMESPACE + Predicates.prepareNameForResource(name);
        ;
        Resource classResource = model.createResource(subject);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource((!isInterface && !isEnum) ? Predicates.CLASS : (isInterface ? Predicates.INTERFACE : Predicates.ENUM));
        model.add(classResource, prop, obj);

        prop = model.createProperty(Predicates.IS_DEFINED_IN);
        model.add(classResource, prop, FILE_RESOURCE);

        prop = model.createProperty(Predicates.HAS_NAME);
        Literal literalObject = model.createTypedLiteral(simpleName, XSDDatatype.XSDstring);
        model.add(classResource, prop, literalObject);

        VisitorUtils.processClassSuperClasses(model, classResource, aClass);
        VisitorUtils.processInterfacesImplementedByClass(model, classResource, aClass);
        VisitorUtils.processModifiers(model, classResource, aClass.getModifiers());
        VisitorUtils.processAnnotations(model, classResource, aClass.getAnnotations());
        VisitorUtils.processDocComment(model, classResource, aClass.getDocComment());

        PsiField[] psiFields = aClass.getFields();
        for(PsiField psiField : psiFields) {
            psiField.accept(new JavaFieldVisitor(classResource, model));
        }

        PsiMethod[] psiMethods = aClass.getMethods();
        for(PsiMethod psiMethod: psiMethods) {
            psiMethod.accept(new JavaMethodVisitor(classResource, model));
        }
    }

}
