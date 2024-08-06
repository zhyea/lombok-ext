package org.chobit.apt;


import java.lang.annotation.*;

/**
 * 标记注解
 * <p>
 * 会为存在该注解的类重写toString()方法，并将结果序列化为json字符串
 *
 * @author robin
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ToJsonString {

}
