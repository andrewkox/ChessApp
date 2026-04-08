package com.chess.chess.model;


public class Queen extends Piece {
    public Queen(int color) {
        super(color);
    }
    
    @Override
    public char getSymbol() {
            return 'Q';
        }

    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        int deltaX = Math.abs(endX - startX);
        int deltaY = Math.abs(endY - startY);

        return (deltaX == deltaY && deltaX != 0) || (deltaX == 0 && deltaY != 0) || (deltaX != 0 && deltaY == 0);
        
    }   
}
