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

    public boolean isEmpty() {return front == null;}
    public int size() {return size;}

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

    public Player peek() {
        return isEmpty() ? null : front.player;
    }

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

    public List<Player> toList() {
        List<Player> list = new ArrayList<>();
        for (Node current = front; current != null; current = current.next) {
            list.add(current.player);
        }
        return list;
    }

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