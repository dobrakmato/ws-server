package sk.emefka.ws.wsserver.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import sk.emefka.ws.wsserver.TypedStringTokenizer;
import sk.emefka.ws.wsserver.actions.v2.Action;
import sk.emefka.ws.wsserver.actions.v2.ClientDetailsReportAction;
import sk.emefka.ws.wsserver.actions.v2.PostClapAddedAction;

import java.util.List;

@Slf4j
public class WebSocketFrameToActionDecoder extends MessageToMessageDecoder<WebSocketFrame> {

    private static final String DELIMITER = "|";

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        if (msg instanceof BinaryWebSocketFrame) {
            out.add(decodeBinary((BinaryWebSocketFrame) msg));
        } else if (msg instanceof TextWebSocketFrame) {
            out.add(decodeText((TextWebSocketFrame) msg));
        }
        // else sorry. no else...
    }

    private Action decodeText(TextWebSocketFrame msg) {
        TypedStringTokenizer buffer = new TypedStringTokenizer(msg.text(), DELIMITER);
        Action action = instantiate(buffer.readByte(), buffer);
        return action;
    }

    private Action decodeBinary(BinaryWebSocketFrame msg) {
        ByteBuf buffer = msg.content();
        Action action = instantiate(buffer.readByte(), buffer);
        return action;
    }

    private Action instantiate(byte type, ByteBuf buffer) {
        switch (type) {
            case 6:
                return new PostClapAddedAction(buffer);
            default:
                log.error("Invalid action type {}", type);
                return null;
        }
    }

    private Action instantiate(byte type, TypedStringTokenizer buffer) {
        switch (type) {
            case 6:
                return new PostClapAddedAction(buffer);
            case 8:
                return new ClientDetailsReportAction(buffer);
            default:
                log.error("Invalid action type {}", type);
                return null;
        }
    }

}
