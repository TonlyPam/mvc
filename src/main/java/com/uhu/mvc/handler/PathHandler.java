package com.uhu.mvc.handler;

import com.uhu.mvc.metadata.RequestMetadata;

/**
 * @Description: 路径处理器
 * @Name: PathHandler
 * @Author: Bomber
 * @CreateTime: 2023/10/8 9:00
 */
@FunctionalInterface
public interface PathHandler extends RequestHandler {

    /**
     * 请求数据集合
     * @param metadata 数据集合
     * @return 响应
     */
    Object handle(RequestMetadata metadata) throws Throwable;
}
