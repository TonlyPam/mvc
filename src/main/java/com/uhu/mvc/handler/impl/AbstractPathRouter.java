package com.uhu.mvc.handler.impl;

import com.uhu.mvc.handler.ExceptionHandler;
import com.uhu.mvc.handler.PathHandler;
import com.uhu.mvc.handler.PathRouter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 默认的路由
 * @Name: DefaultPathRouter
 * @Author: Bomber
 * @CreateTime: 2023/10/8 10:10
 */
public class AbstractPathRouter implements PathRouter {

    private static final ThreadLocal<Map<String, String>> PATH_VARIABLE_MAP_HOLDER = new ThreadLocal<>();

    private final Map<String, ExceptionHandler<Throwable>> exceptionHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, PathHandler> getHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, PathHandler> postHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, PathHandler> putHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, PathHandler> deleteHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, PathHandler>> methodMap = Map.of(
            "get", getHandlerMap,
            "post", postHandlerMap,
            "put", putHandlerMap,
            "delete", deleteHandlerMap);

    @Override
    public AbstractPathRouter addGet(String path, PathHandler handler) {
        getHandlerMap.put(path, handler);
        return this;
    }

    @Override
    public AbstractPathRouter addPost(String path, PathHandler handler) {
        postHandlerMap.put(path, handler);
        return this;
    }

    @Override
    public AbstractPathRouter addPut(String path, PathHandler handler) {
        putHandlerMap.put(path, handler);
        return this;
    }

    @Override
    public AbstractPathRouter addDelete(String path, PathHandler handler) {
        deleteHandlerMap.put(path, handler);
        return this;
    }

    @Override
    public PathHandler getPathHandler(String method, String uri) {
        Map<String, PathHandler> handlerMap = methodMap.get(method);
        for (Map.Entry<String, PathHandler> entry : handlerMap.entrySet()) {
            if (matchUri(entry.getKey(), uri)) return entry.getValue();
        }
        return null;
    }

    /**
     * 获取路径变量集合
     * @return 路径变量集合
     */
    public static Map<String, String> getPathVariableMap() {
        return PATH_VARIABLE_MAP_HOLDER.get();
    }

    /**
     * 匹配uri
     * @param targetUri 目标yri
     * @param uri 当前uri
     * @return 结果
     */
    private boolean matchUri(String targetUri, String uri) {
        Map<String, String> pathVariableMap = new HashMap<>();

        String[] targetUriSplit = targetUri.split("/");
        String[] uriSplit = uri.split("/");
        try {
            for (int i = 0; i < targetUriSplit.length; i++) {
                String template = targetUriSplit[i];

                // 如果是路径参数
                if (template.startsWith("{") && template.endsWith("}")) {
                    template = template.substring(1, template.length() - 1);
                    pathVariableMap.put(template, uriSplit[i]);
                    continue;
                }

                // 普通路径
                if (!template.equals(uriSplit[i])) {
                    return false;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }

        // 匹配成功
        PATH_VARIABLE_MAP_HOLDER.set(pathVariableMap);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <EX extends Throwable> AbstractPathRouter addExceptionAdvice(ExceptionHandler<EX> handler, Class<EX> causeClass) {
        checkException(causeClass);
        exceptionHandlerMap.put(causeClass.getTypeName(), (ExceptionHandler<Throwable>) handler);
        return this;
    }

    @Override
    public ExceptionHandler<Throwable> getExceptionHandler(Throwable e) {
        return exceptionHandlerMap.get(e.getClass().getTypeName());
    }

    /**
     * 检查异常合法性
     * @param causeClass 异常处理器
     */
    private void checkException(Class<?> causeClass) {
        if (Objects.nonNull(exceptionHandlerMap.get(causeClass.getTypeName()))) throw new IllegalArgumentException("已存在此类型的异常处理器");
    }
}
