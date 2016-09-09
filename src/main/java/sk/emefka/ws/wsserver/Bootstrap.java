package sk.emefka.ws.wsserver;

import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class Bootstrap {

    /**
     * Whether the debug mode is enabled or not.
     */
    public static final boolean DEBUG = false;

    public static void main(String[] args) {
        Thread.currentThread().setName("Robin Main");
        log.info("Application started at {}", new SimpleDateFormat().format(new Date()));
        // Set-up environment.
        if (DEBUG) {
            log.info("Debug build. Debug mode enabled!");
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        } else {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE); //todo: should be disabled
        }

        try {
            new RobinServer().start();
        } catch (InterruptedException e) {
            log.error("Fatal error!", e);
        }
    }
}
