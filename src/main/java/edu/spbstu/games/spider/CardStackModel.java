package edu.spbstu.games.spider;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;


@Slf4j
public class CardStackModel {

    @Getter
    @Setter
    private ObservableList<CardModel> cards = FXCollections.observableArrayList();

    @Setter
    @Getter
    private IntegerProperty idx = new SimpleIntegerProperty();

    public CardStackModel(int i) {
        idx.set(i);
    }

    public CardStackModel() {
        idx.set(0);
    }


    public void addCard(CardModel card) {
        cards.add(card);
    }


    public void shuffle() {
        Collections.shuffle(cards);
    }

    public CardModel getFromTop() {
        if (cards.isEmpty()) throw new IllegalStateException("Cannot get top card from empty stack");
        return cards.remove(cards.size() - 1);
    }

    public void showTopCard() {
        if(cards.isEmpty()) return;
        CardModel topCardModel = cards.get(cards.size() - 1);
        topCardModel.getHiddenValue().set(false);
    }

    public void stayCardsInStack() {
        Integer value = idx.getValue();
        idx.set(-1);
        idx.set(value);

    }
}