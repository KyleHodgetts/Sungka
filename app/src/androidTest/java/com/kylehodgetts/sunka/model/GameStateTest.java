package com.kylehodgetts.sunka.model;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kyle Hodgetts
 * @version 1.0
 * Responsible for testing the <code>GameState</code> class
 */
public class GameStateTest extends TestCase {
    private GameState gameState;
    private Board board;
    private Player p1;
    private Player p2;

    /**
     * Initialise game state components
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        board = new Board();
        p1 = new Player();
        p2 = new Player();
        gameState = new GameState(board, p1, p2);
    }

    /**
     * Assert that the correct game board is returned
     * @throws Exception
     */
    @Test
    public void testGetBoard() throws Exception {
        assertTrue(gameState.getBoard() == board);
    }

    /**
     * Assert that, upon setting a new game board, the game state board is updated
     * @throws Exception
     */
    @Test
    public void testSetBoard() throws Exception {
        Board newBoard = new Board();
        gameState.setBoard(newBoard);
        assertFalse(gameState.getBoard() == board);
    }

    /**
     * Assert that Player 1 is returned
     * @throws Exception
     */
    @Test
    public void testGetPlayer1() throws Exception {
        assertTrue(gameState.getPlayer1() == p1);
    }

    /**
     * Assert that, upon setting a new <code>Player</code> 1, the game state is updated
     * @throws Exception
     */
    @Test
    public void testSetPlayer1() throws Exception {
        Player newP1 = new Player();
        gameState.setPlayer1(newP1);
        assertFalse(gameState.getPlayer1() == p1);
    }

    /**
     * Assert that <code>Player</code> 2 is returned
     * @throws Exception
     */
    @Test
    public void testGetPlayer2() throws Exception {
        assertTrue(gameState.getPlayer2() == p2);
    }

    /**
     * Assert that, upon setting a new Player 2, the game state is updated
     * @throws Exception
     */
    @Test
    public void testSetPlayer2() throws Exception {
        Player newP2 = new Player();
        gameState.setPlayer2(newP2);
        assertFalse(gameState.getPlayer2() == p2);
    }

    /**
     * Assert that for the current turn, the correct <code>Player</code> is allowed to move
     * @throws Exception
     */
    @Test
    public void testGetCurrentPlayerIndex() throws Exception {
        gameState.setCurrentPlayerIndex(0);
        assertTrue(gameState.getCurrentPlayerIndex() == 0);
    }

    /**
     * Assert that upon updating the <code>Player</code> turn, the correct <code>Player</code> is
     * allowed to move.
     * @throws Exception
     */
    @Test
    public void testSetCurrentPlayerIndex() throws Exception {
        gameState.setCurrentPlayerIndex(0);
        assertTrue(gameState.getCurrentPlayerIndex() == 0);
        gameState.setCurrentPlayerIndex(1);
        assertFalse(gameState.getCurrentPlayerIndex() == 0);
        assertTrue(gameState.getCurrentPlayerIndex() == 1);
    }

    /**
     * Assert that, based on the current player index, the correct row of trays is enabled
     * @throws Exception
     */
    @Test
    public void testCurrentPlayerRow() throws Exception {
        gameState.setCurrentPlayerIndex(0);
        assertTrue(gameState.currentPlayerRow() == 0);
    }

    /**
     * Based on the current player index, the correct <code>Player</code> is returned.
     * @throws Exception
     */
    @Test
    public void testGetCurrentPlayer() throws Exception {
        gameState.setCurrentPlayerIndex(0);
        assertTrue(gameState.getCurrentPlayer() == p1);
        gameState.setCurrentPlayerIndex(1);
        assertTrue(gameState.getCurrentPlayer() == p2);
    }

    /**
     * Assert that, when a player index is given, the correct <code>Player</code> is returned
     * @throws Exception
     */
    @Test
    public void testGetPlayerFor() throws Exception {
        assertTrue(gameState.getPlayerFor(0) == p1);
        assertTrue(gameState.getPlayerFor(1) == p2);
    }

    /**
     * Assert that, when set, a move is being carried out currently
     * @throws Exception
     */
    @Test
    public void testIsDoingMove() throws Exception {
        gameState.setDoingMove(true);
        assertTrue(gameState.isDoingMove());
        gameState.setDoingMove(false);
        assertFalse(gameState.isDoingMove());
    }

    /**
     * Assert that, when given a truth value of whether a move is being carried out,
     * the game state updates
     * @throws Exception
     */
    @Test
    public void testSetDoingMove() throws Exception {
        gameState.setDoingMove(true);
        assertTrue(gameState.isDoingMove());
        gameState.setDoingMove(false);
        assertFalse(gameState.isDoingMove());
    }

    /**
     * Test that all initial values are as they should be
     * @throws Exception if values not as should be
     */
    @Test
    public void testInitialValues() throws Exception {
        Assert.assertEquals(gameState.getCurrentPlayerIndex(), -1);
        Assert.assertEquals(gameState.playerHasMoved(0), false);
        Assert.assertEquals(gameState.playerHasMoved(1), false);
        Assert.assertEquals(gameState.isFirstMoveOverForPlayer(0), false);
        Assert.assertEquals(gameState.isFirstMoveOverForPlayer(1), false);
    }

    /**
     * Test that switching players works as expected
     */
    @Test
    public void testSetAndSwitchCurrentPlayer() throws Exception {
        gameState.switchCurrentPlayerIndex();
        Assert.assertEquals(gameState.getCurrentPlayerIndex(), -1);

        for (int player = 0; player < 2; player++) {
            int otherPLayer = (player + 1) % 2;
            gameState.setCurrentPlayerIndex(player);
            Assert.assertEquals(gameState.getCurrentPlayerIndex(), player);

            gameState.switchCurrentPlayerIndex();
            Assert.assertEquals(gameState.getCurrentPlayerIndex(), otherPLayer);
            gameState.switchCurrentPlayerIndex();
            Assert.assertEquals(gameState.getCurrentPlayerIndex(), player);

        }
    }

    /**
     * Test that players have moved or not
     * @throws Exception
     */
    @Test
    public void testSetPlayerMoved() throws Exception {

        for (int player = 0; player < 2; player++) {
            gameState = new GameState(board, p1, p2);
            int otherPLayer = (player + 1) % 2;

            Assert.assertEquals(gameState.playerWhoWentFirst(), -1);

            gameState.setPlayerHasMoved(player);

            Assert.assertTrue(gameState.playerHasMoved(player));
            Assert.assertFalse(gameState.playerHasMoved(otherPLayer));

            Assert.assertEquals(gameState.playerWhoWentFirst(), player);

            gameState.setPlayerHasMoved(otherPLayer);

            Assert.assertTrue(gameState.playerHasMoved(otherPLayer));
            Assert.assertEquals(gameState.playerWhoWentFirst(), player);
        }
    }
}
