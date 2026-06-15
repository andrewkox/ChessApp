window.global = window;

import React, { useEffect, useState } from "react";
import "./App.css";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

import wp from "./assets/pieces-svg/pawn-w.svg";
import bp from "./assets/pieces-svg/pawn-b.svg";
import wr from "./assets/pieces-svg/rook4-w.svg";
import br from "./assets/pieces-svg/rook-b.svg";
import wn from "./assets/pieces-svg/knight-w.svg";
import bn from "./assets/pieces-svg/knight-b.svg";
import wb from "./assets/pieces-svg/bishop-w.svg";
import bb from "./assets/pieces-svg/bishop-b.svg";
import wq from "./assets/pieces-svg/rqueen-w.svg";
import bq from "./assets/pieces-svg/rqueen-b.svg";
import wk from "./assets/pieces-svg/king-w.svg";
import bk from "./assets/pieces-svg/king-b.svg";

const pieceMap = {
  P: wp, R: wr, N: wn, B: wb, Q: wq, K: wk,
  p: bp, r: br, n: bn, b: bb, q: bq, k: bk
};

function App() {
  const [board, setBoard] = useState([]);
  const [selected, setSelected] = useState(null);
  const [dragged, setDragged] = useState(null);
  const [userId, setUserId] = useState(null);
  const [gameId, setGameId] = useState(null);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [games, setGames] = useState([]);
  const [game, setGame] = useState(null);
  const [client, setClient] = useState(null);
  const [whiteInCheck, setWhiteInCheck] = useState(false);
  const [blackInCheck, setBlackInCheck] = useState(false);
  const [messages, setMessages] = useState([]);
  const [chatInput, setChatInput] = useState("");
  const [moves, setMoves] = useState([]);

  
  useEffect(() => {
    const loadGames = () => {
      fetch("http://localhost:8081/games")
        .then(res => res.json())
        .then(setGames)
        .catch(console.error);
    };
    loadGames();
    const interval = setInterval(loadGames, 2000);
    return () => clearInterval(interval);
  }, []);


  useEffect(() => {
    const socket = new SockJS("http://localhost:8081/ws");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => console.log("WS CONNECTED"),
    });
    stompClient.activate();
    setClient(stompClient);
    return () => stompClient.deactivate();
  }, []);

  const loadMoves = (id) => {
    fetch(`http://localhost:8081/moves/${id}`)
      .then(r => r.json())
      .then(setMoves)
      .catch(console.error);
  };

  const subscribeToGame = (stomp, id) => {
    if (!stomp) return;

    // Board + auto refresh moves
    stomp.subscribe(`/topic/board/${id}`, (msg) => {
      const data = JSON.parse(msg.body);
      setBoard(data.board.map(row => typeof row === "string" ? row.split("") : row));
      setWhiteInCheck(data.whiteInCheck);
      setBlackInCheck(data.blackInCheck);
      
      
      loadMoves(id);
    });

    
    stomp.subscribe(`/topic/chat/${id}`, (msg) => {
      const data = JSON.parse(msg.body);
      setMessages(prev => [...prev, data]);
    });

    // Game Over
    stomp.subscribe(`/topic/gameover/${id}`, (msg) => {
      const data = JSON.parse(msg.body);
      alert(`GAME OVER - Winner: ${data.winner}`);
    });
  };

  const handleLogin = async () => {
    const res = await fetch(`http://localhost:8081/login?username=${username}&password=${password}`, { method: "POST" });
    if (!res.ok) { alert(await res.text()); return; }
    const user = await res.json();
    setUserId(user.id);
  };

  const handleRegister = async () => {
    const res = await fetch(`http://localhost:8081/register?username=${username}&password=${password}`, { method: "POST" });
    if (!res.ok) { alert(await res.text()); return; }
    alert("Konto utworzone");
  };

  const createGame = async () => {
    if (!client || !client.connected) { alert("WebSocket not connected"); return; }
    const res = await fetch(`http://localhost:8081/createGame?userId=${userId}`, { method: "POST" });
    const id = await res.text();
    subscribeToGame(client, id);
    setGameId(id);

    const boardRes = await fetch(`http://localhost:8081/board/${id}`);
    const boardData = await boardRes.json();
    setBoard(boardData.board.map(row => typeof row === "string" ? row.split("") : row));
    setWhiteInCheck(boardData.whiteInCheck);
    setBlackInCheck(boardData.blackInCheck);

    loadMoves(id);

    const g = await fetch("http://localhost:8081/games").then(r => r.json());
    setGame(g.find(x => x.id == id));
  };

  const joinGame = async (id) => {
    if (!client || !client.connected) { alert("WebSocket not connected"); return; }

    subscribeToGame(client, id);
    await fetch(`http://localhost:8081/joinGame?gameId=${id}&userId=${userId}`, { method: "POST" });
    setGameId(id);

    const gamesRes = await fetch("http://localhost:8081/games");
    const allGames = await gamesRes.json();
    const currentGame = allGames.find(g => g.id == id);
    setGame(currentGame);

    const boardRes = await fetch(`http://localhost:8081/board/${id}`);
    const boardData = await boardRes.json();
    setBoard(boardData.board.map(row => typeof row === "string" ? row.split("") : row));
    setWhiteInCheck(boardData.whiteInCheck);
    setBlackInCheck(boardData.blackInCheck);

    loadMoves(id);
  };

  const handleMove = (sx, sy, ex, ey) => {
    if (!client) return;
    client.publish({
      destination: "/app/move",
      body: JSON.stringify({ gameId, userId, sx, sy, ex, ey })
    });
  };

  const sendChat = () => {
    if (!chatInput.trim()) return;
    client.publish({
      destination: "/app/chat",
      body: JSON.stringify({ gameId, username, message: chatInput })
    });
    setChatInput("");
  };

  const resign = async () => {
    await fetch(`http://localhost:8081/resign?gameId=${gameId}&userId=${userId}`, { method: "POST" });
  };

  const isWhite = game && userId === game.playerWhiteId;
  const isBlack = game && userId === game.playerBlackId;

  const displayBoard = isWhite
    ? [...board].map(row => [...row])
    : [...board].slice().reverse().map(row => [...row].reverse());

  return (
    <div className="App" style={{ display: "flex", justifyContent: "center", gap: 40, padding: 20, color: "white" }}>
      <div>
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", marginBottom: 30 }}>
          <div style={{ 
            width: "320px", 
            height: "100px", 
            background: "#333", 
            display: "flex", 
            alignItems: "center", 
            justifyContent: "center",
            fontSize: "28px",
            color: "#666",
            marginBottom: 10
          }}>
            ♟️ CHESS
          </div>

          <p style={{ color: "#999", marginTop: 10, fontSize: 18 }}>Gramy w szachy</p>

          {!userId && (
            <div>
              <input placeholder="username" value={username} onChange={e => setUsername(e.target.value)} />
              <input type="password" placeholder="password" value={password} onChange={e => setPassword(e.target.value)} />
              <div style={{ marginTop: 10 }}>
                <button onClick={handleLogin}>LOGIN</button>
                <button onClick={handleRegister} style={{ marginLeft: 10 }}>REGISTER</button>
              </div>
            </div>
          )}

          {userId && !gameId && (
            <div>
              <button onClick={createGame}>CREATE GAME</button>
              <h3>Join:</h3>
              {games.map(g => (
                <button key={g.id} onClick={() => joinGame(g.id)} style={{ margin: "5px" }}>
                  Game {g.id}
                </button>
              ))}
            </div>
          )}

          {game && (
            <div style={{ marginBottom: 20, textAlign: "center" }}>
              {isWhite && "Grasz białymi "} 
              {isBlack && "Grasz czarnymi "} 
              {!isWhite && !isBlack && "Obserwujesz"}
              <br />
              Tura: {game.turn === 0 ? "Białe" : "Czarne"}
              <br />
              <button onClick={resign} style={{ marginTop: 10 }}>RESIGN</button>
            </div>
          )}

          {gameId && board.length > 0 && (
            <div className="chessboard">
              {displayBoard.map((row, r) => 
                row.map((cell, c) => {
                  const realRow = isWhite ? r : 7 - r;
                  const realCol = isWhite ? c : 7 - c;
                  const isCheck = (cell === "K" && whiteInCheck) || (cell === "k" && blackInCheck);

                  return (
                    <div
                      key={`${r}-${c}`}
                      className={`square ${(r + c) % 2 ? "dark" : "light"} ${isCheck ? "check" : ""}`}
                      onClick={() => {
                        if (!selected && cell === ".") return;
                        if (!selected) {
                          setSelected({ x: realRow, y: realCol });
                          return;
                        }
                        if (selected.x === realRow && selected.y === realCol) {
                          setSelected(null);
                          return;
                        }
                        handleMove(selected.x, selected.y, realRow, realCol);
                        setSelected(null);
                      }}
                      onDragOver={e => e.preventDefault()}
                      onDrop={() => {
                        if (!dragged) return;
                        handleMove(dragged.x, dragged.y, realRow, realCol);
                        setDragged(null);
                      }}
                    >
                      {cell !== "." && (
                        <img
                          src={pieceMap[cell]}
                          alt=""
                          draggable
                          onDragStart={() => setDragged({ x: realRow, y: realCol })}
                        />
                      )}
                    </div>
                  );
                })
              )}
            </div>
          )}
        </div>
      </div>

      {gameId && (
        <div style={{ width: 350, display: "flex", flexDirection: "column", gap: 20 }}>
          <div style={{ background: "#1e1e1e", padding: 15, borderRadius: 10, height: 300, overflowY: "auto" }}>
            <h3>Moves</h3>
            {moves.map((m, i) => (
              <div key={i}>
                {m.moveNumber}. {m.piece} ({m.sx},{m.sy}) → ({m.ex},{m.ey})
              </div>
            ))}
          </div>

          <div style={{ background: "#1e1e1e", padding: 15, borderRadius: 10, display: "flex", flexDirection: "column", height: 400 }}>
            <h3>Chat</h3>
            <div style={{ flex: 1, overflowY: "auto", marginBottom: 10 }}>
              {messages.map((m, i) => (
                <div key={i} style={{ marginBottom: 5 }}>
                  <b>{m.username}:</b> {m.message}
                </div>
              ))}
            </div>
            <div style={{ display: "flex", gap: 10 }}>
              <input
                value={chatInput}
                onChange={e => setChatInput(e.target.value)}
                placeholder="message..."
                style={{ flex: 1 }}
                onKeyPress={e => e.key === "Enter" && sendChat()}
              />
              <button onClick={sendChat}>SEND</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;