package com.uhu.mvc.router;

import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uhu.mvc.handler.ExceptionHandler;
import com.uhu.mvc.handler.InterceptHandler;
import com.uhu.mvc.handler.PathHandler;
import com.uhu.mvc.interceptor.InterceptorSetter;
import com.uhu.mvc.interceptor.PathInterceptor;
import com.uhu.mvc.router.impl.AbstractPathRouter;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @Description: 路径处理器
 * @Name: PathHandler
 * @Author: Bomber
 * @CreateTime: 2023/10/8 8:58
 */
public interface PathRouter {

    /**
     * 添加一个get请求
     * @param path 请求路径
     * @param handler 处理器
     * @return 自身
     */
    PathRouter addGet(String path, PathHandler handler);

    /**
     * 添加一个post请求
     * @param path 请求路径
     * @param handler 处理器
     * @return 自身
     */
    PathRouter addPost(String path, PathHandler handler);

    /**
     * 添加一个put请求
     * @param path 请求路径
     * @param handler 处理器
     * @return 自身
     */
    PathRouter addPut(String path, PathHandler handler);

    /**
     * 添加一个delete请求
     * @param path 请求路径
     * @param handler 处理器
     * @return 自身
     */
    PathRouter addDelete(String path, PathHandler handler);

    /**
     * 获取一个路径处理器
     * @param method 请求方法
     * @param uri 路径
     * @return 处理器
     */
    PathHandler getPathHandler(String method, String uri);

    /**
     * 添加异常处理
     * @param handler 处理器
     * @param causeClass 异常类型
     * @param <EX> 异常类型
     * @return 自身
     */
    <EX extends Throwable> AbstractPathRouter addExceptionAdvice(ExceptionHandler<EX> handler, Class<EX> causeClass);

    /**
     * 获取异常处理器
     * @param e 异常
     */
    ExceptionHandler<Throwable> getExceptionHandler(Throwable e);

    /**
     * 设置全局的响应类型
     * @return 自身
     */
    PathRouter setGlobalRespContentType(ContentType contentType);

    /**
     * 获取全局的响应类型
     * @return 响应类型
     */
    ContentType getGlobalRespContentType();

    /**
     * 添加一个拦截器
     * @param paths 拦截的路径集合
     * @param interceptor 拦截器
     * @return 自身
     */
    default InterceptorSetter addInterceptor(List<String> paths, PathInterceptor interceptor) {
        return addInterceptor(paths, interceptor, new LinkedList<>());
    }

    /**
     * 添加一个拦截器
     * @param paths 拦截的路径集合
     * @param interceptor 拦截器
     * @param excludePaths 排除的路径集合
     * @return 自身
     */
    InterceptorSetter addInterceptor(List<String> paths, PathInterceptor interceptor, List<String> excludePaths);

    /**
     * 设置拦截器响应
     * @param interceptor  路径拦截器
     * @param handler 响应处理器
     * @return 自身
     */
    PathRouter setInterceptResp(PathInterceptor interceptor, InterceptHandler handler);

    /**
     * 通过拦截器获取响应
     * @param interceptor 拦截器
     * @return 响应
     */
    InterceptHandler getInterceptResp(PathInterceptor interceptor);

    /**
     * 获取拦截器
     * @param uri 资源
     * @return 列表
     */
    List<PathInterceptor> getInterceptors(String uri);

    /**
     * 设置跨域
     * @param allowOrigin 允许来源地址
     * @param allowMethods 允许的请求方法
     * @param allowHeaders 允许的请求头
     * @return 自身
     */
    PathRouter setCors(String allowOrigin, List<String> allowMethods, List<String> allowHeaders);

    /**
     * 给响应设置跨域
     * @param response 响应
     */
    void setCors(HttpServletResponse response);

    /**
     * 添加json消息转换器
     * @param rowType 原类型
     * @param converter 原类型转目标类型转换器
     * @return 自身
     */
    <T> PathRouter addJsonMessageConvert(Class<T> rowType, Function<T, ?> converter);

    /**
     * 获取ObjectMapper对象
     * @return objectMapper
     */
    ObjectMapper getObjectMapper();
}
