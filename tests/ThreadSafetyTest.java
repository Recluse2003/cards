import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This class is made to test thread safety only. It is seperate to all other tests classes as
 * the number of players and decks needed for this test is larger than the other tests, so 
 * keeping this in a seperate class makes it easier to organize and visualise.  
 */
public class ThreadSafetyTest {

    @After
    /**
     * Ensures that all output files created for decks and players are deleted
     */
    public void tearDown() {
        for (int i = 1; i <= 100; i++) {
            File deckOutputFile = new File("deck" + i + "_output.txt");
            File playerOutputFile = new File("player" + i + "_output.txt");
        

            if (deckOutputFile.exists()) {
                deckOutputFile.delete();
            } 

            if (playerOutputFile.exists()) {
                playerOutputFile.delete();
            }             
        }
    }

    @Test
    /**
     * This test method tests the thread safety of the CardGame program, and to test the edge case 
     * of wether the game still performs well with a high number of players. It does this by using 100 
     * player threads, where only player 1 thread has the potential to win. The test will go through
     * 100s of turns before being complete due to the large number of players. 
     * 
     * This test should only be performed when all other tests have been performed to ensure the 
     * code works properly as this test will be using multiple methods from the different classes to 
     * perform this thread safety check. 
     * 
     * This will test: 
     * 1. If any player or deck had more or less than 4 cards after every player has drawn and 
     * discarded a card. This is checked using a 
     * 2. The number of cards at the end, match the number of cards at the start
     * 3. The cards present at the end of the game, match what the game started with
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void testThreadSafety() throws IOException, InterruptedException {
        // Test user input for no. of players, and a valid test pack.
        System.setIn(new ByteArrayInputStream("100\nthreadSafetyTestPack.txt\n".getBytes()));

        // Creates a ByteArrayOutputStream to capture the output from System.out
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Saves the orginal System.out to return later, and ensure other tests aint affected
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outputStream));


        try {
            // Runs method
            CardGame.main(new String[]{});
            CardGame testGame = CardGame.getCardGame();

            // Make sure method is performed fully before checking output
            Thread.sleep(50000); // wait 50 seconds

            // Holds all the cards from all players and decks at the end of game
            List<Integer> allCardsAtEnd = getAllCards(testGame);

            // 2
            assertEquals("The number of cards at the start of the game, is different to the number at the end", 800, allCardsAtEnd.size());

            // 3
            List<Integer> startPack = testGame.getPackCardValues();
            Collections.sort(startPack);
            Collections.sort(allCardsAtEnd);
            assertEquals("There are cards missing or duplicated between the start and the end of game", startPack, allCardsAtEnd);

        } catch (IllegalStateException e) {
            fail("IllegalStateException was thrown meaning a deck or player didn't have 4 cards at the end of their turn: " + e.getMessage());
        } finally {
            System.setOut(originalSystemOut);
        }
    }

    /**
     * Creates a list of intergers that hold all card values from all cards in the game, 
     * meaning the cards stored in players and decks instance
     * 
     * @param game
     * @return Returns a list of all cards in the game
     */
    private List<Integer> getAllCards(CardGame game) {
        List<Integer> allCards = new ArrayList<>();

        // Adds all cards from players to the allCards list
        for (Player player : game.getPlayers()) {
            allCards.addAll(player.getCardValues());
        }

        // Adds all cards from decks to the allCards list
        for (Deck deck : game.getDecks()) {
            allCards.addAll(deck.getCardValues());
        }

        return allCards;
    }
}
