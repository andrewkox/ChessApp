package com.chess.chess.model;



public class Rook extends Piece {
    public Rook(int color) {
        super(color);
    }
    @Override
    public char getSymbol() {
        return (color == 0) ? 'R' : 'r';
    }

    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        return (startX == endX && startY != endY) || (startX != endX && startY == endY);
    }   
}
