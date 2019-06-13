package edu.spbstu.games.spider;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.function.Consumer;
import java.util.ArrayList;

@Slf4j
public class DistributionCardStackView extends CardStackView {
    public static final int ONE_VISIBLE_CARD_CAPACITY = 10;

    public DistributionCardStackView(CardStackModel model, DragContext dragContext, Consumer<List<? extends Node>> onNewCardViewsCallBack) {
        super(model, dragContext, onNewCardViewsCallBack);
    }

    @Override
    protected void init() {
        model.getCards().addListener((ListChangeListener<? super CardModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    onAddCard(change);
                } else if (change.wasRemoved()) {
                    onRemoveCard(change);
                }
            }
        });
    }

    protected List<CardView> createCardNodes() {
        List<CardView> cardViews = new ArrayList<>();
        for (int i = 0; i < model.getCards().size() / ONE_VISIBLE_CARD_CAPACITY; i++) {
            CardView cardView = new CardView(new CardModel(CardModel.Rank.ACE, CardModel.Suit.CLUBS, true));
            cardViews.add(cardView);
        }
        return cardViews;
    }

    protected void onRemoveCard(ListChangeListener.Change<? extends CardModel> change) {
    }
}
