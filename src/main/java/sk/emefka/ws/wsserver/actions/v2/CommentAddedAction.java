package sk.emefka.ws.wsserver.actions.v2;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import sk.emefka.ws.wsserver.TypedStringTokenizer;

@Data
public class CommentAddedAction extends Action {

    private int postId;
    private int commentId;
    private int parentCommentId;

    // todo: merge bytebuf and typedstringtokenizer to common interface
    public CommentAddedAction(ByteBuf from) {
        super(from);
    }

    public CommentAddedAction(TypedStringTokenizer tokenizer) {
        super(tokenizer);
    }

    @Override
    public void encode(ByteBuf to) {
        to.writeInt(postId)
                .writeInt(commentId)
                .writeInt(parentCommentId);
    }

    @Override
    public void decode(ByteBuf from) {
        postId = from.readInt();
        commentId = from.readInt();
        parentCommentId = from.readInt();
    }

    @Override
    public void decode(TypedStringTokenizer tokenizer) {
        postId = tokenizer.readInt();
        commentId = tokenizer.readInt();
        parentCommentId = tokenizer.readInt();
    }

    @Override
    public int size() {
        return Integer.BYTES * 3;
    }
}
