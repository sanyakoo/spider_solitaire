package edu.spbstu.games.spider;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;


@Getter
public class GameModel {

    private CardStackModel distributionStack;

    private List<CardStackModel> stacks = new ArrayList<>();

    private void checkGameOver(ListChangeListener.Change<? extends CardModel> c) {
        checkGameOver();
    }

    public enum GameMode {SINGLE_SUIT}

    public GameModel(GameMode mode) {
        if (mode != GameMode.SINGLE_SUIT) throw new IllegalArgumentException("Unsupported game mode: " + mode);

        stacks.forEach(cardStackModel -> {
            cardStackModel.getCards().addListener((ListChangeListener<? super CardModel>) this::checkGameOver);
        });
        initGame();


    }

    public void initGame() {
        distributionStack = new CardStackModel();
        stacks.clear();
        //13*4
        int suitIdx = 1;
        //11*4 + 10* 6
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 13; j++) {
                distributionStack.addCard(new CardModel(CardModel.Rank.values()[j],
                        CardModel.Suit.values()[suitIdx], true));
            }
        }
        distributionStack.shuffle();


        int[] stackSizes = {6, 6, 6, 6, 5, 5, 5, 5, 5, 5};

        for (int i = 0; i < stackSizes.length; i++) {
            CardStackModel stackModel = new CardStackModel(i);
//            stackModel.getIdx().set(i);
            for (int j = 0; j < stackSizes[i]; j++) {
                stackModel.addCard(distributionStack.getFromTop());
            }
            stackModel.showTopCard();

            stacks.add(stackModel);
        }
    }

    private void checkGameOver() {
        boolean gameOver = false;

        int counterOfFullStacks = 0;
        for (int i = 0; i < stacks.size(); i++) {
            CardStackModel cardStackModel = stacks.get(i);
            ObservableList<CardModel> cards = cardStackModel.getCards();
            if (cards.size() != 13 || cards.get(0).getHiddenValue().get()) {
                break;
            } else {
                boolean fullStack = true;
                CardModel cardModel = cards.get(0);
                for (int j = 1; j < cards.size(); j++) {
                    CardModel nextCardModel = cards.get(i);
                    if (cardModel.getRank().ordinal() - nextCardModel.getRank().ordinal() != 1) {
                        fullStack = false;
                        break;
                    }
                    cardModel = nextCardModel;
                }
                if (fullStack) {
                    counterOfFullStacks++;
                }
            }
        }

        if (counterOfFullStacks == 8) {
            gameOver = true;
        }

        if (gameOver) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setGraphic(null);
            alert.setHeaderText(null);
            alert.setContentText("You did it!;) Congratulations!");
            alert.showAndWait();
        }
    }
}