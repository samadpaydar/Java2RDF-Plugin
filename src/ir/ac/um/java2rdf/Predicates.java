package ir.ac.um.java2rdf;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Resource;

public class Predicates {
    public final static String RDF_PREFIX = "rdf";
    public final static String RDFS_PREFIX = "rdfs";
    public final static String XSD_PREFIX = "xsd";
    public final static String JAVA2RDF_PREFIX = "java2rdf";

    public final static String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public final static String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
    public final static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";
    public final static String JAVA2RDF_NAMESPACE = "http://java2rdf.um.ac.ir/";

    public final static String RDF_TYPE = RDF_NAMESPACE + "type";
    public final static String SOURCE_FILE = JAVA2RDF_NAMESPACE + "SourceFile";
    public final static String IMPORT = JAVA2RDF_NAMESPACE + "Import";
    public final static String PACKAGE = JAVA2RDF_NAMESPACE + "Package";
    public final static String CLASS = JAVA2RDF_NAMESPACE + "Class";
    public final static String INTERFACE = JAVA2RDF_NAMESPACE + "Interface";
    public final static String METHOD = JAVA2RDF_NAMESPACE + "Method";
    public final static String CONSTRUCTOR = JAVA2RDF_NAMESPACE + "Constructor";
    public final static String FIELD = JAVA2RDF_NAMESPACE + "Field";
    public final static String STATEMENT = JAVA2RDF_NAMESPACE + "Statement";
    public final static String BLOCK = JAVA2RDF_NAMESPACE + "Block";
    public final static String FINALLY_BLOCK = JAVA2RDF_NAMESPACE + "FinallyBlock";
    public final static String IF_STATEMENT = JAVA2RDF_NAMESPACE + "IfStatement";
    public final static String FOR_STATEMENT = JAVA2RDF_NAMESPACE + "ForStatement";
    public final static String ENHANCED_FOR_STATEMENT = JAVA2RDF_NAMESPACE + "EnhancedForStatement";
    public final static String WHILE_STATEMENT = JAVA2RDF_NAMESPACE + "WhileStatement";
    public final static String DO_STATEMENT = JAVA2RDF_NAMESPACE + "DoStatement";
    public final static String TRY_STATEMENT = JAVA2RDF_NAMESPACE + "TryStatement";
    public final static String SWITCH_STATEMENT = JAVA2RDF_NAMESPACE + "SwitchStatement";
    public final static String RETURN_STATEMENT = JAVA2RDF_NAMESPACE + "ReturnStatement";
    public final static String THROW_STATEMENT = JAVA2RDF_NAMESPACE + "ThrowStatement";
    public final static String BREAK_STATEMENT = JAVA2RDF_NAMESPACE + "BreakStatement";
    public final static String CONTINUE_STATEMENT = JAVA2RDF_NAMESPACE + "ContinueStatement";
    public final static String EXPRESSION_STATEMENT = JAVA2RDF_NAMESPACE + "ExpressionStatement";
    public final static String LABELED_STATEMENT = JAVA2RDF_NAMESPACE + "LabeledStatement";
    public final static String ASSERT_STATEMENT = JAVA2RDF_NAMESPACE + "AssertStatement";
    public final static String LOCAL_VARIABLE_DECLARATION_STATEMENT = JAVA2RDF_NAMESPACE + "LocalVariableDeclarationStatement";
    public final static String TYPE_DECLARATION_STATEMENT = JAVA2RDF_NAMESPACE + "TypeDeclarationStatement";
    public final static String CONSTRUCTOR_INVOCATION = JAVA2RDF_NAMESPACE + "ConstructorInvocation";
    public final static String SUPER_CONSTRUCTOR_INVOCATION = JAVA2RDF_NAMESPACE + "SuperConstructorInvocation";
    public final static String SWITCH_CASE = JAVA2RDF_NAMESPACE + "SwitchCase"; //the case of the switch
    public final static String NAME = JAVA2RDF_NAMESPACE + "Name";
    public final static String INTEGER_LITERAL = JAVA2RDF_NAMESPACE + "IntegerLiteral";
    public final static String FLOATING_POINT_LITERAL = JAVA2RDF_NAMESPACE + "FloatingPointLiteral";
    public final static String CHARACTER_LITERAL = JAVA2RDF_NAMESPACE + "CharacterLiteral";
    public final static String NULL_LITERAL = JAVA2RDF_NAMESPACE + "NullLiteral";
    public final static String BOOLEAN_LITERAL = JAVA2RDF_NAMESPACE + "BooleanLiteral";
    public final static String STRING_LITERAL = JAVA2RDF_NAMESPACE + "StringLiteral";
    public final static String TYPE_LITERAL = JAVA2RDF_NAMESPACE + "TypeLiteral";
    public final static String THIS_EXPRESSION = JAVA2RDF_NAMESPACE + "ThisExpression";
    public final static String SUPER_FIELD_ACCESS = JAVA2RDF_NAMESPACE + "SuperFieldAccess";
    public final static String FIELD_ACCESS = JAVA2RDF_NAMESPACE + "FieldAccess";
    public final static String ASSIGNMENT = JAVA2RDF_NAMESPACE + "Assignment";
    public final static String PARENTHESIZED_EXPRESSION = JAVA2RDF_NAMESPACE + "ParenthesizedExpression";
    public final static String CLASS_INSTANCE_CREATION = JAVA2RDF_NAMESPACE + "ClassInstanceCreation";
    public final static String ARRAY_CREATION = JAVA2RDF_NAMESPACE + "ArrayCreation";
    public final static String ARRAY_INITIALIZER = JAVA2RDF_NAMESPACE + "ArrayInitializer";
    public final static String METHOD_INVOCATION = JAVA2RDF_NAMESPACE + "MethodInvocation";
    public final static String SUPER_METHOD_INVOCATION = JAVA2RDF_NAMESPACE + "SuperMethodInvocation";
    public final static String ARRAY_ACCESS = JAVA2RDF_NAMESPACE + "ArrayAccess";
    public final static String INFIX_EXPRESSION = JAVA2RDF_NAMESPACE + "InfixExpression";
    public final static String INSTANCEOF_EXPRESSION = JAVA2RDF_NAMESPACE + "InstanceofExpression";
    public final static String CONDITIONAL_EXPRESSION = JAVA2RDF_NAMESPACE + "ConditionalExpression";
    public final static String POSTFIX_EXPRESSION = JAVA2RDF_NAMESPACE + "PostfixExpression";
    public final static String PREFIX_EXPRESSION = JAVA2RDF_NAMESPACE + "PrefixExpression";
    public final static String CAST_EXPRESSION = JAVA2RDF_NAMESPACE + "CastExpression";
    public final static String VARIABLE_DECLARATION_EXPRESSION = JAVA2RDF_NAMESPACE + "VariableDeclarationExpression";
    public final static String EXPRESSION = JAVA2RDF_NAMESPACE + "Expression";

