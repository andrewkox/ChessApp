package com.chess.chess.model;

public class King extends Piece {

    public King(int color) {
        super(color);
    }

    @Override
    public char getSymbol() {
        return (color == 0) ? 'K' : 'k';
    }

    public boolean isValidMove(int startX, int startY, int endX, int endY) {

        int deltaX = Math.abs(endX - startX);
        int deltaY = Math.abs(endY - startY);

        return (deltaX <= 1 && deltaY <= 1) && !(deltaX == 0 && deltaY == 0);
    }
}