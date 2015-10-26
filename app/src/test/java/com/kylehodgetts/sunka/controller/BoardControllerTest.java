package com.kylehodgetts.sunka.controller;

import com.kylehodgetts.sunka.controller.bus.EventBus;
import com.kylehodgetts.sunka.event.PlayerMove;
import com.kylehodgetts.sunka.model.Board;
import com.kylehodgetts.sunka.model.GameState;
import com.kylehodgetts.sunka.model.Player;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Charlie Baker
 * @version 1.0
 */
public class BoardControllerTest extends TestCase {

    private GameManager manager;
    private GameState state;
    private EventBus<GameState> bus;


    /**
     * Setup method to create a new game state, new players and a new EventBus to handle the events and
     * changes to the game state.
     *
     * @throws Exception
     */
    @Before
    protected void setUp() throws Exception {
        super.setUp();

        manager = new GameManager();
        state = new GameState(new Board(),new Player(),new Player(), -1);
        bus = new EventBus<>(state,null);
        bus.registerHandler(manager);
    }

    /**
     * Prints to the console a string representation of the current board's state.
     * Including the shells in each tray and the shells in each player's store.
     *
     * @return A String to show the state of the game board's shells and each player's store
     */
    public String printBoardState() {
        return "Player 1 Store: "+state.getPlayer1().getStonesInPot() + "\n" +
                state.getBoard() +
                "Player 2 Store: " + state.getPlayer2().getStonesInPot() + "\n";
    }

    /**
     * Tests each first move of a new game done by the player from each tray on the board carried out by each player
     * from their own trays only.
     *
     * @throws Exception
     */
    @Test
    public void testMoveOne() throws Exception {

        for(int i = 0; i < 2; ++i) {
            for(int j = 0; j < 7; ++j) {
                bus.feedEvent(new PlayerMove(j, i, i));
                Thread.sleep(10000);
                System.out.println(printBoardState());

                int currentRow = i;
                for(int k = 0; k < 13; ++k) {
                    if(currentRow == i && (k+j) % 7 == j) { assertEquals(0, state.getBoard().getTray(i, j)); }
                    else { assertEquals(k < 7 ? 8 : 7, state.getBoard().getTray(currentRow, (k+j) % 7)); }
                    currentRow = (k+j) % 7 == 6? (currentRow+1) % 2 : currentRow;
                }
                assertEquals(i==0 ? 1 : 0, state.getPlayer1().getStonesInPot());
                assertEquals(i==0 ? 0 : 1, state.getPlayer2().getStonesInPot());
                setUp();
            }
        }
    }

    /**
     * Test two consecutive moves that can be done on the board from a new game.
     *
     * @throws Exception
     */
    @Test
    public void testMoveTwo() throws Exception {

        for(int i = 0; i < 2; ++i) {
            for(int j = 0; j < 7; ++j) {
                bus.feedEvent(new PlayerMove(j, i, i));
                Thread.sleep(10000);
                bus.feedEvent(new PlayerMove(j, (i+1)%2, (i+1)%2));
                Thread.sleep(10000);
                System.out.println(printBoardState());

                int currentRow = i;
                for(int k = 0; k < 13; ++k) {
                    if((k+j) % 7 == j) { assertEquals(0, state.getBoard().getTray((i+1)%2, j)); }
                    else { assertEquals(8, state.getBoard().getTray(currentRow, (k+j) % 7)); }
                    currentRow = (k+j) % 7 == 6? (currentRow+1) % 2 : currentRow;
                }
                assertEquals(1, state.getPlayer1().getStonesInPot());
                assertEquals(1, state.getPlayer2().getStonesInPot());
                setUp();
            }
        }
    }

    /**
     * Tests that the first turn should be won by the first player therefore resulting in the
     * second turn should be given to the first player.
     *
     * @throws Exception
     */
    @Test
    public void testFirstTurn() throws Exception {

        int i = 0;
        for(int j = 0; j < 7; ++j) {
            bus.feedEvent(new PlayerMove(j, i, i));
            bus.feedEvent(new PlayerMove(j, (i+1)%2, (i+1)%2));
            Thread.sleep(10000);
            System.out.println(printBoardState());

            int currentRow = i;
            for(int k = 0; k < 13; ++k) {
                if((k+j) % 7 == j) { assertEquals(0, state.getBoard().getTray((i+1)%2, j)); }
                else { assertEquals(8, state.getBoard().getTray(currentRow, (k+j) % 7)); }
                currentRow = (k+j) % 7 == 6? (currentRow+1) % 2 : currentRow;
            }
            assertEquals(1, state.getPlayer1().getStonesInPot());
            assertEquals(1, state.getPlayer2().getStonesInPot());
            assertEquals(0, state.getPlayerOneTurn());
            setUp();
        }

    }

    /**
     * Tests that the first turn should be won by the second player therefore resulting in the
     * second turn should be given to the second player.
     *
     * @throws Exception
     */
    @Test
    public void testFirstTurnSecondPlayer() throws Exception {

        int i = 1;
        for(int j = 0; j < 7; ++j) {
            bus.feedEvent(new PlayerMove(j, 1, 1));
            bus.feedEvent(new PlayerMove(j, 0, 0));
            Thread.sleep(10000);
            System.out.println(printBoardState());

            int currentRow = i;
            for(int k = 0; k < 13; ++k) {
                if((k+j) % 7 == j) { assertEquals(0, state.getBoard().getTray((i+1)%2, j)); }
                else { assertEquals(8, state.getBoard().getTray(currentRow, (k+j) % 7)); }
                currentRow = (k+j) % 7 == 6? (currentRow+1) % 2 : currentRow;
            }
            assertEquals(1, state.getPlayer1().getStonesInPot());
            assertEquals(1, state.getPlayer2().getStonesInPot());
            assertEquals(1, state.getPlayerOneTurn());
            setUp();
        }


    }


}