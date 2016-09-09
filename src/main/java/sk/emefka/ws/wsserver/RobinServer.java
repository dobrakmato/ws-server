package sk.emefka.ws.wsserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sk.emefka.ws.wsserver.auth.Authenticator;
import sk.emefka.ws.wsserver.auth.EmefkaAuthenticator;
import sk.emefka.ws.wsserver.netty.PipelineUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class RobinServer {

    @Getter
    private final Statistics statistics;
    @Getter
    private final Authenticator authenticator;
    @Getter
    private Configuration config;

    // Boss and Worker IO groups for all IO operations.
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    // Executor group for  long running tasks like mysql queries.
    @Getter
    private EventExecutorGroup executorGroup;

    // All clients channel group and handler to manage this group.
    @Getter
    private ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @RequiredArgsConstructor
    private static class AllChannelGroupHandler extends ChannelInboundHandlerAdapter {
        private final ChannelGroup allChannels;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            allChannels.add(ctx.channel());
            ctx.fireChannelActive();
        }
    }

    public ChannelHandler createMainHandler() {
        return new AllChannelGroupHandler(allChannels);
    }

    private static RobinServer instance;

    public static RobinServer getInstance() {
        return instance;
    }

    public RobinServer() {
        if (instance != null) {
            throw new IllegalStateException("Only one WebSocketServer can be created!");
        }
        instance = this;

        this.configure();
        this.statistics = new Statistics();
        this.authenticator = new EmefkaAuthenticator();
    }

    /**
     * Configures this WebSocket server.
     */
    private void configure() {
        Path configFile = findConfig().toAbsolutePath();
        log.info("Using {} as configuration file.", configFile);
        config = new Configuration(configFile);
    }

    /**
     * Finds configuration file by looking to common places.
     *
     * @return path of configuration file
     * @throws RuntimeException when configuration file couldn't be found in any of common places
     */
    private Path findConfig() {
        if (Files.exists(Paths.get("config.json"))) {
            return Paths.get("config.json");
        }

        if (Files.exists(Paths.get("configuration.json"))) {
            return Paths.get("configuration.json");
        }

        throw new RuntimeException("Can't find configuration file!");
    }

    /**
     * Starts all servers.
     *
     * @throws InterruptedException when server thread is interrupted
     */
    public void start() throws InterruptedException {
        bossGroup = PipelineUtils.newEventLoopGroup(2, new DefaultThreadFactory("Boss IO")); // io-group
        workerGroup = PipelineUtils.newEventLoopGroup(8, new DefaultThreadFactory("Worker IO")); // io-group
        executorGroup = new DefaultEventExecutorGroup(4, new DefaultThreadFactory("Executor"));

        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(PipelineUtils.getServerChannel())
                .childHandler(PipelineUtils.WS_CHANNEL_INITIALIZER)
                .localAddress(config.getListenAddress(), config.getListenPort())
                .bind().addListener(PipelineUtils.WS_BIND_LISTENER);

        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(PipelineUtils.getServerChannel())
                .childHandler(PipelineUtils.BE_CHANNEL_INITIALIZER)
                .localAddress(config.getBackendAddress(), config.getBackendPort())
                .bind().addListener(PipelineUtils.BE_BIND_LISTENER);

        //executorGroup.scheduleAtFixedRate(this::sendSpam, 10L, 10L , TimeUnit.SECONDS);
        executorGroup.scheduleAtFixedRate(this::reportStatus, 10L, 10L , TimeUnit.SECONDS);
    }

    private void reportStatus() {
        log.error("Channels: {}, writable: {}, active: {}, registered: {}, open: {}",
                allChannels.size(),
                allChannels.stream().filter(Channel::isWritable).count(),
                allChannels.stream().filter(Channel::isActive).count(),
                allChannels.stream().filter(Channel::isRegistered).count(),
                allChannels.stream().filter(Channel::isOpen).count());
    }

    private void sendSpam() {
        allChannels.writeAndFlush("Time: " + System.nanoTime());
    }

    /**
     * Stops all servers.
     *
     * @throws InterruptedException when server thread is interrupted
     */
    public void stop() throws InterruptedException {
        log.info("Shutting down...");
        // Shut down all event loops to terminate all threads.
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        executorGroup.shutdownGracefully();

        // Wait until all threads are terminated.
        bossGroup.terminationFuture().sync();
        workerGroup.terminationFuture().sync();
        executorGroup.terminationFuture().sync();

        // Dispose all authenticator resources.
        if (this.authenticator instanceof Disposable) {
            ((Disposable) this.authenticator).dispose();
        }

        log.info("Exiting...");
    }

}
