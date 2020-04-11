package ir.ac.um.java2rdf.visitor;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import ir.ac.um.java2rdf.Constants;
import ir.ac.um.java2rdf.Predicates;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VisitorUtils {
    public static void processClassSuperClasses(InfModel model, Resource classResource, PsiClass aClass) {
        PsiClass superClass = aClass.getSuperClass();
        if (superClass != null) {
            String superClassName = superClass.getQualifiedName();
            Property prop = model.createProperty(Predicates.EXTENDS);
            Resource obj = model.createResource(Predicates.JAVA2RDF_NAMESPACE
                    + Predicates.prepareNameForResource(superClassName));
            model.add(classResource, prop, obj);
        }
    }

    public static void processInterfacesImplementedByClass(InfModel model, Resource classResource, PsiClass aClass) {
        for (PsiClass implementedInterface : aClass.getInterfaces()) {
            String interfaceName = implementedInterface.getQualifiedName();
            Property prop = model.createProperty(Predicates.IMPLEMENTS);
            Resource obj = model.createResource(Predicates.JAVA2RDF_NAMESPACE
                    + Predicates.prepareNameForResource(interfaceName));
            model.add(classResource, prop, obj);
        }
    }

    public static String getMethodQualifiedName(PsiMethod method) {
        String result = null;
        try {
            String methodSemiQualifiedName = getMethodSemiQualifiedName(method);
            String className = method.getContainingClass().getQualifiedName();
            if (className == null) {
                //the method belongs to an anonymous class
                return null;
            }
            String declaringClassQualifiedName = Predicates.prepareNameForResource(className);
            result = declaringClassQualifiedName + Constants.UNDERLINE_CHAR + methodSemiQualifiedName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void processMethodParameters(InfModel model, Resource methodResource, PsiMethod method) {
        int parameterNumber = 0;
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            parameterNumber++;
            Resource parameterResource = Predicates.createBlankResource(model);
            Property prop = model.createProperty(Predicates.RDF_TYPE);
            Resource obj = model.createResource(Predicates.PARAMETER);
            model.add(parameterResource, prop, obj);

            prop = model.createProperty(Predicates.HAS_NUMBER);
            Literal literalObject = model.createTypedLiteral(parameterNumber, XSDDatatype.XSDnonNegativeInteger);
            model.add(parameterResource, prop, literalObject);

            prop = model.createProperty(Predicates.HAS_PARAMETER);
            model.add(methodResource, prop, parameterResource);

            VisitorUtils.processTypeAndName(model, parameterResource, parameter.getType(), parameter.getName());
        }

    }

    private static String getMethodSemiQualifiedName(PsiMethod method) {
        String methodName = method.getName();
        try {
            methodName = URLEncoder.encode(methodName, "UTF-8");
            if (method.getParameters().length > 0) {
                for (PsiParameter parameter : method.getParameterList().getParameters()) {
                    String parameterType = parameter.getTypeElement().getType().getCanonicalText();
                    parameterType = Predicates.prepareNameForResource(parameterType);
                    methodName += Constants.UNDERLINE_CHAR + parameterType;
                }
            } else {
                methodName += Constants.UNDERLINE_CHAR;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return methodName;
    }

    public static void processModifiers(InfModel model, Resource resource, JvmModifier[] modifiers) {
        if (modifiers != null) {
            for (JvmModifier modifier : modifiers) {
                Property prop = model.createProperty(Predicates.HAS_MODIFIER);
                Literal literalObject = model.createTypedLiteral(modifier.toString().toLowerCase(), XSDDatatype.XSDstring);
                model.add(resource, prop, literalObject);
            }
        }
    }

    public static void processTypeAndName(InfModel model, Resource resource, PsiType type, String name) {
        Property prop;
        Literal literalObject;
        Resource object = null;

        if (name != null) {
            prop = model.createProperty(Predicates.HAS_NAME);
            literalObject = model.createTypedLiteral(name, XSDDatatype.XSDstring);
            model.add(resource, prop, literalObject);
        }

        literalObject = null;
        if (type instanceof PsiPrimitiveType) {
            prop = model.createProperty(Predicates.IS_PRIMITIVE_TYPE);
            literalObject = model.createTypedLiteral(true, XSDDatatype.XSDboolean);
            model.add(resource, prop, literalObject);

            literalObject = model.createTypedLiteral(type.getCanonicalText(), XSDDatatype.XSDstring);
        } else if (type instanceof PsiClassReferenceType) {
            PsiClassReferenceType classReference = (PsiClassReferenceType) type;
            PsiClass cls = classReference.resolve();
            if (cls != null) {
                String className = cls.getQualifiedName();
                className = Predicates.JAVA2RDF_NAMESPACE + Predicates.prepareNameForResource(className);
                object = model.createResource(className);
            } else {
//                TODO
            }
        } else if (type instanceof PsiArrayType) {
            prop = model.createProperty(Predicates.IS_ARRAY);
            literalObject = model.createTypedLiteral(true, XSDDatatype.XSDboolean);
            model.add(resource, prop, literalObject);

            PsiArrayType arrayType = (PsiArrayType) type;
            PsiType deepType = arrayType.getDeepComponentType();

            int arrayDimension = 1;
            while (arrayType.getComponentType() instanceof PsiArrayType) {
                arrayType = (PsiArrayType) arrayType.getComponentType();
                arrayDimension++;
            }
            prop = model.createProperty(Predicates.HAS_DIMENSION);
            literalObject = model.createTypedLiteral(arrayDimension, XSDDatatype.XSDnonNegativeInteger);
            model.add(resource, prop, literalObject);
            literalObject = null;
            if (deepType instanceof PsiPrimitiveType) {
                literalObject = model.createTypedLiteral(deepType.getCanonicalText(), XSDDatatype.XSDstring);
            } else if (deepType instanceof PsiClassReferenceType) {
                PsiClassReferenceType classReference = (PsiClassReferenceType) deepType;
                PsiClass cls = classReference.resolve();
                String className = cls.getQualifiedName();
                className = Predicates.JAVA2RDF_NAMESPACE + Predicates.prepareNameForResource(className);
                literalObject = model.createTypedLiteral(className, XSDDatatype.XSDstring);
            }
        }
        if (literalObject != null) {
            prop = model.createProperty(Predicates.HAS_TYPE);
            model.add(resource, prop, literalObject);
        } else if (object != null) {
            prop = model.createProperty(Predicates.HAS_TYPE);
            model.add(resource, prop, object);
        }
    }

    public static void processAnnotations(InfModel model, Resource parentResource, PsiAnnotation[] annotations) {
        for (PsiAnnotation annotation : annotations) {
            Resource annotationResource = Predicates.createBlankResource(model);
            Property prop = model.createProperty(Predicates.RDF_TYPE);
            Resource obj = model.createResource(Predicates.ANNOTATION);
            model.add(annotationResource, prop, obj);

            prop = model.createProperty(Predicates.HAS_ANNOTATION);
            model.add(parentResource, prop, annotationResource);

            String annotationName = annotation.getQualifiedName();
            String subject = Predicates.JAVA2RDF_NAMESPACE + Predicates.prepareNameForResource(annotationName);

            prop = model.createProperty(Predicates.HAS_TYPE);
            obj = model.createResource(subject);
            model.add(annotationResource, prop, obj);

            prop = model.createProperty(Predicates.HAS_NAME);
            Literal literalObject = model.createTypedLiteral(annotation.getText().replace("@", ""), XSDDatatype.XSDstring);
            model.add(annotationResource, prop, literalObject);
        }
    }

    public static void processDocComment(InfModel model, Resource parentResource, PsiDocComment comment) {
        if (comment == null) {
            return;
        }
        Resource commentResource = Predicates.createBlankResource(model);
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.DOC_COMMENT);
        model.add(commentResource, prop, obj);

        prop = model.createProperty(Predicates.HAS_DOC_COMMENT);
        model.add(parentResource, prop, commentResource);
        for (PsiDocTag tag : comment.getTags()) {
            Resource tagResource = Predicates.createBlankResource(model);
            prop = model.createProperty(Predicates.RDF_TYPE);
            obj = model.createResource(Predicates.DOC_TAG);
            model.add(tagResource, prop, obj);

            prop = model.createProperty(Predicates.HAS_DOC_TAG);
            model.add(commentResource, prop, tagResource);

            String tagName = tag.getName();
            String tagText = tag.getText();
            int index = tagText.indexOf(tagName);
            if (index != -1) {
                tagText = tagText.substring(index + tagName.length());
            }
            tagText = tagText.trim();
            if (tagText.endsWith("*")) {
                tagText = tagText.substring(0, tagText.length() - 1).trim();
            }
            prop = model.createProperty(Predicates.HAS_NAME);
            Literal literalObject = model.createTypedLiteral(tagName, XSDDatatype.XSDstring);
            model.add(tagResource, prop, literalObject);

            prop = model.createProperty(Predicates.HAS_TEXT);
            literalObject = model.createTypedLiteral(tagText, XSDDatatype.XSDstring);
            model.add(tagResource, prop, literalObject);
        }
        prop = model.createProperty(Predicates.HAS_TEXT);
        Literal literalObject = model.createTypedLiteral(comment.getText(), XSDDatatype.XSDstring);
        model.add(commentResource, prop, literalObject);
    }

    public static void processBlock(InfModel model, Resource parentMethodResource, Resource parentResource, PsiCodeBlock block) {
        if (block == null) {
            return;
        }
        Seq blockResource = model.createSeq();
        Property prop = model.createProperty(Predicates.RDF_TYPE);
        Resource obj = model.createResource(Predicates.BLOCK);
        model.add(blockResource, prop, obj);

        prop = model.createProperty(Predicates.HAS_BODY);
        model.add(parentResource, prop, blockResource);

        PsiStatement[] statements = block.getStatements();
        int statementCount = statements.length;
        prop = model.createProperty(Predicates.HAS_STATEMENT_COUNT);
        Literal literalObject = model.createTypedLiteral(statementCount, XSDDatatype.XSDnonNegativeInteger);
        model.add(blockResource, prop, literalObject);

        int statementNumber = 0;
        for (PsiStatement statement : statements) {
            statement.accept(new JavaStatementVisitor(parentMethodResource, blockResource, model, ++statementNumber));
        }
    }

    public static void processPhysicalLOC(InfModel model, Resource resource, String text) {
        if (text == null) {
            return;
        }
        int numberOfLines = VisitorUtils.getNumberOfLines(text);
        Literal literalObject = model.createTypedLiteral(numberOfLines, XSDDatatype.XSDnonNegativeInteger);
        Property prop = model.createProperty(Predicates.HAS_PHYSICAL_LOC);
        model.add(resource, prop, literalObject);
    }

    private static int getNumberOfLines(String text) {
        Matcher matcher = Pattern.compile("\r\n|\r|\n").matcher(text);
        int lines = 1;
        while (matcher.find()) {
            lines++;
        }
        return lines;
    }

}

