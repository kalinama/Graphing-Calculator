package controller;

import model.Function;
import model.Point;
import view.GraphicCanvas;

public class Func2 implements Runnable {
    private final Function linearFunction;
    private final int calcTaskNumber;
    private final GraphicCanvas graphicCanvas;
    private static final double E =0.001;
    private int a;
    private int b;



    public Func2(Function linearFunction, int calcTaskNumber, GraphicCanvas graphicCanvas, int a, int b) {
        this.linearFunction = linearFunction;
        this.calcTaskNumber = calcTaskNumber;
        this.graphicCanvas = graphicCanvas;
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {

        int sleepTime = 70;

        for (double x = linearFunction.getXDownLimit(); x <= linearFunction.getXUpLimit(); x += 1) {
            double prevRes;
            double rez = 0;
            double i = 1;
            do {
                prevRes = rez;
                rez += Math.pow(-1, i-1)*Math.sin(i*(a*x-b))/i;
                i++;
            }
            while (Math.abs(prevRes-rez)>E);

            linearFunction.getPoints().add(new Point(x, rez));
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
