package com.chess.chess.model;

public class Board {
    

    private Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        initializeBoard();
    }

   
    public void initializeBoard(){
        
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(1); 
            board[6][i] = new Pawn(0); 
        }

        
        board[0][0] = new Rook(1);
        board[0][7] = new Rook(1);
        board[7][0] = new Rook(0);
        board[7][7] = new Rook(0);

        
        board[0][1] = new Knight(1);
        board[0][6] = new Knight(1);
        board[7][1] = new Knight(0);
        board[7][6] = new Knight(0);

        
        board[0][2] = new Bishop(1);
        board[0][5] = new Bishop(1);
        board[7][2] = new Bishop(0);
        board[7][5] = new Bishop(0);

        
        board[0][3] = new Queen(1);
        board[7][3] = new Queen(0);

        
        board[0][4] = new King(1);
        board[7][4] = new King(0);
    }

  public boolean movePiece(int sx, int sy, int ex, int ey) {

        Piece piece = board[sx][sy];

        if (piece == null)
            return false;

        if (!piece.isValidMove(sx, sy, ex, ey))
            return false;
            board[ex][ey] = piece;
            board[sx][sy] = null;

        return true;
    }

 public void printBoard() {
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {

            if (board[i][j] == null) {
                System.out.print(". ");
            } else {
                System.out.print(board[i][j].getSymbol() + " ");
            }

        }
        System.out.println();
    }
}}
