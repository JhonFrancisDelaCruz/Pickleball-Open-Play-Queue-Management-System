package model;

public class Player {
    private final String id;
    private final String name;
    private int consecutiveGames = 0;        // games completed back-to-back without sitting out a selection
    private boolean skipNextSelection = false; // true = must be skipped once before becoming eligible again
    private boolean leaveAfterGame = false;  // requested to leave while playing
    private int wins = 0;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getConsecutiveGames() {
        return consecutiveGames;
    }

    public boolean isEligible() {
        return !skipNextSelection;
    }

    public boolean isLeaveAfterGame() {
        return leaveAfterGame;
    }

    public void setLeaveAfterGame(boolean leaveAfterGame) {
        this.leaveAfterGame = leaveAfterGame;
    }

    public int getWins() {
        return wins;
    }

    public void addWin() {
        wins++;
    }

    /** Called when the player finishes a game and returns to the queue. */
    public void recordCompletedGame() {
        consecutiveGames++;
        if (consecutiveGames >= 2) {
            skipNextSelection = true; // must sit out the next game selection
        }
    }

    /** Called when the player is passed over during a game selection. */
    public void consumeSkip() {
        skipNextSelection = false;
        consecutiveGames = 0;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
