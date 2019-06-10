package edu.spbstu.games.spider;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;

@Data
public class CardModel {

    public enum Suit {CLUBS, SPADES, HEARTS, DIAMONDS}

    public enum Rank {
        ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING
    }

    private Rank rank;
    private Suit suit;
    private BooleanProperty hiddenValue = new SimpleBooleanProperty(true);


    public CardModel(Rank rank, Suit suit, boolean hiddenValue) {
        this.rank = rank;
        this.suit = suit;
        this.hiddenValue.setValue(hiddenValue);
    }


}