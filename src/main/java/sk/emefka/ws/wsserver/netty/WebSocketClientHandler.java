package sk.emefka.ws.wsserver.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import sk.emefka.ws.wsserver.actions.Action;
import sk.emefka.ws.wsserver.actions.TextActionSerializer;

@Slf4j
public class WebSocketClientHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // user connected
        log.info("Connection {} from {}!", ctx.channel().id(), ctx.channel().remoteAddress());
        ctx.fireChannelActive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // user handshaked
            //log.info("User {} completed handshake!", ctx.channel().id());
            ctx.writeAndFlush(new TextWebSocketFrame("Hey! Welcome to our server!"));
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // user sent something.
        Action action = TextActionSerializer.deserialize(msg.text());
        log.info("User {} said {} {}!", ctx.channel().id(), action.getType(), msg.text());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // user disconnected
        log.info("Disconnected {}!", ctx.channel().id());
        ctx.fireChannelInactive();
    }
}
