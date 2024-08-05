package org.chobit.apt;


import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;


/**
 * @author robin
 */
public class ToJsonStringProcessor extends AbstractTypeProcessor {


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annoTypes = new HashSet<>(1);
        annoTypes.add(ToJsonString.class.getCanonicalName());
        return annoTypes;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!isInitialized()) {
            return true;
        }

        Set<TypeElement> typeElements = ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(ToJsonString.class));
        if (typeElements.isEmpty()) {
            return true;
        }

        for (TypeElement ele : typeElements) {
            this.makeToStringMethod(ele);
        }

        return true;
    }


    private void makeToStringMethod(TypeElement typeElement) {
        makeImport(typeElement, JsonStringSerializer.class);


        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);
        JCTree.JCExpression returnType = getClassExpression(String.class.getName());
        Name methodName = names.fromString("toString");
        List<JCTree.JCVariableDecl> parameters = List.nil();
        List<JCTree.JCTypeParameter> generics = List.nil();
        List<JCTree.JCExpression> exceptThrows = List.nil();
        JCTree.JCBlock methodBody = makeToStringBody();

        JCTree.JCMethodDecl methodDecl = treeMaker.MethodDef(modifiers, methodName, returnType, generics, parameters, exceptThrows, methodBody, null);
        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(typeElement);

        classDecl.defs.append(methodDecl);
    }


    private JCTree.JCBlock makeToStringBody() {
        JCTree.JCExpression serializerIdent = getMethodExpression(JsonStringSerializer.class.getName(), "toJson");
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        JCTree.JCExpressionStatement statement = treeMaker.Exec(
                treeMaker.Apply(
                        List.nil(),
                        serializerIdent,
                        List.of(treeMaker.Ident(names.fromString("this")))
                )
        );
        statements.append(statement);

        return treeMaker.Block(0, statements.toList());
    }


}
