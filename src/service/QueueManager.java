package service;

import model.Player;
import queue.PlayerQueue;
import java.util.*;

public class QueueManager {
    public static final int PLAYERS_PER_GAME = 4;
    public enum RegisterResult { SUCCESS, DUPLICATE, QUEUE_FULL }
    private int maxWaitingPlayers = 15;

    private final PlayerQueue waitingQueue = new PlayerQueue(); 
    private final List<Player> players = new ArrayList<>();    

    public RegisterResult registerPlayer(Player player) {
        if (findPlayer(player.getId()) != null) { return RegisterResult.DUPLICATE;}
        if (isQueueFull()) { return RegisterResult.QUEUE_FULL;}
        
        players.add(player);
        waitingQueue.enqueue(player);
        return RegisterResult.SUCCESS;
    }

    public List<Player> peekNextEligible(int n) {
        List<Player> result = new ArrayList<>();
        for (Player p : waitingQueue.toList()) {
            if (result.size() == n) { break; }
            if (p.isEligible()) {result.add(p);}
        }
        return result;
    }

    public int countEligible() {return peekNextEligible(Integer.MAX_VALUE).size();}

    public List<Player> selectPlayersForGame() {
        if (countEligible() < PLAYERS_PER_GAME) { return null; }
        List<Player> chosen = new ArrayList<>();
        int waiting = waitingQueue.size();
        for (int i = 0; i < waiting; i++) {
            Player front = waitingQueue.peek();  
            waitingQueue.dequeue();              
            boolean needMore = chosen.size() < PLAYERS_PER_GAME;
            if (needMore && front.isEligible()) {
                chosen.add(front);              
            } else {
                if (needMore) {
                    front.consumeSkip();         
                }
                waitingQueue.enqueue(front);      
            }
        } return chosen;
    }

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

    public List<Player> rotatePlayers(List<Player> returning) {
        List<Player> noRoom = new ArrayList<>();
        for (Player p : returning) {
            p.recordCompletedGame();            
            if (isQueueFull()) {
                players.remove(p);
                noRoom.add(p);
            } else {
                waitingQueue.enqueue(p);       
            }
        }
        return noRoom;
    }

    public Player leaveQueue(String playerId) {
        Player removed = waitingQueue.remove(playerId);
        if (removed != null) {
            players.remove(removed);
        }
        return removed;
    }
    
    public int getAvailableSlotsAfterGame(List<Player> gamePlayers) {
    return Math.max(0, maxWaitingPlayers - waitingQueue.size());
    }

    public boolean isQueueFull() {return waitingQueue.size() >= maxWaitingPlayers;}

    public void setMaxWaitingPlayers(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Waiting limit cannot be negative.");
        }
        maxWaitingPlayers = limit;
    }

    public int getMaxWaitingPlayers() { return maxWaitingPlayers;}

    public PlayerQueue getPlayerQueue() { return waitingQueue;}

    private Player findPlayer(String playerId) {
        for (Player player : players) {
            if (player.getId().equalsIgnoreCase(playerId)) {
                return player;
            }
        }
        return null;
    }
}
