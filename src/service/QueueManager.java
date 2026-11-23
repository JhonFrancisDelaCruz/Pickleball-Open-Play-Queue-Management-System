package service;

import model.Player;
import queue.PlayerQueue;
import java.util.*;

public class QueueManager {
    public static final int PLAYERS_PER_GAME = 4;
    private int maxWaitingPlayers = 15;
    private int lastPlayerNumber = 0;   // counter used to build the auto IDs (P001, P002, ...)

    private final PlayerQueue waitingQueue = new PlayerQueue();
    private final List<Player> players = new ArrayList<>();

    // Builds the next unique player ID like P001, P002, P003...
    private String generatePlayerId() {
        lastPlayerNumber++;
        return String.format("P%03d", lastPlayerNumber);
    }

    // Registers a new player by name with an auto-made ID; returns null if the queue is full.
    public Player registerPlayer(String name) {
        if (isQueueFull()) { return null; }

        Player player = new Player(generatePlayerId(), name);
        players.add(player);
        waitingQueue.enqueue(player);
        return player;
    }

    // Returns the players who would play next WITHOUT changing the queue (used by "next 4" display).
    public List<Player> peekNextGamePlayers() {
        List<Player> queue = waitingQueue.toList();
        List<Player> eligible = new ArrayList<>();
        for (Player p : queue) {
            if (p.isEligible()) { eligible.add(p); }
        }

        Set<Player> chosen = new HashSet<>();
        if (eligible.size() >= PLAYERS_PER_GAME) {
            // Enough normal players: take the first 4 eligible, skip players keep waiting
            chosen.addAll(eligible.subList(0, PLAYERS_PER_GAME));
        } else if (queue.size() >= PLAYERS_PER_GAME) {
            // Not enough normal players: take all of them, then fill the rest with skip players in queue order
            chosen.addAll(eligible);
            for (Player p : queue) {
                if (chosen.size() == PLAYERS_PER_GAME) { break; }
                if (!p.isEligible()) { chosen.add(p); }
            }
        }

        List<Player> result = new ArrayList<>();
        for (Player p : queue) {          // keep the queue order so teams are fair
            if (chosen.contains(p)) { result.add(p); }
        }
        return result;
    }

    // Returns true when the next game must use skip players (fewer than 4 normal players waiting).
    public boolean needsSkipPlayers() {
        int eligible = 0;
        for (Player p : waitingQueue.toList()) {
            if (p.isEligible()) { eligible++; }
        }
        return eligible < PLAYERS_PER_GAME;
    }

    // Returns true when a game can start (4 players waiting, skip players count if needed).
    public boolean canStartGame() {
        return peekNextGamePlayers().size() == PLAYERS_PER_GAME;
    }

    // Takes the 4 players out of the queue for a game; returns null if there are not enough players.
    public List<Player> selectPlayersForGame() {
        List<Player> chosen = peekNextGamePlayers();
        if (chosen.size() < PLAYERS_PER_GAME) { return null; }

        // Position of the last chosen player: skip players before it were "passed over"
        List<Player> queue = waitingQueue.toList();
        int lastChosenIndex = 0;
        for (int i = 0; i < queue.size(); i++) {
            if (chosen.contains(queue.get(i))) { lastChosenIndex = i; }
        }

        int waiting = waitingQueue.size();
        for (int i = 0; i < waiting; i++) {
            Player front = waitingQueue.dequeue();
            if (chosen.contains(front)) {
                if (!front.isEligible()) {
                    front.consumeSkip();      // a skip player used to fill the game: skip is used up
                }
            } else {
                if (!front.isEligible() && i < lastChosenIndex) {
                    front.consumeSkip();      // skip player was passed over: skip is used up, plays next time
                }
                waitingQueue.enqueue(front);  // not chosen: goes back, keeping the same order
            }
        }
        return chosen;
    }

    // Finishes a game: players who continue go back to the queue, the others leave the system.
    public List<Player> completeGame(List<Player> gamePlayers, List<Player> continuing) {
        List<Player> returning = new ArrayList<>();
        for (Player p : gamePlayers) {
            boolean stays = continuing.contains(p) && !p.isLeaveAfterGame();
            if (stays) {
                returning.add(p);
            } else {
                players.remove(p);
            }
        }
        return rotatePlayers(returning);
    }

    // Puts returning players at the end of the queue; returns the ones who could not fit (queue full).
    public List<Player> rotatePlayers(List<Player> returning) {
        List<Player> noRoom = new ArrayList<>();
        for (Player p : returning) {
            p.recordCompletedGame();          // counts the game; 2 in a row = skip next selection
            if (isQueueFull()) {
                players.remove(p);
                noRoom.add(p);
            } else {
                waitingQueue.enqueue(p);
            }
        }
        return noRoom;
    }

    // Removes a waiting player from the queue by ID; returns null if not found.
    public Player leaveQueue(String playerId) {
        Player removed = waitingQueue.remove(playerId);
        if (removed != null) {
            players.remove(removed);
        }
        return removed;
    }

    // Returns how many free waiting slots are left after a game ends.
    public int getAvailableSlotsAfterGame(List<Player> gamePlayers) {
        return Math.max(0, maxWaitingPlayers - waitingQueue.size());
    }

    // Returns true when the waiting queue reached its maximum size.
    public boolean isQueueFull() {return waitingQueue.size() >= maxWaitingPlayers;}

    // Sets the maximum number of waiting players (changes when courts are added/deleted).
    public void setMaxWaitingPlayers(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Waiting limit cannot be negative.");
        }
        maxWaitingPlayers = limit;
    }

    // Returns the maximum number of waiting players.
    public int getMaxWaitingPlayers() { return maxWaitingPlayers;}

    // Returns the waiting queue so the screen can display it.
    public PlayerQueue getPlayerQueue() { return waitingQueue;}
}
