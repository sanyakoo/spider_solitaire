package edu.spbstu.games.spider;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Slf4j
@Data
public class Controller {

    private static Controller INSTANCE;

    @Setter
    private Application app;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Controller() {
        INSTANCE = this;
    }

    public static Controller getInstance() {
        return INSTANCE;
    }

    @FXML
    public Pane resultBox;

    @FXML
    public Pane distribPane;


    private GameModel gameModel = new GameModel(GameModel.GameMode.SINGLE_SUIT);

    private GameView gameView;

    @FXML
    public void initialize() {
        gameView = new GameView(gameModel, distribPane, resultBox);
    }
}