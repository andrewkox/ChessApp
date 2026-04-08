package com.chess.chess.model;



public class Bishop extends Piece {
    public Bishop(int color) {
        super(color);
    }

    @Override
    public char getSymbol() {
        return 'B';
    }

    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        int deltaX = Math.abs(endX - startX);
        int deltaY = Math.abs(endY - startY);

        return deltaX == deltaY && deltaX != 0;
    }   
}
