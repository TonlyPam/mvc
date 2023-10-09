package com.uhu.mvc.server;

import com.uhu.mvc.router.PathRouter;
import com.uhu.mvc.servlet.DispatchServlet;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.Objects;

/**
 * @Description: web服务
 * @Name: JettyWebServer
 * @Author: Bomber
 * @CreateTime: 2023/10/8 8:50
 */
@Data
@Slf4j
@Accessors(chain = true)
public class JettyWebServer implements Runnable {

    private String applicationName;
    private String host = "localhost";
    private int port = 8080;
    private final PathRouter router;

    public JettyWebServer(PathRouter router) {
        this.router = router;
    }

    @Override
    public void run() {
        checkRouter();

        // 路径处理
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.addServlet(new ServletHolder("dispatchServlet", new DispatchServlet(router)), "/");

        // web服务
        Server server = new Server();
        server.setHandler(handler);

        // 连接设置
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setHost(host);
        connector.setName(applicationName);
        server.addConnector(connector);

        // 启动服务
        try {
            server.start();
            log.info("web服务启动成功[{}:{}]", host, port);
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查路由是否合法
     */
    private void checkRouter() {
        if (Objects.isNull(router)) throw new IllegalStateException("路由不可为空");
    }
}
