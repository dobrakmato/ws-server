package sk.emefka.ws.wsserver.actions.v2;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import sk.emefka.ws.wsserver.TypedStringTokenizer;

@Data
public class PostClapRemovedAction extends Action {

    private int postId;
    private int userId;
    private int claps;

    public PostClapRemovedAction(int postId, int userId, int claps) {
        this.postId = postId;
        this.userId = userId;
        this.claps = claps;
    }

    // todo: merge bytebuf and typedstringtokenizer to common interface
    public PostClapRemovedAction(ByteBuf from) {
        super(from);
    }

    public PostClapRemovedAction(TypedStringTokenizer tokenizer) {
        super(tokenizer);
    }

    @Override
    public void encode(ByteBuf to) {
        to.writeInt(postId)
                .writeInt(userId)
                .writeInt(claps);
    }

    @Override
    public void decode(ByteBuf from) {
        postId = from.readInt();
        userId = from.readInt();
        claps = from.readInt();
    }

    @Override
    public void decode(TypedStringTokenizer from) {
        postId = from.readInt();
        userId = from.readInt();
        claps = from.readInt();
    }

    @Override
    public int size() {
        return 3 * Integer.BYTES;
    }
}
