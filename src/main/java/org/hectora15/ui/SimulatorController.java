package org.hectora15.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.hectora15.Server;
import org.hectora15.core.TrafficSimulator;
import org.hectora15.core.SimulationMetrics;
import org.hectora15.core.MersenneTwisterEngine;
import org.hectora15.core.RandomValidator;

public class SimulatorController {

    @FXML private TextField hoursInput;
    @FXML private TextField lambdaInput;
    @FXML private TextField probabilityInput;
    @FXML private TextField seedInput;
    @FXML private Button runSimulationButton;
    @FXML private TextArea consoleOutput;
    @FXML private Label statusLabel;

    // New Graph Components
    @FXML private PieChart successPieChart;
    @FXML private LineChart<Number, Number> trafficLineChart;

    @FXML
    public void executeSimulation() {
        try {
            int simulatedHours = Integer.parseInt(hoursInput.getText());
            double poissonLambda = Double.parseDouble(lambdaInput.getText());
            double bernoulliProbability = Double.parseDouble(probabilityInput.getText());
            long randomSeed = Long.parseLong(seedInput.getText());

            prepareUiForExecution();

            // The task now returns a custom object holding both the Simulator and the text log
            Task<SimulationResult> simulationTask = createSimulationTask(
                    simulatedHours, poissonLambda, bernoulliProbability, randomSeed);

            simulationTask.setOnSucceeded(event -> {
                SimulationResult result = simulationTask.getValue();
                consoleOutput.setText(result.textLog);
                updateCharts(result.simulator);
                restoreUiAfterExecution("Simulation completed successfully.");
            });

            simulationTask.setOnFailed(event -> {
                consoleOutput.setText("An error occurred.\n" + simulationTask.getException().getMessage());
                restoreUiAfterExecution("Simulation failed.");
            });

            Thread backgroundThread = new Thread(simulationTask);
            backgroundThread.setDaemon(true);
            backgroundThread.start();

        } catch (NumberFormatException exception) {
            consoleOutput.setText("Invalid input values. Please verify all numeric fields.");
        }
    }

    private void prepareUiForExecution() {
        runSimulationButton.setDisable(true);
        consoleOutput.clear();
        successPieChart.getData().clear();
        trafficLineChart.getData().clear();
        statusLabel.setText("Status: Running simulation (this may take a moment)...");
    }

    private void restoreUiAfterExecution(String finalStatus) {
        runSimulationButton.setDisable(false);
        statusLabel.setText("Status: " + finalStatus);
    }

    /**
     * Updates the JavaFX charts based on the completed simulator data.
     */
    private void updateCharts(TrafficSimulator simulator) {
        Server server = simulator.getServer();

        // 1. Update Pie Chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Successful", server.getSuccessfulRequests()),
                new PieChart.Data("Failed", server.getFailedRequests())
        );
        successPieChart.setData(pieChartData);

        // 2. Update Line Chart
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Requests per second");

        int[] requests = simulator.getRequestsPerSecond();

        // We use downsampling if there are too many data points to prevent UI freezing
        int maxPointsToDraw = 150;
        int step = Math.max(1, requests.length / maxPointsToDraw);

        for (int i = 0; i < requests.length; i += step) {
            series.getData().add(new XYChart.Data<>(i, requests[i]));
        }

        trafficLineChart.getData().add(series);
    }

    private Task<SimulationResult> createSimulationTask(int hours, double lambda, double probability, long seed) {
        return new Task<>() {
            @Override
            protected SimulationResult call() {
                StringBuilder outputBuilder = new StringBuilder();

                Server targetServer = new Server("Main-Web-Node", 1000, 30);
                TrafficSimulator monteCarloSimulator = new TrafficSimulator(lambda, probability, targetServer, seed);

                monteCarloSimulator.simulate(hours);

                SimulationMetrics finalMetrics = monteCarloSimulator.getMetrics();
                outputBuilder.append("--- SIMULATION RESULTS ---\n");
                outputBuilder.append(finalMetrics.toString()).append("\n\n");
                outputBuilder.append(monteCarloSimulator.getServer().toString()).append("\n\n");

                outputBuilder.append("--- STATISTICAL VALIDATION ---\n");

                int[] generatedRequests = monteCarloSimulator.getRequestsPerSecond();
                double[] normalizedData = new double[generatedRequests.length];

                for (int i = 0; i < generatedRequests.length; i++) {
                    normalizedData[i] = (generatedRequests[i] % 100) / 100.0;
                }

                MersenneTwisterEngine validationRng = new MersenneTwisterEngine(seed);
                RandomValidator statisticalValidator = new RandomValidator(validationRng, normalizedData);
                boolean isDataUniform = statisticalValidator.isValid();

                if (isDataUniform) {
                    outputBuilder.append("Chi-Square Test: PASSED\n");
                    outputBuilder.append("Kolmogorov-Smirnov Test: PASSED\n");
                } else {
                    outputBuilder.append("Warning: One or both statistical tests failed.\n");
                    outputBuilder.append("Conclusion: Parameter adjustments might be required for pure uniformity.\n");
                }

                return new SimulationResult(monteCarloSimulator, outputBuilder.toString());
            }
        };
    }

    /**
     * Helper class to transport the simulator and the logs from the background thread to the UI thread.
     */
    private static class SimulationResult {
        final TrafficSimulator simulator;
        final String textLog;

        SimulationResult(TrafficSimulator simulator, String textLog) {
            this.simulator = simulator;
            this.textLog = textLog;
        }
    }
}