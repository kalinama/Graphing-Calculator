package controller;

import view.GraphicCanvas;


public class DrawingTask implements Runnable {
    private final GraphicCanvas graphic;


    public DrawingTask(GraphicCanvas graphic){
        this.graphic = graphic;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            graphic.update();
        }
    }
}