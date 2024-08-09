# Lombok-ext


主要用来维护一些生成式注解，是对lombok的一个补充或扩展。

## @ToJsonString

使用`@ToJsonString` 可以重写`toString()`方法。并将当前对象以json字符串的形式返回。  
底层依赖的是**jackson**，可以使用jackson各种注解对对象进行各种处理。

在idea使用时需要添加一些设置：在 settings–>Build, Execution, deployment–>Compiler，为Shared build process VM options选项添加如下的配置：
```text
-Djps.track.ap.dependencies=false
```

