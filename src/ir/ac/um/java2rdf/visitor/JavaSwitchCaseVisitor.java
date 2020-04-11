package ir.ac.um.java2rdf.visitor;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiSwitchLabelStatementImpl;
import ir.ac.um.java2rdf.Predicates;


/**
 * The statements in a Case (in a switch statement) are not children of Case, but its Siblings.
 * So, in order to correctly represent these statements as children of case (so that
 * it is known to which case a statement belongs), this visitor is implemented.
 */
public class JavaSwitchCaseVisitor extends JavaRecursiveElementVisitor {
    private InfModel model;
    private final Resource PARENT_RESOURCE;
    private final Resource PARENT_METHOD_RESOURCE;

    public JavaSwitchCaseVisitor(Resource parentMethodResource, Resource parentResource, InfModel model) {
        this.model = model;
        this.PARENT_RESOURCE = parentResource;
        this.PARENT_METHOD_RESOURCE = parentMethodResource;
    }

    //    TODO handle the following
    /*
    ths statements after a case in Switch are not children of the case, but its siblings, so it is not represented in the output
    that a statement belongs to which case
     */
    @Override
    public void visitSwitchLabelStatement(PsiSwitchLabelStatement statement) {
        this.visitStatement(statement);
        Resource caseResource = Predicates.createBlankResource(model);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.SWITCH_CASE);
        model.add(caseResource, prop, obj);
        prop = model.createProperty(Predicates.IS_DEFAULT);
        Literal literalObject = model.createTypedLiteral(statement.isDefaultCase(), XSDDatatype.XSDboolean);
        model.add(caseResource, prop, literalObject);
        prop = model.createProperty(Predicates.HAS_CASE);
        model.add(PARENT_RESOURCE, prop, caseResource);

        PsiElement sibling = statement.getNextSibling();
        while (sibling != null) {
            if (sibling instanceof PsiSwitchLabelStatementImpl) {
                break;
            }
            if (sibling instanceof PsiStatement) {
                PsiStatement siblingStatement = (PsiStatement) sibling;
                Resource siblingStatementResource = Predicates.createBlankResource(model);
                prop = model.createProperty(Predicates.HAS_STATEMENT);
                model.add(caseResource, prop, siblingStatementResource);
                model.add(PARENT_METHOD_RESOURCE, prop, siblingStatementResource);
                siblingStatement.accept(new JavaSpecificStatementVisitor(PARENT_METHOD_RESOURCE, siblingStatementResource, model));
            }
            sibling = sibling.getNextSibling();
        }
    }


}
