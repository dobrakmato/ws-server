package sk.emefka.ws.wsserver.actions.v2;

import io.netty.buffer.ByteBuf;
import sk.emefka.ws.wsserver.TypedStringTokenizer;

public abstract class Action {

    public Action(ByteBuf from) {
        this.decode(from);
    }

    public Action(TypedStringTokenizer tokenizer) {
        decode(tokenizer);
    }

    public Action() {
    }

    public abstract void encode(ByteBuf to);

    public abstract void decode(ByteBuf from);

    public void decode(TypedStringTokenizer tokenizer) {
        throw new UnsupportedOperationException("string parsing not implemented");
    }

    public abstract int size();
}
