package queue;

import model.Player;
import java.util.*;
public class PlayerQueue {

    // The line itself. Just a normal list of players.
    private final List<Player> line = new ArrayList<>();

    // Join the line: put the player at the BACK.
    public void enqueue(Player player) {
        line.add(player);
    }

    // Leave from the FRONT: remove and return the first player.
    // Returns null if nobody is waiting.
    public Player dequeue() {
        if (line.isEmpty()) {
            return null;
        }
        return line.remove(0);
    }

    /* Look at the FRONT player without removing them.
     Returns null if nobody is waiting.*/
    public Player peek() {
        if (line.isEmpty()) {
            return null;
        }
        return line.get(0);
    }

    // True when nobody is waiting.
    public boolean isEmpty() {
        return line.isEmpty();
    }

    // How many players are waiting.
    public int size() {
        return line.size();
    }

    // Remove a specific player by ID (used when someone presses Leave).
    // Returns the removed player, or null if the ID was not found.
    public Player remove(String playerId) {
        for (int i = 0; i < line.size(); i++) {
            Player p = line.get(i);
            if (p.getId().equalsIgnoreCase(playerId)) {
                line.remove(i);
                return p;
            }
        }
        return null;
    }

    // A copy of the line, in order, so other code can look through it
    // without changing the real queue.
    public List<Player> toList() {
        return new ArrayList<>(line);
    }

    public String display() {
        StringBuilder sb = new StringBuilder();
        for (Player p : line) {
            sb.append("  ").append(p.getName()).append(" - ").append(p.getId());
            if (!p.isEligible()) {
                sb.append("  (skips next game)");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}