package com.chess.chess.model;

public class Pawn extends Piece {

    public Pawn(int color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY) {

        int direction = (color == 0) ? -1 : 1;

        
        if (startX == endX && endY - startY == direction) {
            return true;
        }

        
        if (startX == endX && (
            (color == 0 && startY == 6 && endY == 4) || 
            (color == 1 && startY == 1 && endY == 3)    
        )) {
            return true;
        }

        
        if (Math.abs(endX - startX) == 1 && endY - startY == direction) {
            return true;
        }

        return false;
    }

    @Override
    public char getSymbol() {
        return (color == 0) ? 'P' : 'p';
    }
}