import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/*
* Testing class for Deck
*/
public class DeckTest {
    // Attributes

    /**
     * Stores the Deck object being tested on
     */
    private Deck deck;

    // Methods
    
    @Before
    /**
     * Constructs a test Deck instance with the specified attributes.
     * Used to test Deck methods.
     * 
     * @throws IOException
     */
    public void setUp() throws IOException {
        try {
            this.deck = new Deck(1);
        } catch (IOException e) {
            fail("Failed to create/overerite or assign output file to deck test object");
        }
        // Ensures the top card is 1, then 2, then 3
        Card card1 = new Card(1); deck.getDeckCards().add(card1);
        Card card2 = new Card(2); deck.getDeckCards().add(card2);
        Card card3 = new Card(3); deck.getDeckCards().add(card3);
    }

    @After
    /**
     * Cleans up deck and its variable, as well as resets the deck output file
     * after each test method. This ensures each test is done with a clean state.
     * 
     * @throws IOException
     */
    public void tearDown() throws IOException {
        // clears deck output file
        if (deck.getOutputFile().exists()) {
            new FileWriter(deck.getOutputFile(), false).close();
        }

        // clears deck
        deck = null;
    }

    @Test
    /**
     * Tests the addCardToTop method to ensure that it adds a card (integer) to the top of deck. This 
     * test is done as it uses a queue, that normally doesn't have a way to add to the front of the
     * queue. This test will check:
     * 
     * 1. The front card is correct after a card is added. 
     * 2. The order of the deck is correct after a card is added, and the deck didn't delete a card.
     */
    public void testAddCardToTop_UpdatesDeckCorrectly() {
        // Performs test method
        Card card1 = new Card(4); deck.addCardToTop(card1);

        // Expected contents of the queue after card is added to top of deck
        Queue<Integer> expectedContents = new LinkedList<>();
        expectedContents.add(4); 
        expectedContents.add(1); 
        expectedContents.add(2); 
        expectedContents.add(3); 

        // 1
        assertEquals("The top of the deck does not equal the value insteaded to be added", 4, (int) deck.getCardValues().peek());

        // 2
        assertEquals("The deck contents are not correct." + deck.getCardValues(), expectedContents, deck.getCardValues());
        assertEquals("The deck size is not correct after a card is added.", 4, deck.getDeckCards().size());
    }

    @Test
    /**
     * Tests the addCardToBottom method to ensure that it adds a card (integer) to the bottom of deck. This 
     * test is done as the deck uses a queue, which normally don't have way of adding to the front of the
     * queue. This test will check:
     * 
     * 1. The last card is correct after the card is added. 
     * 2. The order of the deck is correct after the card is added, and the deck didn't delete a card.
     */
    public void testAddCardToBottom_UpdatesDeckCorrectly() {
        // Performs test method
        Card card1 = new Card(4); deck.addCardToBottom(card1);

        // Expected contents of the queue after card is added to top of deck
        Queue<Integer> expectedContents = new LinkedList<>();
        expectedContents.add(1); 
        expectedContents.add(2); 
        expectedContents.add(3); 
        expectedContents.add(4); 

        // 1
        assertEquals("The top of the deck does not equal the value insteaded to be added", 4, (int) ((LinkedList<Integer>) deck.getCardValues()).peekLast());

        // 2
        assertEquals("The deck contents are not correct. Expected contents: [1, 2, 3, 4], Actual Contents: " + deck.getCardValues(), expectedContents, deck.getCardValues());
        assertEquals("The deck size is not correct after a card is added.", 4, deck.getDeckCards().size());
    }

    @Test
    /**
     * Tests the removeCardFromTop method to ensure it correctly removes a card from the top of the
     * deck. This test will check:
     * 
     * 1. It removes the three cards from the setup deck in the correct order
     * 2. It throws the IllegalStateException if the deck is empty.
     * 
     * @throws IllegalStateException
     */
    public void testRemoveCardFromTop_CardsRemovedInCorrectOrder() throws IllegalStateException {
        // Performs the method to test
        Card firstCard = deck.removeCardFromTop();
        Card secondCard = deck.removeCardFromTop();
        Card thirdCard = deck.removeCardFromTop();

        // 1 
        assertEquals("The expected first card removed from the top of the deck is not the expected result", 1, firstCard.getCardValue());
        assertEquals("The expected second card removed from the top of the deck is not the expected result", 2, secondCard.getCardValue());
        assertEquals("The expected third card removed from the top of the deck is not the expected result", 3, thirdCard.getCardValue());

        // 2
        assertThrows("It should throw an exception when", IllegalStateException.class, () -> deck.removeCardFromTop());
    }

    @Test 
    /**
     * Tests the getCardValues method to see if it returns all card values in the deck. 
     * 
     * @throws IOException
     */
    public void testGetCardValues_ReturnsCorrectCardValues() throws IOException {
        List<Integer> expectedContents = Arrays.asList(1, 2, 3);
        Queue<Integer> actualContents = deck.getCardValues();

        assertEquals("The deck card values is not what is expected", expectedContents, actualContents);
    }

    @Test
    /**
     * Tests the writeFinalDeckContentsToFile method. This test will specifically check:
     * 
     * 1. Checks if output file is empty before the file is written to
     * 2. Checks if the line written to the outputfile is correct
     * 
     * @throws IOException
     */
    public void testWriteFinalDeckContentsToFile_CorrectOutputFileContents() throws IOException {
        // 1
        assertTrue("The file is not empty before method is used", deck.getOutputFile().length() == 0);
        
        deck.writeFinalDeckContentsToFile();

        // Expected and actual contents of the file
        String expectedContents = "deck 1 contents: [1, 2, 3]";
        String actualContents = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(deck.getOutputFile()))) {
            actualContents = reader.readLine();
        } catch (IOException e) {
            fail("Failed to read contents of deck output file");
        }

        // 2
        assertTrue("The file contents are incorrect.", actualContents.contains(expectedContents));
    }
}




