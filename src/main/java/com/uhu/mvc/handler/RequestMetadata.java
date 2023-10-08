package com.uhu.mvc.handler;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.uhu.mvc.handler.impl.AbstractPathRouter;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * @Description: 请求数据
 * @Name: RequestMetadata
 * @Author: Bomber
 * @CreateTime: 2023/10/8 9:01
 */
@Data
public class RequestMetadata {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private Map<String, String> pathVariableMap;
    private JSON requestBody;
    private ContentType respContentType = ContentType.TEXT_HTML;

    public RequestMetadata(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.pathVariableMap = AbstractPathRouter.getPathVariableMap();
        if (ContentType.JSON.getValue().equals(request.getContentType())) {
            StringBuilder sb = new StringBuilder();
            try {
                request.getReader().lines().forEach(sb::append);
                requestBody = JSONUtil.parse(sb.toString());
            } catch (IOException ignored) {}
        }
    }

    /**
     * 获取请求参数
     * @param key 参数
     * @return
     */
    public String getParameter(String key) {
        return request.getParameter(key);
    }

    /**
     * 获取session
     * @return session
     */
    public HttpSession getSession() {
        return request.getSession();
    }

    /**
     * 获取路径变量
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getPathVariable(String key, Class<T> clazz) {
        return JSONUtil.toBean(pathVariableMap.get(key), clazz);
    }
}