    //predicates
    public final static String HAS_IMPORT_DECLARATION = JAVA2RDF_NAMESPACE + "hasImportDeclaration";
    public final static String IMPORTS = JAVA2RDF_NAMESPACE + "imports";
    public final static String IS_SINGLE_TYPE_IMPORT = JAVA2RDF_NAMESPACE + "isSingleTypeImport";
    public final static String IS_STATIC_IMPORT = JAVA2RDF_NAMESPACE + "isStaticImport";
    public final static String HAS_IMPORT_DECLARATION_COUNT = JAVA2RDF_NAMESPACE + "hasImportDeclarationCount";
    public final static String IS_DEFINED_IN = JAVA2RDF_NAMESPACE + "isDefinedIn";
    public final static String HAS_PACKAGE_DECLARATION = JAVA2RDF_NAMESPACE + "hasPackageDeclaration";
    public final static String HAS_MODIFIER = JAVA2RDF_NAMESPACE + "hasModifier";
    public final static String EXTENDS = JAVA2RDF_NAMESPACE + "extends";
    public final static String IMPLEMENTS = JAVA2RDF_NAMESPACE + "implements";
    public final static String HAS_METHOD = JAVA2RDF_NAMESPACE + "hasMethod";
    public final static String HAS_CONSTRUCTOR = JAVA2RDF_NAMESPACE + "hasConstructor";
    public final static String HAS_RETURN_TYPE = JAVA2RDF_NAMESPACE + "hasReturnType";
    public final static String HAS_FIELD = JAVA2RDF_NAMESPACE + "hasField";
    public final static String HAS_TYPE = JAVA2RDF_NAMESPACE + "hasType";
    public final static String HAS_NAME = JAVA2RDF_NAMESPACE + "hasName";
    public final static String HAS_STATEMENT_COUNT = JAVA2RDF_NAMESPACE + "hasStatementCount";
    public final static String HAS_STATEMENT = JAVA2RDF_NAMESPACE + "hasStatement";
    public final static String INVOKES = JAVA2RDF_NAMESPACE + "invokes";
    public final static String IS_INVOKED_BY = JAVA2RDF_NAMESPACE + "isInvokedBy";
    public final static String INSTANTIATES = JAVA2RDF_NAMESPACE + "instantiates";
    public final static String IS_INSTANTIATED_BY = JAVA2RDF_NAMESPACE + "isInstantiatedBy";
    public final static String HAS_NUMBER = JAVA2RDF_NAMESPACE + "hasNumber";
    public final static String IS_DEFAULT = JAVA2RDF_NAMESPACE + "isDefault";
    public final static String HAS_EXPRESSION = JAVA2RDF_NAMESPACE + "hasExpression";
    public final static String HAS_THROWN_EXCEPTION_TYPE = JAVA2RDF_NAMESPACE + "hasThrownExceptionType";
    public final static String HAS_CATCH_BLOCK = JAVA2RDF_NAMESPACE + "hasCatchBlock";
    public final static String HAS_EXCEPTION_TYPE = JAVA2RDF_NAMESPACE + "hasExceptionType";
    public final static String HAS_FINALLY_BLOCK = JAVA2RDF_NAMESPACE + "hasFinallyBlock";
    public final static String HAS_BODY = JAVA2RDF_NAMESPACE + "hasBody";

