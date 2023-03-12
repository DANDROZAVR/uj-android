package com.nani.engine.game.nonogram;

import java.util.StringTokenizer;

public class Field {
    public enum Content {
        EMPTY,
        CROSSED,
        FILLED
    }
    private Content value;
    public Field(StringTokenizer s) {
        String item = s.nextToken();
        if (item.equals("0"))
            item = "EMPTY"; else
        if (item.equals("1"))
            item = "CROSSED"; else
        if (item.equals("2"))
            item = "FILLED";
        value = Content.valueOf(item);
    }
    public Content getContent() { return value; }
    public void setContent(Content value) {
        this.value = value;
    }
    public void setEmpty() {
        setContent(Content.EMPTY);
    }
    public void setCrossed() {
        setContent(Content.CROSSED);
    }
    public void setFilled() {
        setContent(Content.FILLED);
    }
    public boolean isFilled() { return value == Content.FILLED; }
    public boolean isCrossed() { return value == Content.CROSSED; }
    public boolean isEmpty() { return value == Content.EMPTY; }
}
