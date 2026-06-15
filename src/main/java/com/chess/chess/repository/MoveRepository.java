package com.chess.chess.repository;

import com.chess.chess.entity.Move;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoveRepository extends JpaRepository<Move, Long> {

    
    List<Move> findByGameIdOrderByIdAsc(String gameId);

    
    long countByGameId(String gameId);
}