package sample;

import controller.Controller;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.GraphicBuildingComponent;
import view.GraphicCanvas;
import model.Function;


public class Main extends Application {
    public static final int MAIN_FORM_HEIGHT = 550;
    public static final int MAIN_FORM_WIDTH = 800;


    @Override
    public void start(Stage primaryStage) throws Exception {
        String mainFormTitle = "Function graph building";

        int arrayFunctionXDownLimit = 2;
        Function arrayFunction = new Function("Array");
        arrayFunction.setXDownLimit(arrayFunctionXDownLimit);
        Function linearFunction = new Function("5x - 1");

        ObservableList<Function> functions = FXCollections.observableArrayList();
        functions.addAll(arrayFunction, linearFunction);

        GraphicCanvas graphic = new GraphicCanvas(functions);
        Controller controller = new Controller(arrayFunction, linearFunction, graphic);
        GraphicBuildingComponent graphicBuildingComponent =
                new GraphicBuildingComponent(arrayFunction, graphic, controller);

        primaryStage.setResizable(false);
        primaryStage.setTitle(mainFormTitle);
        primaryStage.setHeight(MAIN_FORM_HEIGHT);
        primaryStage.setWidth(MAIN_FORM_WIDTH);
        primaryStage.setScene(new Scene(graphicBuildingComponent.getGridPane()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
