import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Mainapp extends Application {
    @Override
public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/SuatChieu/SuatChieu.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("Quản lý Phim – Rạp Chiếu Phim");
    stage.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}
