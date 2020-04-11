package ir.ac.um.java2rdf.visitor;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import com.intellij.psi.*;
import ir.ac.um.java2rdf.Predicates;

public class JavaStatementVisitor extends JavaElementVisitor {
    private InfModel model;
    private final Resource PARENT_RESOURCE;
    private final Resource PARENT_METHOD_RESOURCE;
    private int statementNumber;

    public JavaStatementVisitor(Resource parentMethodResource,Resource parentResource, InfModel model, int statementNumber) {
        this.model = model;
        this.PARENT_RESOURCE = parentResource;
        this.PARENT_METHOD_RESOURCE = parentMethodResource;
        this.statementNumber = statementNumber;
    }

    @Override
    public void visitStatement(PsiStatement statement) {
        super.visitStatement(statement);
        Resource statementResource = Predicates.createBlankResource(model);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.STATEMENT);
        model.add(statementResource, prop, obj);

        prop = model.createProperty(Predicates.HAS_STATEMENT);
        model.add(PARENT_RESOURCE, prop, statementResource);
        model.add(PARENT_METHOD_RESOURCE, prop, statementResource);

        prop = model.createProperty(Predicates.HAS_NUMBER);
        Literal literalObject = model.createTypedLiteral(statementNumber, XSDDatatype.XSDnonNegativeInteger);
        model.add(statementResource, prop, literalObject);
        statement.accept(new JavaSpecificStatementVisitor(PARENT_METHOD_RESOURCE, statementResource, model));
    }

    @Override
    public void visitDeclarationStatement(PsiDeclarationStatement statement) {

        this.visitStatement(statement);

    }

}

