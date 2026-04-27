package org.hectora15.core;

import java.util.Arrays;

// Container for simulation metrics.
public class SimulationMetrics {
    private long totalRequests;
    private long successfulRequests;
    private long failedRequests;
    private int maxRequestsPerSecond;
    private int percentile95;
    private int percentile5;
    private double stdDeviation;

    public double getSuccessRate() {
        if (totalRequests == 0) return 0;
        return (double) successfulRequests / totalRequests * 100;
    }

    public double getFailureRate() {
        return 100 - getSuccessRate();
    }

    public void calculatePercentiles(int[] data) {
        int[] sorted = data.clone();
        Arrays.sort(sorted);

        this.percentile5 = sorted[(int) (sorted.length * 0.05)];
        this.percentile95 = sorted[(int) (sorted.length * 0.95)];

        double mean = Arrays.stream(sorted).average().orElse(0);
        double variance = 0;
        for (int val : sorted) {
            variance += Math.pow(val - mean, 2);
        }
        this.stdDeviation = Math.sqrt(variance / sorted.length);
    }

    public void setTotalRequests(long total) { this.totalRequests = total; }
    public void setSuccessfulRequests(long s) { this.successfulRequests = s; }
    public void setFailedRequests(long f) { this.failedRequests = f; }
    public void setMaxRequestsPerSecond(int m) { this.maxRequestsPerSecond = m; }
    public int getMaxRequestsPerSecond() { return maxRequestsPerSecond; }
}