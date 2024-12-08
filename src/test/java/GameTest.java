import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.example.Game;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;
import java.util.List;

public class GameTest {

    @BeforeEach
    void setUp() {
        String input = "Player1\nPlayer2\n5\nyes\n";
        provideInput(input);
        Game.scanner = new Scanner(System.in);
        Game.initializeGame();
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @Test
    void testBearOffConditions() {
        Game.board = new int[Game.BOARD_SIZE];
        Game.board[18] = 2; // Player 1's checker in bearing off position
        Game.isPlayer1Turn = true;
        assertTrue(Game.canBearOff(18, 6));
        assertFalse(Game.canBearOff(12, 6)); // Cannot bear off from lower points
    }

    @Test
    void testRollDice() {
        // Mocking random.nextInt to always return 3
        Game.random = new Random() {
            @Override
            public int nextInt(int bound) {
                return 2; // to simulate rolling 3
            }
        };
        Game.rollDice();
        assertArrayEquals(new int[]{3, 3}, Game.diceRoll);
    }
    @Test
    void testHandleDoubleCommandWithAccept() {
        // Setup initial game state
        Game.cubeOwner = 0;
        Game.isPlayer1Turn = true;
        Game.currentStake = 1;
        Game.cubeOffered = false;

        // Simulate user accepting the double
        InputStream originalIn = System.in;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("yes\n".getBytes());
            System.setIn(in);
            Game.handleDoubleCommand();
            assertEquals(2, Game.currentStake);
            assertEquals(2, Game.cubeOwner); // Player 2 now owns the cube
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testDetermineFirstPlayerWithDifferentRolls() {
        // Mocking random.nextInt to return different values for each player
        Game.random = new Random() {
            private int count = 0;
            @Override
            public int nextInt(int bound) {
                return count++ % 2 == 0 ? 3 : 4; // Alice rolls 3, Bob rolls 4
            }
        };
        Game.player1Name = "Jhon";
        Game.player2Name = "Bob";
        Game.determineFirstPlayer();
        assertFalse(Game.isPlayer1Turn); // Bob should go first
    }

<<<<<<< HEAD
    @Test
    void testHighestOccupiedPointForPlayer2() {
        Game.board = new int[Game.BOARD_SIZE];
        Game.board[3] = -3;
        Game.board[8] = -2;
        assertEquals(3, Game.highestOccupiedPoint(false));
    }
    @Test
    void testIsGameOverWithAllCheckersBornOff() {
        Game.player1BearOff = 15;
        assertTrue(Game.isGameOver());
    }
    @Test
    void testIsGamemonWithAllCheckersNotBornOff() {
        Game.isPlayer1Turn = true;
        Game.player2BearOff = 0;
        assertTrue(Game.isGamemon());
    }

    @Test
    void testIsBackgammonWithCheckersOnBar() {
        Game.isPlayer1Turn = true;
        Game.player2BearOff = 0;
        Game.player2Bar = 1;
        assertTrue(Game.isBackgammon());
    }
    @Test
    void testCalculatePointsWithBackgammon() {
        // Setup initial game state for backgammon
        Game.isPlayer1Turn = true;
        Game.currentStake = 1;
        Game.player2BearOff = 0;
        Game.player2Bar = 1;

        int points = Game.calculatePoints();
        assertEquals(6, points); // Backgammon triples the stake from 1 to 6
    }
    @Test
    void testHandleTestCommandWithEmptyFile() {
        String[] command = {"test", "emptyfile.txt"};
        // Mock System.out to capture the output
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(outputStream));
            Game.handleTestCommand(command);
            String output = outputStream.toString();
            assertTrue(output.contains("Error: File not found"));
        } finally {
            System.setOut(originalOut);
        }
    }
=======
>>>>>>> eb65dc1738817297f2514744fc79458bfb06c02e
}