package com.chess.chess;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Chess backend works!";
    }

    @GetMapping("/board")
    public int[][] board() {
        int[][] board = new int[8][8];

        board[1][0] = 1; 
        board[6][0] = 2; 
        
        return board;
    }

    @GetMapping("/players")
    public String index() {
        return "index";
    }

}
