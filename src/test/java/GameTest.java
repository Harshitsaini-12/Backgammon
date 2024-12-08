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



}