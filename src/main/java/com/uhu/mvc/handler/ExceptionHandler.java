package com.uhu.mvc.handler;

import com.uhu.mvc.metadata.RequestMetadata;

/**
 * @Description: 异常处理器
 * @Name: ExceptionHandler
 * @Author: Bomber
 * @CreateTime: 2023/10/8 9:57
 */
@FunctionalInterface
public interface ExceptionHandler<EX extends Throwable> {

    /**
     * 异常处理
     * @param cause 异常
     * @return 结果
     */
    Object handle(EX cause, RequestMetadata metadata);
}
