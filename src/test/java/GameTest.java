////import org.junit.*;
////import com.example.Game;
////
////import java.util.List;
////
////import static org.junit.Assert.assertEquals;
////import static org.junit.Assert.assertTrue;
////
////
////public class GameTest {
////
////        private static Game game;
////
////        @BeforeClass
////        public static void setUp() {
////            game = new Game();
////            game.initializeGame();
////        }
////
////        @Test
////        public void testInitialBoardSetup() {
////            int[] board = game.getBoard();
////            assertEquals(2, board[0]);
////            assertEquals(-5, board[5]);
////            assertEquals(-3, board[7]);
////            assertEquals(5, board[11]);
////            assertEquals(-5, board[12]);
////            assertEquals(3, board[16]);
////            assertEquals(5, board[18]);
////            assertEquals(-2, board[23]);
////        }
////
////        @Test
////        public void testRollDice() {
////            int[] dice = game.rollDice();
////            assertTrue(dice[0] >= 1 && dice[0] <= 6);
////            assertTrue(dice[1] >= 1 && dice[1] <= 6);
////        }
////
////        @Test
////        public void testCalculateLegalMoves() {
////            game.setDiceRoll(new int[]{3, 4});
////            game.setPlayer1Turn(true);
////            List<String> legalMoves = game.calculateLegalMoves();
////            assertFalse(legalMoves.isEmpty());
////        }
////
////        @Test
////        public void testApplyMove() {
////            game.setBoard(new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
////            game.setPlayer1Turn(true);
////            game.applyMove("1-4");
////            int[] board = game.getBoard();
////            assertEquals(0, board[0]);
////            assertEquals(1, board[3]);
////        }
////
////        @Test
////        public void testGameOver() {
////            game.setPlayer1BearOff(15);
////            assertTrue(game.isGameOver());
////        }
////
////        @Test
////        public void testCalculatePipCount() {
////            int pipCount = game.calculatePipCount(true);
////            assertEquals(167, pipCount);
////        }
////
////        @Test
////        public void testHandleDoubleCommand() {
////            game.setCubeOwner(0);
////            game.setPlayer1Turn(true);
////            game.handleDoubleCommand();
////            assertEquals(2, game.getCurrentStake());
////            assertEquals(2, game.getCubeOwner());
////        }
////
////        @Test
////        public void testHandleDiceCommand() {
////            game.handleDiceCommand(new String[]{"dice", "3", "4"});
////            int[] diceRoll = game.getDiceRoll();
////            assertEquals(3, diceRoll[0]);
////            assertEquals(4, diceRoll[1]);
////        }
////    }
////
//
//import org.junit.*;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//import com.example.Game;
//import org.junit.jupiter.api.BeforeEach;
//
//import java.util.List;
//
//class GameTest {
//
//    private static Game game;
//
//    @BeforeEach
//    void setUp() {
//        Game.initializeGame();
//    }
//
//    @Test
//    void testInitialBoardSetup() {
//        assertEquals(2, Game.board[0]);
//        assertEquals(-5, Game.board[5]);
//        assertEquals(-3, Game.board[7]);
//        assertEquals(5, Game.board[11]);
//        assertEquals(-5, Game.board[12]);
//        assertEquals(3, Game.board[16]);
//        assertEquals(5, Game.board[18]);
//        assertEquals(-2, Game.board[23]);
//    }
//
//    @Test
//    void testRollDie() {
//        int roll = Game.rollDie();
//        assertTrue(roll >= 1 && roll <= 6);
//    }
//
//    @Test
//    void testCalculatePipCount() {
//        int player1PipCount = Game.calculatePipCount(true);
//        int player2PipCount = Game.calculatePipCount(false);
//        assertEquals(167, player1PipCount);
//        assertEquals(167, player2PipCount);
//    }
//
//    @Test
//    void testCanMoveToPoint() {
//        assertTrue(Game.canMoveToPoint(0));
//        assertFalse(Game.canMoveToPoint(5));
//    }
//
//    @Test
//    void testIsGameOver() {
//        assertFalse(Game.isGameOver());
//        Game.player1BearOff = 15;
//        assertTrue(Game.isGameOver());
//    }
//
//    @Test
//    void testHandleDoubleCommand() {
//        Game.cubeOwner = 0;
//        Game.isPlayer1Turn = true;
//        Game.handleDoubleCommand();
//        assertEquals(2, Game.currentStake);
//        assertEquals(2, Game.cubeOwner);
//    }
//
//    @Test
//    void testCalculateLegalMoves() {
//        Game.diceRoll = new int[]{3, 4};
//        Game.isPlayer1Turn = true;
//        List<String> legalMoves = Game.calculateLegalMoves();
//        assertFalse(legalMoves.isEmpty());
//    }
//
//    @Test
//    void testHandleDiceCommand() {
//        String[] command = {"dice", "3", "4"};
//        Game.handleDiceCommand(command);
//        assertArrayEquals(new int[]{3, 4}, Game.diceRoll);
//    }
//}



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import com.example.Game;

