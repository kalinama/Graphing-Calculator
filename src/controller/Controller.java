package controller;

import view.GraphicCanvas;
import model.Function;

import java.util.HashMap;
import java.util.Map;


public class Controller {
    private Function func1;
    private Function func2;
    private GraphicCanvas graphicCanvas;
    private Thread Func1;
    private Thread Func2;

    private Map<Function, Integer> functionOrders;
    private Integer functionOrder;

    private boolean isTCalcThreadsAlive;


    public Controller(Function func1, Function func2, GraphicCanvas graphicCanvas) {
        this.graphicCanvas = graphicCanvas;
        this.func1 = func1;
        this.func2 = func2;

        Func1 = new Thread("func1");
        Func2 = new Thread("func2");

        Thread drawThread = new Thread(new DrawingTask(graphicCanvas));
        drawThread.setName("draw");
        drawThread.setDaemon(true); //служит основным потокам
        drawThread.start();

        functionOrders = new HashMap<>();
        functionOrder = 0;
        functionOrders.put(this.func1, functionOrder++);
        functionOrders.put(this.func2, functionOrder++);

        isTCalcThreadsAlive = false;
    }

    public void startGraphicBuilding(int a, int b) {
        if (!isTCalcThreadsAlive) {
            func2.getPoints().clear();
            func1.getPoints().clear();
            graphicCanvas.clearFlag=true;

            Func2 = new Thread(new Func2(
                    func2, functionOrders.get(func2), graphicCanvas, a, b
            ));
            Func1 = new Thread(new Func1(
                    func1, functionOrders.get(func1), graphicCanvas
            ));

            Func2.setDaemon(true);
            Func1.setDaemon(true);

            Func2.start();
            Func1.start();

            isTCalcThreadsAlive = true;
        }
    }

    public void incrementGraphicScale() {
        graphicCanvas.zoomIn();
    }

    public void decrementGraphicScale() {
        graphicCanvas.zoomOut();
    }

    public void stopGraphicBuilding() {
        if (isTCalcThreadsAlive) {
            Func2.interrupt();
            Func1.interrupt();
            isTCalcThreadsAlive = false;
        }
    }
}
