import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GaleryGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/GUIGalery.fxml"));
            primaryStage.setOnCloseRequest(evt -> {
                    // prevent window from closing
                    evt.consume();

                    // execute own shutdown procedure
                    GaleryController.gifFrame.dispose();
                    primaryStage.close();
                });
            VBox root = (VBox) loader.load();
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage. setResizable(false);
            primaryStage.show();
        } 
        catch(Exception e)    {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
