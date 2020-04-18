package view;

import controller.Controller;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.Function;
import model.Point;
import sample.Main;


public class GraphicBuildingComponent {
    private static final String SCALE_TEXT = "Scale: ";
    private static final String SINGLE_SEGMENT_TEXT = "Single seg.: ";
    private static final int FULL_PERCENTS = 100;

    private GridPane gridPane;

    private Label singleScaleSegment;
    private TextField nTextField;
    private TextField kTextField;
    private Button startBuildButton;
    private Button stopBuildButton;
    private TableView<Point> functionTable;
    private GraphicCanvas graphicCanvas;

    private Button incGraphicScaleButton;
    private Button decGraphicScaleButton;
    private Label currentGraphicScaleLabel;

    private Function arrayFunction;
    private Controller controller;


    public GraphicBuildingComponent(Function arrayFunction, GraphicCanvas graphicCanvas, Controller controller) {
        singleScaleSegment = new Label(SINGLE_SEGMENT_TEXT + (int) graphicCanvas.getSingleScaleSegment());
        nTextField = new TextField();
        kTextField = new TextField();
        startBuildButton = new Button("Start");
        initStartButtonConfig();
        stopBuildButton = new Button("Stop");
        initStopButtonConfig();
        functionTable = new TableView<>();
        initFunctionTableConfig(arrayFunction);

        this.graphicCanvas = graphicCanvas;
        initScrollCtrlScaling();

        incGraphicScaleButton = new Button("+");
        initIncGraphicScaleButtonConfig();
        decGraphicScaleButton = new Button("-");
        initDecGraphicScaleButtonConfig();
        currentGraphicScaleLabel = new Label(SCALE_TEXT + (int) (graphicCanvas.getCurrentScale() * FULL_PERCENTS)  + "%");

        gridPane = new GridPane();
        initGridPaneConfig();

        this.arrayFunction = arrayFunction;
        this.controller = controller;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    // Configs
    private void initGridPaneConfig() {
        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(20);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(80);
        gridPane.getColumnConstraints().add(0, column0);
        gridPane.getColumnConstraints().add(1, column1);

        VBox controlPanel = new VBox(
                new TwoNodesGrid(new Label("n"), nTextField).getGridPane(),
                new TwoNodesGrid(new Label("k"), kTextField).getGridPane(),
                new TwoNodesGrid(startBuildButton, stopBuildButton).getGridPane(),
                functionTable
        );

        HBox scalingBox = new HBox(
                singleScaleSegment,
                new Separator(Orientation.VERTICAL),
                decGraphicScaleButton, currentGraphicScaleLabel, incGraphicScaleButton
        );

        int boxesSpacing = 5;

        scalingBox.setAlignment(Pos.CENTER);
        scalingBox.setSpacing(boxesSpacing);

        HBox funDefBox = new HBox();

        for (int funIter = 0; funIter < graphicCanvas.getFunctions().size(); funIter++) {
            funDefBox.getChildren().addAll(
                    createFunctionHintLine(graphicCanvas.getColorsForFunctions().get(funIter)),
                    new Label(graphicCanvas.getFunctions().get(funIter).getDefinition())
            );
        }

        funDefBox.setAlignment(Pos.CENTER);
        funDefBox.setSpacing(boxesSpacing);

        ScrollPane funScrollPane = new ScrollPane();
        funScrollPane.setPannable(true);
        funScrollPane.setContent(funDefBox);
        funScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        funScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        funScrollPane.setPrefWidth(Main.MAIN_FORM_WIDTH / 5);
        funScrollPane.setMinHeight(4);
        funScrollPane.setStyle("-fx-background-color:transparent;");

        HBox unitedBox = new HBox(scalingBox, new Separator(Orientation.VERTICAL), funScrollPane);
        unitedBox.setAlignment(Pos.CENTER);
        unitedBox.setSpacing(boxesSpacing);

        VBox graphicsPanel = new VBox(
                graphicCanvas.getScrollPane(),
                unitedBox
        );
        graphicsPanel.setAlignment(Pos.CENTER);
        graphicsPanel.setSpacing(boxesSpacing);

        gridPane.add(controlPanel, 0, 0);
        gridPane.add(graphicsPanel, 1,0);
        GridPane.setMargin(controlPanel, new Insets(10));
        GridPane.setMargin(graphicsPanel, new Insets(10));
        GridPane.setHalignment(graphicsPanel, HPos.CENTER);
    }

    private void initStartButtonConfig() {
        startBuildButton.setOnAction(e -> {
            String nString = nTextField.getText();
            String kString = kTextField.getText();

            arrayFunction.setXUpLimit(Function.MAX_X_UP_LIMIT);

            int n;
            int k;

                n = Integer.parseInt(nString);
                k = Integer.parseInt(kString);


            arrayFunction.setXUpLimit(n);

            controller.startGraphicBuilding(n,k);
        });
    }

    private void initStopButtonConfig() {
        stopBuildButton.setOnAction(e -> {
            controller.stopGraphicBuilding();
        });
    }

    private void initFunctionTableConfig(Function function) {
        functionTable.setItems(function.getPoints());
        functionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Point, Double> xColumn = new TableColumn<>("x");
        TableColumn<Point, Double> yColumn = new TableColumn<>("y");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        functionTable.getColumns().addAll(xColumn, yColumn);
    }

    private void initIncGraphicScaleButtonConfig() {
        incGraphicScaleButton.setOnAction(e -> {
            controller.incrementGraphicScale();
            currentGraphicScaleLabel.setText(SCALE_TEXT + (int) (graphicCanvas.getNewScale() * FULL_PERCENTS) + "%");
            singleScaleSegment.setText(SINGLE_SEGMENT_TEXT + (int) graphicCanvas.getSingleScaleSegment());
        });
    }

    private void initDecGraphicScaleButtonConfig() {
        decGraphicScaleButton.setOnAction(e -> {
            controller.decrementGraphicScale();
            currentGraphicScaleLabel.setText(SCALE_TEXT + (int) (graphicCanvas.getNewScale() * FULL_PERCENTS) + "%");
            singleScaleSegment.setText(SINGLE_SEGMENT_TEXT + (int) graphicCanvas.getSingleScaleSegment());
        });
    }

    private void initScrollCtrlScaling() {
        graphicCanvas.getCanvas().setOnScroll(e -> {
            if (e.isControlDown()) {
                if (e.getDeltaY() > 0) {
                    graphicCanvas.zoomIn();
                    currentGraphicScaleLabel.setText(SCALE_TEXT + (int) (graphicCanvas.getNewScale() * FULL_PERCENTS) + "%");
                    singleScaleSegment.setText(SINGLE_SEGMENT_TEXT + (int) graphicCanvas.getSingleScaleSegment());
                }
                if (e.getDeltaY() < 0) {
                    graphicCanvas.zoomOut();
                    currentGraphicScaleLabel.setText(SCALE_TEXT + (int) (graphicCanvas.getNewScale() * FULL_PERCENTS) + "%");
                    singleScaleSegment.setText(SINGLE_SEGMENT_TEXT + (int) graphicCanvas.getSingleScaleSegment());
                }
            }
        });
    }


    private Alert createErrorDialog(Node content) {
        Alert alert = new Alert(Alert.AlertType.NONE);

        alert.setTitle("Error");
        alert.getDialogPane().setContent(content);
        alert.getButtonTypes().add(ButtonType.OK);

        return alert;
    }

    private Canvas createFunctionHintLine(Color functionColor) {
        Canvas canvas = new Canvas(20, 3);
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.setFill(functionColor);
        graphicsContext2D.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

        return canvas;
    }
}
