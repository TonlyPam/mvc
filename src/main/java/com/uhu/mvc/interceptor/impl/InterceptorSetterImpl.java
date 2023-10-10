package com.uhu.mvc.interceptor.impl;

import com.uhu.mvc.handler.InterceptHandler;
import com.uhu.mvc.interceptor.InterceptorSetter;
import com.uhu.mvc.interceptor.PathInterceptor;
import com.uhu.mvc.router.PathRouter;

/**
 * @Description: 拦截器设置实现类
 * @Name: InterceptorSetterImpl
 * @Author: Bomber
 * @CreateTime: 2023/10/10 9:30
 */
public class InterceptorSetterImpl implements InterceptorSetter {

    private final PathRouter router;
    private final PathInterceptor interceptor;

    public InterceptorSetterImpl(PathRouter router, PathInterceptor interceptor) {
        this.router = router;
        this.interceptor = interceptor;
    }

    @Override
    public PathRouter setInterceptResp(InterceptHandler handler) {
        router.setInterceptResp(interceptor, handler);
        return router;
    }
}
