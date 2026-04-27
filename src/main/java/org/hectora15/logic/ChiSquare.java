package org.hectora15.logic;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

/**
 * 4. CHI-SQUARE METHOD
 * Calculates the chi-square statistic to determine whether a data set is uniform.
 */
public class ChiSquare {
    private final int dataCount;
    private double[] data;
    private int[] observedFrequency;
    private double expectedFrequency;
    private double tolerance = 0.05;
    private int intervalCount;

    public ChiSquare(int intervalCount, double[] data) {
        this.data = data;
        this.dataCount = data.length;
        this.intervalCount = intervalCount;
    }

    public double calculateExpectedFrequency() {
        return (double) this.dataCount / this.intervalCount;
    }

    public double calculateChiSquare() {
        observedFrequency = new int[intervalCount];
        expectedFrequency = calculateExpectedFrequency();

        for (int i = 0; i < dataCount; i++) {
            int value = (int) (data[i] * intervalCount);

            if (value < intervalCount) {
                observedFrequency[value]++;
            } else {
                observedFrequency[intervalCount - 1]++;
            }
        }

        double chiSquare = 0;
        for (int i = 0; i < intervalCount; i++) {
            chiSquare += Math.pow(observedFrequency[i] - expectedFrequency, 2) / expectedFrequency;
        }
        return chiSquare;
    }

    public int calculateDegreesOfFreedom() {
        return intervalCount - 1;
    }

    public boolean isUniform() {
        double calculatedChiSquare = calculateChiSquare();
        double criticalValue = calculateCriticalValue();
        return calculatedChiSquare < criticalValue;
    }

    public double calculateCriticalValue() {
        int degreesOfFreedom = calculateDegreesOfFreedom();
        ChiSquaredDistribution dist = new ChiSquaredDistribution(degreesOfFreedom);
        return dist.inverseCumulativeProbability(1.0 - tolerance);
    }
}