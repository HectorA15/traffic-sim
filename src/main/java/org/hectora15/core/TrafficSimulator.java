package org.hectora15.core;

import org.hectora15.Request;
import org.hectora15.Server;
import java.util.*;

/**
 * 6. MONTE CARLO METHOD
 * Main simulator: Monte Carlo over server traffic.
 *
 * Flow:
 * 1. Generates N hours of traffic.
 * 2. Each second, Poisson determines incoming requests.
 * 3. For each request, Bernoulli determines success.
 * 4. Records metrics.
 */
public class TrafficSimulator {
    private Poisson poisson;
    private Bernoulli trial;
    private MersenneTwisterEngine rng;
    private Server server;
    private List<Request> allRequests;
    private SimulationMetrics metrics;
    private int[] requestsPerSecond;

    public TrafficSimulator(double lambdaRequestsPerSec, double successProbability, Server server, long seed) {
        this.rng = new MersenneTwisterEngine(seed);
        this.poisson = new Poisson(lambdaRequestsPerSec, rng);
        this.trial = new Bernoulli(successProbability, rng);
        this.server = server;
        this.allRequests = new ArrayList<>();
        this.metrics = new SimulationMetrics();
    }

    public void simulate(int hours) {
        long requestId = 0;
        int maxSecondsPerSecond = 0;
        int totalSeconds = hours * 3600;
        requestsPerSecond = new int[totalSeconds];

        for (int second = 0; second < totalSeconds; second++) {
            int arrivingRequests = poisson.nextArrivalCount();
            requestsPerSecond[second] = arrivingRequests;
            maxSecondsPerSecond = Math.max(maxSecondsPerSecond, arrivingRequests);

            for (int i = 0; i < arrivingRequests; i++) {
                long arrivalTimeMs = (long) second * 1000L;
                Request req = new Request(arrivalTimeMs, "/api/endpoint");

                boolean success = trial.trial();
                server.processRequest(req, success);

                allRequests.add(req);
                requestId++;
            }
        }

        metrics.setTotalRequests(allRequests.size());
        metrics.setSuccessfulRequests(server.getSuccessfulRequests());
        metrics.setFailedRequests(server.getFailedRequests());
        metrics.setMaxRequestsPerSecond(maxSecondsPerSecond);
        metrics.calculatePercentiles(requestsPerSecond);
    }

    public SimulationMetrics getMetrics() {
        return metrics;
    }

    public Server getServer() {
        return server;
    }

    // Required for validation methods
    public int[] getRequestsPerSecond() {
        return requestsPerSecond;
    }
}