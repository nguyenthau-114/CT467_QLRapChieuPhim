package giaodien;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

public class DashboardController {
    @FXML private AnchorPane leftPane; // hộp bên trái

    private static class Delta { double dx, dy; }

    @FXML
    private void initialize() {
        // Gắn kéo cho mọi node có class "draggable-left" trong leftPane
        for (Node n : leftPane.lookupAll(".draggable-left")) {
            makeDraggable((Region) n);
        }
    }

    private void makeDraggable(Region node) {
        node.setManaged(false);
        node.setCursor(Cursor.HAND);
        Delta d = new Delta();

        node.setOnMousePressed(e -> {
            d.dx = node.getLayoutX() - e.getSceneX();
            d.dy = node.getLayoutY() - e.getSceneY();
            node.setCursor(Cursor.MOVE);
            e.consume();
        });

        node.setOnMouseDragged(e -> {
            double nx = e.getSceneX() + d.dx;
            double ny = e.getSceneY() + d.dy;

            // giới hạn trong khung trái
            double maxX = Math.max(0, leftPane.getWidth()  - node.getWidth());
            double maxY = Math.max(0, leftPane.getHeight() - node.getHeight());
            node.setLayoutX(Math.max(0, Math.min(nx, maxX)));
            node.setLayoutY(Math.max(0, Math.min(ny, maxY)));
            e.consume();
        });

        node.setOnMouseReleased(e -> node.setCursor(Cursor.HAND));
    }
}
