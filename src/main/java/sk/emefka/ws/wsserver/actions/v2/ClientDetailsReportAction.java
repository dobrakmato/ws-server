package sk.emefka.ws.wsserver.actions.v2;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import sk.emefka.ws.wsserver.TypedStringTokenizer;

@Data
public class ClientDetailsReportAction extends Action {

    private String browser;
    private String os;
    private String platform;

    // todo: merge bytebuf and typedstringtokenizer to common interface
    public ClientDetailsReportAction(ByteBuf from) {
        super(from);
    }

    public ClientDetailsReportAction(TypedStringTokenizer tokenizer) {
        super(tokenizer);
    }

    @Override
    public void encode(ByteBuf to) {

    }

    @Override
    public void decode(ByteBuf from) {

    }

    @Override
    public void decode(TypedStringTokenizer tokenizer) {
        browser = tokenizer.readString();
        os = tokenizer.readString();
        platform = tokenizer.readString();
    }

    @Override
    public int size() {
        return -1;
    }
}
