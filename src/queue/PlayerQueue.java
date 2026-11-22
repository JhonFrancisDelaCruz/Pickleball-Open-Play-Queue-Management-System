package queue;

import model.Player;
import java.util.*;
public class PlayerQueue {
    private final List<Player> line = new ArrayList<>();
    
    public void enqueue(Player player) {line.add(player);}

    public Player dequeue() {
        if (line.isEmpty()) {return null;}
        return line.remove(0);
    }

    public Player peek() {
        if (line.isEmpty()) { return null;}
        return line.get(0);
    }

    public boolean isEmpty() { return line.isEmpty();}

    public int size() { return line.size();}

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