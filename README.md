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

## 二、关于RequestMetadata

存储处理web请求的大部分功能

- 获取request
- 获取response
- 获取请求param
- 获取session
- 获取pathVariable
- 获取header
- 设置响应类型`setRespContentType`