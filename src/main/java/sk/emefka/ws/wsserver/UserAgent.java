package sk.emefka.ws.wsserver;

import lombok.Data;

@Data
public class UserAgent {

    /**
     * Unknown User Agent constant. This is default value for all newly created Clients.
     */
    public static final UserAgent UNKNOWN = new UserAgent(Platform.DESKTOP, Browser.OTHER, 0, OS.OTHER, 0);

    enum Browser {
        CHROME, // chrome
        OPERA, // opera
        FIREFOX, // firefox
        INTERNET_EXPLORER, // ie
        EDGE, // edge
        SAFARI, // safari
        OTHER // other
    }

    enum OS {
        WINDOWS, // windows
        LINUX, // linux
        ANDROID, // android
        IOS, // ios
        MACINTOSH, // osx
        OTHER
    }

    enum Platform {
        DESKTOP, // desktop
        MOBILE // mobile
    }

    private final Platform platform;
    private final Browser browser;
    private final int browserVersion;
    private final OS os;
    private final int osVersion;
}
