# MY-MVC

这是一个极简的mvc框架，可以帮助你急速开发一个webapp。

**使用的web容器：Jetty**

**开发方式：Router**

## 一、快速上手

### 1.引入依赖

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>my-mvc</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2.样例

```java
package com.uhu.mvc;


import cn.hutool.http.ContentType;
import com.uhu.mvc.router.PathRouter;
import com.uhu.mvc.router.impl.AbstractPathRouter;
import com.uhu.mvc.server.JettyWebServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Bomber
 * @Description 主方法
 * @Date 2023/10/7 21:29
 * @Version 1.0
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // 创建路由
        PathRouter router = new AbstractPathRouter()
                // 添加路径映射处理
                .addGet("/hello/{name}/{age}", metadata -> {
                    // 设置响应类型，之后return的结果会转为json后响应
                    metadata.setRespContentType(ContentType.JSON);
                    // 返回结果
                    return new Student(metadata.getPathVariable("name"),
                            metadata.getPathVariable("age", Integer.class));
                });

        // 创建服务并启动
        new JettyWebServer(router).setHost("localhost").setPort(8080).run();
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Student {
    String name;
    Integer age;
}

```

启动即可

## 二、关于PathRouter

进行请求的处理分发功能

- 添加GET请求处理`PathRouter addGet(String path, PathHandler handler)`
- 添加POST请求处理`PathRouter addPost(String path, PathHandler handler)`
- 添加PUT请求处理`PathRouter addPut(String path, PathHandler handler)`
- 添加DELETE请求处理`PathRouter addDelete(String path, PathHandler handler)`
- 添加全局异常处理`<EX extends Throwable> AbstractPathRouter addExceptionAdvice(ExceptionHandler<EX> handler, Class<EX> causeClass)`
- 设置全局响应类型`PathRouter setGlobalRespContentType(ContentType contentType)`
- 设置全局跨域`PathRouter setCors(String allowOrigin, List<String> allowMethods, List<String> allowHeaders);`

  样例：
    ```java
        // 设置全局跨域
        router.setCors("*", List.of("*"), List.of("*"))
    ```

### 关于拦截器的操作

在这个框架中，建议使用PathInterceptor而非使用Servlet中的Filter，因为Interceptor的设置颗粒度可以更好的把控，同时支持`/*`和`**`的通配符

`InterceptorSetter addInterceptor(List<String> paths, PathInterceptor interceptor)`

添加完毕拦截器后会响应一个拦截器设置类型，需要设置拦截的结果返回

```java
PathRouter router = new AbstractPathRouter()
// 添加一个拦截器
                .addInterceptor(List.of("/hello/**"), interceptor)
                .setInterceptResp(metadata -> "拦截成功");
```

### 关于JSON返回值转换的设置

在PathRouter接口中，存在`<T> PathRouter addJsonMessageConvert(Class<T> rowType, Function<T, ?> converter);`方法，供开发者自定义JSON的返回值类型转换

场景：经常返回的对象中有Long类型的属性，传递回前端的时候会出现尾精度丢失问题，就需要配置一个Long ==> String的JSON转换器

代码：
```java
// 添加json转换设置 (Long --> String)
pathRouter.addJsonMessageConvert(Long.class, (value) -> {
            return String.valueOf(value);
        })
```


## 三、关于RequestMetadata

存储处理web请求的大部分功能

- 获取request
- 获取response
- 获取请求param
- 获取session
- 获取pathVariable
- 获取header
- 设置响应类型`setRespContentType`

## 关于ORM框架

自带了`MybatisPlus`的工具对象，如下即可获取DAO（MAPPER）和SERVICE对象

```java
public class Main {
  public static void main(String[] args) throws Exception {
    // 创建orm框架的工具类
    MybatisPlus mybatisPlus = MybatisPlus.build()
            .setId("default")
            .setDriver(Driver.class.getTypeName())
            .setUrl("jdbc:mysql://localhost:3306/dining_app")
            .setUsername("root")
            .setPassword("123456")
            .setDataSourceFactory(new PooledDataSourceFactory())
            .setTransactionFactory(new JdbcTransactionFactory())
            .setMapperPackage("com.uhu.mvc.main")
            .build();

    StudentMapper studentMapper = mybatisPlus.getMapper(StudentMapper.class);
    ServiceImpl<StudentMapper, Student> studentService = mybatisPlus.getService(StudentMapper.class, Student.class);
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class Student {
    String name;
    Integer age;
  }

  @Mapper
  interface StudentMapper extends BaseMapper<Student> {}
```