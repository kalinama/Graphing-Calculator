package controller;

import view.GraphicCanvas;
import model.Function;
import model.Point;


public class Func1 implements Runnable {
    private final Function linearFunction;
    private final int calcTaskNumber;
    private final GraphicCanvas graphicCanvas;


    public Func1(Function linearFunction, int calcTaskNumber, GraphicCanvas graphicCanvas) {
        this.linearFunction = linearFunction;
        this.calcTaskNumber = calcTaskNumber;
        this.graphicCanvas = graphicCanvas;
    }

    @Override
    public void run() {
        double a = 5;
        double b = -1;
        double step = 1;
        int sleepTime = 70;

        for (double x = linearFunction.getXDownLimit(); x <= linearFunction.getXUpLimit(); x += step) {
            linearFunction.getPoints().add(new Point(x, a*x + b));
            graphicCanvas.updateFunctionIterator(linearFunction);

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        Thread.currentThread().interrupt();
    }
}
