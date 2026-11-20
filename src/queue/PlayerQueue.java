package queue;

import model.Player;
import java.util.LinkedList;
import java.util.Queue;

public class PlayerQueue {

    private Queue<Player> players = new LinkedList<>();

    public void addPlayer(Player player) {
        players.offer(player);
    }

    public Player getNextPlayer() {
        return players.poll();
    }

    public Queue<Player> getPlayers() {
        return players;
    }
}