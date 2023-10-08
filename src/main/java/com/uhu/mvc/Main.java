package com.uhu.mvc;


import cn.hutool.http.ContentType;
import com.uhu.mvc.handler.ExceptionHandler;
import com.uhu.mvc.handler.PathRouter;
import com.uhu.mvc.handler.RequestMetadata;
import com.uhu.mvc.handler.impl.AbstractPathRouter;
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

        JettyWebServer server = new JettyWebServer(router);
        server.run();
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Student {
    String name;
    Integer age;
}
