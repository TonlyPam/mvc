package com.uhu.mvc.interceptor;

import com.uhu.mvc.handler.InterceptHandler;
import com.uhu.mvc.router.PathRouter;

/**
 * @Description: 拦截器设置类
 * @Name: InterceptorSetter
 * @Author: Bomber
 * @CreateTime: 2023/10/10 9:29
 */
public interface InterceptorSetter {
    /**
     * 设置拦截器响应
     * @param handler 拦截器
     * @return 自身
     */
    PathRouter setInterceptResp (InterceptHandler handler);
}
