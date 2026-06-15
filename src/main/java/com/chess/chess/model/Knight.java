package com.chess.chess.model;


public class Knight extends Piece {
   
    public Knight(int color) {
        super(color);
    }
    @Override
    public char getSymbol() {
        return (color == 0) ? 'N' : 'n';
    }
    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        int deltaX = Math.abs(endX - startX);
        int deltaY = Math.abs(endY - startY);

        return (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2);
    }   
}
