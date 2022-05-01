import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class BildbearbeitungGUI extends Application {
    config cfg = new config();
    //static Stage classStage = new Stage();
    public static void main(String[] args) {
        /*BildbearbeitungGUI bGUI = new BildbearbeitungGUI();
        
        // Then call its start() method in the following way:

        bGUI.start(BildbearbeitungGUI.classStage);
        //bGUI.start(BildbearbeitungGUI.classStage);
        Application.launch(BildbearbeitungGUI.class);
        new Thread() {
        @Override
        public void run() {
        javafx.application.Application.launch(BildbearbeitungGUI.class);
        }
        }.start();*/
        // startUpTest = StartUpTest.waitForStartUpTest();
        //startUpTest.printSomething();
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        deleteTemp.delete();
        try {
            if (cfg.ftpServerON) {
                ftpServer ftp = new ftpServer();
                ftp.serverTest();
                ftp.initializeHomeDirectory();
                ftp.createDirectory(cfg.ftpProjectHomeDirectory+"/images/");
            }

            FXMLLoader loader;

            primaryStage.setOnCloseRequest(evt -> {
                    // prevent window from closing
                    evt.consume();

                    // execute own shutdown procedure
                    deleteTemp.delete();
                    primaryStage.close();
                });

            if (language.getLanguage().equals("German")) {
                loader = new FXMLLoader(getClass().getResource("view/GUIMainGer.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("view/GUIMainEng.fxml"));
            }
            VBox root = (VBox) loader.load();
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.show();
        } 
        catch(Exception e)    {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
