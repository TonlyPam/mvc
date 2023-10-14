# MY-MVC

This is a minimalist mvc framework that can help you quickly develop a web app.

**Web container：Jetty**

**programming style ：Router**

## ONE、quick start

### 1.Introducing Dependencies

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>my-mvc</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2.Sample

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

start over...

## TOW、About PathRouter

Processing and distribution functions for requests

- Add GET request processing`PathRouter addGet(String path, PathHandler handler)`
- Add POST request processing`PathRouter addPost(String path, PathHandler handler)`
- Add PUT request processing`PathRouter addPut(String path, PathHandler handler)`
- Add DELETE request processing`PathRouter addDelete(String path, PathHandler handler)`
- Add global exception handling`<EX extends Throwable> AbstractPathRouter addExceptionAdvice(ExceptionHandler<EX> handler, Class<EX> causeClass)`
- Set global response type`PathRouter setGlobalRespContentType(ContentType contentType)`
- Set global cross domain`PathRouter setCors(String allowOrigin, List<String> allowMethods, List<String> allowHeaders);`

  sample：
    ```java
        // Set global cross domain
        router.setCors("*", List.of("*"), List.of("*"))
    ```

### About the operation of interceptors

In this framework, it is recommended to use PathInterceptor instead of Filter in Servlet, as the granularity of Interceptor settings can be better controlled, while supporting wildcard characters such as`/*`and`**`

`InterceptorSetter addInterceptor(List<String> paths, PathInterceptor interceptor)`

After adding the interceptor, it will respond with a type of interceptor setting, and the interceptor result needs to be set

```java
PathRouter router = new AbstractPathRouter()
// Add an interceptor
                .addInterceptor(List.of("/hello/**"), interceptor)
                .setInterceptResp(metadata -> "拦截成功");
```

### Settings for JSON return value conversion

In the PathRouter interface, there is a`<T> PathRouter addJsonMessageConvert(Class<T> rowType, Function<T, ?> converter);`Method for developers to customize the return value type conversion of JSON

Scenario: Frequently returning objects with Long type attributes may result in loss of tail precision when passed back to the front-end. Therefore, a JSON converter with Long==>String needs to be configured

Code：
```java
// adding the interceptor (Long --> String)
pathRouter.addJsonMessageConvert(Long.class, (value) -> {
            return String.valueOf(value);
        })
```


## THREE、About RequestMetadata

Most of the functions for storing and processing web requests

- get request
- get response
- get requestParam
- get session
- get pathVariable
- get header
- set RespContentType`setRespContentType`

## About frame of ORM

The built-in `MybatisPlus` tool object allows you to obtain DAO (MAPPER) and Service objects as follows

```java
public class Main {
    public static void main(String[] args) throws Exception {
        // build instance
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
```# MY-MVC

This is a minimalist mvc framework that can help you quickly develop a web app.

**Web container：Jetty**

**programming style ：Router**

## ONE、quick start

### 1.Introducing Dependencies

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>my-mvc</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2.Sample

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

start over...

## TOW、About PathRouter

Processing and distribution functions for requests

- Add GET request processing`PathRouter addGet(String path, PathHandler handler)`
- Add POST request processing`PathRouter addPost(String path, PathHandler handler)`
- Add PUT request processing`PathRouter addPut(String path, PathHandler handler)`
- Add DELETE request processing`PathRouter addDelete(String path, PathHandler handler)`
- Add global exception handling`<EX extends Throwable> AbstractPathRouter addExceptionAdvice(ExceptionHandler<EX> handler, Class<EX> causeClass)`
- Set global response type`PathRouter setGlobalRespContentType(ContentType contentType)`
- Set global cross domain`PathRouter setCors(String allowOrigin, List<String> allowMethods, List<String> allowHeaders);`

  sample：
    ```java
        // Set global cross domain
        router.setCors("*", List.of("*"), List.of("*"))
    ```

### About the operation of interceptors

In this framework, it is recommended to use PathInterceptor instead of Filter in Servlet, as the granularity of Interceptor settings can be better controlled, while supporting wildcard characters such as`/*`and`**`

`InterceptorSetter addInterceptor(List<String> paths, PathInterceptor interceptor)`

After adding the interceptor, it will respond with a type of interceptor setting, and the interceptor result needs to be set

```java
PathRouter router = new AbstractPathRouter()
// Add an interceptor
                .addInterceptor(List.of("/hello/**"), interceptor)
                .setInterceptResp(metadata -> "拦截成功");
```

### Settings for JSON return value conversion

In the PathRouter interface, there is a`<T> PathRouter addJsonMessageConvert(Class<T> rowType, Function<T, ?> converter);`Method for developers to customize the return value type conversion of JSON

Scenario: Frequently returning objects with Long type attributes may result in loss of tail precision when passed back to the front-end. Therefore, a JSON converter with Long==>String needs to be configured

Code：
```java
// adding the interceptor (Long --> String)
pathRouter.addJsonMessageConvert(Long.class, (value) -> {
            return String.valueOf(value);
        })
```


## THREE、About RequestMetadata

Most of the functions for storing and processing web requests

- get request
- get response
- get requestParam
- get session
- get pathVariable
- get header
- set RespContentType`setRespContentType`

## About frame of ORM

The built-in `MybatisPlus` tool object allows you to obtain DAO (MAPPER) and Service objects as follows

```java
public class Main {
    public static void main(String[] args) throws Exception {
        // build instance
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