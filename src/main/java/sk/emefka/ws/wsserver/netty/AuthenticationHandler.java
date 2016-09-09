package sk.emefka.ws.wsserver.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import sk.emefka.ws.wsserver.Client;
import sk.emefka.ws.wsserver.RobinServer;
import sk.emefka.ws.wsserver.auth.AuthenticationResult;
import sk.emefka.ws.wsserver.auth.Authenticator;

import java.text.DecimalFormat;

@Slf4j
public class AuthenticationHandler extends ChannelInboundHandlerAdapter {
    private final DecimalFormat decimalFormat = new DecimalFormat("##.##");
    private Authenticator authenticator = RobinServer.getInstance().getAuthenticator();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            channelRead0(ctx, (FullHttpRequest) msg);
        }
    }

    private void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if ("websocket".equalsIgnoreCase(req.headers().get("Upgrade", null))) {
            // Create Client object for this channel.
            ctx.channel().attr(PipelineUtils.KEY_CLIENT).set(new Client(ctx.channel()));

            // Initiate authentication
            authenticator.authenticate(ctx, req).addListener(future -> {
                if (future.isSuccess()) {
                    AuthenticationResult result = (AuthenticationResult) future.get();
                    if (result != null) {
                        Client client = ctx.channel().attr(PipelineUtils.KEY_CLIENT).get();
                        log.info(" Authenticated {} in {} ms!", result.getUsername(),
                                decimalFormat.format((System.nanoTime() - client.getJoinedAtNano()) / 1_000_000f));
                        client.setAuthenticated(result);
                    }
                } else {
                    log.error("Auth failed!", future.cause());
                }
            });
        }

        // remove this handler and pass message further
        ctx.pipeline().remove(this);
        ctx.fireChannelRead(req);
    }
}
