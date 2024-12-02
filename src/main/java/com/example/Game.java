//-------------------------------------------------------------------------------------------------------

package com.example;

import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;

public class Game {
    public static final int BOARD_SIZE = 24;
    public static String player1Name;
    public static String player2Name;
    public static int[] board;
    public static Random random;
    public static Scanner scanner;
    public static boolean isPlayer1Turn;
    public static int[] diceRoll;
    public static int player1Bar = 0;
    public static int player2Bar = 0;
    public static int player1BearOff = 0;
    public static int player2BearOff = 0;
    public static int matchLength;
    public static int player1MatchScore = 0;
    public static int player2MatchScore = 0;
    public static int currentStake = 1;
    public static boolean cubeOffered = false;
    public static int cubeOwner = 0; // 0 for centered, 1 for player1, 2 for player2

    public static void initializeGame() {
        board = new int[BOARD_SIZE];
        random = new Random();
        scanner = new Scanner(System.in);
        setupBoard();
        player1Name = getPlayerName("Player 1");
        player2Name = getPlayerName("Player 2");
        System.out.print("Enter the match length: ");
        matchLength = Integer.parseInt(scanner.nextLine());
        determineFirstPlayer();
        System.out.println("\nFantastic! Let's get started, " + player1Name + " and " + player2Name + "!");
        cubeOffered = false;
        cubeOwner = 0;
        currentStake = 1;
    }

    public static void playGame() {
        while (true) {
            displayBoard();
            displayMatchInfo();
            String currentPlayer = isPlayer1Turn ? player1Name : player2Name;
            System.out.println("\nCurrent player: " + currentPlayer);
            System.out.print(currentPlayer + ", enter a command (roll, pip, hint, double, dice, test, quit): ");
            String command = scanner.nextLine().trim().toLowerCase();
            String[] parts = command.split("\\s+");
            switch (parts[0]) {
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
                case "double":
                    handleDoubleCommand();
                    break;
                case "dice":
                    handleDiceCommand(parts);
                    break;
                case "test":
                    handleTestCommand(parts);
                    break;
                case "quit":
                    System.out.println("Thanks for playing!");
                    return;
                default:
                    System.out.println("Invalid command. Type 'hint' for a list of commands.");
            }
            if (isGameOver()) {
                endGame(false);
            }
        }
    }

    public static void displayPipCount() {
        int player1Pips = calculatePipCount(true);
        int player2Pips = calculatePipCount(false);
        System.out.println(player1Name + " pip count: " + player1Pips);
        System.out.println(player2Name + " pip count: " + player2Pips);
    }

    public static int calculatePipCount(boolean forPlayer1) {
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

    public static void displayMatchInfo() {
        System.out.println("Match length: " + matchLength);
        System.out.println("Match score - " + player1Name + ": " + player1MatchScore + ", " + player2Name + ": " + player2MatchScore);
        System.out.println("Current stake: " + currentStake);
        System.out.println("Cube owner: " + (cubeOwner == 0 ? "Centered" : (cubeOwner == 1 ? player1Name : player2Name)));
    }

    public static void handleDoubleCommand() {
        if (cubeOffered) {
            System.out.println("Error: Double has already been offered this turn.");
            return;
        }
        if ((isPlayer1Turn && cubeOwner == 2) || (!isPlayer1Turn && cubeOwner == 1)) {
            System.out.println("Error: You don't have the right to double.");
            return;
        }
        cubeOffered = true;
        System.out.println((isPlayer1Turn ? player1Name : player2Name) + " offers a double.");
        System.out.print((isPlayer1Turn ? player2Name : player1Name) + ", do you accept? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("yes")) {
            currentStake *= 2;
            cubeOwner = isPlayer1Turn ? 2 : 1;
            System.out.println("Double accepted. The stake is now " + currentStake);
        } else {
            endGame(true);
        }
        cubeOffered = false;
    }

    public static void handleDiceCommand(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Error: Invalid dice command format. Use 'dice <int> <int>'");
            return;
        }
        try {
            int die1 = Integer.parseInt(parts[1]);
            int die2 = Integer.parseInt(parts[2]);
            if (die1 < 1 || die1 > 6 || die2 < 1 || die2 > 6) {
                System.out.println("Error: Dice values must be between 1 and 6");
                return;
            }
            diceRoll = new int[]{die1, die2};
            System.out.println("Next roll set to: " + die1 + " and " + die2);
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid dice values");
        }
    }

