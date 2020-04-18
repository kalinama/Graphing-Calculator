package view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;


public class TwoNodesGrid {
    private Node leftNode;
    private Node rightNode;

    private GridPane gridPane;


    public TwoNodesGrid(Node leftNode, Node rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;

        gridPane = createGridPane();
    }

    private GridPane createGridPane() {
        gridPane = new GridPane();

        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(50);
        column.setHalignment(HPos.LEFT);

        int insets = 8;
        gridPane.getColumnConstraints().addAll(column, column);
        GridPane.setMargin(leftNode, new Insets(insets));
        GridPane.setMargin(rightNode, new Insets(insets));

        gridPane.add(leftNode, 0,0);
        gridPane.add(rightNode,1,0);

        return gridPane;
    }

    public GridPane getGridPane() {
        return gridPane;
    }
}