    public final static String ENUM = JAVA2RDF_NAMESPACE + "Enum";
    public final static String HAS_PATH = JAVA2RDF_NAMESPACE + "hasPath";
    public final static String HAS_CASE = JAVA2RDF_NAMESPACE + "hasCase";
    public final static String HAS_PARAMETER_COUNT = JAVA2RDF_NAMESPACE + "hasParameterCount";
    public final static String PROJECT = JAVA2RDF_NAMESPACE + "Project";
    public final static String CONTAINS = JAVA2RDF_NAMESPACE + "contains";
    public final static String DEFINES_ANONYMOUS_CLASS = JAVA2RDF_NAMESPACE + "definesAnonymousClass";
    public final static String ANONYMOUS_CLASS = JAVA2RDF_NAMESPACE + "AnonymousClass";
    public final static String PARAMETER = JAVA2RDF_NAMESPACE + "Parameter";
    public final static String HAS_PARAMETER = JAVA2RDF_NAMESPACE + "hasParameter";
    public final static String HAS_ELSE = JAVA2RDF_NAMESPACE + "hasElse";
    public final static String DECLARES_LOCAL_VARIABLE = JAVA2RDF_NAMESPACE + "declaresLocalVariable";
    public final static String IS_PRIMITIVE_TYPE = JAVA2RDF_NAMESPACE + "isPrimitiveType";
    public final static String IS_ARRAY = JAVA2RDF_NAMESPACE + "isArray";
    public final static String HAS_DIMENSION = JAVA2RDF_NAMESPACE + "hasDimension";
    public final static String HAS_DESCRIPTION = JAVA2RDF_NAMESPACE + "hasDescription";
    public final static String HAS_ANNOTATION = JAVA2RDF_NAMESPACE + "hasAnnotation";
    public final static String ANNOTATION = JAVA2RDF_NAMESPACE + "Annotation";
    public final static String DOC_COMMENT = JAVA2RDF_NAMESPACE + "DocComment";
    public final static String HAS_DOC_COMMENT = JAVA2RDF_NAMESPACE + "hasDocComment";
    public final static String HAS_TEXT = JAVA2RDF_NAMESPACE + "hasText";
    public final static String DOC_TAG = JAVA2RDF_NAMESPACE + "DocTag";
    public final static String HAS_DOC_TAG = JAVA2RDF_NAMESPACE + "hasDocTag";
    public final static String HAS_PHYSICAL_LOC = JAVA2RDF_NAMESPACE + "hasPhysicalLOC";
    public final static String HAS_THEN = JAVA2RDF_NAMESPACE + "hasThen";

    private final static char SLASH = '/';

    public static Resource getResourceForProject(InfModel model, String projectName) {
        return model.createResource(Predicates.PROJECT + Predicates.SLASH + Predicates.prepareNameForResource(projectName));
    }

    public static Resource createBlankResource(InfModel model) {
        return model.createResource();
    }

    public static String prepareNameForResource(String name) {
        if (name != null) {
            name = name.replace(Constants.DOT_CHAR, Constants.UNDERLINE_CHAR);
            name = name.replace(Constants.LEFT_BRACKET_CHAR, Constants.UNDERLINE_CHAR);
            name = name.replace(Constants.RIGHT_BRACKET_CHAR, Constants.UNDERLINE_CHAR);
            name = name.replace(Constants.LESS_THAN_CHAR, Constants.UNDERLINE_CHAR);
            name = name.replace(Constants.GREATER_THAN_CHAR, Constants.UNDERLINE_CHAR);
            name = name.replace(Constants.COMMA_CHAR, Constants.UNDERLINE_CHAR);
            name = name.replace(Constants.BLANK_SPACE_CHAR, Constants.UNDERLINE_CHAR);
        }
        return name;
    }

}