    public static void handleTestCommand(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Error: Invalid test command format. Use 'test <filename>'");
            return;
        }
        String filename = parts[1];
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                String command = fileScanner.nextLine().trim();
                System.out.println("Executing command: " + command);
                processCommand(command);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filename);
        }
    }

    public static void processCommand(String command) {
        String[] parts = command.split("\\s+");
        switch (parts[0]) {
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
            case "double":
                handleDoubleCommand();
                break;
            case "dice":
                handleDiceCommand(parts);
                break;
            case "quit":
                System.out.println("Quitting the game...");
                System.exit(0);
            default:
                System.out.println("Invalid command in test file: " + command);
        }
    }

    public static void endGame(boolean forfeit) {
        int points = forfeit ? currentStake : calculatePoints();
        if (isPlayer1Turn) {
            player1MatchScore += points;
        } else {
            player2MatchScore += points;
        }
        displayMatchInfo();
        System.out.println((isPlayer1Turn ? player1Name : player2Name) + " wins " + points + " points!");
        if (player1MatchScore >= matchLength || player2MatchScore >= matchLength) {
            System.out.println((player1MatchScore > player2MatchScore ? player1Name : player2Name) + " wins the match!");
            System.out.print("Start a new match? (yes/no): ");
            if (scanner.nextLine().trim().toLowerCase().equals("yes")) {
                startNewMatch();
            } else {
                System.exit(0);
            }
        } else {
            initializeGame();
        }
    }

    public static int calculatePoints() {
        int points = currentStake;
        if (isGamemon()) {
            points *= 2;
            System.out.println("Gammon!");
        }
        if (isBackgammon()) {
            points *= 3;
            System.out.println("Backgammon!");
        }
        return points;
    }

    public static boolean isGamemon() {
        return (isPlayer1Turn && player2BearOff == 0) || (!isPlayer1Turn && player1BearOff == 0);
    }

    public static boolean isBackgammon() {
        if (isPlayer1Turn) {
            return player2BearOff == 0 && (player2Bar > 0 || hasCheckersInHomeBoard(!isPlayer1Turn));
        } else {
            return player1BearOff == 0 && (player1Bar > 0 || hasCheckersInHomeBoard(!isPlayer1Turn));
        }
    }

    public static boolean hasCheckersInHomeBoard(boolean forPlayer1) {
        int start = forPlayer1 ? 0 : 18;
        int end = forPlayer1 ? 6 : 24;
        for (int i = start; i < end; i++) {
            if ((forPlayer1 && board[i] > 0) || (!forPlayer1 && board[i] < 0)) {
                return true;
            }
        }
        return false;
    }

    public static void startNewMatch() {
        System.out.print("Enter the new match length: ");
        matchLength = Integer.parseInt(scanner.nextLine());
        player1MatchScore = 0;
        player2MatchScore = 0;
        currentStake = 1;
        cubeOwner = 0;
        initializeGame();
    }

    public static void displayHint() {
        System.out.println("Available commands:");
        System.out.println("- roll: Roll the dice and make a move");
        System.out.println("- pip: Display pip count for both players");
        System.out.println("- hint: Display this list of commands");
        System.out.println("- double: Offer a double to the opponent");
        System.out.println("- dice <int> <int>: Set the next dice roll");
        System.out.println("- test <filename>: Run commands from a file");
        System.out.println("- quit: End the game");
    }

    public static void determineFirstPlayer() {
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

    public static String getPlayerName(String playerNumber) {
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

    public static void setupBoard() {
        board[0] = 2; // Player 1's checkers
        board[5] = -5; // Player 2's checkers
        board[7] = -3; // Player 2's checkers
        board[11] = 5; // Player 1's checkers
        board[12] = -5; // Player 2's checkers
        board[16] = 3; // Player 1's checkers
        board[18] = 5; // Player 1's checkers
        board[23] = -2; // Player 2's checkers
    }

    public static void displayBoard() {
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

    public static void displayPipNumbers(boolean isTopRow) {
        StringBuilder pipNumbers = new StringBuilder();
        int start = isTopRow ? (isPlayer1Turn ? 13 : 12) : (isPlayer1Turn ? 12 : 13);
        int end = isTopRow ? (isPlayer1Turn ? 25 : 24) : (isPlayer1Turn ? 0 : 1);
        int step = isTopRow ? 1 : -1;
        for (int i = start; i != end; i += step) {
            pipNumbers.append(String.format("%2d ", i % 24));
        }
        System.out.println(pipNumbers.toString());
    }

    public static void printUpperRow() {
        for (int i = 12; i < 24; i++) {
            printPosition(board[i]);
        }
        System.out.println();
    }

    public static void printLowerRow() {
        for (int i = 11; i >= 0; i--) {
            printPosition(board[i]);
        }
        System.out.println();
    }

    public static void printPosition(int checkers) {
        if (checkers > 0) {
            System.out.printf("+%1d ", checkers);
        } else if (checkers < 0) {
            System.out.printf("%2d ", checkers);
        } else {
            System.out.print(" . ");
        }
    }

    public static void rollDice() {
        diceRoll = new int[]{rollDie(), rollDie()};
        System.out.println("Rolled: " + diceRoll[0] + " and " + diceRoll[1]);
    }

    public static int rollDie() {
        return random.nextInt(6) + 1;
    }

    public static List<String> calculateLegalMoves() {
        List<String> legalMoves = new ArrayList<>();
        Set<String> uniqueMoves = new HashSet<>();
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
        legalMoves.addAll(uniqueMoves);
        return legalMoves;
    }

    public static boolean canMoveToPoint(int targetPoint) {
        int checkers = board[targetPoint];
        return (isPlayer1Turn && (checkers >= -1 || checkers > 0)) || (!isPlayer1Turn && (checkers <= 1 || checkers < 0));
    }

    public static boolean canBearOff(int fromPoint, int die) {
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

    public static int highestOccupiedPoint(boolean forPlayer1) {
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

    public static void displayLegalMoves(List<String> legalMoves) {
        System.out.println("Legal moves:");
        for (int i = 0; i < legalMoves.size(); i++) {
            System.out.println((char) ('A' + i) + ") " + legalMoves.get(i));
        }
    }

    public static String getSelectedMove(List<String> legalMoves) {
        while (true) {
            System.out.print("Enter your move (letter or 'X-Y' format): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.length() == 1 && input.charAt(0) >= 'a' && input.charAt(0) < 'a' + legalMoves.size()) {
                return legalMoves.get(input.charAt(0) - 'a');
            }
            if (input.matches("\\d+-\\d+") || input.matches("\\d+-off") || input.matches("bar-\\d+")) {
                if (legalMoves.contains(input)) {
                    return input;
                }
            }
            System.out.println("Invalid move. Please try again.");
        }
    }

    public static void applyMove(String move) {
        String[] parts = move.split("-");
        int from = parts[0].equals("bar") ? -1 : Integer.parseInt(parts[0]) - 1;
        int to = parts[1].equals("off") ? -1 : Integer.parseInt(parts[1]) - 1;
        if (from == -1) {
            // Move from bar
            if (isPlayer1Turn) player1Bar--;
            else player2Bar--;
        } else {
            board[from] += isPlayer1Turn ? -1 : 1;
        }
        if (to == -1) {
            // Bear off
            if (isPlayer1Turn) player1BearOff++;
            else player2BearOff++;
        } else {
            if (board[to] == (isPlayer1Turn ? -1 : 1)) {
                // Hit opponent's blot
                board[to] = 0;
                if (isPlayer1Turn) player2Bar++;
                else player1Bar++;
            }
            board[to] += isPlayer1Turn ? 1 : -1;
        }
        System.out.println("Move applied: " + move);
    }

    public static boolean isGameOver() {
        return player1BearOff == 15 || player2BearOff == 15;
    }

//    public static void announceWinner() {
//        String winner = player1BearOff == 15 ? player1Name : player2Name;
//        System.out.println("Congratulations! " + winner + " has won the game!");
//    }
}


