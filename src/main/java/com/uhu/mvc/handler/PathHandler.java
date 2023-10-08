package com.uhu.mvc.handler;

/**
 * @Description: 路径处理器
 * @Name: PathHandler
 * @Author: Bomber
 * @CreateTime: 2023/10/8 9:00
 */
@FunctionalInterface
public interface PathHandler {

    /**
     * 请求数据集合
     * @param metadata 数据集合
     * @return 响应
     */
    Object handle(RequestMetadata metadata) throws Throwable;
}
