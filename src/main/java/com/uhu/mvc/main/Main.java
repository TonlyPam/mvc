package com.uhu.mvc.main;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.jdbc.Driver;
import com.uhu.mvc.interceptor.PathInterceptor;
import com.uhu.mvc.router.PathRouter;
import com.uhu.mvc.router.impl.AbstractPathRouter;
import com.uhu.mvc.server.JettyWebServer;
import com.uhu.mvc.orm.MybatisPlus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.util.List;

/**
 * @Author Bomber
 * @Description 主方法
 * @Date 2023/10/7 21:29
 * @Version 1.0
 */
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

        // 拦截器
        PathInterceptor interceptor = metadata -> {
            String h = metadata.getHeader("user");
            return !StrUtil.isEmpty(h);
        };

        PathRouter router = new AbstractPathRouter()
                // 设置全局响应类型
                .setGlobalRespContentType(ContentType.JSON)
                // 添加json转换设置 (Long --> String)
                .addJsonMessageConvert(Long.class, String::valueOf)
                // 添加一个Get路由
                .addGet("/hello/{name}/{age}", metadata -> {
                    // 设置响应类型
                    metadata.setRespContentType(ContentType.JSON);
                    return new Student(metadata.getPathVariable("name"),
                            metadata.getPathVariable("age", Integer.class));
                })
                // 添加一个拦截器
                .addInterceptor(List.of("/hello/**"), interceptor, List.of("/hello/zhangsan/**"))
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

@Mapper
interface StudentMapper extends BaseMapper<Student> {}