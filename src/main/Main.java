package main;

import gui.LoginPage;
import javax.swing.SwingUtilities;

public class Main {
    // Program start: opens the login page.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {new LoginPage().setVisible(true); });
    }
}
