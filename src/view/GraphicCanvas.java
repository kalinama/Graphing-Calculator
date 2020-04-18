package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Function;
import model.Point;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class GraphicCanvas {
    private static final double MIN_SCALE = 1.0;
    private static final double MAX_SCALE = 3.0;
    private static final double SCALING_STEP = 0.1;
    private static final int SCALING_ROUNDING = 1;

    private static final double START_CANVAS_SIZE = 600;
    private static final double MAX_CANVAS_SIZE = 4000;

    private static final double X_RESIZING_BORDER = 0.85;
    private static final double Y_RESIZING_BORDER = 0.15;
    private static final double RESIZING_STEP = 0.5;
    private static final double SCROLL_PANE_CENTER_POSITION = 0.5;

    private static final double ARROW_DISTANCE = 6;
    private static final double MARK_DISTANCE = 20;
    private static final  double REDUCTION_KOF = 5;

    private double currentScale;
    private double newScale;
    private double canvasSize;
    private double singleScaleSegment;

    private boolean redrawFlag;
    public boolean clearFlag;

    private Point nextPoint;
    private double maxX;
    private double minY;

    private ScrollPane scrollPane;
    private Canvas canvas;
    private GraphicsContext graphic;

    private ObservableList<Function> functions;
    private ObservableList<Point> prevPoints;
    private ObservableList<Color> colorsForFunctions;
    private ObservableList<Integer> functionPointsIterators;

    private Lock drawingLock;


    public GraphicCanvas(ObservableList<Function> functions) {

        this.functions = functions;

        newScale = MIN_SCALE;
        currentScale = newScale;
        singleScaleSegment = MARK_DISTANCE / newScale;

        redrawFlag = false;
        nextPoint = new Point();
        maxX = Function.MIN_X_DOWN_LIMIT;
        minY = Function.MAX_X_UP_LIMIT;

        canvas = new Canvas();
        graphic = canvas.getGraphicsContext2D();


        prevPoints = FXCollections.observableArrayList();
        colorsForFunctions = FXCollections.observableArrayList();
        functionPointsIterators = FXCollections.observableArrayList();

        colorsForFunctions.add(Color.RED);
        colorsForFunctions.add(Color.AQUA);

        clearFlag = true;
        resizeCanvas();

        scrollPane = new ScrollPane();
        scrollPane.setPannable(true);
        scrollPane.setContent(canvas);
        scrollPane.setVvalue(SCROLL_PANE_CENTER_POSITION);
        scrollPane.setHvalue(SCROLL_PANE_CENTER_POSITION);

        for (int funIter = 0; funIter < functions.size(); funIter++) {
            prevPoints.add(null);
            functionPointsIterators.add(-1);
        }

        drawingLock = new ReentrantLock();
    }


    private void resizeCanvas() {
        canvas.setWidth(canvasSize);
        canvas.setHeight(canvasSize);
    }

    public void update() {
        if (clearFlag) {
            clearGraphic();
            clearFlag = false;
        }

        if (redrawFlag) {
            redrawFunctionGraphics();
            redrawFlag = false;
        }

        if (newScale != currentScale) {
            drawingLock.lock();

            canvasSize *= newScale / currentScale;
            resizeCanvas();
            redrawFlag = true;

            currentScale = newScale;

            scrollPane.setVvalue(SCROLL_PANE_CENTER_POSITION);
            scrollPane.setHvalue(SCROLL_PANE_CENTER_POSITION);

            drawingLock.unlock();
        }

        if ((maxX >= X_RESIZING_BORDER * canvasSize) || (minY <= Y_RESIZING_BORDER * canvasSize)) {
            drawingLock.lock();

            if (canvasSize < MAX_CANVAS_SIZE) { // to avoid bufferOverflowException
                canvasSize += RESIZING_STEP * canvasSize * newScale;

                resizeCanvas();
                redrawFlag = true;

                scrollPane.setVvalue(SCROLL_PANE_CENTER_POSITION);
                scrollPane.setHvalue(SCROLL_PANE_CENTER_POSITION);
            }

            drawingLock.unlock();
        }

        drawFunctionGraphics();
    }

    private void updateCoordinatePlane() {

        double markLineWidth = 0.7;
        graphic.setLineWidth(markLineWidth);

        double markTextSize = 9;
        graphic.setFont(Font.font(markTextSize));

        graphic.setStroke(Color.BLACK);

        double halfCanvasSize = canvasSize / 2;

        graphic.strokeLine(0, halfCanvasSize, canvasSize, halfCanvasSize); // x
        graphic.strokeLine(halfCanvasSize, 0, halfCanvasSize, canvasSize); // y
        // СТРЕЛКИ И ПОДПИСИ
        graphic.strokeLine(canvasSize - ARROW_DISTANCE, halfCanvasSize - ARROW_DISTANCE, canvasSize, halfCanvasSize);
        graphic.strokeLine(canvasSize - ARROW_DISTANCE, halfCanvasSize + ARROW_DISTANCE, canvasSize, halfCanvasSize);
        graphic.strokeText("x", canvasSize - 10, halfCanvasSize - 10);

        graphic.strokeLine(halfCanvasSize - ARROW_DISTANCE,ARROW_DISTANCE, halfCanvasSize, 0);
        graphic.strokeLine(halfCanvasSize + ARROW_DISTANCE,ARROW_DISTANCE, halfCanvasSize, 0);
        graphic.strokeText("y", halfCanvasSize - 15, 10);


        double halfMarkSize = 4;
        int frequencyOfMarkText = 1; //как часто подписывать отметки

        graphic.strokeText("0",
                halfCanvasSize + halfMarkSize, halfCanvasSize + 2 * halfMarkSize); // coor. center

        for (double mark = 0; mark < canvasSize / 2 - MARK_DISTANCE; mark += MARK_DISTANCE) {
            if (mark != 0) {

                if ((mark % (MARK_DISTANCE * frequencyOfMarkText)) == 0) {
                    // 'x' axis positives mark
                    graphic.strokeText(String.valueOf((int) (mark / newScale)),
                            halfCanvasSize + mark, halfCanvasSize + 3 * halfMarkSize);
                    // 'y' axis positives mark
                    graphic.strokeText(String.valueOf((int) (mark / newScale)),
                            halfCanvasSize + 2 * halfMarkSize, halfCanvasSize - mark);
                    // 'x' axis negatives mark
                    graphic.strokeText(String.valueOf((int) (-mark / newScale)),
                            halfCanvasSize - mark, halfCanvasSize + 3 * halfMarkSize);
                    // 'y' axis negatives mark
                    graphic.strokeText(String.valueOf((int) (-mark / newScale)),
                            halfCanvasSize + 2 * halfMarkSize, halfCanvasSize + mark);
                }

                graphic.strokeLine(
                        mark + halfCanvasSize, halfCanvasSize - halfMarkSize,
                        mark + halfCanvasSize, halfCanvasSize + halfMarkSize
                ); // 'x' axis positives

                graphic.strokeLine(
                        halfCanvasSize - halfMarkSize, halfCanvasSize - mark,
                        halfCanvasSize + halfMarkSize, halfCanvasSize - mark
                ); // 'y' axis positives

                graphic.strokeLine(
                        halfCanvasSize - mark, halfCanvasSize - halfMarkSize,
                        halfCanvasSize - mark, halfCanvasSize + halfMarkSize
                ); // 'x' axis negatives

                graphic.strokeLine(
                        halfCanvasSize - halfMarkSize, halfCanvasSize + mark,
                        halfCanvasSize + halfMarkSize, halfCanvasSize + mark
                ); // 'y' axis negatives

            }
        }

    }

    public void zoomIn() {
        if (canvasSize > MAX_CANVAS_SIZE) {
            return;
        }

        if (newScale < MAX_SCALE) {
            newScale = new BigDecimal(newScale + SCALING_STEP)
                    .setScale(SCALING_ROUNDING, RoundingMode.HALF_UP).doubleValue();
        }

        singleScaleSegment = MARK_DISTANCE / newScale;
    }

    public void zoomOut() {


        if (newScale > MIN_SCALE) {
            newScale = new BigDecimal(newScale - SCALING_STEP)
                    .setScale(SCALING_ROUNDING, RoundingMode.HALF_UP).doubleValue();
        }

        singleScaleSegment = MARK_DISTANCE / newScale;
    }


    private void redrawFunctionGraphics() {
        drawingLock.lock();

        maxX = Function.MIN_X_DOWN_LIMIT;
        minY = Function.MAX_X_UP_LIMIT;

        // To avoid axes overlaying
        graphic.setFill(Color.WHITE);
        graphic.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        updateCoordinatePlane();

        double functionLineWidth = 1;
        double halfCanvasSize = canvasSize / 2;
        graphic.setLineWidth(functionLineWidth);

        for (int funIter = 0; funIter < prevPoints.size(); funIter++) {
            prevPoints.set(funIter, null);
        }

        for (int funIter = 0; funIter < functions.size(); funIter++) {
            for (int pointsIter = 0; pointsIter <= functionPointsIterators.get(funIter); pointsIter++) {
                nextPoint = new Point(
                        functions.get(funIter).getPoints()
                                .get(pointsIter).getX() * newScale + halfCanvasSize,
                        -functions.get(funIter).getPoints()
                                .get(pointsIter).getY() * newScale + halfCanvasSize
                );

                if (prevPoints.get(funIter) == null) {
                    prevPoints.set(funIter, nextPoint);
                }

                graphic.setStroke(colorsForFunctions.get(funIter));
                graphic.strokeLine(
                        prevPoints.get(funIter).getX(), prevPoints.get(funIter).getY(),
                        nextPoint.getX(), nextPoint.getY()
                );

                prevPoints.set(funIter, nextPoint);
            }
        }

        drawingLock.unlock();
    }

    private void drawFunctionGraphics() {
        double functionLineWidth = 1;
        double halfCanvasSize = canvasSize / 2;
        graphic.setLineWidth(functionLineWidth);

        for (int funIter = 0; funIter < functions.size(); funIter++) {
            if ((functions.get(funIter).getPoints().size() == 0)
                    || (functionPointsIterators.get(funIter) == -1)
                    || ((functions.get(funIter).getPoints().size() - 1) <= functionPointsIterators.get(funIter))) {
                continue;
            }

            nextPoint = new Point(
                    functions.get(funIter).getPoints()
                            .get(functionPointsIterators
                                    .get(funIter)).getX() * newScale + halfCanvasSize,
                    -functions.get(funIter).getPoints()
                            .get(functionPointsIterators.get(funIter))
                            .getY() * newScale + halfCanvasSize
            );

            if (prevPoints.get(funIter) == null) {
                prevPoints.set(funIter, nextPoint);
            }

            graphic.setStroke(colorsForFunctions.get(funIter));
            graphic.strokeLine(
                    prevPoints.get(funIter).getX(), prevPoints.get(funIter).getY(),
                    nextPoint.getX(), nextPoint.getY()
            );

            prevPoints.set(funIter, nextPoint);

            if (nextPoint.getX() > maxX) {
                maxX = nextPoint.getX();
            }
            if (nextPoint.getY() < minY) {
                minY = nextPoint.getY();
            }
        }
    }

    private void clearGraphic() {
        drawingLock.lock();

        canvasSize = START_CANVAS_SIZE * newScale;

        resizeCanvas();

        for (int funIter = 0; funIter < prevPoints.size(); funIter++) {
            prevPoints.set(funIter, null);
            functionPointsIterators.set(funIter, -1);
        }

        maxX = Function.MIN_X_DOWN_LIMIT;
        minY = Function.MAX_X_UP_LIMIT;

        graphic.setFill(Color.WHITE);
        graphic.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        updateCoordinatePlane();

        drawingLock.unlock();
    }

    public void updateFunctionIterator(Function function) {
        drawingLock.lock();

        functionPointsIterators.set(
                functions.indexOf(function),
                functionPointsIterators.get(functions.indexOf(function)) + 1
        );

        drawingLock.unlock();
    }

    public double getSingleScaleSegment() {
        return singleScaleSegment;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public double getNewScale() {
        return newScale;
    }

    public double getCurrentScale() {
        return currentScale;
    }

    public ObservableList<Color> getColorsForFunctions() {
        return colorsForFunctions;
    }

    public ObservableList<Function> getFunctions() {
        return functions;
    }

}
