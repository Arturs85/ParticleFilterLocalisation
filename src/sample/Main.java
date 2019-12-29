package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    ObstacleMap board;
    ParticleSet particleSet;
    MonteCarloAgent monteCarloAgent;
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Canvas canvas = new Canvas(520, 550);
        ScrollPane scrollPane = new ScrollPane(canvas);
        BorderPane root = new BorderPane(scrollPane);
        board = new ObstacleMap(10, 10, canvas);
        board.draw(canvas);
        particleSet = new ParticleSet(board);
        // PASimulator simulator = new PASimulator(board);
        // PolicyIterator policyIterator = new PolicyIterator(board);
        monteCarloAgent = new MonteCarloAgent(board);
        Button obstButton = new Button("Set Obstacle");
        Button calcWeightsButton = new Button("Calculate weights");
        Button startPositionButton = new Button("Set Start Position");
        Button saveImageButton = new Button("Save image");
        Button makeMoveButton = new Button("Move");
        obstButton.setPrefWidth(130);
        calcWeightsButton.setPrefWidth(130);
        startPositionButton.setPrefWidth(130);
        saveImageButton.setPrefWidth(130);
        makeMoveButton.setPrefWidth(70);
        calcWeightsButton.setDisable(true);
        Button xPlus = new Button("X+");
        Button xMinus = new Button("X-");
        Button yPlus = new Button("Y+");
        Button yMinus = new Button("Y-");

        Button turnRightButton = new Button(">>");
        Button turnLeftButton = new Button("<<");

        HBox adjustmentPanel = new HBox(5, xPlus, xMinus, yPlus, yMinus);
        adjustmentPanel.setAlignment(Pos.CENTER);
        HBox robotControlPanel = new HBox(turnLeftButton, makeMoveButton, turnRightButton);
        xPlus.setOnAction(event -> {
            board.setWidth(board.width + 1);
            redrawBoard();
        });
        xMinus.setOnAction(event -> {
            board.setWidth(board.width - 1);
            redrawBoard();
        });
        yPlus.setOnAction(event -> {
            board.setHeight(board.height + 1);
            redrawBoard();
        });
        yMinus.setOnAction(event -> {
            board.setHeight(board.height - 1);
            redrawBoard();
        });

        turnLeftButton.setOnAction(event -> {
            monteCarloAgent.turnLeft();
            particleSet.turnLeftParticles();

            draw();
            calcWeightsButton.setDisable(false);

        });
        turnRightButton.setOnAction(event -> {
            monteCarloAgent.turnRight();
            particleSet.turnRightParticles();
            draw();
            calcWeightsButton.setDisable(false);

        });

        canvas.setOnMouseClicked(event -> {
            board.selectCell(event.getX(), event.getY());
            if (board.selectedCell != null)
                redrawObstButton(obstButton, board.selectedCell.isObstacle);
            //System.out.println(event.getX() + "  " + event.getY());
            // if (simulator != null)
            ///     simulator.draw();
            //else
            board.draw();
        });


        saveImageButton.setOnAction(event -> {
         String filename="images";
            if(calcWeightsButton.isDisabled())
                filename += "/resample";
            else
                filename+= "/move";
            CanvasFileSaver.saveToFile(canvas,filename );

        });
        makeMoveButton.setOnAction(event -> {
            //       simulator.startSimulationSeries();
            //simulator.startSimulation();
            monteCarloAgent.move(monteCarloAgent.agentActualDirection);
            particleSet.moveParticlesAccordingModel();


            draw();
            calcWeightsButton.setDisable(false);

        });


        obstButton.setOnAction(event -> {
            if (board.selectedCell != null) {
                if (!board.selectedCell.isObstacle) {
                    board.selectedCell.isObstacle = true;
                } else {
                    board.selectedCell.isObstacle = false;
                }
                redrawObstButton(obstButton, board.selectedCell.isObstacle);
                //  System.out.println(MotionModel.getRelativeDirection());
            }


            //     if (simulator != null)
            //         simulator.draw();
            //     else
            board.draw();
        });
        calcWeightsButton.setOnAction(event -> {
            particleSet.calculateParticleWeights(monteCarloAgent.lastMeasurement);
            particleSet.resampleParticles();

            draw();
            calcWeightsButton.setDisable(true);
        });

        startPositionButton.setOnAction(event -> {
            if (board.selectedCell != null && !board.selectedCell.isObstacle) {
                board.clearStartPosition();
                board.selectedCell.isStartPosition = true;
                monteCarloAgent.currentLocation = board.selectedCell;
            }
            redrawObstButton(obstButton, board.selectedCell.isObstacle);


            // if (simulator != null)
            ///     simulator.draw();
            // else
particleSet.initializeParticles();
            draw();
        });
        //  Button resetButton = new Button("Default");
//        Slider sliderSimSpeed = new Slider();
//        sliderSimSpeed.setTooltip(new Tooltip("Sim Speed adjustment"));
//        sliderSimSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
//            //   System.out.println("Speed slider value changed "+ newValue);
//            //      simulator.timeline.setRate(newValue.intValue());//setDelay(Duration.millis(simulation.simStepDefDuration/newValue.intValue()));
//
//        });


        VBox buttonBar = new VBox(20, adjustmentPanel, obstButton, calcWeightsButton, startPositionButton, saveImageButton, robotControlPanel);
        buttonBar.setPadding(new Insets(5, 5, 5, 5));
        buttonBar.setAlignment(Pos.CENTER);

        root.setRight(buttonBar);


        primaryStage.setTitle("Particle localisation");
        primaryStage.setScene(new Scene(root, 750, 675));
        primaryStage.show();
    }

    void redrawObstButton(Button b, boolean isObstacle) {
        if (isObstacle)
            b.setText("Clear obstacle");
        else
            b.setText("Set obstacle");

    }

    void draw() {
        board.draw();
        particleSet.draw();
        monteCarloAgent.draw();
   GraphicsContext g =board.canvas.getGraphicsContext2D();
   double delta = Math.sqrt(Math.pow(monteCarloAgent.currentLocation.x-particleSet.estimate.getX(),2)+Math.pow(monteCarloAgent.currentLocation.y-particleSet.estimate.getY(),2));
   g.strokeText("Lokalizācijas kļūda : "+delta,ObstacleMap.cellsOffset,board.height*MapCell.size+ObstacleMap.cellsOffset+20);
        g.strokeText("Distances mērījums :"+monteCarloAgent.lastMeasurement,ObstacleMap.cellsOffset,board.height*MapCell.size+ObstacleMap.cellsOffset+35);

    }

    void redrawBoard() {
        board.createMapCells();
        board.draw();
        particleSet.initializeParticles();
        particleSet.draw();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
