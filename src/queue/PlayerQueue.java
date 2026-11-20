package queue;

import model.Player;
import java.util.ArrayList;
import java.util.List;

public class PlayerQueue {

    private static class Node {
        Player data;
        Node next;

        Node(Player data) {
            this.data = data;
        }
    }

    private Node front;
    private Node rear;
    private int size;

    /** Adds a player to the rear of the queue. */
    public void enqueue(Player player) {
        Node node = new Node(player);
        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
        size++;
    }

    /** Removes and returns the player at the front (null if empty). */
    public Player dequeue() {
        if (isEmpty()) {
            return null;
        }
        Player player = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return player;
    }

    /** Returns the player at the front without removing them (null if empty). */
    public Player peek() {
        return isEmpty() ? null : front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    /** Text view of the queue in FIFO order. */
    public String display() {
        if (isEmpty()) {
            return "(queue is empty)";
        }
        StringBuilder sb = new StringBuilder();
        for (Node cur = front; cur != null; cur = cur.next) {
            sb.append(cur.data.getId()).append(" - ").append(cur.data.getName()).append("\n");
        }
        return sb.toString();
    }

    /** Snapshot of the queue in FIFO order (does not modify the queue). */
    public List<Player> toList() {
        List<Player> list = new ArrayList<>();
        for (Node cur = front; cur != null; cur = cur.next) {
            list.add(cur.data);
        }
        return list;
    }

    /** Removes a specific player (by ID) while keeping everyone else's order. */
    public Player remove(String playerId) {
        Node prev = null;
        for (Node cur = front; cur != null; prev = cur, cur = cur.next) {
            if (cur.data.getId().equalsIgnoreCase(playerId)) {
                if (prev == null) {
                    front = cur.next;
                } else {
                    prev.next = cur.next;
                }
                if (cur == rear) {
                    rear = prev;
                }
                size--;
                return cur.data;
            }
        }
        return null;
    }

    public boolean contains(String playerId) {
        for (Node cur = front; cur != null; cur = cur.next) {
            if (cur.data.getId().equalsIgnoreCase(playerId)) {
                return true;
            }
        }
        return false;
    }
}
