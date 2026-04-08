package com.chess.chess.model;

public class TestBoard {

    public static void main(String[] args) {

        Board board = new Board();
        
       

        boolean moved = board.movePiece(6, 0, 5, 0);
        
        board.printBoard();


        System.out.println("Move result: " + moved);

    }
}