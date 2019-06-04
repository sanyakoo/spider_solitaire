package edu.spbstu.games.spider;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class CardStack {

    public static final int VERTICAL_SHIFT = 25;

    public static final int WIDTH = 100;
    public static final int CARD_LEFT_MARGIN = 10;


    //    private List<ImageView> cardsViews = new ArrayList<>();
    @Getter
    private List<Card> cards = new ArrayList<>();

    public CardStack() {
    }

    public void setCards(List<Card> cards1) {
        this.cards = cards1;


    }

    public List<Node> getCardNodes() {
        List<Node> cardsViews = new ArrayList<>();
        for (Card card : cards) {
            ImageView iv;
            if (card.isBack()) {
                Image img = new Image("/card_back.png");
                iv = new ImageView(img);
                iv.setFitWidth(card.getCardWidth());
                iv.setFitHeight(card.getCardHeight());
            } else {
                Image img = new Image("/cards.png");
                Rectangle2D cellClip = new Rectangle2D(card.getX(), card.getY(), card.getWidth(), card.getHeight());
                iv = new ImageView(img);
                iv.setViewport(cellClip);
//                iv.setImage();
            }
            cardsViews.add(iv);
        }
        for (int i = 0; i < cardsViews.size(); i++) {
            Node card = cardsViews.get(i);
            card.setLayoutY(VERTICAL_SHIFT * (i + 1));
            card.setLayoutX(CARD_LEFT_MARGIN);
        }

        return cardsViews;
    }


}