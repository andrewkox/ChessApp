package com.chess.chess.dto;

public class MoveRequest {

    private String gameId;
    private Long userId;

    private int sx;
    private int sy;
    private int ex;
    private int ey;

    public MoveRequest() {}



    public String getGameId() {
        return gameId;
    }

    public Long getUserId() {
        return userId;
    }

    public int getSx() {
        return sx;
    }

    public int getSy() {
        return sy;
    }

    public int getEx() {
        return ex;
    }

    public int getEy() {
        return ey;
    }



    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setSx(int sx) {
        this.sx = sx;
    }

    public void setSy(int sy) {
        this.sy = sy;
    }

    public void setEx(int ex) {
        this.ex = ex;
    }

    public void setEy(int ey) {
        this.ey = ey;
    }
}