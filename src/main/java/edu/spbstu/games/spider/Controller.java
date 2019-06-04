package edu.spbstu.games.spider;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static edu.spbstu.games.spider.CardStack.CARD_LEFT_MARGIN;
import static edu.spbstu.games.spider.CardStack.VERTICAL_SHIFT;

@Slf4j
@Data
public class Controller {

    public static final int STACK_WIDTH = 110;
    public static final int STACK_TOP_MARGIN = 47;
    private static Controller INSTANCE;

    @Setter
    private Application app;


    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Controller() {
        INSTANCE = this;
    }

    public static Controller getInstance() {
        return INSTANCE;
    }


    @FXML
    public Pane resultBox;

    @FXML
    public VBox razdachaBox;

    @FXML
    public Pane distribPane;


    public int getStackIndexByCoord(double x) {
        return (int) (x / STACK_WIDTH);
    }


    private List<Rectangle> rects = new ArrayList<>();

    private List<CardStack> stacks = new ArrayList<>();
    private List<List<Node>> stackNodes = new ArrayList<>();


    private DragContext dragContext = new DragContext();


    public static final class DragContext {
        public double mouseAnchorX;
        public double mouseAnchorY;
        public double initialX;
        public double initialY;
        public Node currentDraggedNode;
    }


    private Map<Node, Integer> nodesStackIdx = new HashMap<>();

    @FXML
    public void initialize() {
        CardStack cardStack = getCardStack();

        Rectangle rectangle = getStackBackground();
        List<Node> cardNodes = cardStack.getCardNodes();


        distribPane.getChildren().add(rectangle);
        distribPane.getChildren().addAll(cardNodes);


        resultBox.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            int stackIndexByCoord = getStackIndexByCoord(event.getX());
//            log.debug("MOUSE dragged event OVER stack: {} , x: {}, y: {}, type: {}",
//                    stackIndexByCoord,
//                    event.getX(), event.getY(), event.getEventType());


            for (Rectangle rect : rects) {
                rect.getStyleClass().remove("allowed");
            }
            if (stackIndexByCoord >= 0 && stackIndexByCoord < rects.size()) {
                rects.get(stackIndexByCoord).getStyleClass().add("allowed");
            }
        });

        resultBox.setOnMouseReleased(event -> {
            if (dragContext.currentDraggedNode == null) return;
            int stackIndexByCoord = getStackIndexByCoord(event.getX());

            for (Rectangle rect : rects) {
                rect.getStyleClass().remove("allowed");
            }


            Integer currentDraggedNodeStackIdx = nodesStackIdx.get(dragContext.currentDraggedNode);
            addCardToStack(currentDraggedNodeStackIdx, stackIndexByCoord, dragContext.currentDraggedNode, null);
        });

