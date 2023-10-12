package com.uhu.mvc.router.impl;

import cn.hutool.http.ContentType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.uhu.mvc.handler.ExceptionHandler;
import com.uhu.mvc.handler.InterceptHandler;
import com.uhu.mvc.handler.PathHandler;
import com.uhu.mvc.interceptor.InterceptorSetter;
import com.uhu.mvc.interceptor.PathInterceptor;
import com.uhu.mvc.interceptor.impl.InterceptorSetterImpl;
import com.uhu.mvc.router.PathRouter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @Description: 默认的路由
 * @Name: DefaultPathRouter
 * @Author: Bomber
 * @CreateTime: 2023/10/8 10:10
 */
public class AbstractPathRouter implements PathRouter {

    private static final ThreadLocal<Map<String, String>> PATH_VARIABLE_MAP_HOLDER = new ThreadLocal<>();
    private final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, ExceptionHandler<Throwable>> exceptionHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, PathHandler> getHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, PathHandler> postHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, PathHandler> putHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, PathHandler> deleteHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, List<PathInterceptor>> interceptorMap = new ConcurrentHashMap<>();
    private final Map<String, List<String>> excludeInterceptMap = new ConcurrentHashMap<>();
    private final Map<PathInterceptor, InterceptHandler> interceptorHandlerMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, PathHandler>> methodMap = Map.of(
            "get", getHandlerMap,
            "post", postHandlerMap,
            "put", putHandlerMap,
            "delete", deleteHandlerMap);

    private ContentType globalContentType = ContentType.TEXT_HTML;
    private Function<HttpServletResponse, HttpServletResponse> setRespFunc = response -> response;

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
            if (matchUri(entry.getKey(), uri, "{}")) return entry.getValue();
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

    @Override
    public AbstractPathRouter setGlobalRespContentType(ContentType contentType) {
        if (Objects.isNull(contentType)) throw new IllegalArgumentException("响应类型不能是null");
        globalContentType = contentType;
        return this;
    }

    @Override
    public ContentType getGlobalRespContentType() {
        return globalContentType;
    }

    @Override
    public InterceptorSetter addInterceptor(List<String> paths, PathInterceptor interceptor, List<String> excludePaths) {
        if (Objects.isNull(interceptor)) throw new IllegalArgumentException("拦截器不能为空");
        paths.forEach(path -> {
            interceptorMap.putIfAbsent(path, new LinkedList<>());
            excludeInterceptMap.putIfAbsent(path + interceptor, new LinkedList<>());
            List<String> excludePathList = excludeInterceptMap.get(path + interceptor);
            List<PathInterceptor> interceptors = interceptorMap.get(path);
            excludePathList.addAll(excludePaths);
            interceptors.add(interceptor);
        });
        return new InterceptorSetterImpl(this, interceptor);
    }

    @Override
    public AbstractPathRouter setInterceptResp(PathInterceptor interceptor, InterceptHandler handler) {
        interceptorHandlerMap.put(interceptor, handler);
        return this;
    }

    @Override
    public InterceptHandler getInterceptResp(PathInterceptor interceptor) {
        return interceptorHandlerMap.get(interceptor);
    }

    @Override
    public List<PathInterceptor> getInterceptors(String uri) {
        List<PathInterceptor> interceptors = new LinkedList<>();
        for (Map.Entry<String, List<PathInterceptor>> entry : interceptorMap.entrySet()) {
            // 不匹配直接下一个
            String path = entry.getKey();
            if (!matchUri(path, uri, "*")) continue;

            // 获取拦截器列表
            List<PathInterceptor> interceptorList = entry.getValue();
            outer:
            for (PathInterceptor interceptor : interceptorList) {

                // 获取排除的路径
                List<String> excludePaths = excludeInterceptMap.get(path + interceptor);
                for (String excludePath : excludePaths) {

                    // 如果是排除的路径，则换下一个拦截器
                    if (matchUri(excludePath, uri, "*")) {
                        continue outer;
                    }
                }
                // 不是排除的路径，添加到返回
                interceptors.add(interceptor);
            }
        }
        return interceptors;
    }

    @Override
    public AbstractPathRouter setCors(String allowOrigin, List<String> allowMethods, List<String> allowHeaders) {
        Function<HttpServletResponse, HttpServletResponse> previous = this.setRespFunc;
        this.setRespFunc = response -> {
            response = previous.apply(response);
            response.setHeader("Access-Control-Allow-Origin", allowOrigin);
            response.setHeader("Access-Control-Allow-Methods", list2String(allowMethods));
            response.setHeader("Access-Control-Allow-Headers", list2String(allowHeaders));
            return response;
        };
        return this;
    }

    @Override
    public void setCors(HttpServletResponse response) {
        this.setRespFunc.apply(response);
    }

    /**
     * list转为string
     * @param allowMethods 允许的方法
     * @return 结果
     */
    private String list2String(List<String> allowMethods) {
        if (allowMethods.size() != 0){
            StringBuilder sb = new StringBuilder();
            allowMethods.forEach(method -> sb.append(method).append(","));
            return sb.delete(sb.length() - 1, sb.length()).toString();
        }
        return "";
    }

    /**
     * 检查异常合法性
     * @param causeClass 异常处理器
     */
    private void checkException(Class<?> causeClass) {
        if (Objects.nonNull(exceptionHandlerMap.get(causeClass.getTypeName())))
            throw new IllegalArgumentException("已存在此类型的异常处理器");
    }

    /**
     * 匹配uri
     * @param targetUri 目标yri
     * @param uri 当前uri
     * @param genericSign 匹配标识
     * @return 结果
     */
    private boolean matchUri(String targetUri, String uri, String genericSign) {
        Map<String, String> pathVariableMap = new HashMap<>();

        String[] targetUriSplit = targetUri.split("/");
        String[] uriSplit = uri.split("/");
        try {
            for (int i = 0; i < targetUriSplit.length; i++) {
                String template = targetUriSplit[i];

                // 如果是路径参数
                switch (genericSign) {
                    case "{}" -> {
                        if (template.startsWith("{") && template.endsWith("}")) {
                            template = template.substring(1, template.length() - 1);
                            pathVariableMap.put(template, uriSplit[i]);
                            continue;
                        }
                    }
                    case "*" -> {
                        try {
                            boolean previousIsGeneric = false;
                            // 字符逐个匹配
                            for (int j = 0; j < template.length(); j++) {
                                if (template.charAt(j) != '*') {
                                    // 判断是否相等
                                    if (template.charAt(j) != uriSplit[i].charAt(j)) {
                                        return false;
                                    } else {
                                        previousIsGeneric = false;
                                    }
                                } else {
                                    // 如果这个是通配符，上一个也是，那么就是匹配的
                                    if (previousIsGeneric) return true;
                                    previousIsGeneric = true;
                                }
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            return false;
                        }
                    }
                }

                // 普通路径
                if (!template.equals(uriSplit[i]) && "{}".equals(genericSign)) {
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
    public <T> PathRouter addJsonMessageConvert(Class<T> rowType, Function<T, ?> converter) {
        SimpleModule simpleModule = new SimpleModule();
        JsonSerializer<T> serializer = new JsonSerializer<>() {
            @Override
            public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                Object out = converter.apply(t);
                jsonGenerator.writeObject(out);
            }
        };
        simpleModule.addSerializer(rowType, serializer);
        mapper.registerModule(simpleModule);
        return this;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return mapper;
    }
}
