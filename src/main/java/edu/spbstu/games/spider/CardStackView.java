package edu.spbstu.games.spider;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@Slf4j
public class CardStackView {

    public static final int STACK_WIDTH = 110;
    public static final int STACK_TOP_MARGIN = 47;


    public static final int VERTICAL_SHIFT = 25;

    //    public static final int CARD_WIDTH = 100;
    public static final int CARD_LEFT_MARGIN = 10;

    private final Rectangle backGroundRect;

    @Getter
    private final List<CardView> cardNodes = new ArrayList<>();

    private CardStackModel model;
    private DragContext dragContext;
    private boolean distributionStack;

    public CardStackView(CardStackModel model, DragContext dragContext, boolean distributionStack,
                         Consumer<List<? extends Node>> cardViewsOnNewCallback) {
        this.model = model;
        this.dragContext = dragContext;
        this.distributionStack = distributionStack;

        backGroundRect = createStackBackground();
        cardNodes.addAll(createCardNodes(distributionStack));

        model.getCards().forEach(cardModel -> cardModel.getHiddenValue().addListener((observable, oldValue, newValue) -> {
            makeDraggable2(cardNodes);
        }));

        makeDraggable2(cardNodes);
        getNodes().forEach(node -> {
            node.setLayoutY(node.getLayoutY() + STACK_TOP_MARGIN);
        });

        setXIdx(model.getIdx().get());
        model.getIdx().addListener((observable, oldValue, newValue) -> {

            log.debug("stack IDX = {}", newValue);
            setXIdx((Integer) newValue);
        });

        model.getCards().addListener((ListChangeListener<? super CardModel>) change -> {
            //TO DO check all stacks for game finish
            while (change.next()) {
                if (change.wasAdded()) {
                    List<CardView> addedCardViews = dragContext.currentDraggedCardViews;
                    if (addedCardViews == null) {
                        addedCardViews = new ArrayList<>();
                        List<? extends CardModel> addedSubList = change.getAddedSubList();
                        for (CardModel cardModel : addedSubList) {
                            cardModel.getHiddenValue().set(false);
                            addedCardViews.add(new CardView(cardModel));
                        }
                        if (cardViewsOnNewCallback != null) {
                            cardViewsOnNewCallback.accept(addedCardViews);
                        }
                    }
                    log.debug("Added to stack N{}: {} ", model.getIdx().get(), addedCardViews.size());
                    cardNodes.addAll(addedCardViews);

                    setXIdx(model.getIdx().get());
                    makeDraggable2(cardNodes);
                } else if (change.wasRemoved()) {

                    int removedSize = change.getRemovedSize();
                    log.debug("deleted from N{}: {}", model.getIdx().get(), removedSize);
                    for (int i = 0; i < (distributionStack ? removedSize / 10 : removedSize); i++) {
                        cardNodes.remove(cardNodes.size() - 1);
                    }
                }
            }
        });
    }


    private Rectangle createStackBackground() {
        Rectangle rectangle = new Rectangle(STACK_WIDTH - 15, 300);
        rectangle.getStyleClass().add("stack");
        return rectangle;
    }

    private List<CardView> createCardNodes(boolean distributionStack) {
        List<CardView> cardsViews = new ArrayList<>();
        if (distributionStack) {
            for (int i = 0; i < model.getCards().size() / 10; i++) {
                // any card - it always be hidden
                CardView cardView = new CardView(new CardModel(CardModel.Rank.ACE, CardModel.Suit.CLUBS, true));
                cardsViews.add(cardView);
            }
        } else {
            for (CardModel card : model.getCards()) {
                CardView cardView = new CardView(card);
                cardsViews.add(cardView);
            }
        }
        for (int i = 0; i < cardsViews.size(); i++) {
            Node card = cardsViews.get(i);
            card.setLayoutY(VERTICAL_SHIFT * (i + 1));
            card.setLayoutX(CARD_LEFT_MARGIN);
        }

        return cardsViews;
    }


    public void setAllowDrop(boolean b) {
        if (!b) {
            backGroundRect.getStyleClass().remove("allowed");
        } else {
            backGroundRect.getStyleClass().add("allowed");
        }
    }

    public boolean isAllowedDrop() {
        return backGroundRect.getStyleClass().contains("allowed");
    }

    public List<Node> getNodes() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(backGroundRect);
        nodes.addAll(cardNodes);
        return nodes;
    }

    private void makeDraggable2(List<CardView> cardsViews) {
        for (int i = cardsViews.size() - 1; i >= 0; i--) {
            CardModel cardModel = getCardNodes().get(i).getCardModel();

            makeDraggable(i, !cardModel.getHiddenValue().get() && checkCorrectStackSequence(i));
        }
    }

    private boolean checkCorrectStackSequence(int idx) {
        CardModel cardModel = getCardNodes().get(idx).getCardModel();
        for (int i = idx + 1; i < getCardNodes().size(); i++) {
            CardModel nextCardModel = getCardNodes().get(i).getCardModel();
            if (cardModel.getRank().ordinal() - nextCardModel.getRank().ordinal() != 1) return false;
            cardModel = nextCardModel;
        }
        return true;
    }

    public void makeDraggable(int idx, boolean makeDraggable) {


        final CardView deepestCard = getCardNodes().get(idx);// new Group(

//        wrapGroup.addEventFilter(
//                MouseEvent.ANY,
//                mouseEvent -> {
////                    if (dragModeActiveProperty.get())
// {
//                        // disable mouse events for all children
////                        mouseEvent.consume();
//                    }
//                });

        EventHandler<MouseEvent> eventHandlerMousePress = mouseEvent -> {
            // remember initial mouse cursor coordinates
            // and node position
            dragContext.mouseAnchorX = mouseEvent.getSceneX();
            dragContext.mouseAnchorY = mouseEvent.getSceneY();


            dragContext.initialX = new ArrayList<>();
            dragContext.initialY = new ArrayList<>();
            List<CardView> draggedNodes = new ArrayList<>();
            for (int i = idx; i < CardStackView.this.getCardNodes().size(); i++) {
                CardView cardView = CardStackView.this.getCardNodes().get(i);
                log.debug("Dragging card: {}", cardView.getCardModel());
                draggedNodes.add(cardView);
                cardView.toFront();

                dragContext.initialX.add(
                        cardView.getLayoutX());
                dragContext.initialY.add(
                        cardView.getLayoutY());

            }
            dragContext.currentDraggedCardViews = draggedNodes;

        };

        deepestCard.setOnMousePressed(makeDraggable ? eventHandlerMousePress : null);
        deepestCard.setOnMouseDragged(makeDraggable ? this::handleMouseDrag : null);
    }

    private void setXIdx(int i) {
        backGroundRect.setLayoutX(STACK_WIDTH * i);
        for (int j = 0; j < getCardNodes().size(); j++) {
            CardView cardView = getCardNodes().get(j);
            cardView.setLayoutX(CARD_LEFT_MARGIN + STACK_WIDTH * i);
            cardView.setLayoutY(STACK_TOP_MARGIN + VERTICAL_SHIFT * (j + 1));
        }
    }

    private void handleMouseDrag(MouseEvent mouseEvent) {
        for (int i = 0; i < dragContext.initialX.size(); i++) {
            Node node = dragContext.currentDraggedCardViews.get(i);
            node.setLayoutX(
                    dragContext.initialX.get(i)
                            + mouseEvent.getSceneX()
                            - dragContext.mouseAnchorX);
            node.setLayoutY(
                    dragContext.initialY.get(i)
                            + mouseEvent.getSceneY()
                            - dragContext.mouseAnchorY);
        }
    }
}