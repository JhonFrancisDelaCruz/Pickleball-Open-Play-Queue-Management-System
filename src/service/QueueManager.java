package service;

import model.Player;
import queue.PlayerQueue;
import java.util.ArrayList;
import java.util.List;

public class QueueManager {

    public static final int MAX_WAITING = 15;
    public static final int PLAYERS_PER_GAME = 4;

    public enum RegisterResult { SUCCESS, DUPLICATE, QUEUE_FULL }

    private final PlayerQueue playerQueue = new PlayerQueue();
    private final List<Player> players = new ArrayList<>(); // everyone in the system (waiting + playing)

    // ---------- Registration ----------

    public RegisterResult registerPlayer(Player player) {
        if (isPlayerRegistered(player.getId())) {
            return RegisterResult.DUPLICATE;
        }
        if (isQueueFull()) {
            return RegisterResult.QUEUE_FULL;
        }
        players.add(player);
        playerQueue.enqueue(player);
        return RegisterResult.SUCCESS;
    }

    public boolean isPlayerRegistered(String playerId) {
        return findPlayer(playerId) != null;
    }

    public Player findPlayer(String playerId) {
        for (Player player : players) {
            if (player.getId().equalsIgnoreCase(playerId)) {
                return player;
            }
        }
        return null;
    }

    public boolean isQueueFull() {
        return playerQueue.size() >= MAX_WAITING;
    }

    // ---------- Eligibility / court assignment ----------

    /** Next (up to) n eligible players in FIFO order, without removing them. */
    public List<Player> peekNextEligible(int n) {
        List<Player> result = new ArrayList<>();
        for (Player p : playerQueue.toList()) {
            if (result.size() == n) {
                break;
            }
            if (p.isEligible()) {
                result.add(p);
            }
        }
        return result;
    }

    public int countEligible() {
        return peekNextEligible(Integer.MAX_VALUE).size();
    }

    public List<Player> selectPlayersForGame() {
        List<Player> chosen = peekNextEligible(PLAYERS_PER_GAME);
        if (chosen.size() < PLAYERS_PER_GAME) {
            return null;
        }
        Player last = chosen.get(chosen.size() - 1);
        // everyone passed over before the last chosen player has now skipped a selection
        for (Player p : playerQueue.toList()) {
            if (p == last) {
                break;
            }
            if (!p.isEligible()) {
                p.consumeSkip();
            }
        }
        for (Player p : chosen) {
            playerQueue.remove(p.getId());
        }
        return chosen;
    }

    public Player leaveQueue(String playerId) {
        Player removed = playerQueue.remove(playerId);
        if (removed != null) {
            players.remove(removed);
        }
        return removed;
    }

    public List<Player> completeGame(List<Player> gamePlayers, List<Player> continuing) {
        List<Player> noRoom = new ArrayList<>();
        for (Player p : gamePlayers) {
            boolean stays = continuing.contains(p) && !p.isLeaveAfterGame();
            if (!stays) {
                players.remove(p);
                continue;
            }
            p.recordCompletedGame();
            if (isQueueFull()) {
                players.remove(p);
                noRoom.add(p);
            } else {
                playerQueue.enqueue(p);
            }
        }
        return noRoom;
    }

    public PlayerQueue getPlayerQueue() { return playerQueue; }
    public List<Player> getPlayers() { return players; }
}
