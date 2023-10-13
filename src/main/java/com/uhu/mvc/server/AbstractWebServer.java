package com.uhu.mvc.server;

import com.uhu.mvc.router.PathRouter;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author Bomber
 * @Description 抽象的web服务
 * @Date 2023/10/13 19:40
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public abstract class AbstractWebServer implements Runnable {

    protected String applicationName;
    protected String host = "localhost";
    protected int port = 8080;
    protected final PathRouter router;

    public AbstractWebServer(PathRouter router) {
        this.router = router;
    }
}