//        -----------
        for (int i = 0; i < 10; i++) {
            CardStack cardStack2 = getCardStack();

            rectangle = getStackBackground();

            rects.add(rectangle);


            cardNodes = cardStack2.getCardNodes();
            Node nodeTop = cardNodes.get(cardNodes.size() - 1);

            Node draggableNode = makeDraggable(nodeTop);

//            draggableNode.setLayoutX(10);
//            draggableNode.setLayoutY(VERTICAL_SHIFT * 6);
            cardNodes.set(cardNodes.size() - 1, draggableNode);
            List<Node> nodes = new ArrayList<>();
            nodes.add(rectangle);
            nodes.addAll(cardNodes);

            for (Node node : nodes) {
                node.setLayoutX(node.getLayoutX() + STACK_WIDTH * i);
                node.setLayoutY(node.getLayoutY() + STACK_TOP_MARGIN);
                nodesStackIdx.put(node, i);
//                node.relocate(STACK_WIDTH * i, STACK_TOP_MARGIN);
            }


            resultBox.getChildren().add(rectangle);
            resultBox.getChildren().addAll(cardNodes);

            stacks.add(cardStack2);

            stackNodes.add(cardNodes);
        }
    }


    public void addCardToStack(int srcIdx, int targetIdx, Node cardNode, Card card) {

        if (srcIdx == targetIdx || targetIdx < 0 || targetIdx >= stackNodes.size()) {
            cardNode.setLayoutX(dragContext.initialX);
            cardNode.setLayoutY(dragContext.initialY);
            return;
        }
        log.debug("Moving from stack {} to stack {}", srcIdx, targetIdx);

        List<Node> nodes = stackNodes.get(targetIdx);
        CardStack stack = stacks.get(targetIdx);

        cardNode.setLayoutX((targetIdx * STACK_WIDTH) + CARD_LEFT_MARGIN);
//        cardNode.setTranslateX(0);
//        cardNode.setTranslateY(0);
        cardNode.setLayoutY((nodes.size() + 1) * VERTICAL_SHIFT + STACK_TOP_MARGIN);
        if (srcIdx >= 0) {


            List<Node> srcStackNodes = stackNodes.get(srcIdx);
            CardStack cardStackSrc = stacks.get(srcIdx);
            srcStackNodes.remove(cardNode);
            Card removed = cardStackSrc.getCards().remove(cardStackSrc.getCards().size() - 1);

            int index = srcStackNodes.size() - 1;
            if (index >= 0) {
                Node node = srcStackNodes.get(index);
                showFrontSide((ImageView) node, stacks.get(srcIdx).getCards().get(index));
                makeDraggable(node);
            }
            nodes.add(cardNode);
            stack.getCards().add(removed);

            nodesStackIdx.put(cardNode, targetIdx);
        }

        dragContext.currentDraggedNode = null;

    }

    public void showFrontSide(ImageView iv, Card card) {

        Rectangle2D cellClip = new Rectangle2D(card.getX(), card.getY(), card.getWidth(), card.getHeight());

        Image img = new Image("/cards.png");
        iv.setViewport(cellClip);
        iv.setImage(img);

    }

    private Rectangle getStackBackground() {
        Rectangle rectangle = new Rectangle(STACK_WIDTH - 15, 300);
        rectangle.getStyleClass().add("stack");
        return rectangle;
    }

    private CardStack getCardStack() {
        CardStack cardStack = new CardStack();
        List<Card> cards = new ArrayList<>();

        for (int j = 0; j < 6; j++) {
            Card card = new Card();
            card.setRank(Card.Rank.values()[j % 10]);
            card.setSuit(Card.Suit.values()[j % 4]);
            card.setBack(j != 5);
            cards.add(card);
        }

        cardStack.setCards(cards);

        return cardStack;
    }


    public Node makeDraggable(final Node node) {


        final Node wrapGroup = node;// new Group(

//        wrapGroup.addEventFilter(
//                MouseEvent.ANY,
//                mouseEvent -> {
////                    if (dragModeActiveProperty.get())
// {
//                        // disable mouse events for all children
////                        mouseEvent.consume();
//                    }
//                });

        wrapGroup.setOnMousePressed(mouseEvent -> {
//                    if (dragModeActiveProperty.get())
            {
                // remember initial mouse cursor coordinates
                // and node position
                dragContext.mouseAnchorX = mouseEvent.getSceneX();
                dragContext.mouseAnchorY = mouseEvent.getSceneY();
                dragContext.initialX =
                        node.getLayoutX();
                dragContext.initialY =
                        node.getLayoutY();
                wrapGroup.toFront();

                dragContext.currentDraggedNode = node;
            }
        });

        wrapGroup.setOnMouseDragged(mouseEvent -> {
            {
                // shift node from its initial position by delta
                // calculated from mouse cursor movement
                node.setLayoutX(
                        dragContext.initialX
                                + mouseEvent.getSceneX()
                                - dragContext.mouseAnchorX);
                node.setLayoutY(
                        dragContext.initialY
                                + mouseEvent.getSceneY()
                                - dragContext.mouseAnchorY);
            }
        });

        return wrapGroup;
    }
}