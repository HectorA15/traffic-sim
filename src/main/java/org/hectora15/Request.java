package org.hectora15;

/**
 * Represents an individual HTTP request.
 */
public class Request {
    private long arrivalTimeMs;
    private int responseTimeMs;
    private boolean success;
    private String endpoint;

    public Request(long arrivalTimeMs, String endpoint) {
        this.arrivalTimeMs = arrivalTimeMs;
        this.endpoint = endpoint;
        this.success = false;
        this.responseTimeMs = 0;
    }

    public long getArrivalTimeMs() {
        return arrivalTimeMs;
    }

    public void setResponseTimeMs(int ms) {
        this.responseTimeMs = ms;
    }

    public int getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public String toString() {
        return String.format("Request[arrival=%d, endpoint=%s, success=%s, time=%dms]",
                arrivalTimeMs, endpoint, success, responseTimeMs);
    }
}