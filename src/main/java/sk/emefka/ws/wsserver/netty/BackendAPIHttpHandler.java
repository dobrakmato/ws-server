package sk.emefka.ws.wsserver.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sk.emefka.ws.wsserver.RobinServer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class BackendAPIHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static final HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;

    // Internal router.
    private final Function<FullHttpRequest, Function<FullHttpRequest, FullHttpResponse>> router;

    private static class DefaultRouter implements Function<FullHttpRequest, Function<FullHttpRequest, FullHttpResponse>> {

        private final Map<String, Function<FullHttpRequest, FullHttpResponse>> routes = new HashMap<>();

        @Override
        public Function<FullHttpRequest, FullHttpResponse> apply(FullHttpRequest request) {
            if (routes.containsKey(request.uri())) {
                return routes.get(request.uri());
            }
            return null;
        }

        // Routes
        {
            routes.put("/publish", this::publish);
        }

        private FullHttpResponse publish(FullHttpRequest request) {
            log.info("Broadcasting {} to all clients.", request.content().toString(CharsetUtil.UTF_8));
            RobinServer.getInstance().getAllChannels().writeAndFlush(new TextWebSocketFrame(request.content()).retain());
            return new DefaultFullHttpResponse(HTTP_VERSION, HttpResponseStatus.OK,
                    Unpooled.copiedBuffer("{\"status\":\"success\"}", CharsetUtil.UTF_8));
        }
    }

    public BackendAPIHttpHandler() {
        router = new DefaultRouter();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Function<FullHttpRequest, FullHttpResponse> controller = router.apply(request);
        if (controller != null) {
            FullHttpResponse response = controller.apply(request);

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.writeAndFlush(new DefaultFullHttpResponse(HTTP_VERSION, HttpResponseStatus.NOT_FOUND)).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
