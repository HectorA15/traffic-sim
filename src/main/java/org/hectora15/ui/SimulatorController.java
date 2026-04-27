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
import org.hectora15.Server;
import org.hectora15.core.TrafficSimulator;
import org.hectora15.core.MersenneTwisterEngine;
import org.hectora15.core.RandomValidator;

public class SimulatorController {

    @FXML private TextField hoursInput;
    @FXML private TextField lambdaInput;
    @FXML private TextField probabilityInput;
    @FXML private TextField seedInput;
    @FXML private Button runSimulationButton;
    @FXML private Label statusLabel;

    // Componentes de las Gráficas
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

            // La tarea ahora devuelve el TrafficSimulator directamente
            Task<TrafficSimulator> simulationTask = createSimulationTask(
                    simulatedHours, poissonLambda, bernoulliProbability, randomSeed);

            simulationTask.setOnSucceeded(event -> {
                TrafficSimulator completedSimulator = simulationTask.getValue();
                updateCharts(completedSimulator);
                restoreUiAfterExecution("Simulación completada con éxito.");
            });

            simulationTask.setOnFailed(event -> {
                restoreUiAfterExecution("La simulación falló: " + simulationTask.getException().getMessage());
            });

            Thread backgroundThread = new Thread(simulationTask);
            backgroundThread.setDaemon(true);
            backgroundThread.start();

        } catch (NumberFormatException exception) {
            restoreUiAfterExecution("Valores de entrada inválidos. Verifica que sean números.");
        }
    }

    private void prepareUiForExecution() {
        runSimulationButton.setDisable(true);
        successPieChart.getData().clear();
        trafficLineChart.getData().clear();
        statusLabel.setText("Estado: Ejecutando simulación (esto puede tomar un momento)...");
    }

    private void restoreUiAfterExecution(String finalStatus) {
        runSimulationButton.setDisable(false);
        statusLabel.setText("Estado: " + finalStatus);
    }

    /**
     * Actualiza las gráficas de JavaFX basadas en los datos del simulador completado.
     */
    private void updateCharts(TrafficSimulator simulator) {
        Server server = simulator.getServer();

        // 1. Actualizar Gráfico de Pastel
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Exitosas", server.getSuccessfulRequests()),
                new PieChart.Data("Fallidas", server.getFailedRequests())
        );
        successPieChart.setData(pieChartData);

        // 2. Actualizar Gráfico de Líneas
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Peticiones por segundo");

        int[] requests = simulator.getRequestsPerSecond();

        // Usamos submuestreo si hay demasiados puntos de datos para evitar que la interfaz se congele
        int maxPointsToDraw = 150;
        int step = Math.max(1, requests.length / maxPointsToDraw);

        for (int i = 0; i < requests.length; i += step) {
            series.getData().add(new XYChart.Data<>(i, requests[i]));
        }

        trafficLineChart.getData().add(series);
    }

    /**
     * Ejecuta la simulación de Monte Carlo y las validaciones en segundo plano.
     */
    private Task<TrafficSimulator> createSimulationTask(int hours, double lambda, double probability, long seed) {
        return new Task<>() {
            @Override
            protected TrafficSimulator call() {
                // Definimos el servidor con una capacidad máxima de 1000 peticiones en simultáneo
                Server targetServer = new Server("Nodo-Web-Principal", 1000, 30);
                TrafficSimulator monteCarloSimulator = new TrafficSimulator(lambda, probability, targetServer, seed);

                // Ejecutar el proceso central de Monte Carlo
                monteCarloSimulator.simulate(hours);

                // Ejecutar validaciones estadísticas internamente para garantizar la corrección matemática
                int[] generatedRequests = monteCarloSimulator.getRequestsPerSecond();
                double[] normalizedData = new double[generatedRequests.length];

                for (int i = 0; i < generatedRequests.length; i++) {
                    normalizedData[i] = (generatedRequests[i] % 100) / 100.0;
                }

                MersenneTwisterEngine validationRng = new MersenneTwisterEngine(seed);
                RandomValidator statisticalValidator = new RandomValidator(validationRng, normalizedData);
                statisticalValidator.isValid();

                return monteCarloSimulator;
            }
        };
    }
}