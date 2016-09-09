package sk.emefka.ws.wsserver;

public interface Disposable {
    /**
     * Releases all resources allocated by the instance, clears all
     * ThreadLocals.
     */
    void dispose();
}
