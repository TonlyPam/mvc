package com.uhu.mvc.handler;

import cn.hutool.http.ContentType;
import com.uhu.mvc.handler.impl.AbstractPathRouter;

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
}
