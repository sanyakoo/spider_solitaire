package edu.spbstu.games.spider;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;



public class CardStack extends Pane {

    public static final int SHIFT = 25;
//    private List<ImageView> cardsViews = new ArrayList<>();
    private List<Card> cards = new ArrayList<>();
    private List<ImageView> cardsViews;

    public CardStack() {
        getStyleClass().add("stack");

        setOnDragEntered(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            getStyleClass().add("allowed");
        });
        setOnDragExited(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            getStyleClass().removeAll("allowed");
        });

        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.ANY);

            if (getChildren().size() > 0) {
                ClipboardContent content = new ClipboardContent();
                content.putImage(cardsViews.get(cardsViews.size() - 1).getImage());
                db.setContent(content);
            }

            DragHolder.cards = cards;
            event.consume();
        });
    }

    public void setCards(List<Card> cards1) {
        this.cards = cards1;

        cardsViews = new ArrayList<>();
        for (Card card : cards1) {
            Image img = new Image("/cards.png");
            Rectangle2D cellClip = new Rectangle2D(card.getX(), card.getY(), card.getWidth(), card.getHeight());
            ImageView iv = new ImageView(img);
//            iv.setFitWidth(100);
//            iv.setFitHeight(303 * 100 / 200);

            iv.setViewport(cellClip);
            cardsViews.add(iv);
        }
        getChildren().clear();
        for (int i = 0; i < cardsViews.size(); i++) {
            ImageView card = cardsViews.get(i);
            card.setTranslateY(SHIFT * (i + 1));
            card.setTranslateX(10);
            getChildren().add(card);
        }
    }
}
