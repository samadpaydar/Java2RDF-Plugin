package ir.ac.um.java2rdf.visitor;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.extractor.values.Value;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.tree.java.PsiBinaryExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiBlockStatementImpl;
import com.intellij.psi.impl.source.tree.java.PsiBreakStatementImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import ir.ac.um.java2rdf.Predicates;
import javassist.compiler.ast.Visitor;

public class JavaSpecificStatementVisitor extends JavaElementVisitor {
    private InfModel model;
    private final Resource STATEMENT_RESOURCE;
    private final Resource PARENT_METHOD_RESOURCE;

    public JavaSpecificStatementVisitor(Resource parentMethodResource, Resource statementResource, InfModel model) {
        this.model = model;
        this.STATEMENT_RESOURCE = statementResource;
        this.PARENT_METHOD_RESOURCE = parentMethodResource;
    }

    @Override
    public void visitForStatement(PsiForStatement statement) {
        super.visitStatement(statement);
        this.visitLoop(statement, Predicates.FOR_STATEMENT);
    }

    @Override
    public void visitDoWhileStatement(PsiDoWhileStatement statement) {
        super.visitStatement(statement);
        this.visitLoop(statement, Predicates.DO_STATEMENT);
    }

    @Override
    public void visitWhileStatement(PsiWhileStatement statement) {
        super.visitStatement(statement);
        this.visitLoop(statement, Predicates.WHILE_STATEMENT);
    }

    @Override
    public void visitForeachStatement(PsiForeachStatement statement) {
        super.visitStatement(statement);
        this.visitLoop(statement, Predicates.ENHANCED_FOR_STATEMENT);

    }

    private void visitLoop(PsiLoopStatement statement, String predicate) {
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(predicate);
        model.add(STATEMENT_RESOURCE, prop, obj);

        processThenBranchForLoop(statement);
    }

