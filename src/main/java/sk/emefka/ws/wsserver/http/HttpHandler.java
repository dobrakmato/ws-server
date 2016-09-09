package sk.emefka.ws.wsserver.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpHandler {
    FullHttpResponse handle(FullHttpRequest req);
}
