package sk.emefka.ws.wsserver.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;
import sk.emefka.ws.wsserver.Client;
import sk.emefka.ws.wsserver.RobinServer;
import sk.emefka.ws.wsserver.Statistics;
import sk.emefka.ws.wsserver.netty.HttpRequestHandler;
import sk.emefka.ws.wsserver.netty.PipelineUtils;

public class ServerStatusHttpHandler implements HttpHandler {
    @Override
    public FullHttpResponse handle(FullHttpRequest req) {
        Statistics stats = RobinServer.getInstance().getStatistics();
        JSONObject object = new JSONObject();

        object.put("totalConnections", stats.getTotalConnections());
        object.put("totalRequests", stats.getTotalRequests());
        object.put("totalBadRequests", stats.getTotalBadRequests());

        int authenticated = countAuthenticated(RobinServer.getInstance().getAllChannels());
        object.put("currentClients", RobinServer.getInstance().getAllChannels().size());
        object.put("currentAuthenticatedClients", authenticated);
        object.put("currentGuestClients", RobinServer.getInstance().getAllChannels().size() - authenticated);

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpRequestHandler.HTTP_VERSION, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(object.toString(2), CharsetUtil.UTF_8));
        response.headers().add("Content-Type", "text/json");
        return response;
    }

    private int countAuthenticated(ChannelGroup allChannels) {
        int authenticated = 0;
        for (Channel channel : allChannels) {
            if (channel.hasAttr(PipelineUtils.KEY_CLIENT)) {
                Client c = channel.attr(PipelineUtils.KEY_CLIENT).get();
                if (c != null) {
                    if (c.isAuthenticated()) {
                        authenticated++;
                    }
                }
            }
        }
        return authenticated;
    }
}
