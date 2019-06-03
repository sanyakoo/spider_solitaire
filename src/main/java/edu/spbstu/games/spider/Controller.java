package edu.spbstu.games.spider;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
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
//
//    @FXML
//    public TilePane tilePane;

    @FXML
    public HBox workBox;

    @FXML
    public HBox resultBox;

    @FXML
    public VBox razdachaBox;


    @FXML
    public void initialize() {
        CardStack cardStack = getCardStack();
        razdachaBox.getChildren().add(cardStack);

        for (int i = 0; i < 10; i++) {
            CardStack cardStack2 = getCardStack();
            resultBox.getChildren().add(cardStack2);
        }

        for (int i = 0; i < 10; i++) {
            CardStack cardStack2 = getCardStack();
            workBox.getChildren().add(cardStack2);
        }
    }

    private CardStack getCardStack() {
        CardStack cardStack = new CardStack();
        List<Card> cards = new ArrayList<>();

        for (int j = 0; j < 6; j++) {
            Card card = new Card();
//            card.setImage("/king.png");
//            card.setValue(10);
            card.setRank(Card.Rank.values()[j % 10]);
            card.setSuit(Card.Suit.values()[j % 4]);
            cards.add(card);
        }

        cardStack.setPrefWidth(120);
        cardStack.setCards(cards);

        return cardStack;
    }

}
