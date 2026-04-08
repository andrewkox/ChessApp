package com.chess.chess.model;

public abstract class Piece {

    protected int color;

    public Piece(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public abstract boolean isValidMove(int startX, int startY, int endX, int endY);

    public abstract char getSymbol();
}