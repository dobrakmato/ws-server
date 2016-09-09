package sk.emefka.ws.wsserver.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import sk.emefka.ws.wsserver.RobinServer;
import sk.emefka.ws.wsserver.http.ClientListHttpHandler;
import sk.emefka.ws.wsserver.http.HttpHandler;
import sk.emefka.ws.wsserver.http.ServerStatusHttpHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpRequestHandler extends ChannelInboundHandlerAdapter {

    public static final HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;

    private final Map<String, HttpHandler> registeredHandlers = new HashMap<>();

    {
        addHandler(RobinServer.getInstance().getConfig().getServerStatusPath(), new ServerStatusHttpHandler());
        addHandler(RobinServer.getInstance().getConfig().getClientListPath(), new ClientListHttpHandler());
    }

    private void addHandler(String uri, HttpHandler handler) {
        registeredHandlers.put(uri, handler);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RobinServer.getInstance().getStatistics().request();
        if (msg instanceof FullHttpRequest) {
            boolean handled = handle(ctx, (FullHttpRequest) msg);
            if (!handled) {
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception caught while executing HttpHandler", cause);
    }

    private boolean handle(ChannelHandlerContext ctx, FullHttpRequest req) {
        if ("websocket".equalsIgnoreCase(req.headers().get("Upgrade", null))) {
            ctx.pipeline().remove(this);
            return false;
        } else {
            FullHttpResponse response = handleHttp(req);
            req.release();
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return true;
        }
    }

    private FullHttpResponse handleHttp(FullHttpRequest req) {
        if (registeredHandlers.get(req.uri()) != null) {
            return registeredHandlers.get(req.uri()).handle(req);
        } else {
            return createBadRequest();
        }
    }

    private FullHttpResponse createBadRequest() {
        return new DefaultFullHttpResponse(HTTP_VERSION, HttpResponseStatus.BAD_REQUEST);
    }
}
