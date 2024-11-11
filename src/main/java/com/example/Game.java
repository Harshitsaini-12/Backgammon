package com.example;

import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Game {
    private static final int BOARD_SIZE = 24;
    private static String player1Name;
    private static String player2Name;
    private static int[] board;
    private static Random random;
    private static Scanner scanner;
    private static boolean isPlayer1Turn;
    private static int[] diceRoll;
    private static int player1Bar = 0;
    private static int player2Bar = 0;
    private static int player1BearOff = 0;
    private static int player2BearOff = 0;

    public static void initializeGame() {
        board = new int[BOARD_SIZE];
        random = new Random();
        scanner = new Scanner(System.in);

        setupBoard();
        player1Name = getPlayerName("Player 1");
        player2Name = getPlayerName("Player 2");
        determineFirstPlayer();

        System.out.println("\nFantastic! Let's get started, " + player1Name + " and " + player2Name + "!");
    }

    private static void determineFirstPlayer() {
        int player1Roll = rollDie();
        int player2Roll = rollDie();
        System.out.println(player1Name + " rolled a " + player1Roll);
        System.out.println(player2Name + " rolled a " + player2Roll);

        if (player1Roll > player2Roll) {
            isPlayer1Turn = true;
            System.out.println(player1Name + " goes first!");
        } else if (player2Roll > player1Roll) {
            isPlayer1Turn = false;
            System.out.println(player2Name + " goes first!");
        } else {
            System.out.println("It's a tie! Rolling again...");
            determineFirstPlayer();
            return;
        }
        diceRoll = new int[]{player1Roll, player2Roll};
    }

    private static String getPlayerName(String playerNumber) {
        String name = "";
        while (name.trim().isEmpty()) {
            System.out.print("Welcome to Backgammon! Please enter " + playerNumber + "'s name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Error: Name is mandatory. Please try again.");
            }
        }
        return name;
    }

    private static void setupBoard() {
        board[0] = 2;   // Player 1's checkers
        board[5] = -5;  // Player 2's checkers
        board[7] = -3;  // Player 2's checkers
        board[11] = 5;  // Player 1's checkers
        board[12] = -5; // Player 2's checkers
        board[16] = 3;  // Player 1's checkers
        board[18] = 5;  // Player 1's checkers
        board[23] = -2; // Player 2's checkers 1
    }

    public static void playGame() {
        while (true) {
            displayBoard();
            String currentPlayer = isPlayer1Turn ? player1Name : player2Name;
            System.out.println("\nCurrent player: " + currentPlayer);
            System.out.print(currentPlayer + ", enter a command (roll, pip, hint, quit): ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "roll":
                    rollDice();
                    List<String> legalMoves = calculateLegalMoves();
                    if (legalMoves.isEmpty()) {
                        System.out.println("No legal moves available. Turn passes.");
                    } else {
                        displayLegalMoves(legalMoves);
                        applyMove(getSelectedMove(legalMoves));
                    }
                    isPlayer1Turn = !isPlayer1Turn;
                    break;
                case "pip":
                    displayPipCount();
                    break;
                case "hint":
                    displayHint();
                    break;
                case "quit":
                    System.out.println("Thanks for playing!");
                    return;
                default:
                    System.out.println("Invalid command. Type 'hint' for a list of commands.");
            }

            if (isGameOver()) {
                announceWinner();
                return;
            }
        }
    }

    private static void displayBoard() {
        System.out.println("\n--- Current Backgammon Board ---");
        displayPipNumbers(true);
        System.out.println("----------------BAR----------------");
        printUpperRow();
        System.out.println("| BAR: " + player1Name + ": " + player1Bar + ", " + player2Name + ": " + player2Bar + " |");
        printLowerRow();
        System.out.println("----------------BAR----------------");
        displayPipNumbers(false);
        System.out.println("\nBear off - " + player1Name + ": " + player1BearOff + ", " + player2Name + ": " + player2BearOff);
    }

    private static void displayPipNumbers(boolean isTopRow) {
        StringBuilder pipNumbers = new StringBuilder();
        int start = isTopRow ? (isPlayer1Turn ? 13 : 12) : (isPlayer1Turn ? 12 : 13);
        int end = isTopRow ? (isPlayer1Turn ? 25 : 24) : (isPlayer1Turn ? 0 : 1);
        int step = isTopRow ? 1 : -1;

        for (int i = start; i != end; i += step) {
            pipNumbers.append(String.format("%2d ", i % 24));
        }

        System.out.println(pipNumbers.toString());
    }

    private static void printUpperRow() {
        for (int i = 12; i < 24; i++) {
            printPosition(board[i]);
        }
        System.out.println();
    }

    private static void printLowerRow() {
        for (int i = 11; i >= 0; i--) {
            printPosition(board[i]);
        }
        System.out.println();
    }

    private static void printPosition(int checkers) {
        if (checkers > 0) {
            System.out.printf("+%1d ", checkers);
        } else if (checkers < 0) {
            System.out.printf("%2d ", checkers);
        } else {
            System.out.print(" . ");
        }
    }

    private static void rollDice() {
        diceRoll = new int[]{rollDie(), rollDie()};
        System.out.println("Rolled: " + diceRoll[0] + " and " + diceRoll[1]);
    }

    private static int rollDie() {
        return random.nextInt(6) + 1;
    }

    private static List<String> calculateLegalMoves() {
        List<String> legalMoves = new ArrayList<>();
        Set<String> uniqueMoves = new HashSet<>(); // Use a Set to store unique moves
        int[] availableMoves = new int[]{diceRoll[0], diceRoll[1]};

        if ((isPlayer1Turn && player1Bar > 0) || (!isPlayer1Turn && player2Bar > 0)) {
            // Handle moves from the bar
            for (int die : availableMoves) {
                int targetPoint = isPlayer1Turn ? die - 1 : BOARD_SIZE - die;
                if (canMoveToPoint(targetPoint)) {
                    uniqueMoves.add("Bar-" + (targetPoint + 1));
                }
            }
        } else {
            // Handle regular moves and bearing off
            for (int i = 0; i < BOARD_SIZE; i++) {
                int checkers = board[i];
                if ((isPlayer1Turn && checkers > 0) || (!isPlayer1Turn && checkers < 0)) {
                    for (int die : availableMoves) {
                        int targetPoint = isPlayer1Turn ? i + die : i - die;
                        if (targetPoint >= 0 && targetPoint < BOARD_SIZE && canMoveToPoint(targetPoint)) {
                            uniqueMoves.add((i + 1) + "-" + (targetPoint + 1));
                        } else if (canBearOff(i, die)) {
                            uniqueMoves.add((i + 1) + "-off");
                        }
                    }
                }
            }
        }

        legalMoves.addAll(uniqueMoves); // Add all unique moves to the legalMoves list
        return legalMoves;
    }

    private static boolean canMoveToPoint(int targetPoint) {
        int checkers = board[targetPoint];
        return (isPlayer1Turn && (checkers >= -1 || checkers > 0)) || (!isPlayer1Turn && (checkers <= 1 || checkers < 0));
    }

    private static boolean canBearOff(int fromPoint, int die) {
        if (isPlayer1Turn) {
            if (fromPoint < 18) return false;
            for (int i = fromPoint + 1; i < BOARD_SIZE; i++) {
                if (board[i] > 0) return false;
            }
            return fromPoint + die >= BOARD_SIZE || fromPoint == highestOccupiedPoint(true);
        } else {
            if (fromPoint > 5) return false;
            for (int i = fromPoint - 1; i >= 0; i--) {
                if (board[i] < 0) return false;
            }
            return fromPoint - die < 0 || fromPoint == highestOccupiedPoint(false);
        }
    }

    private static int highestOccupiedPoint(boolean forPlayer1) {
        if (forPlayer1) {
            for (int i = BOARD_SIZE - 1; i >= 0; i--) {
                if (board[i] > 0) return i;
            }
        } else {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i] < 0) return i;
            }
        }
        return -1;
    }

    private static void displayLegalMoves(List<String> legalMoves) {
        System.out.println("Legal moves:");
        for (int i = 0; i < legalMoves.size(); i++) {
            System.out.println((char)('A' + i) + ") " + legalMoves.get(i));
        }
    }

    private static String getSelectedMove(List<String> legalMoves) {
        while (true) {
            System.out.print("Enter your move in the format 'X-Y' or 'off': ");
            String input = scanner.nextLine().trim().toLowerCase();

            if ("off".equals(input)) {
                return input;
            }

            if (input.matches("\\d+-\\d+") || input.matches("\\d+-off")) {
                if (legalMoves.contains(input)) {
                    return input;
                } else {
                    System.out.println("Invalid move. Please try again.");
                }
            } else {
                System.out.println("Invalid format. Please enter a valid move.");
            }
        }
    }

    private static void applyMove(String move) {
        String[] parts = move.split("-");
        int from = parts[0].equals("Bar") ? -1 : Integer.parseInt(parts[0]) - 1;
        int to = parts[1].equals("off") ? -1 : Integer.parseInt(parts[1]) - 1;

        if (from == -1) {
            // Move from bar
            if (isPlayer1Turn) player1Bar--; else player2Bar--;
        } else {
            board[from] += isPlayer1Turn ? -1 : 1;
        }

        if (to == -1) {
            // Bear off
            if (isPlayer1Turn) player1BearOff++; else player2BearOff++;
        } else {
            if (board[to] == (isPlayer1Turn ? -1 : 1)) {
                // Hit opponent's blot
                board[to] = 0;
                if (isPlayer1Turn) player2Bar++; else player1Bar++;
            }
            board[to] += isPlayer1Turn ? 1 : -1;
        }

        System.out.println("Move applied: " + move);
    }

    private static void displayPipCount() {
        int player1Pips = calculatePipCount(true);
        int player2Pips = calculatePipCount(false);
        System.out.println(player1Name + " pip count: " + player1Pips);
        System.out.println(player2Name + " pip count: " + player2Pips);
    }

    private static int calculatePipCount(boolean forPlayer1) {
        int pipCount = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            int checkers = board[i];
            if ((forPlayer1 && checkers > 0) || (!forPlayer1 && checkers < 0)) {
                pipCount += Math.abs(checkers) * (forPlayer1 ? 24 - i : i + 1);
            }
        }
        if (forPlayer1) {
            pipCount += player1Bar * 25;
        } else {
            pipCount += player2Bar * 25;
        }
        return pipCount;
    }

    private static void displayHint() {
        System.out.println("Available commands:");
        System.out.println("- roll: Roll the dice and make a move");
        System.out.println("- pip: Display pip count for both players");
        System.out.println("- hint: Display this list of commands");
        System.out.println("- quit: End the game");
    }

    private static boolean isGameOver() {
        return player1BearOff == 15 || player2BearOff == 15;
    }

    private static void announceWinner() {
        String winner = player1BearOff == 15 ? player1Name : player2Name;
        System.out.println("Congratulations! " + winner + " has won the game!");
    }
}