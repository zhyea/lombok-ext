package org.chobit.core;


import com.sun.source.tree.ClassTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.Set;


/**
 * @author robin
 */
@SupportedAnnotationTypes({"org.chobit.core.ToJsonString"})
public class ToJsonStringProcessor extends AbstractProcessor {


	private Trees trees;
	private TreeMaker treeMaker;
	private Names names;


	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		Context context = ((JavacProcessingEnvironment) processingEnv).getContext();

		this.trees = JavacTrees.instance(context);
		this.treeMaker = TreeMaker.instance(context);
		this.names = Names.instance(context);
	}


	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		Set<TypeElement> typeElements = ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(ToJsonString.class));

		for (TypeElement ele : typeElements) {
			this.makeToStringMethod(ele);
		}

		return true;
	}


	private void makeToStringMethod(TypeElement typeElement) {
		JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);
		JCTree.JCExpression returnType = treeMaker.Ident(names.fromString(String.class.getName()));
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


	/**
	 * 获取方法表达式
	 */
	private JCTree.JCExpression getMethodExpression(String className, String methodName) {
		JCTree.JCExpression ident = getClassExpression(className);
		return treeMaker.Select(ident, names.fromString(methodName));
	}


	/**
	 * 获取类表达式
	 */
	private JCTree.JCExpression getClassExpression(String className) {
		String[] arr = className.split("\\.");
		JCTree.JCExpression ident = treeMaker.Ident(names.fromString(arr[0]));

		for (int i = 1; i < arr.length; i++) {
			ident = treeMaker.Select(ident, names.fromString(arr[i]));
		}

		return ident;
	}
}
