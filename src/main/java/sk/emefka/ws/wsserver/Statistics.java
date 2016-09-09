package sk.emefka.ws.wsserver;

import java.util.concurrent.atomic.AtomicLong;

public final class Statistics {

    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong totalBadRequests = new AtomicLong();
    private final AtomicLong totalConnections = new AtomicLong();

    public void request() {
        totalRequests.incrementAndGet();
    }

    public void badRequest() {
        totalBadRequests.incrementAndGet();
    }

    public void connection() {
        totalConnections.incrementAndGet();
    }

    public long getTotalRequests() {
        return totalRequests.get();
    }

    public long getTotalConnections() {
        return totalConnections.get();
    }

    public long getTotalBadRequests() {
        return totalBadRequests.get();
    }
}
