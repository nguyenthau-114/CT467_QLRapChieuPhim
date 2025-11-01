package doituong;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Mainapp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load file movie.fxml (đảm bảo file này nằm trong cùng package hoặc dùng đường dẫn tuyệt đối)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/doituong/Phim_truycap.fxml"));
Parent root = loader.load();

//Phim_truycapController controller = loader.getController();
//controller.taiDuLieu(); // hoặc controller.loadData();

stage.setScene(new Scene(root));
stage.setTitle("🎬 Demo JavaFX Scene");
stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
