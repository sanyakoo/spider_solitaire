package edu.spbstu.games.spider;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

public class CardView extends ImageView {


    @Getter
    private CardModel cardModel;

    public CardView(CardModel cardModel) {
        this.cardModel = cardModel;
        if (cardModel.getHiddenValue().get()) {
            hideValue();
        } else {
            showValue();
        }

        cardModel.getHiddenValue().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                hideValue();
            } else {
                showValue();
            }
        });

    }

    public void showValue() {
        Image img = new Image("/cards.png");
        Rectangle2D cellClip = new Rectangle2D(getXOnPict(), getYOnPict(), getCardWidth(), getCardHeight());
        this.setImage(img);
        this.setViewport(cellClip);
    }

    public void hideValue() {
        Image img = new Image("/card_back.png");
        this.setImage(img);
        this.setFitWidth(getCardWidth());
        this.setFitHeight(getCardHeight());
    }



    @Getter
    private int cardWidth = 950 / 13;

    @Getter
    private int cardHeight = 98;


    private int getYOnPict() {
        return cardHeight * cardModel.getSuit().ordinal();
    }

    private int getXOnPict() {
        return cardWidth * cardModel.getRank().ordinal();
    }

}