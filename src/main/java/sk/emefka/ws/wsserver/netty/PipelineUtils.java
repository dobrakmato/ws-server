package sk.emefka.ws.wsserver.netty;

import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import io.netty.util.internal.PlatformDependent;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import sk.emefka.ws.wsserver.Client;
import sk.emefka.ws.wsserver.Configuration;
import sk.emefka.ws.wsserver.RobinServer;
import sk.emefka.ws.wsserver.Utils;

import java.util.concurrent.ThreadFactory;

@Slf4j
@UtilityClass
public class PipelineUtils {

    // Attribute keys.
    public static final AttributeKey<Client> KEY_CLIENT = AttributeKey.valueOf("Client");

    // WebSocket bind listener.
    public static ChannelFutureListener WS_BIND_LISTENER = (ChannelFutureListener) future -> {
        Configuration config = RobinServer.getInstance().getConfig();
        if (future.isSuccess()) {
            log.info("WebSocket server listening on {}:{}{}", config.getListenAddress(),
                    config.getListenPort(), config.getWebsocketPath());
        } else {
            log.warn("Couldn't bind to host" + config.getListenAddress(), future.cause());
        }
    };

    // Backend bind listener.
    public static ChannelFutureListener BE_BIND_LISTENER = (ChannelFutureListener) future -> {
        Configuration config = RobinServer.getInstance().getConfig();
        if (future.isSuccess()) {
            log.info("Backend server listening on {}:{}", config.getBackendAddress(),
                    config.getBackendPort());
        } else {
            log.warn("Couldn't bind to host" + config.getListenAddress(), future.cause());
        }
    };

    // WebSocket channel initializer.
    public static ChannelHandler WS_CHANNEL_INITIALIZER = new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(Channel ch) throws Exception {
            Configuration config = RobinServer.getInstance().getConfig();
            RobinServer.getInstance().getStatistics().connection();

            ch.pipeline().addLast(RobinServer.getInstance().createMainHandler());   // handler main channel group
            //ch.pipeline().addLast(new ReadTimeoutHandler(1, TimeUnit.MINUTES));     // handles socket timeouts
            ch.pipeline().addLast(new HttpServerCodec());                           // handles http codec
            ch.pipeline().addLast(new HttpObjectAggregator(65535));                 // aggregates http objects to fullrequests
            ch.pipeline().addLast(new AuthenticationHandler());                     // used to authenticate a websocket (one time)
            ch.pipeline().addLast(new HttpRequestHandler());                        // used to process http request (one time)
            ch.pipeline().addLast(new WebSocketServerProtocolHandler(config.getWebsocketPath())); // used to handle websocket server
            ch.pipeline().addLast(new WebSocketClientHandler());                    // used to handle websocket frames
        }
    };

    // Backend channel initializer.
    public static ChannelHandler BE_CHANNEL_INITIALIZER = new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(new HttpServerCodec());
            ch.pipeline().addLast(new HttpObjectAggregator(65535));
            ch.pipeline().addLast(new BackendAPIHttpHandler());
        }
    };

    // Epoll stuff and epoll/nio factory methods.
    private static boolean epollAvailable;

    static {
        if (!PlatformDependent.isWindows()) {
            epollAvailable = Epoll.isAvailable();
            if (epollAvailable) {
                log.info("Epoll is working, utilising it!");
            } else {
                log.info("Epoll is not working, falling back to NIO: {}", Utils.exception(Epoll.unavailabilityCause()));
            }
        } else {
            log.info("We are on Windows, so no epoll.");
        }
    }

    public static EventLoopGroup newEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        return epollAvailable ? new EpollEventLoopGroup(nThreads, threadFactory) : new NioEventLoopGroup(nThreads, threadFactory);
    }

    public static Class<? extends ServerChannel> getServerChannel() {
        return epollAvailable ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends Channel> getChannel() {
        return epollAvailable ? EpollSocketChannel.class : NioSocketChannel.class;
    }

}
