package service;

import model.Player;
import queue.PlayerQueue;

public class QueueManager {

    private PlayerQueue playerQueue = new PlayerQueue();

    public void registerPlayer(Player player) {
        playerQueue.addPlayer(player);
    }

    public PlayerQueue getPlayerQueue() {
        return playerQueue;
    }
}