class GameTest {
    private static Game game;

    @BeforeEach
    void setUp() {
        Game.initializeGame();
    }

    @Test
    void testSetupBoard() {
        assertEquals(2, Game.board[0]);
        assertEquals(-5, Game.board[5]);
        assertEquals(-3, Game.board[7]);
        assertEquals(5, Game.board[11]);
        assertEquals(-5, Game.board[12]);
        assertEquals(3, Game.board[16]);
        assertEquals(5, Game.board[18]);
        assertEquals(-2, Game.board[23]);
    }

    @Test
    void testGetPlayerName() {
        InputStream originalIn = System.in;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("TestPlayer\n".getBytes());
            System.setIn(in);
            assertEquals("TestPlayer", Game.getPlayerName("Test"));
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testRollDie() {
        for (int i = 0; i < 100; i++) {
            int roll = Game.rollDie();
            assertTrue(roll >= 1 && roll <= 6);
        }
    }

    @Test
    void testCalculatePipCount() {
        assertEquals(167, Game.calculatePipCount(true));
        assertEquals(167, Game.calculatePipCount(false));
    }

    @Test
    void testCanMoveToPoint() {
        assertTrue(Game.canMoveToPoint(0));
        assertFalse(Game.canMoveToPoint(5));
    }

    @Test
    void testIsGameOver() {
        assertFalse(Game.isGameOver());
        Game.player1BearOff = 15;
        assertTrue(Game.isGameOver());
    }

    @Test
    void testHandleDoubleCommand() {
        Game.cubeOwner = 0;
        Game.isPlayer1Turn = true;
        InputStream originalIn = System.in;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("yes\n".getBytes());
            System.setIn(in);
            Game.handleDoubleCommand();
            assertEquals(2, Game.currentStake);
            assertEquals(2, Game.cubeOwner);
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testCalculateLegalMoves() {
        Game.diceRoll = new int[]{3, 4};
        Game.isPlayer1Turn = true;
        List<String> legalMoves = Game.calculateLegalMoves();
        assertFalse(legalMoves.isEmpty());
    }

    @Test
    void testHandleDiceCommand() {
        String[] command = {"dice", "3", "4"};
        Game.handleDiceCommand(command);
        assertArrayEquals(new int[]{3, 4}, Game.diceRoll);
    }

    @Test
    void testIsGamemon() {
        assertFalse(Game.isGamemon());
        Game.player2BearOff = 15;
        Game.isPlayer1Turn = true;
        assertTrue(Game.isGamemon());
    }

    @Test
    void testIsBackgammon() {
        assertFalse(Game.isBackgammon());
        Game.player2BearOff = 0;
        Game.player2Bar = 1;
        Game.isPlayer1Turn = true;
        assertTrue(Game.isBackgammon());
    }

    @Test
    void testHasCheckersInHomeBoard() {
        assertTrue(Game.hasCheckersInHomeBoard(true));
        assertTrue(Game.hasCheckersInHomeBoard(false));
    }

    @Test
    void testHighestOccupiedPoint() {
        assertEquals(18, Game.highestOccupiedPoint(true));
        assertEquals(23, Game.highestOccupiedPoint(false));
    }

    @Test
    void testCanBearOff() {
        assertFalse(Game.canBearOff(0, 6));
        Game.board = new int[24];
        Game.board[23] = 1;
        assertTrue(Game.canBearOff(23, 1));
    }

    @Test
    void testHandleTestCommand(@TempDir Path tempDir) throws Exception {
        File testFile = tempDir.resolve("test_commands.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("roll\npip\n");
        }
        String[] command = {"test", testFile.getAbsolutePath()};
        Game.handleTestCommand(command);
        // Assert expected behavior after running test commands
    }
}