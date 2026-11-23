package queue;

import model.Player;
import java.util.ArrayList;
import java.util.List;

public class PlayerQueue {

    private static class Node {
        Player player;
        Node next;

        Node(Player player) {
            this.player = player;
        }
    }

    private Node front;
    private Node rear;
    private int size;

    // True if nobody is waiting.
    public boolean isEmpty() {return front == null;}
    // Returns how many players are waiting.
    public int size() {return size;}

    // Adds a player to the back of the line.
    public void enqueue(Player player) {
        Node newBox = new Node(player);
        if (isEmpty()) {
            front = newBox;
        } else {
            rear.next = newBox;
        }
        rear = newBox;
        size++;
    }

    // Removes and returns the player at the front of the line.
    public Player dequeue() {
        if (isEmpty()) {
            return null;
        }
        Player firstPlayer = front.player;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return firstPlayer;
    }

    // Looks at the front player without removing.
    public Player peek() {
        return isEmpty() ? null : front.player;
    }

    // Removes a player from anywhere in the line by ID.
    public Player remove(String playerId) {
        if (isEmpty()) {
            return null;
        }
        if (front.player.getId().equalsIgnoreCase(playerId)) {
            return dequeue();
        }
        Node current = front;
        while (current.next != null) {
            Node boxBehind = current.next;
            if (boxBehind.player.getId().equalsIgnoreCase(playerId)) {
                current.next = boxBehind.next;
                if (boxBehind == rear) {
                    rear = current;
                }
                size--;
                return boxBehind.player;
            }
            current = current.next;
        }
        return null;
    }

    // Copies the line into a list (front to back) for reading.
    public List<Player> toList() {
        List<Player> list = new ArrayList<>();
        for (Node current = front; current != null; current = current.next) {
            list.add(current.player);
        }
        return list;
    }

    // Builds the text shown in the Waiting Players box.
    public String display() {
        StringBuilder text = new StringBuilder();
        for (Node current = front; current != null; current = current.next) {
            Player p = current.player;
            text.append("  ").append(p.getName()).append(" - ").append(p.getId());
            if (!p.isEligible()) {
                text.append("  (skips next game)");
            }
            text.append("\n");
        }
        return text.toString();
    }
}