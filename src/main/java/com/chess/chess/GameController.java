package com.chess.chess;

import com.chess.chess.dto.ChatMessage;
import com.chess.chess.dto.MoveRequest;
import com.chess.chess.entity.Game;
import com.chess.chess.entity.Move;
import com.chess.chess.model.Board;
import com.chess.chess.model.Piece;
import com.chess.chess.repository.GameRepository;
import com.chess.chess.repository.MoveRepository;
import com.chess.chess.repository.UserRepository;
import com.chess.chess.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class GameController {

    private final Map<String, Board> boards =
            new ConcurrentHashMap<>();

            //spring sam tworzy obiekty i wstrzykuje je do kontrolera
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MoveRepository moveRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired 
    private UserRepository userRepository;

    @PostMapping("/login")
        public ResponseEntity<?> login(
        @RequestParam String username,
        @RequestParam String password
        ) {

        User user =
                userRepository.findByUsername(username);

        if (user == null) {
                return ResponseEntity
                        .badRequest()
                        .body("User not found");
        }

        if (!user.getPassword().equals(password)) {
                return ResponseEntity
                        .badRequest()
                        .body("Wrong password");
        }

        return ResponseEntity.ok(user);
        }
        
        //rejestracja
        @PostMapping("/register")
        public ResponseEntity<?> register(
        @RequestParam String username,
        @RequestParam String password
        ) {

        User existing =
                userRepository.findByUsername(username);

        if (existing != null) {
                return ResponseEntity
                        .badRequest()
                        .body("User already exists");
        }

        User user = new User();

        user.setUsername(username);
        user.setPassword(password);

        userRepository.save(user);

        return ResponseEntity.ok(user);
        }

   //tworzenie nowej gry
    @PostMapping("/createGame")
    public String createGame(
            @RequestParam Long userId
    ) {

        Game game = new Game();

        game.setPlayerWhiteId(userId);

        game.setStarted(false);

        game.setFinished(false);

        game.setTurn(0);

        gameRepository.save(game);

        String gameId =
                game.getId().toString();

        boards.put(gameId, new Board());

        messagingTemplate.convertAndSend(
                "/topic/board/" + gameId,
                buildBoard(gameId)
        );

        return gameId;
    }

   //lista gier/pokoi do ktorych mozna dolaczyc
    @GetMapping("/games")
    public List<Game> getGames() {

        return gameRepository.findAll();
    }

    
    @PostMapping("/joinGame")
    public void joinGame(
            @RequestParam String gameId,
            @RequestParam Long userId
    ) {

        Game game = gameRepository
                .findById(
                        Long.parseLong(gameId)
                )
                .orElse(null);

        if (game == null) return;

        game.setPlayerBlackId(userId);

        game.setStarted(true);

        gameRepository.save(game);

        boards.putIfAbsent(
                gameId,
                new Board()
        );

        messagingTemplate.convertAndSend(
                "/topic/board/" + gameId,
                buildBoard(gameId)
        );
    }

    
    @GetMapping("/board/{gameId}")
    public Map<String, Object> getBoard(
            @PathVariable String gameId
    ) {

        return buildBoard(gameId);
    }

   
    @GetMapping("/moves/{gameId}")
    public List<Move> getMoves(
            @PathVariable String gameId
    ) {

        return moveRepository.findByGameIdOrderByIdAsc(gameId);
    }

    //todo; dodanie do oblsugi ruchow websocketow, 
    // tak aby po wykonaniu ruchu wszyscy gracze widzieli aktualna plansze

    //sprawdzenie czy dziala bezzarzutow
    @MessageMapping("/move")
    public void moveWS(
            @Payload MoveRequest request
    ) {

        if (request == null) return;

        String gameId =
                request.getGameId();

        Long userId =
                request.getUserId();

        if (gameId == null ||
            userId == null) {
            return;
        }

        Game game = gameRepository
                .findById(
                        Long.parseLong(gameId)
                )
                .orElse(null);

        Board board =
                boards.get(gameId);

        if (game == null ||
            board == null) {
            return;
        }

        if (game.isFinished()) {
            return;
        }

        int currentTurn =
                game.getTurn();

        boolean isWhitePlayer =
                userId.equals(
                        game.getPlayerWhiteId()
                );

        
        if ((currentTurn == 0 &&
             !isWhitePlayer) ||

            (currentTurn == 1 &&
             isWhitePlayer)) {

            return;
        }

        Piece piece =
                board.getPiece(
                        request.getSx(),
                        request.getSy()
                );

        if (piece == null) return;

       
        if ((currentTurn == 0 &&
             piece.getColor() != 0) ||

            (currentTurn == 1 &&
             piece.getColor() != 1)) {

            return;
        }

        boolean result =
                board.movePiece(
                        request.getSx(),
                        request.getSy(),
                        request.getEx(),
                        request.getEy()
                );

        if (!result) return;

        
        Move move = new Move();

        move.setGameId(gameId);

        move.setPlayerId(userId);

        //hibernate wykonuje select count(*)
        //jezeli ruch jest 12 to nowy ruch dostanie numer 13
        move.setMoveNumber(
            (int) moveRepository.countByGameId(gameId) + 1
        );

        move.setSx(request.getSx());
        move.setSy(request.getSy());

        move.setEx(request.getEx());
        move.setEy(request.getEy());

        move.setPiece(
                String.valueOf(
                        piece.getSymbol()
                )
        );

        moveRepository.save(move);

        
        game.setTurn(
                1 - currentTurn
        );

        gameRepository.save(game);

       
        messagingTemplate.convertAndSend(
                "/topic/board/" + gameId,
                buildBoard(gameId)
        );
    }

    //done

   
    @MessageMapping("/chat")
    public void chat(
            @Payload ChatMessage message
    ) {

        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getGameId(),
                message
        );
    }

    
    @PostMapping("/resign")
    public void resign(
            @RequestParam String gameId,
            @RequestParam Long userId
    ) {

        Game game = gameRepository
                .findById(
                        Long.parseLong(gameId)
                )
                .orElse(null);

        if (game == null) return;

        boolean white =
                userId.equals(
                        game.getPlayerWhiteId()
                );

        game.setFinished(true);

        game.setWinner(
                white
                        ? "BLACK"
                        : "WHITE"
        );

        gameRepository.save(game);

        messagingTemplate.convertAndSend(
                "/topic/gameover/" + gameId,
                game
        );
    }

    
    @PostMapping("/offerDraw")
    public void offerDraw(
            @RequestParam String gameId
    ) {

        messagingTemplate.convertAndSend(
                "/topic/draw/" + gameId,
                "DRAW_OFFER"
        );
    }

    
    @PostMapping("/acceptDraw")
    public void acceptDraw(
            @RequestParam String gameId
    ) {

        Game game = gameRepository
                .findById(
                        Long.parseLong(gameId)
                )
                .orElse(null);

        if (game == null) return;

        game.setFinished(true);

        game.setWinner("DRAW");

        gameRepository.save(game);

        messagingTemplate.convertAndSend(
                "/topic/gameover/" + gameId,
                game
        );
    }

  
    private Map<String, Object> buildBoard(
            String gameId
    ) {

        Board board =
                boards.get(gameId);

        if (board == null)
            return Map.of();

        char[][] result =
                new char[8][8];

        for (int i = 0; i < 8; i++) {

            for (int j = 0; j < 8; j++) {

                Piece piece =
                        board.getPiece(i, j);

                result[i][j] =
                        (piece == null)
                                ? '.'
                                : piece.getSymbol();
            }
        }

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "board",
                result
        );

        response.put(
                "whiteInCheck",
                board.isKingInCheck(0)
        );

        response.put(
                "blackInCheck",
                board.isKingInCheck(1)
        );

        return response;
    }


}