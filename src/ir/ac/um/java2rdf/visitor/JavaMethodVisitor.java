package ir.ac.um.java2rdf.visitor;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import com.intellij.psi.*;
import ir.ac.um.java2rdf.Predicates;

import java.net.URLEncoder;

public class JavaMethodVisitor extends JavaRecursiveElementVisitor {
    private InfModel model;
    private final Resource CLASS_RESOURCE;

    public JavaMethodVisitor(Resource classResource, InfModel model) {
        this.model = model;
        this.CLASS_RESOURCE = classResource;
    }

    @Override
    public void visitMethod(PsiMethod method) {
        super.visitElement(method);
        try {
            String methodName = method.getName();
            String methodQualifiedName = VisitorUtils.getMethodQualifiedName(method);
            if (methodQualifiedName != null) {
                Resource methodResource = model.createResource(Predicates.JAVA2RDF_NAMESPACE + methodQualifiedName);
                Property prop = model.createProperty(Predicates.RDF_TYPE);
                Resource obj = model
                        .createResource((method.isConstructor()) ? Predicates.CONSTRUCTOR : Predicates.METHOD);
                model.add(methodResource, prop, obj);

                prop = model.createProperty(Predicates.HAS_NAME);
                Literal literalObject = model.createTypedLiteral(methodName, XSDDatatype.XSDstring);
                model.add(methodResource, prop, literalObject);
                prop = model.createProperty((method.isConstructor()) ? Predicates.HAS_CONSTRUCTOR : Predicates.HAS_METHOD);
                model.add(CLASS_RESOURCE, prop, methodResource);


                String methodBody = getMethodBodyWithoutBraces(method.getBody());
                VisitorUtils.processPhysicalLOC(model, methodResource, methodBody);

                processThrownExceptionTypes(methodResource, method);
                VisitorUtils.processModifiers(model, methodResource, method.getModifiers());
                processMethodReturnType(methodResource, method);
                processMethodParameters(methodResource, method);
                processMethodAnnotations(methodResource, method);
                processMethodDocComment(methodResource, method);
                processMethodBody(methodResource, method);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMethodBodyWithoutBraces(PsiCodeBlock block) {
        if (block == null) {
            return null;
        }
        String text = block.getText();
        int index1 = text.indexOf("{");
        index1++;
        while (true) {
            char ch = text.charAt(index1);
            if (ch == ' ' || ch == '\t') {
                index1++;
            } else if (ch == '\n') {
                index1++;
                break;
            } else {
                break;
            }
        }

        int index2 = text.lastIndexOf("}");
        index2--;
        while (true) {
            char ch = text.charAt(index2);
            if (ch == ' ' || ch == '\t') {
                index2--;
            } else if (ch == '\n') {
                index2--;
                break;
            } else {
                break;
            }
        }
        if (index1 < index2) {
            return text.substring(index1, index2);
        } else {
            return null;
        }
    }

    private void processThrownExceptionTypes(Resource methodResource, PsiMethod method) {
        try {
            Property prop = model.createProperty(Predicates.HAS_THROWN_EXCEPTION_TYPE);
            for (PsiJavaCodeReferenceElement exception : method.getThrowsList().getReferenceElements()) {
                String thrownExceptionTypeName = exception.getQualifiedName();
                thrownExceptionTypeName = URLEncoder.encode(thrownExceptionTypeName, "UTF-8");
                Resource obj = model.createResource(Predicates.JAVA2RDF_NAMESPACE
                        + Predicates.prepareNameForResource(thrownExceptionTypeName));
                model.add(methodResource, prop, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void processMethodReturnType(Resource methodResource, PsiMethod method) {
        try {
            if (!method.isConstructor()) {
                Property prop = model.createProperty(Predicates.HAS_RETURN_TYPE);
                Resource typeResource = Predicates.createBlankResource(model);
                model.add(methodResource, prop, typeResource);

                VisitorUtils.processTypeAndName(model, typeResource, method.getReturnType(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMethodParameters(Resource methodResource, PsiMethod method) {
        int parameterCount = method.getParameterList().getParameters().length;
        Literal literalObject = model.createTypedLiteral(parameterCount, XSDDatatype.XSDnonNegativeInteger);
        Property prop = model.createProperty(Predicates.HAS_PARAMETER_COUNT);
        model.add(methodResource, prop, literalObject);

        VisitorUtils.processMethodParameters(model, methodResource, method);
    }

    private void processMethodAnnotations(Resource methodResource, PsiMethod method) {
        VisitorUtils.processAnnotations(model, methodResource, method.getAnnotations());
    }

    private void processMethodDocComment(Resource methodResource, PsiMethod method) {
        VisitorUtils.processDocComment(model, methodResource, method.getDocComment());
    }

    private void processMethodBody(Resource methodResource, PsiMethod method) {
        VisitorUtils.processBlock(model, methodResource, methodResource, method.getBody());
    }


}
