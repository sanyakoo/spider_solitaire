package edu.spbstu.games.spider;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DragContext {
    public double mouseAnchorX;
    public double mouseAnchorY;
    public Map<CardView, Pair<Double, Double>> draggedCardViews = new LinkedHashMap<>();
    public List<CardView> getDraggedCardsList() {
        return new ArrayList<>(draggedCardViews.keySet());
    }
}