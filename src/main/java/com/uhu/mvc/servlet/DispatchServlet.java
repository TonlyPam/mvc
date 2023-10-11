package com.uhu.mvc.servlet;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.uhu.mvc.handler.ExceptionHandler;
import com.uhu.mvc.handler.InterceptHandler;
import com.uhu.mvc.handler.PathHandler;
import com.uhu.mvc.interceptor.PathInterceptor;
import com.uhu.mvc.router.PathRouter;
import com.uhu.mvc.metadata.RequestMetadata;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 全局分发Servlet
 * @Name: DispatcherServlet
 * @Author: Bomber
 * @CreateTime: 2023/10/8 8:55
 */
public class DispatchServlet extends HttpServlet {

    private static final Object NULL = new Object();
    private final PathRouter router;

    public DispatchServlet(PathRouter router) {
        this.router = router;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PathHandler handler = router.getPathHandler("get", req.getRequestURI());
        dispatchRequest(req, resp, handler);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PathHandler handler = router.getPathHandler("post", req.getRequestURI());
        dispatchRequest(req, resp, handler);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PathHandler handler = router.getPathHandler("put", req.getRequestURI());
        dispatchRequest(req, resp, handler);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PathHandler handler = router.getPathHandler("delete", req.getRequestURI());
        dispatchRequest(req, resp, handler);
    }

    /**
     * 分发请求
     * @param req 请求
     * @param resp 响应
     * @param handler 处理
     */
    private void dispatchRequest(HttpServletRequest req, HttpServletResponse resp, PathHandler handler) throws IOException {
        if (Objects.isNull(handler)) {
            return404(resp);
            return;
        }

        router.setCors(resp);
        RequestMetadata requestMetadata = new RequestMetadata(req, resp);
        requestMetadata.setRespContentType(router.getGlobalRespContentType());

        Object handle = NULL;
        try {
            // 拦截器验证
            List<PathInterceptor> interceptors = router.getInterceptors(req.getRequestURI());
            for (PathInterceptor interceptor : interceptors) {
                if (!Objects.equals(NULL, handle)) break;
                if (!interceptor.handle(requestMetadata)) {
                    InterceptHandler interceptResp = router.getInterceptResp(interceptor);
                    handle = interceptResp.handle(requestMetadata);
                }
            }

            // 未拦截,请求处理
            if (Objects.equals(NULL, handle)) handle = handler.handle(requestMetadata);
        } catch (Throwable e) {

            ExceptionHandler<Throwable> exceptionHandler = router.getExceptionHandler(e);
            if (Objects.isNull(exceptionHandler)) {
                resp.setStatus(500);
                resp.setContentType(ContentType.TEXT_HTML.getValue());
                PrintWriter writer = resp.getWriter();
                writer.println(e.getClass().getTypeName() + ":" + e.getMessage());
                Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> writer.println(stackTraceElement.toString()));
                writer.close();
                return;
            }

            // 异常处理
            handle = exceptionHandler.handle(e, requestMetadata);
        }

        // 响应结果
        resp.setContentType(requestMetadata.getRespContentType().getValue());
        if (ContentType.JSON.getValue().equals(resp.getContentType())) {
            handle = JSONUtil.toJsonStr(handle);
        }
        resp.getWriter().write(handle.toString());
        resp.getWriter().close();
    }

    /**
     * 响应404
     * @param resp 响应
     */
    private void return404(HttpServletResponse resp) throws IOException {
        resp.setStatus(404);
        resp.getWriter().write("NO FOUND PAGE.");
        resp.getWriter().close();
    }
}
