package edu.spbstu.games.spider;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Spider extends Application {


    @Getter
    private static Stage mainStage;

    private static Application app;

    public static synchronized Application getInstance() {
        return app;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        synchronized (Spider.class){
            app = this;
        }

        mainStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main_window.fxml"));
        Parent root = fxmlLoader.load();

        Controller controller = fxmlLoader.getController();

        controller.setApp(this);

        primaryStage.setTitle("Spider");
        primaryStage.setScene(new Scene(root, 1300, 400));
//        try (InputStream resourceAsStream = Spider.class.getResourceAsStream("/icon.png")) {
//            primaryStage.getIcons().add(new Image(resourceAsStream));
//        }

//        primaryStage.setResizable(false);

        primaryStage.show();
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}