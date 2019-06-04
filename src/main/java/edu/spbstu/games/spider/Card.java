package edu.spbstu.games.spider;

import lombok.Data;

@Data
public class Card {


    public enum Suit {CLUBS, SPADES, HEARTS, DIAMONDS}

//    private String image;

    // Kinds of ranks
    public enum Rank {
        ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING
    }


    private Rank rank;
    private Suit suit;

    private boolean back;

    private int cardWidth = 950 / 13;
    private int cardHeight = 98;


    public int getWidth() {
        return cardWidth;
    }

    public int getHeight() {
        return cardHeight;
    }

    public int getY() {
        return cardHeight * suit.ordinal();
    }

    public int getX() {
        return cardWidth * rank.ordinal();
    }
}