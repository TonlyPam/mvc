package com.uhu.mvc.handler;

import com.uhu.mvc.metadata.RequestMetadata;

/**
 * @Author Bomber
 * @Description 处理器
 * @Date 2023/10/9 22:41
 * @Version 1.0
 */
@FunctionalInterface
public interface RequestHandler {

    /**
     * 处理
     * @param metadata 数据集
     * @return 响应
     */
    Object handle(RequestMetadata metadata) throws Throwable;
}
