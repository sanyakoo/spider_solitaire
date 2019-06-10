package edu.spbstu.games.spider;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static edu.spbstu.games.spider.CardStackView.*;


@Slf4j
public class GameView {

    private GameModel model;
    private final Pane distributionPane;
    private final Pane stacksPane;

    private CardStackView distribStackView;

    private List<CardStackView> stackViews = new ArrayList<>();

    private DragContext dragContext = new DragContext();
    private Map<Node, Integer> nodesStackIdx = new HashMap<>();


    public GameView(GameModel model, Pane distributionPane, Pane stacksPane) {
        this.model = model;
        this.distributionPane = distributionPane;
        this.stacksPane = stacksPane;
        init();
    }

    private void init() {
        distribStackView = new CardStackView(model.getDistributionStack(), dragContext, true, null);
        distributionPane.getChildren().addAll(distribStackView.getNodes());


        // click on distribution stack adds one card to each work stack
        distribStackView.getCardNodes().forEach(cardView -> cardView.setOnMouseClicked(event -> {
            for (int i = 0; i < model.getStacks().size(); i++) {
                model.getStacks().get(i).getCards().add(model.getDistributionStack().getFromTop());
            }
            CardView removed = distribStackView.getCardNodes().remove(distribStackView.getCardNodes().size() - 1);
            distributionPane.getChildren().removeAll(removed);

        }));


        stacksPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            int stackIndexByCoord = getStackIndexByCoord(event.getX());
            for (CardStackView stackView : stackViews) {
                stackView.setAllowDrop(false);
            }
            if (stackIndexByCoord >= 0 && stackIndexByCoord < stackViews.size() && checkAbilityToDrop(stackIndexByCoord, dragContext.currentDraggedCardViews)) {
                stackViews.get(stackIndexByCoord).setAllowDrop(true);
            }
        });

        stacksPane.setOnMouseReleased(event -> {
            if (dragContext.currentDraggedCardViews == null || dragContext.currentDraggedCardViews.isEmpty()) return;
            int targetStackIdx = getStackIndexByCoord(event.getX());


            log.debug("dragged ctx: {}", dragContext.currentDraggedCardViews.size());
            Integer currentDraggedNodeStackIdx = nodesStackIdx.get(dragContext.currentDraggedCardViews.get(0));


            if (currentDraggedNodeStackIdx == targetStackIdx || targetStackIdx < 0 || targetStackIdx >= stackViews.size() || !stackViews.get(targetStackIdx).isAllowedDrop()) {
                CardStackModel srcStack = model.getStacks().get(currentDraggedNodeStackIdx);
                srcStack.stayCardsInStack();
            } else {
                for (CardStackView cardStackView : stackViews) {
                    cardStackView.setAllowDrop(false);
                }

                addCardToStack(currentDraggedNodeStackIdx, targetStackIdx, dragContext.currentDraggedCardViews);//dragContext.currentDraggedCardView


                log.debug("STACKS: {} {} {} {} {} {} {} {} {} {}",
                        model.getStacks().get(0).getCards().size(),
                        model.getStacks().get(1).getCards().size(),
                        model.getStacks().get(2).getCards().size(),
                        model.getStacks().get(3).getCards().size(),
                        model.getStacks().get(4).getCards().size(),
                        model.getStacks().get(5).getCards().size(),
                        model.getStacks().get(6).getCards().size(),
                        model.getStacks().get(7).getCards().size(),
                        model.getStacks().get(8).getCards().size(),
                        model.getStacks().get(9).getCards().size()
                );
            }
        });


        for (int i = 0; i < model.getStacks().size(); i++) {
            CardStackModel cardStackModel = model.getStacks().get(i);
            int finalI1 = i;
            CardStackView cardStackView = new CardStackView(cardStackModel, dragContext, false,
                    (List<? extends Node> cardViews) -> {
                        stacksPane.getChildren().addAll(cardViews);
                        cardViews.forEach(o -> nodesStackIdx.put(o, finalI1));
                    });
            stackViews.add(cardStackView);
            int finalI = i;
            cardStackView.getCardNodes().forEach(cardView -> nodesStackIdx.put(cardView, finalI));
            stacksPane.getChildren().addAll(cardStackView.getNodes());

        }


       /* for (int i = 0; i < stackViews.size(); i++) {
            CardStackView cardStackView = stackViews.get(i);
            int finalI = i;
            cardStackView.getCardNodes().addListener((ListChangeListener<? super CardView>) change -> {
                while (change.next()) {
                    if (change.wasAdded()) {
                        List<? extends CardView> addedSubList = change.getAddedSubList();
                        stacksPane.getChildren().addAll(addedSubList);
                        for (CardView cardView : addedSubList) {
                            nodesStackIdx.put(cardView, finalI);
                        }
                    }
                }
            });
        }*/
    }

    private boolean checkAbilityToDrop(int stackIndexByCoord, List<CardView> currentDraggedCardViews) {
        CardStackModel cardStack = model.getStacks().get(stackIndexByCoord);
        ObservableList<CardModel> cards = cardStack.getCards();
        if (cards.isEmpty()) return true;
        CardModel cardModel = cards.get(cards.size() - 1);
        CardModel.Rank rank = cardModel.getRank();


        CardModel cardModelDraggedDeepest = currentDraggedCardViews.get(0).getCardModel();

        CardModel.Rank rankDragged = cardModelDraggedDeepest.getRank();


        return rank.ordinal() - rankDragged.ordinal() == 1;
    }


    public int getStackIndexByCoord(double x) {
        return (int) (x / STACK_WIDTH);
    }


    public void addCardToStack(int srcIdx, int targetIdx, List<CardView> currentDraggedCardViews) {//CardView cardNode
        CardStackModel srcStack = model.getStacks().get(srcIdx);

//        if (srcIdx == targetIdx || targetIdx < 0 || targetIdx >= stackViews.size()) {
//            srcStack.stayCardsInStack();
//        } else
        {
            log.debug("Moving from stack {} to stack {}", srcIdx, targetIdx);

            CardStackModel targetStack = model.getStacks().get(targetIdx);

            int minIdx = srcStack.getCards().size() - currentDraggedCardViews.size();
            List<CardModel> toMove = new ArrayList<>();
            for (int i = minIdx; i < srcStack.getCards().size(); i++) {
                CardModel cardModel = srcStack.getCards().get(i);
                toMove.add(cardModel);
            }

            srcStack.getCards().removeAll(toMove);
            targetStack.getCards().addAll(toMove);
            srcStack.showTopCard();

            for (CardView currentDraggedCardView : currentDraggedCardViews) {
                nodesStackIdx.put(currentDraggedCardView, targetIdx);
            }
        }

        dragContext.currentDraggedCardViews = null;
    }


}