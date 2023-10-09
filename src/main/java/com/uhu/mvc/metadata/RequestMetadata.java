package com.uhu.mvc.metadata;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import com.uhu.mvc.router.impl.AbstractPathRouter;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

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
    @SuppressWarnings("unchecked")
    public <T> T getPathVariable(String key, Class<T> clazz) {
        if (Objects.isNull(pathVariableMap)) return null;
        String value = pathVariableMap.get(key);
        try {
            return JSONUtil.toBean(value, clazz);
        } catch (JSONException e) {
            if (Objects.isNull(value)) return null;
            Object result = null;
            if (Byte.class.equals(clazz)) result = Byte.valueOf(value);
            if (Short.class.equals(clazz)) result = Short.valueOf(value);
            if (Integer.class.equals(clazz)) result = Integer.valueOf(value);
            if (Long.class.equals(clazz)) result = Long.valueOf(value);
            if (Float.class.equals(clazz)) result = Float.valueOf(value);
            if (Double.class.equals(clazz)) result = Double.valueOf(value);
            if (Boolean.class.equals(clazz)) result = Boolean.valueOf(value);
            if (Character.class.equals(clazz) && StrUtil.isNotBlank(value)) result = value.charAt(0);
            return (T) result;
        }
    }

    /**
     * 获取路径变量
     * @param key
     * @return
     */
    public String getPathVariable(String key) {
        if (Objects.isNull(pathVariableMap)) return null;
        return pathVariableMap.get(key);
    }

    /**
     * 获取请求头
     * @param key key
     * @return value
     */
    public String getHeader(String key) {
        return request.getHeader(key);
    }
}
