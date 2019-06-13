package edu.spbstu.games.spider;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.Map;


@Slf4j
public class CardStackView {

    public static final int STACK_WIDTH = 110;
    public static final int STACK_TOP_MARGIN = 47;


    public static final int VERTICAL_SHIFT = 25;

    //    public static final int CARD_WIDTH = 100;
    public static final int CARD_LEFT_MARGIN = 10;

    protected Rectangle backGroundRect;

    @Getter
    protected final List<CardView> cardNodes = new ArrayList<>();

    protected CardStackModel model;
    private DragContext dragContext;
    private Consumer<List<? extends Node>> onNewCardViewsCallback;

    public CardStackView(CardStackModel model, DragContext dragContext, Consumer<List<? extends Node>> onNewCardViewsCallback) {
        this.model = model;
        this.dragContext = dragContext;
        this.onNewCardViewsCallback = onNewCardViewsCallback;

        backGroundRect = createStackBackground();
        cardNodes.addAll(createCardNodes());
        setXIdx(model.getIdx().get());

        model.getIdx().addListener((observable, oldValue, newValue) -> {
            log.debug("stack IDX = {}", newValue);
            setXIdx((Integer) newValue);
        });

        init();
    }

    protected void init() {
        model.getCards().forEach(cardModel -> cardModel.getHiddenValue().addListener((observable, oldValue, newValue) -> {
            makeDraggable2(cardNodes);
        }));

        makeDraggable2(cardNodes);
    /*    getNodes().forEach(node -> {
            node.setLayoutY(node.getLayoutY() + STACK_TOP_MARGIN);
        });*/

        model.getIdx().addListener((observable, oldValue, newValue) -> {

            log.debug("stack IDX = {}", newValue);
            setXIdx((Integer) newValue);
        });

        model.getCards().addListener((ListChangeListener<? super CardModel>) change -> {
            //TO DO check all stacks for game finish
            while (change.next()) {
                if (change.wasAdded()) {
                    onAddCard(change);
                } else if (change.wasRemoved()) {
                    onRemoveCard(change);
                }
            }
        });
    }

    protected void onAddCard(ListChangeListener.Change<? extends CardModel> change) {
        List<CardView> addedCardViews = new ArrayList<>(dragContext.draggedCardViews.keySet());
        if (addedCardViews.isEmpty()) {
            List<? extends CardModel> addedSubList = change.getAddedSubList();
            for (CardModel cardModel : addedSubList) {
                cardModel.getHiddenValue().set(false);
                addedCardViews.add(new CardView(cardModel));
            }
            if (onNewCardViewsCallback != null) {
                onNewCardViewsCallback.accept(addedCardViews);
            }
        }
        log.debug("Added to stack N{}: {}", model.getIdx().get(), addedCardViews.size());
        cardNodes.addAll(addedCardViews);
        setXIdx(model.getIdx().get());
        makeDraggable2(cardNodes);
    }

    protected void onRemoveCard(ListChangeListener.Change<? extends CardModel> change) {
        int removedSize = change.getRemovedSize();
        log.debug("deleted from N{}: {}", model.getIdx().get(), removedSize);
        for (int i = 0; i < removedSize; i++) {
            cardNodes.remove(cardNodes.size() - 1);
        }
        makeDraggable2(cardNodes);
    }

    protected Rectangle createStackBackground() {
        Rectangle rectangle = new Rectangle(STACK_WIDTH - 15, 300);
        rectangle.getStyleClass().add("stack");
        return rectangle;
    }

    protected List<CardView> createCardNodes() {
        List<CardView> cardsViews = new ArrayList<>();

        for (CardModel card : model.getCards()) {
            CardView cardView = new CardView(card);
            cardsViews.add(cardView);
        }
        return cardsViews;
    }

    protected void setXIdx(int i) {
        backGroundRect.setLayoutX(STACK_WIDTH * i);
        backGroundRect.setLayoutY(STACK_TOP_MARGIN);
        for (int j = 0; j < getCardNodes().size(); j++) {
            CardView cardView = getCardNodes().get(j);
            cardView.setLayoutX(CARD_LEFT_MARGIN + STACK_WIDTH * i);
            cardView.setLayoutY(STACK_TOP_MARGIN + VERTICAL_SHIFT * (j + 1));
        }
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

            dragContext.draggedCardViews.clear();
            for (int i = idx; i < CardStackView.this.getCardNodes().size(); i++) {
                CardView cardView = CardStackView.this.getCardNodes().get(i);
                log.debug("Dragging card: {}", cardView.getCardModel());
                cardView.toFront();
                dragContext.draggedCardViews.put(cardView, ImmutablePair.of(cardView.getLayoutX(), cardView.getLayoutY()));
            }
        };

        deepestCard.setOnMousePressed(makeDraggable ? eventHandlerMousePress : null);
        deepestCard.setOnMouseDragged(makeDraggable ? this::handleMouseDrag : null);
    }

    private void handleMouseDrag(MouseEvent mouseEvent) {
        for (Map.Entry<CardView, Pair<Double, Double>> draggedCardView : dragContext.draggedCardViews.entrySet()) {

            CardView node = draggedCardView.getKey();
            Pair<Double, Double> initialCoords = draggedCardView.getValue();
            node.setLayoutX(
                    initialCoords.getLeft() + mouseEvent.getSceneX() - dragContext.mouseAnchorX);
            node.setLayoutY(
                    initialCoords.getRight() + mouseEvent.getSceneY() - dragContext.mouseAnchorY);
        }
    }
}