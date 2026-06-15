# Chess Online

Internetowa aplikacja szachowa umożliwiająca rozgrywanie partii pomiędzy dwoma użytkownikami w czasie rzeczywistym.

## Technologie

### Backend

* Java 21
* Spring Boot
* Spring Data JPA
* Hibernate ORM
* PostgreSQL
* WebSocket (STOMP)
* Maven

### Frontend

* React
* Vite
* SockJS
* STOMP.js

---

## Funkcjonalności

* Rejestracja użytkowników
* Logowanie użytkowników
* Tworzenie nowych gier
* Dołączanie do istniejących gier
* Rozgrywka szachowa online
* Synchronizacja planszy w czasie rzeczywistym
* Historia wykonanych ruchów
* Czat między graczami
* Poddanie partii
* Propozycja i akceptacja remisu

---

## Architektura projektu

Aplikacja została wykonana w architekturze klient-serwer.

Frontend (React) komunikuje się z backendem (Spring Boot) poprzez:

* REST API
* WebSocket

Dane przechowywane są w bazie PostgreSQL.

Hibernate ORM odpowiada za mapowanie encji Java na tabele bazy danych.

---

## Struktura bazy danych

### User

Przechowuje informacje o użytkownikach.

| Pole     | Typ    |
| -------- | ------ |
| id       | Long   |
| username | String |
| password | String |

### Game

Przechowuje informacje o rozgrywkach.

| Pole          | Typ     |
| ------------- | ------- |
| id            | Long    |
| playerWhiteId | Long    |
| playerBlackId | Long    |
| started       | boolean |
| finished      | boolean |
| winner        | String  |
| turn          | int     |
| drawOffered   | boolean |

### Move

Przechowuje historię ruchów.

| Pole       | Typ    |
| ---------- | ------ |
| id         | Long   |
| gameId     | String |
| playerId   | Long   |
| moveNumber | int    |
| piece      | String |
| sx         | int    |
| sy         | int    |
| ex         | int    |
| ey         | int    |

---

## Uruchomienie backendu

1. Utworzyć bazę PostgreSQL:

```sql
CREATE DATABASE chessdb;
```

2. Skonfigurować dane połączenia w pliku:

```properties
src/main/resources/application.properties
```

Przykład:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/chessdb
spring.datasource.username=postgres
spring.datasource.password=haslo
```

3. Uruchomić aplikację:

```bash
./mvnw spring-boot:run
```

Backend zostanie uruchomiony na:

```text
http://localhost:8081
```

---

## Uruchomienie frontendu

Przejść do katalogu frontend:

```bash
cd chessUI/chessfrontend
```

Instalacja zależności:

```bash
npm install
```

Uruchomienie:

```bash
npm run dev
```

Frontend zostanie uruchomiony na:

```text
http://localhost:5173
```

---

## Komunikacja czasu rzeczywistego

Projekt wykorzystuje Spring WebSocket.

Endpoint WebSocket:

```text
/ws
```

Kanały:

```text
/ topic/board/{gameId}
/ topic/chat/{gameId}
/ topic/gameover/{gameId}
/ topic/draw/{gameId}
```

Dzięki temu wszyscy gracze otrzymują aktualizacje planszy natychmiast po wykonaniu ruchu.

---

## Autor

Projekt wykonany w ramach zajęć z programowania aplikacji internetowych przy użyciu Spring Boot, Hibernate ORM oraz PostgreSQL.## Autor

Andrzej Antosz
Państwowa Wyższa Szkoła Techniczno-Ekonomiczna im. ks. Bronisława Markiewicza w Jarosławiu
Numer albumu: s41145

Projekt zaliczeniowy wykonany w ramach przedmiotu dotyczącego tworzenia aplikacji internetowych z wykorzystaniem technologii Spring Boot, Hibernate ORM oraz relacyjnych baz danych.

