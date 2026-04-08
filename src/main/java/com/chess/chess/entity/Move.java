package com.chess.chess.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;





@Entity
public class Move {
    @Id
    @GeneratedValue
    private Long id;

    private int fromRow;
    private int fromCol;

    private int toRow;
    private int toCol;

    @ManyToOne
    private Game game;
}
