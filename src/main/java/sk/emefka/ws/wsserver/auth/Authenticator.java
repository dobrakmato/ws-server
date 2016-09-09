package sk.emefka.ws.wsserver.auth;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.Future;

public interface Authenticator {
    /**
     * Authenticates this user using first upgrade HTTP request.
     *
     * @param ctx     channel handler context associated with this request
     * @param req     websocket upgrade http request
     */
    Future<AuthenticationResult> authenticate(ChannelHandlerContext ctx, FullHttpRequest req);
}
