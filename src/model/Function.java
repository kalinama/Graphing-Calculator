package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Function {
    public static final double MIN_X_DOWN_LIMIT = 1;
    public static final double MAX_X_UP_LIMIT = 5000;

    public static final double H = 0.1;

    private String definition;

    private double xDownLimit;
    private double xUpLimit;


    private ObservableList<Point> points;


    public Function(String definition, double xDownLimit, double xUpLimit) {
        this.definition = definition;

        this.xDownLimit = xDownLimit;
        this.xUpLimit = xUpLimit;

        points = FXCollections.observableArrayList();
    }

    public Function() {
        this("", MIN_X_DOWN_LIMIT, MAX_X_UP_LIMIT);
    }

    public Function(String definition) {
        this(definition, MIN_X_DOWN_LIMIT, MAX_X_UP_LIMIT);
    }

    public String getDefinition() {
        return definition;
    }

    public double getXUpLimit() {
        return xUpLimit;
    }

    public void setXUpLimit(double xUpLimit) {
        this.xUpLimit = xUpLimit;
    }

    public double getXDownLimit() {
        return xDownLimit;
    }

    public void setXDownLimit(double xDownLimit) {
        this.xDownLimit = xDownLimit;
    }

    public ObservableList<Point> getPoints() {
        return points;
    }

}

