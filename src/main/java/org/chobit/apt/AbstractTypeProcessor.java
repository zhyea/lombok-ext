package org.chobit.apt;

import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public abstract class AbstractTypeProcessor extends AbstractProcessor {


    private Trees trees;
    private TreeMaker treeMaker;
    private Names names;
    private Messager messager;


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

        this.messager = processingEnv.getMessager();

        if (processingEnv instanceof JavacProcessingEnvironment) {

            Context context = ((JavacProcessingEnvironment) processingEnv).getContext();

            this.trees = JavacTrees.instance(context);
            this.treeMaker = TreeMaker.instance(context);
            this.names = Names.instance(context);

            super.init(processingEnv);
        } else {
            messager.printMessage(Diagnostic.Kind.WARNING, "@ToJsonString is not supported.");
        }
    }


    /**
     * 填充import内容
     */
    protected void makeImport(TypeElement typeElement, Class<?> clazz) {
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) trees.getPath(typeElement).getCompilationUnit();
        JCTree.JCExpression importExp = getClassExpression(clazz.getName());
        JCTree.JCImport importVal = getTreeMaker().Import(importExp, false);
        compilationUnit.defs.append(importVal);
    }


    /**
     * 获取方法表达式
     */
    protected JCTree.JCExpression getMethodExpression(String className, String methodName) {
        JCTree.JCExpression ident = getClassExpression(className);
        return treeMaker.Select(ident, names.fromString(methodName));
    }


    /**
     * 获取类表达式
     */
    protected JCTree.JCExpression getClassExpression(String className) {
        String[] arr = className.split("\\.");
        JCTree.JCExpression ident = treeMaker.Ident(names.fromString(arr[0]));

        for (int i = 1; i < arr.length; i++) {
            ident = treeMaker.Select(ident, names.fromString(arr[i]));
        }

        return ident;
    }


    public Trees getTrees() {
        return trees;
    }

    public TreeMaker getTreeMaker() {
        return treeMaker;
    }

    public Names getNames() {
        return names;
    }

    public Messager getMessager() {
        return messager;
    }
}
