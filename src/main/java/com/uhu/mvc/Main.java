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
        PathRouter router = new AbstractPathRouter()
                .addGet("/hello/{name}/{age}", metadata -> {
                    metadata.setRespContentType(ContentType.JSON);
                    return new Student(metadata.getPathVariable("name"),
                            metadata.getPathVariable("age", Integer.class));
                });

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
