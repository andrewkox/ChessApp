package com.chess.chess.entity;
import jakarta.persistence.*;
import java.util.*;


@Entity
public class Game {
    @Id
    @GeneratedValue
    private Long id;
    private String playerWhite;
    private String playerBlack;

    private String result;
}
