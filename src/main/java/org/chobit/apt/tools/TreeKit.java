package org.chobit.apt.tools;


import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

/**
 * 处理结构树的一个工具包
 *
 * @author robin
 */
public final class TreeKit {

	/**
	 * toString方法名
	 */
	private static final String TO_STRING_METHOD_NAME = "toString";


	/**
	 * 检查toString方法是否存在
	 *
	 * @param typeElement 类Element
	 * @return true 存在， false 不存在
	 */
	public static boolean isToStringMethodExisted(TypeElement typeElement) {
		for (ExecutableElement exec : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
			if (exec.getSimpleName().contentEquals(TO_STRING_METHOD_NAME) && exec.getParameters().isEmpty()) {
				return true;
			}
		}
		return false;
	}


	private TreeKit() {
		throw new AssertionError("Private constructor, cannot be accessed.");
	}

}
