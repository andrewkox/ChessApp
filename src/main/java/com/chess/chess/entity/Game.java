package com.chess.chess.entity;

import jakarta.persistence.*;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long id;
    private Long playerWhiteId;
    private Long playerBlackId;
    private boolean finished;
    private String winner;
    private boolean started;
    private int turn;

   
    private boolean drawOffered;

    public Long getId() {
        return id;
    }

    public Long getPlayerWhiteId() {
        return playerWhiteId;
    }

    public void setPlayerWhiteId(Long playerWhiteId) {
        this.playerWhiteId = playerWhiteId;
    }

    public Long getPlayerBlackId() {
        return playerBlackId;
    }

    public void setPlayerBlackId(Long playerBlackId) {
        this.playerBlackId = playerBlackId;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public boolean isDrawOffered() {
        return drawOffered;
    }

    public void setDrawOffered(boolean drawOffered) {
        this.drawOffered = drawOffered;
    }
}