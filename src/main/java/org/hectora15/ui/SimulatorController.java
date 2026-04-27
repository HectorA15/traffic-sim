package org.hectora15.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.hectora15.core.Server;
import org.hectora15.core.TrafficSimulator;
import org.hectora15.logic.MersenneTwisterEngine;
import org.hectora15.logic.RandomValidator;

public class SimulatorController {

    @FXML private TextField hoursInput;
    @FXML private TextField lambdaInput;
    @FXML private TextField probabilityInput;
    @FXML private TextField seedInput;
    @FXML private Button runSimulationButton;
    @FXML private Label statusLabel;

    @FXML private PieChart successPieChart;
    @FXML private LineChart<Number, Number> trafficLineChart;

    @FXML
    public void executeSimulation() {
        try {
            // Read values from the user interface
            int hours = Integer.parseInt(hoursInput.getText());
            double lambda = Double.parseDouble(lambdaInput.getText());
            double prob = Double.parseDouble(probabilityInput.getText());
            long seed = Long.parseLong(seedInput.getText());

            // Reset the UI for a new simulation
            runSimulationButton.setDisable(true);
            successPieChart.getData().clear();
            trafficLineChart.getData().clear();
            statusLabel.setText("Estado: Ejecutando simulación de Monte Carlo...");

            //  Run the simulation in a background thread to keep the UI responsive
            Task<TrafficSimulator> simulationTask = new Task<>() {
                @Override
                protected TrafficSimulator call() {
                    Server targetServer = new Server("Nodo-Web-Principal", 1000, 30);
                    TrafficSimulator simulator = new TrafficSimulator(lambda, prob, targetServer, seed);
                    simulator.simulate(hours);
                    return simulator;
                }
            };


            simulationTask.setOnSucceeded(event -> {
                TrafficSimulator completedSimulator = simulationTask.getValue();

                // Update the UI charts with the results from the simulation
                updateCharts(completedSimulator);

                // Validate the random numbers and get the results as a formatted string
                String validationResults = validateMath(hours, seed);

                // Update the UI with the results
                statusLabel.setText("Estado: Simulación completada.\n" + validationResults);
                runSimulationButton.setDisable(false);
            });

            // Handle errors gracefully
            simulationTask.setOnFailed(event -> {
                statusLabel.setText("Error: La simulación falló.");
                runSimulationButton.setDisable(false);
            });

            // start the task
            Thread backgroundThread = new Thread(simulationTask);
            backgroundThread.setDaemon(true);
            backgroundThread.start();

        } catch (NumberFormatException exception) {
            statusLabel.setText("Error: Valores de entrada inválidos.");
        }
    }

    // updates the UI charts.
    private void updateCharts(TrafficSimulator simulator) {
        Server server = simulator.getServer();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Exitosas", server.getSuccessfulRequests()),
                new PieChart.Data("Fallidas", server.getFailedRequests())
        );
        successPieChart.setData(pieChartData);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Peticiones por segundo");

        int[] requests = simulator.getRequestsPerSecond();
        int step = Math.max(1, requests.length / 150);

        for (int i = 0; i < requests.length; i += step) {
            series.getData().add(new XYChart.Data<>(i, requests[i]));
        }

        trafficLineChart.getData().add(series);
    }

    //Validates the random numbers and returns a formatted string.
    private String validateMath(int hours, long seed) {
        MersenneTwisterEngine validationRng = new MersenneTwisterEngine(seed);
        int totalNumbers = hours * 3600;
        double[] randomNumbers = new double[totalNumbers];

        for (int i = 0; i < totalNumbers; i++) {
            randomNumbers[i] = validationRng.nextDouble();
        }

        RandomValidator statisticalValidator = new RandomValidator(validationRng, randomNumbers);

        String chiSquareStatus = statisticalValidator.passesChiSquare() ? "APROBADA" : "FALLIDA";
        String kolmogorovStatus = statisticalValidator.passesKolmogorov() ? "APROBADA" : "FALLIDA";

        return "[ " + chiSquareStatus + " ] Chi-Cuadrada\n[ " + kolmogorovStatus + " ] Kolmogorov";
    }
}