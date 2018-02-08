package sk.emefka.ws.wsserver;

import java.util.StringTokenizer;

public class TypedStringTokenizer extends StringTokenizer {

    public TypedStringTokenizer(String str, String delim, boolean returnDelims) {
        super(str, delim, returnDelims);
    }

    public TypedStringTokenizer(String str, String delim) {
        super(str, delim);
    }

    public TypedStringTokenizer(String str) {
        super(str);
    }

    public byte readByte() {
        return Byte.valueOf(nextToken());
    }

    public short readShort() {
        return Short.valueOf(nextToken());
    }

    public int readInt() {
        return Integer.valueOf(nextToken());
    }

    public long readLong() {
        return Long.valueOf(nextToken());
    }

    public float readFloat() {
        return Float.valueOf(nextToken());
    }

    public double readDouble() {
        return Double.valueOf(nextToken());
    }

    public String readString() {
        return nextToken();
    }
}
