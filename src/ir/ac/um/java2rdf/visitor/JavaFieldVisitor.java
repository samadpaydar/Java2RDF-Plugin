package ir.ac.um.java2rdf.visitor;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import com.intellij.psi.*;
import ir.ac.um.java2rdf.Predicates;

public class JavaFieldVisitor extends JavaRecursiveElementVisitor {
    private InfModel model;
    private final Resource CLASS_RESOURCE;

    public JavaFieldVisitor(Resource classResource, InfModel model) {
        this.model = model;
        this.CLASS_RESOURCE = classResource;
    }

    @Override
    public void visitField(PsiField field) {
        super.visitVariable(field);
        try {
            Resource fieldResource = Predicates.createBlankResource(model);
            Property prop = model.createProperty(Predicates.RDF_TYPE);
            Resource obj = model.createResource(Predicates.FIELD);
            model.add(fieldResource, prop, obj);

            prop = model.createProperty(Predicates.HAS_FIELD);
            model.add(CLASS_RESOURCE, prop, fieldResource);

            VisitorUtils.processTypeAndName(model, fieldResource, field.getType(), field.getName());
            VisitorUtils.processModifiers(model, fieldResource, field.getModifiers());
            VisitorUtils.processDocComment(model, fieldResource, field.getDocComment());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
