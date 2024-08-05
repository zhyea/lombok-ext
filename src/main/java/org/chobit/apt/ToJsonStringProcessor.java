package org.chobit.apt;


import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
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

		JCTree.JCModifiers modifiers = getTreeMaker().Modifiers(Flags.PUBLIC, List.nil());
		JCTree.JCExpression returnType = getClassExpression(String.class.getName());

		List<JCTree.JCVariableDecl> parameters = List.nil();
		List<JCTree.JCTypeParameter> generics = List.nil();
		Name methodName = getName("toString");
		List<JCTree.JCExpression> exceptThrows = List.nil();

		JCTree.JCBlock methodBody = makeToStringBody();

		JCTree.JCMethodDecl methodDecl =
				getTreeMaker().MethodDef(modifiers, methodName, returnType, generics, parameters, exceptThrows,
						methodBody, null);

		JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) getTrees().getTree(typeElement);

		classDecl.defs.append(methodDecl);
	}


	private JCTree.JCBlock makeToStringBody() {
		JCTree.JCExpression serializerIdent = getMethodExpression(JsonStringSerializer.class.getName(), "toJson");
		List<JCTree.JCExpression> toJsonArgs = List.from(List.of(getTreeMaker().Ident(getName("this"))));


		JCTree.JCReturn returnStatement = getTreeMaker().Return(
				getTreeMaker().Apply(List.nil(), serializerIdent, toJsonArgs)
		);

		return getTreeMaker().Block(0, List.of(returnStatement));
	}


}
