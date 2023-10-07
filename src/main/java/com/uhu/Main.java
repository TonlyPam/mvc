package com.uhu;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Bomber
 * @Description 主方法
 * @Date 2023/10/7 21:29
 * @Version 1.0
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // 路径处理
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.getWriter().println("hello world!");
            }
        }), "/");

        // web服务
        Server server = new Server();
        server.setHandler(handler);

        // 连接设置
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        connector.setHost("localhost");
        connector.setName("webapp");
        server.addConnector(connector);

        // 启动服务
        server.start();
        server.join();
    }
}
