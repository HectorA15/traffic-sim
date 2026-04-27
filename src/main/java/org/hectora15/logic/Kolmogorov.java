package org.hectora15.logic;

import java.util.Arrays;

/**
 * 5. KOLMOGOROV-SMIRNOV METHOD
 * Tests for uniformity of generated random data.
 */
public class Kolmogorov {
    private static final double CRITICAL_VALUE_ALPHA_05 = 1.36;
    private final MersenneTwisterEngine random;
    private int totalNumbers;

    public Kolmogorov(int totalNumbers, MersenneTwisterEngine random) {
        if (totalNumbers <= 0) {
            throw new IllegalArgumentException("Total numbers must be greater than 0");
        }
        this.totalNumbers = totalNumbers;
        this.random = random;
    }

    public KolmogorovResult executeTest() {
        double[] sortedNumbers = generateSortedNumbers();
        double maximumDistance = calculateMaximumDistance(sortedNumbers);
        double criticalValue = calculateCriticalValue();
        boolean passesTest = maximumDistance <= criticalValue;

        return new KolmogorovResult(maximumDistance, criticalValue, passesTest, sortedNumbers);
    }

    public double[] generateSortedNumbers() {
        double[] numbers = new double[totalNumbers];
        for (int i = 0; i < totalNumbers; i++) {
            numbers[i] = random.nextDouble();
        }
        Arrays.sort(numbers);
        return numbers;
    }

    public double calculateMaximumDistance(double[] sortedNumbers) {
        double maximumDistance = 0.0;
        for (int i = 0; i < sortedNumbers.length; i++) {
            double upperEdge = (i + 1.0) / sortedNumbers.length - sortedNumbers[i];
            double lowerEdge = sortedNumbers[i] - (i * 1.0) / sortedNumbers.length;
            double largerDistance = Math.max(upperEdge, lowerEdge);
            maximumDistance = Math.max(maximumDistance, largerDistance);
        }
        return maximumDistance;
    }

    public double calculateCriticalValue() {
        return CRITICAL_VALUE_ALPHA_05 / Math.sqrt(totalNumbers);
    }

    public record KolmogorovResult(double maximumDistance, double criticalValue, boolean passesTest, double[] sortedNumbers) {
    }
}