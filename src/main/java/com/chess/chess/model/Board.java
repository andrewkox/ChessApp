package com.chess.chess.model;

public class Board {

    private Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        initializeBoard();
    }

    public void initializeBoard(){

        for (int i = 0; i < 8; i++) {
            board[i][1] = new Pawn(1);
            board[i][6] = new Pawn(0);
        }

        board[0][0] = new Rook(1);
        board[7][0] = new Rook(1);
        board[0][7] = new Rook(0);
        board[7][7] = new Rook(0);

        board[1][0] = new Knight(1);
        board[6][0] = new Knight(1);
        board[1][7] = new Knight(0);
        board[6][7] = new Knight(0);

        board[2][0] = new Bishop(1);
        board[5][0] = new Bishop(1);
        board[2][7] = new Bishop(0);
        board[5][7] = new Bishop(0);

        board[3][0] = new Queen(1);
        board[3][7] = new Queen(0);

        board[4][0] = new King(1);
        board[4][7] = new King(0);
    }

    public Piece getPiece(int x, int y) {
        return board[x][y];
    }

    // 🔥 SPRAWDZANIE DROGI (dla rook/bishop/queen)
    private boolean isPathClear(int sx, int sy, int ex, int ey) {
        int dx = Integer.compare(ex, sx);
        int dy = Integer.compare(ey, sy);

        int x = sx + dx;
        int y = sy + dy;

        while (x != ex || y != ey) {
            if (board[x][y] != null) return false;
            x += dx;
            y += dy;
        }

        return true;
    }

    // 🔥 CZY POLE JEST ATAKOWANE
    public boolean isSquareAttacked(int x, int y, int attackerColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                Piece p = board[i][j];
                if (p == null || p.getColor() != attackerColor) continue;

                if (p.isValidMove(i, j, x, y)) {
                    if (p instanceof Knight || p instanceof King || p instanceof Pawn) {
                        return true;
                    }
                    if (isPathClear(i, j, x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 🔥 SZACH
    public boolean isKingInCheck(int color) {
        int kingX = -1, kingY = -1;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                if (p instanceof King && p.getColor() == color) {
                    kingX = i;
                    kingY = j;
                }
            }
        }

        return isSquareAttacked(kingX, kingY, 1 - color);
    }

    public boolean movePiece(int sx, int sy, int ex, int ey) {

        Piece piece = board[sx][sy];
    
        if (piece == null) return false;
    
        Piece target = board[ex][ey];
    
        
        if (target != null &&
            target.getColor() == piece.getColor()) {
    
            return false;
        }
    
        
        if (!piece.isValidMove(sx, sy, ex, ey)) {
            return false;
        }
    
        
        if (!(piece instanceof Knight) &&
            !(piece instanceof Pawn)) {
    
            if (!isPathClear(sx, sy, ex, ey)) {
                return false;
            }
        }
    
        
        if (piece instanceof Pawn) {
    
            
            if (sx == ex &&
                board[ex][ey] != null) {
    
                return false;
            }
    
            
            if (Math.abs(ex - sx) == 1 &&
                board[ex][ey] == null) {
    
                return false;
            }
        }
    
        
        Piece oldTarget = board[ex][ey];
    
        
        board[ex][ey] = piece;
        board[sx][sy] = null;
    
        
        if (isKingInCheck(piece.getColor())) {
    
            
            board[sx][sy] = piece;
            board[ex][ey] = oldTarget;
    
            return false;
        }
    
        return true;
    }

    public boolean hasAnyValidMove(int color) {

        for (int sx = 0; sx < 8; sx++) {
            for (int sy = 0; sy < 8; sy++) {
    
                Piece piece = board[sx][sy];
                if (piece == null || piece.getColor() != color) continue;
    
                for (int ex = 0; ex < 8; ex++) {
                    for (int ey = 0; ey < 8; ey++) {
    
                        // nie bij własnych figur
                        Piece target = board[ex][ey];
                        if (target != null && target.getColor() == color) continue;
    
                        // czy ruch figury jest poprawny
                        if (!piece.isValidMove(sx, sy, ex, ey)) continue;
    
                        // sprawdzanie ścieżki (dla wszystkich poza skoczkiem)
                        if (!(piece instanceof Knight)) {
                            if (!isPathClear(sx, sy, ex, ey)) continue;
                        }
    
                        // 🔥 SYMULACJA RUCHU
                        board[ex][ey] = piece;
                        board[sx][sy] = null;
    
                        boolean inCheck = isKingInCheck(color);
    
                        // 🔥 COFNIĘCIE RUCHU
                        board[sx][sy] = piece;
                        board[ex][ey] = target;
    
                        // jeśli po ruchu NIE ma szacha → legalny ruch
                        if (!inCheck) {
                            return true;
                        }
                    }
                }
            }
        }
    
        return false;
    }

    public boolean isCheckmate(int color) {
        return isKingInCheck(color) && !hasAnyValidMove(color);
    }

    public boolean isStalemate(int color) {
        return !isKingInCheck(color) && !hasAnyValidMove(color);
    }
}
