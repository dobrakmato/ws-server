package sk.emefka.ws.wsserver;

import io.netty.handler.codec.http.cookie.Cookie;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class Utils {
    public static Map<String, Cookie> cookiesSetToMap(Set<Cookie> cookies) {
        HashMap<String, Cookie> map = new HashMap<>(cookies.size());
        for (Cookie cookie : cookies) {
            map.put(cookie.name(), cookie);
        }
        return map;
    }

    public static String exception(Throwable t) {
        StackTraceElement[] trace = t.getStackTrace();
        return t.getClass().getSimpleName() + " : " + t.getMessage()
                + ((trace.length > 0) ? " @ " + t.getStackTrace()[0].getClassName() + ":" + t.getStackTrace()[0].getLineNumber() : "");
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
