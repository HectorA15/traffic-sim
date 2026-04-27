package org.hectora15;

/**
 * Web server that processes requests.
 * It registers successes, failures, and statistics.
 */
public class Server {
    private String name;
    private int capacity;           // Max requests in processing
    private int processingTimeMs;   // Average response time
    private long totalRequests = 0;
    private long successfulRequests = 0;
    private long failedRequests = 0;

    public Server(String name, int capacity, int processingTimeMs) {
        this.name = name;
        this.capacity = capacity;
        this.processingTimeMs = processingTimeMs;
    }

    /**
     * Processes a request.
     * Based on the success boolean, it registers a success or failure.
     */
    public void processRequest(Request req, boolean success) {
        totalRequests++;

        if (success) {
            req.setSuccess(true);
            req.setResponseTimeMs(processingTimeMs);
            successfulRequests++;
        } else {
            req.setSuccess(false);
            req.setResponseTimeMs(processingTimeMs * 2); // Error means slower response
            failedRequests++;
        }
    }

    public double getSuccessRate() {
        if (totalRequests == 0) return 0;
        return (double) successfulRequests / totalRequests;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getSuccessfulRequests() {
        return successfulRequests;
    }

    public long getFailedRequests() {
        return failedRequests;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("Server[%s, requests=%d, success_rate=%.2f%%]",
                name, totalRequests, getSuccessRate() * 100);
    }
}