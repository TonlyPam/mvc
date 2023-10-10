package com.uhu.mvc;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import com.uhu.mvc.interceptor.PathInterceptor;
import com.uhu.mvc.router.PathRouter;
import com.uhu.mvc.router.impl.AbstractPathRouter;
import com.uhu.mvc.server.JettyWebServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Bomber
 * @Description 主方法
 * @Date 2023/10/7 21:29
 * @Version 1.0
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // 拦截器
        PathInterceptor interceptor = metadata -> {
            String h = metadata.getHeader("user");
            return !StrUtil.isEmpty(h);
        };

        PathRouter router = new AbstractPathRouter()
                // 设置全局响应类型
                .setGlobalRespContentType(ContentType.JSON)
                // 添加一个Get路由
                .addGet("/hello/{name}/{age}", metadata -> {
                    // 设置响应类型
                    metadata.setRespContentType(ContentType.JSON);
                    return new Student(metadata.getPathVariable("name"),
                            metadata.getPathVariable("age", Integer.class));
                })
                // 添加一个拦截器
                .addInterceptor(List.of("/hello/**"), interceptor)
                .setInterceptResp(metadata -> "拦截成功");

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
