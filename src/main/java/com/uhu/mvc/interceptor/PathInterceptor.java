package com.uhu.mvc.interceptor;

import com.uhu.mvc.handler.RequestHandler;
import com.uhu.mvc.metadata.RequestMetadata;

/**
 * @Author Bomber
 * @Description 路径拦截器
 * @Date 2023/10/9 22:19
 * @Version 1.0
 */
@FunctionalInterface
public interface PathInterceptor extends RequestHandler {

    /**
     * 拦截方法
     * @param metadata 数据集合
     * @return 是否允许
     */
    Boolean handle(RequestMetadata metadata);
}
