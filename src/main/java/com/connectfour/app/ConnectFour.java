package com.connectfour.app;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ConnectFour extends JFrame {
    private static final int rows = 6;
    private static final int cols = 7;
    private boolean currentPlayerX = true;
    private boolean gameOver = false;
    JPanel buttonPanel;

    private final Map<String, Integer> lettersMap = new HashMap<>() {{
        put("A", 0);
        put("B", 1);
        put("C", 2);
        put("D", 3);
        put("E", 4);
        put("F", 5);
        put("G", 6);
    }};
    private final Map<Integer, String> numbersMap = new HashMap<>() {{
        put(0, "A");
        put(1, "B");
        put(2, "C");
        put(3, "D");
        put(4, "E");
        put(5, "F");
        put(6, "G");
    }};

    private final String[][] gameBoard = new String[cols][rows];

    public ConnectFour() {
        super("Connect Four");
        initializeGameBoard();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null);

        initLookAndFeel();
        initComponents();

        setVisible(true);
    }

    /**
     * Initialize the look and feel since JButton doesn't colorize on macOS
     */
    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G' };

        buttonPanel = new JPanel(new GridLayout(rows, cols));

        for (int i = rows; i >= 1; i--) {
            for (char c : letters) {
                JButton acceptButton = new JButton(" ");
                String coordinate = "Button" + c + i;
                acceptButton.setName(coordinate);
                acceptButton.setBackground(Color.lightGray);
                acceptButton.setMargin(new Insets(0, 0, 0, 0));
                acceptButton.addActionListener(e -> {
                    // Check if the column is filled, if it is, don't execute playerMoveUpdateGrid
                    if (!gameOver) playerMoveUpdateGrid(buttonPanel, coordinate);
                });
                buttonPanel.add(acceptButton, BorderLayout.SOUTH);
            }
        }

        add(buttonPanel);

        JPanel resetButtonPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        resetButton.setMargin(new Insets(0, 0, 0, 0));
        resetButtonPanel.add(resetButton);
        resetButton.setName("ButtonReset");
        resetButton.addActionListener(e -> resetGame());
        add(resetButtonPanel, BorderLayout.SOUTH);
    }

    private void resetGame() {
        // Clear game board
        initializeGameBoard();

        // Clear button texts
        Component[] components = buttonPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton button) {
                button.setText("");
                button.setBackground(Color.lightGray);
            }
        }

        currentPlayerX = true;
        gameOver = false;
    }


    private void playerMoveUpdateGrid(JPanel buttonPanel, String coordinate) {
        String letter = coordinate.substring(6, 7);
        String currentPlayer = getCurrentPlayer();
        int col = lettersMap.get(letter);

        String name = "";

        for (int i = 0; i <= gameBoard[col].length - 1; i++) {
            if (gameBoard[col][i].isEmpty()) {
                gameBoard[col][i] = currentPlayer;
                name = String.format("%s%d", letter, i + 1);
                break;
            }
        }

        Component[] components = buttonPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton button) {
                if (name.equals(button.getName().substring(6))) {
                    button.setText(currentPlayer);
                    updateCurrentPlayer();
                    break;
                }
            }
        }

        // Check for a winner
        if (!name.isEmpty()) {
            Queue<String> winningButtons = checkForWinner(col, Integer.parseInt(name.substring(1)) - 1, currentPlayer);
            if (!winningButtons.contains(-1)) {
                for (String s : winningButtons) {
                    var newString = "Button" + s;
                    for (Component component : components) {
                        if (component instanceof JButton button) {
                            if (button.getName().equals(newString)) {
                                button.setBackground(Color.CYAN);
                                break;
                            }
                        }
                    }
                }
                gameOver = true;
            }
        }
    }

    private <T> Queue<T> checkForWinner(int col, int row, String player) {
        Queue<String> winningButtons = new LinkedList<>();

        // Check vertical win
        int count = 0;
        for (int i = 0; i < gameBoard[col].length; i++) {
            if (!gameBoard[col][i].isEmpty() && gameBoard[col][i].equals(player)) {
                winningButtons.add(mapCoordinateToButtonName(col, i));
                if (winningButtons.size() > 4) winningButtons.poll();
                count++;
                if (count == 4) {
                    return (Queue<T>) winningButtons;
                }
            } else {
                count = 0;
            }
        }

        // Check horizontal win
        count = 0;
        for (int i = 0; i < gameBoard.length; i++) {
            if (!gameBoard[i][row].isEmpty() && gameBoard[i][row].equals(player)) {
                winningButtons.add(mapCoordinateToButtonName(i, row));
                if (winningButtons.size() > 4) winningButtons.poll();
                count++;
                if (count == 4) {
                    return (Queue<T>) winningButtons;
                }
            } else {
                count = 0;
            }
        }

        // Check diagonal win (top-left to bottom-right)
        count = 0;
        int offset = Math.min(col, row);
        int startCol = col - offset;
        int startRow = row - offset;
        for (int i = startCol, j = startRow; i < gameBoard.length && j < gameBoard[i].length; i++, j++) {
            if (!gameBoard[i][j].isEmpty() && gameBoard[i][j].equals(player)) {
                winningButtons.add(mapCoordinateToButtonName(i, j));
                if (winningButtons.size() > 4) winningButtons.poll();
                count++;
                if (count == 4) {
                    return (Queue<T>) winningButtons;
                }
            } else {
                count = 0;
            }
        }

        // Check diagonal win (bottom-left to top-right)
        count = 0;
        offset = Math.min(col, gameBoard[col].length - row - 1);
        startCol = col - offset;
        startRow = row + offset;
        for (int i = startCol, j = startRow; i < gameBoard.length && j >= 0; i++, j--) {
            if (!gameBoard[i][j].isEmpty() && gameBoard[i][j].equals(player)) {
                winningButtons.add(mapCoordinateToButtonName(i, j));
                if (winningButtons.size() > 4) winningButtons.poll();
                count++;
                if (count == 4) {
                    return (Queue<T>) winningButtons;
                }
            } else {
                count = 0;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        queue.add(-1);
        return (Queue<T>) queue;
    }

    private void updateCurrentPlayer() {
        currentPlayerX = !currentPlayerX;
    }

    private String getCurrentPlayer() {
        return currentPlayerX ? "X" : "O";
    }

    private String mapCoordinateToButtonName(int i, int j) {
        return numbersMap.get(i) + (j + 1);
    }

    private void initializeGameBoard() {
        for (String[] strings : gameBoard) {
            Arrays.fill(strings, "");
        }
    }
}
