package sk.emefka.ws.wsserver;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class UserAgent {

    /**
     * Unknown User Agent constant. This is default value for all newly created Clients.
     */
    public static final UserAgent UNKNOWN = new UserAgent(Platform.DESKTOP, Browser.OTHER, 0, OS.OTHER, 0);

    @Getter
    @RequiredArgsConstructor
    enum Browser {
        CHROME(0), // chrome
        OPERA(1), // opera
        FIREFOX(2), // firefox
        INTERNET_EXPLORER(3), // ie
        EDGE(4), // edge
        SAFARI(5), // safari
        OTHER(127); // other

        private final int id;
    }

    @Getter
    @RequiredArgsConstructor
    enum OS {
        WINDOWS(0), // windows
        LINUX(1), // linux
        ANDROID(2), // android
        IOS(3), // ios
        MACINTOSH(4), // osx
        OTHER(127);

        private final int id;
    }

    @Getter
    @RequiredArgsConstructor
    enum Platform {
        DESKTOP(0), // desktop
        MOBILE(1); // mobile

        private final int id;
    }

    private final Platform platform;
    private final Browser browser;
    private final int browserVersion;
    private final OS os;
    private final int osVersion;
}