    @Override
    public void visitBreakStatement(PsiBreakStatement statement) {
        super.visitStatement(statement);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.BREAK_STATEMENT);
        model.add(STATEMENT_RESOURCE, prop, obj);
    }

    @Override
    public void visitContinueStatement(PsiContinueStatement statement) {
        super.visitStatement(statement);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.CONTINUE_STATEMENT);
        model.add(STATEMENT_RESOURCE, prop, obj);
    }

    @Override
    public void visitIfStatement(PsiIfStatement statement) {
        super.visitStatement(statement);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.IF_STATEMENT);
        model.add(STATEMENT_RESOURCE, prop, obj);

        processConditionExpressionForIf(statement);
        processThenBranchForIf(statement);
        processElseBranchForIf(statement);
    }

    private void processConditionExpressionForIf(PsiIfStatement statement) {
        //            TODO handle this
        /*
        PsiExpression condition = statement.getCondition();
        if (condition instanceof PsiBinaryExpressionImpl) {
        }
        */
    }

    public void visitExpressionStatement(PsiExpressionStatement statement) {
        super.visitStatement(statement);
        PsiExpression expression = statement.getExpression();
        if (expression instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
            this.visitMethodCallExpression(methodCallExpression);
        }
    }

    private void processThenBranchForLoop(PsiLoopStatement statement) {
        PsiStatement bodyStatement = statement.getBody();
        if (bodyStatement instanceof PsiBlockStatementImpl) {
            PsiBlockStatementImpl bodyBlock = (PsiBlockStatementImpl) bodyStatement;
            VisitorUtils.processBlock(model, PARENT_METHOD_RESOURCE, STATEMENT_RESOURCE, bodyBlock.getCodeBlock());
        } else {
            Resource bodyStatementResource = Predicates.createBlankResource(model);
            Property prop = model.createProperty(Predicates.HAS_BODY);
            model.add(STATEMENT_RESOURCE, prop, bodyStatementResource);
            bodyStatement.accept(new JavaStatementVisitor(PARENT_METHOD_RESOURCE, bodyStatementResource, model, 1));
        }
    }


    private void processThenBranchForIf(PsiIfStatement statement) {
        PsiStatement thenStatement = statement.getThenBranch();
        Resource thenStatementResource = Predicates.createBlankResource(model);
        Property prop = model.createProperty(Predicates.HAS_THEN);
        model.add(STATEMENT_RESOURCE, prop, thenStatementResource);
        if (thenStatement instanceof PsiBlockStatementImpl) {
            PsiBlockStatementImpl thenBlock = (PsiBlockStatementImpl) thenStatement;
            VisitorUtils.processBlock(model, PARENT_METHOD_RESOURCE, thenStatementResource, thenBlock.getCodeBlock());
        } else {
            thenStatement.accept(new JavaStatementVisitor(PARENT_METHOD_RESOURCE, thenStatementResource, model, 1));
        }
    }

    private void processElseBranchForIf(PsiIfStatement statement) {
        PsiStatement elseStatement = statement.getElseBranch();
        if (elseStatement != null) {
            Resource elseStatementResource = Predicates.createBlankResource(model);
            Property prop = model.createProperty(Predicates.HAS_ELSE);
            model.add(STATEMENT_RESOURCE, prop, elseStatementResource);
            if (elseStatement instanceof PsiBlockStatementImpl) {
                PsiBlockStatementImpl elseBlock = (PsiBlockStatementImpl) elseStatement;
                VisitorUtils.processBlock(model, PARENT_METHOD_RESOURCE, elseStatementResource, elseBlock.getCodeBlock());
            } else {
                elseStatement.accept(new JavaStatementVisitor(PARENT_METHOD_RESOURCE, elseStatementResource, model, 1));
            }
        }

    }

    @Override
    public void visitThrowStatement(PsiThrowStatement statement) {
        super.visitStatement(statement);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.THROW_STATEMENT);
        model.add(STATEMENT_RESOURCE, prop, obj);

        if (statement.getException() != null) {
            PsiType exceptionType = statement.getException().getType();
            Resource exceptionResource = Predicates.createBlankResource(model);
            prop = model.createProperty(Predicates.HAS_EXCEPTION_TYPE);
            model.add(STATEMENT_RESOURCE, prop, exceptionResource);
            VisitorUtils.processTypeAndName(model, exceptionResource, exceptionType, null);
        }
    }

    @Override
    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        super.visitMethodCallExpression(expression);
        try {
            PsiElement element = expression.getMethodExpression().getReference().resolve();
            if (element instanceof PsiMethod) {
                PsiMethod calledMethod = (PsiMethod) element;
                String calledMethodQualifiedName = VisitorUtils.getMethodQualifiedName(calledMethod);
                if (calledMethodQualifiedName != null) {
                    Resource calledMethodResource = model.createResource(Predicates.JAVA2RDF_NAMESPACE + calledMethodQualifiedName);

                    //Resource methodInvocationResource = Predicates.createBlankResource(model);
                    Property prop = model.createProperty(Predicates.RDF_TYPE);
                    Resource obj = model.createResource(Predicates.METHOD_INVOCATION);
                    model.add(STATEMENT_RESOURCE, prop, obj);

                    prop = model.createProperty(Predicates.INVOKES);
                    model.add(STATEMENT_RESOURCE, prop, calledMethodResource);

                    prop = model.createProperty(Predicates.RDF_TYPE);
                    obj = model.createResource((calledMethod.isConstructor()) ? Predicates.CONSTRUCTOR : Predicates.METHOD);
                    model.add(calledMethodResource, prop, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visitAnonymousClass(PsiAnonymousClass aClass) {
        super.visitClass(aClass);

        Resource classResource = Predicates.createBlankResource(model);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.ANONYMOUS_CLASS);
        model.add(classResource, prop, obj);

        prop = model.createProperty(Predicates.DEFINES_ANONYMOUS_CLASS);
        model.add(STATEMENT_RESOURCE, prop, classResource);

        aClass.accept(new JavaClassVisitor(classResource, model));
        VisitorUtils.processClassSuperClasses(model, classResource, aClass);
        VisitorUtils.processInterfacesImplementedByClass(model, classResource, aClass);
    }

    @Override
    public void visitSwitchStatement(PsiSwitchStatement statement) {
        super.visitStatement(statement);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.SWITCH_STATEMENT);
        model.add(STATEMENT_RESOURCE, prop, obj);
        statement.getBody().accept(new JavaSwitchCaseVisitor(PARENT_METHOD_RESOURCE, STATEMENT_RESOURCE, model));
    }

    @Override
    public void visitReturnStatement(PsiReturnStatement statement) {
        super.visitStatement(statement);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.RETURN_STATEMENT);
        PsiExpression expression = statement.getReturnValue();
//        TODO handle expression
        model.add(STATEMENT_RESOURCE, prop, obj);
    }

    @Override
    public void visitTryStatement(PsiTryStatement statement) {
        super.visitStatement(statement);

        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.TRY_STATEMENT);
        model.add(STATEMENT_RESOURCE, prop, obj);

        PsiParameter[] catchParameters = statement.getCatchBlockParameters();
        PsiCodeBlock[] catchBlocks = statement.getCatchBlocks();
        if (catchParameters.length == catchBlocks.length) {
            for (int i = 0; i < catchBlocks.length; i++) {
                PsiParameter catchParameter = catchParameters[i];
                PsiCodeBlock catchBlock = catchBlocks[i];
                Resource catchBlockResource = Predicates.createBlankResource(model);
                prop = model.createProperty(Predicates.HAS_CATCH_BLOCK);
                model.add(STATEMENT_RESOURCE, prop, catchBlockResource);

                processCatchBlock(catchBlockResource, catchBlock, catchParameter);
                VisitorUtils.processBlock(model, PARENT_METHOD_RESOURCE, catchBlockResource, catchBlocks[i]);
            }
        }
        prop = model.createProperty(Predicates.HAS_FINALLY_BLOCK);
        Literal literalObject = model.createTypedLiteral(statement.getFinallyBlock() != null, XSDDatatype.XSDboolean);
        model.add(STATEMENT_RESOURCE, prop, literalObject);
        if (statement.getFinallyBlock() != null) {
            VisitorUtils.processBlock(model, PARENT_METHOD_RESOURCE, STATEMENT_RESOURCE, statement.getFinallyBlock());
        }

    }

    private void processCatchBlock(Resource catchBlockResource, PsiCodeBlock catchBlock, PsiParameter catchParameter) {
        PsiType catchParamterType = catchParameter.getTypeElement().getType();
        if (catchParamterType instanceof PsiClassReferenceType) {
            PsiClassReferenceType type = (PsiClassReferenceType) catchParamterType;
            String catchParameterClassName = type.resolve().getQualifiedName();
            String subject = Predicates.JAVA2RDF_NAMESPACE + Predicates.prepareNameForResource(catchParameterClassName);
            Resource resource = model.createResource(subject);
            Property prop = model.createProperty(Predicates.HAS_EXCEPTION_TYPE);
            model.add(catchBlockResource, prop, resource);
        } else if (catchParamterType instanceof PsiDisjunctionType) {//this is for multi-parameter catches
            PsiDisjunctionType types = (PsiDisjunctionType) catchParamterType;
            for (PsiType tempType : types.getDisjunctions()) {
                if (tempType instanceof PsiClassReferenceType) {
                    PsiClassReferenceType type = (PsiClassReferenceType) tempType;
                    String catchParameterClassName = type.resolve().getQualifiedName();
                    String subject = Predicates.JAVA2RDF_NAMESPACE + Predicates.prepareNameForResource(catchParameterClassName);
                    Resource resource = model.createResource(subject);
                    Property prop = model.createProperty(Predicates.HAS_EXCEPTION_TYPE);
                    model.add(catchBlockResource, prop, resource);
                }
            }
        }
    }

    @Override
    public void visitLocalVariable(PsiLocalVariable variable) {
        super.visitVariable(variable);

        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.LOCAL_VARIABLE_DECLARATION_STATEMENT);
        model.add(STATEMENT_RESOURCE, prop, obj);

        Resource variableResource = Predicates.createBlankResource(model);
        prop = model.createProperty(Predicates.DECLARES_LOCAL_VARIABLE);
        model.add(STATEMENT_RESOURCE, prop, variableResource);

        VisitorUtils.processTypeAndName(model, variableResource, variable.getTypeElement().getType(), variable.getName());
        VisitorUtils.processModifiers(model, variableResource, variable.getModifiers());
    }


    public void visitAssertStatement(PsiAssertStatement statement) {
        super.visitStatement(statement);

        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.ASSERT_STATEMENT);
        model.add(STATEMENT_RESOURCE, prop, obj);

        PsiExpression condition = statement.getAssertCondition();
        PsiExpression description = statement.getAssertDescription();
        if (description != null) {
            prop = model.createProperty(Predicates.HAS_DESCRIPTION);
            Literal literalObject = model.createTypedLiteral(description.getText(), XSDDatatype.XSDstring);
            model.add(STATEMENT_RESOURCE, prop, literalObject);
        }
//TODO process condition
    }

}

