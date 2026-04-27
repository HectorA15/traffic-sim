package org.hectora15.core;

import org.hectora15.Request;
import org.hectora15.Server;
import java.util.*;

/**
 * 6. MONTE CARLO METHOD
 * Main simulator: Monte Carlo over server traffic.
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
            // 1. Poisson decide cuántas peticiones llegan este segundo
            int arrivingRequests = poisson.nextArrivalCount();
            requestsPerSecond[second] = arrivingRequests;
            maxSecondsPerSecond = Math.max(maxSecondsPerSecond, arrivingRequests);

            for (int i = 0; i < arrivingRequests; i++) {
                long arrivalTimeMs = (long) second * 1000L;
                Request req = new Request(arrivalTimeMs, "/api/endpoint");

                boolean success;

                // === LÓGICA DE SOBRECARGA ===
                // Si el número de petición 'i' supera la capacidad del servidor, falla automáticamente
                if (i >= server.getCapacity()) {
                    success = false;
                } else {
                    // Si el servidor aún tiene espacio, usamos Bernoulli para ver si tiene éxito
                    success = trial.trial();
                }

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

    public int[] getRequestsPerSecond() {
        return requestsPerSecond;
    }
}