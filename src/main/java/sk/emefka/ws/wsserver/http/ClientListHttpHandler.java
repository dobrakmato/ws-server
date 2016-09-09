package sk.emefka.ws.wsserver.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import sk.emefka.ws.wsserver.Client;
import sk.emefka.ws.wsserver.RobinServer;
import sk.emefka.ws.wsserver.netty.HttpRequestHandler;
import sk.emefka.ws.wsserver.netty.PipelineUtils;

@Slf4j
public class ClientListHttpHandler implements HttpHandler {
    @Override
    public FullHttpResponse handle(FullHttpRequest req) {
        long now = System.nanoTime();
        JSONObject object = new JSONObject();
        JSONArray clients = new JSONArray();
        for (Channel ch : RobinServer.getInstance().getAllChannels()) {
            Client c = ch.attr(PipelineUtils.KEY_CLIENT).get();
            if(c != null) {
                JSONObject client = new JSONObject();
                client.put("id", ch.id().asShortText());
                client.put("databaseId", c.getDatabaseId());
                client.put("name", c.getNickname());
                client.put("joinedAt", c.getJoinedAt().toString());
                long s = c.getSessionDuration().getSeconds();
                client.put("sessionDuration", String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60)));

                client.put("ip", ch.remoteAddress().toString());
                clients.put(client);
            }
        }
        object.put("clients", clients);

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpRequestHandler.HTTP_VERSION, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(object.toString(2), CharsetUtil.UTF_8));
        response.headers().add("Content-Type", "text/json");
        return response;
    }
}
