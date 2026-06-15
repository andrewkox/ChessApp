package com.chess.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.chess.chess.entity.Game;


public interface GameRepository extends JpaRepository<Game, Long> {
    
}
