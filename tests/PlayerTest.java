import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Scanner;

/*
* Testing class for Player
*/
public class PlayerTest {

    // Attributes

    /**
     * Stores the player objects being tested on
     */
    private Player player1;
    private Player player2;
    /**
     * Stores the deck the player1 draws cards from, and player2 discards to
     */
    private Deck deck1;
    /**
     * Stores the deck the player2 draws cards from, and player1 discards to
     */
    private Deck deck2;

    private CardGame game;

    // BeforeEach Methods

    @Before
    /**
     * Constructs a test Player instance with the specified attributes.
     * Used to test Player methods.
     * 
     * @throws IOException
     */
    public void setUp() throws IOException {
        game = new CardGame();
        game.setNumOfPlayers(2);

        // Sets up player 1 draw deck, and player 2 discard deck
        deck1 = new Deck(1);

        deck2 = new Deck(2); 

        player1 = new Player(1, deck1, deck2, game);

        player2 = new Player(2, deck2, deck1, game);

        game.setPlayers(Arrays.asList(player1, player2));
        game.setDecks(Arrays.asList(deck1, deck2));
    }

    @After
    /**
     * Cleans up objects and variable, as well as resets the output files after
     * each test method. This ensures each test is done with a clean state.
     * 
     * @throws IOException
     */
    public void tearDown() throws IOException {
        // deletes deck output files
        if (deck1.getOutputFile().exists()) {
            deck1.getOutputFile().delete();
        }
        if (deck2.getOutputFile().exists()) {
            deck2.getOutputFile().delete();
        }

        // deletes player output files
        if (player1.getOutputFile().exists()) {
            player1.getOutputFile().delete();
        }
        if (player2.getOutputFile().exists()) {
            player2.getOutputFile().delete();
        }

        // clears objects
        game = null;
        deck1 = null;
        deck2 = null;
        player1 = null;
        player2 = null;
    }

    // Test Methods

    @Test 
    /**
     * Tests the getCardValues method to see if it returns the values of all cards associated with the player. 
     * 
     * @throws IOException
     */
    public void testGetCardValues_ReturnsCorrectCardValues() throws IOException {
        Card card1 = new Card(1); player1.getCardsInHand().add(card1);
        Card card2 = new Card(2); player1.getCardsInHand().add(card2);
        Card card3 = new Card(3); player1.getCardsInHand().add(card3);
        Card card4 = new Card(4); player1.getCardsInHand().add(card4);

        List<Integer> expectedContents = Arrays.asList(1, 2, 3, 4);

        List<Integer> actualContents = player1.getCardValues();

        assertEquals("The deck card values are not what is expected", expectedContents, actualContents);
    }

    @Test
    /**
     * Tests the run method to make it ends the game correctly if a player has a winning hand at the
     * start. Specifically, this will check:
     * 
     * 1. The player output file has the correct text written within it
     * 2. Player variable gameInProgress should be equal to false
     * 
     * @throws IOException
     */
    public void testRun_CorrectTextinOutputFileFromImmediateWin() throws IOException {
        CardGame runGame = new CardGame();
        Deck runDeck = new Deck(1);
        Player runPlayer = new Player(1, runDeck, runDeck, runGame);
        
        runGame.setNumOfPlayers(1);

        // Ensures runPlayer has a winning hand at the start
        Card card1 = new Card(1); runPlayer.getCardsInHand().add(card1); 
        Card card2 = new Card(1); runPlayer.getCardsInHand().add(card2); 
        Card card3 = new Card(1); runPlayer.getCardsInHand().add(card3); 
        Card card4 = new Card(1); runPlayer.getCardsInHand().add(card4); 

        runPlayer.run();

        // 1
        int lineNumber = 0;
        try(Scanner scanner = new Scanner(new File("player1_output.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;

                switch (lineNumber) {
                    case 1:
                        assertEquals("First line in player output file incorrect", "player 1 initial hand: [1, 1, 1, 1]", line);
                        break;    
                    case 2:
                        assertEquals("Second line in player output file should be empty", "", line);
                        break; 
                    case 3:
                        assertEquals("Third line in player output file incorrect", "player 1 wins", line);
                        break; 
                    case 4:
                        assertEquals("Forth line in player output file incorrect", "player 1 exits", line);
                        break;
                    case 5:
                        assertEquals("Fifth line in player output file incorrect", "player 1 final hand: [1, 1, 1, 1]", line);
                        break; 
                    default:
                        fail("Additional line in output file unexpected");                        
                }
            }
        } catch (FileNotFoundException e) {
            fail("Output file not found");
        }

        // 2
        assertEquals("gameInProgress should be equal to false", false, runPlayer.getGameInProgress());
    }

    @Test
    /**
     * Tests the run method to make it ends the game correctly if a player has a winning hand after a
     * single turn. This will check:
     * 
     * 1. The player output file has the correct text written within it
     * 2. Player variable gameInProgress should be equal to false
     * 
     * @throws IOException
     */
    public void testRun_CorrectTextinOutputFileFromWinAfterDraw() throws IOException {
        CardGame runGame = new CardGame();
        Deck runDeck = new Deck(1);
        Player runPlayer = new Player(1, runDeck, runDeck, runGame);
        
        runGame.setNumOfPlayers(1);

        // Ensures runPlayer has a winning hand after a card is drawn
        Card card1 = new Card(1); runPlayer.getCardsInHand().add(card1); 
        Card card2 = new Card(2); runPlayer.getCardsInHand().add(card2); 
        Card card3 = new Card(1); runPlayer.getCardsInHand().add(card3); 
        Card card4 = new Card(1); runPlayer.getCardsInHand().add(card4); 

        // Ensures deck has four cards
        Card card5 = new Card(1); runDeck.getDeckCards().add(card5);
        Card card6 = new Card(2); runDeck.getDeckCards().add(card6);
        Card card7 = new Card(5); runDeck.getDeckCards().add(card7);
        Card card8 = new Card(7); runDeck.getDeckCards().add(card8);

        runPlayer.run();

        // 1
        int lineNumber = 0;
        try(Scanner scanner = new Scanner(new File("player1_output.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;

                switch (lineNumber) {
                    case 1:
                        assertEquals("First line in player output file incorrect", "player 1 initial hand: [1, 2, 1, 1]", line);
                        break;    
                    case 2:
                        assertEquals("Second line in player output file should be empty", "", line);
                        break; 
                    case 3:
                        assertEquals("Third line in player output file incorrect", "player 1 draws a 1 from deck 1", line);
                        break; 
                    case 4:
                        assertEquals("Forth line in player output file incorrect", "player 1 discards a 2 to deck 1", line);
                        break;
                    case 5:
                        assertEquals("Fifth line in player output file incorrect", "player 1 current hand: [1, 1, 1, 1]", line);
                        break; 
                    case 6:
                        assertEquals("Sixth line in player output file should be empty", "", line);
                        break; 
                    case 7:
                        assertEquals("Seventh line in player output file incorrect", "player 1 wins", line);
                        break; 
                    case 8:
                        assertEquals("Eighth line in player output file incorrect", "player 1 exits", line);
                        break;
                    case 9:
                        assertEquals("Ninth line in player output file incorrect", "player 1 final hand: [1, 1, 1, 1]", line);
                        break; 
                    default:
                        fail("Additional line in output file unexpected");                        
                }
            }
        } catch (FileNotFoundException e) {
            fail("Output file not found");
        }

        // 2
        assertEquals("gameInProgress should be equal to false", false, runPlayer.getGameInProgress());
    }

    @Test
    /**
     * Tests the run method to make sure it throws the IllegalStateException when player 1 draw deck
     * only has 3 cards after one turn.
     * 
     * @throws IOException
     */
    public void testRunCard_SizeVerificationCodeWorksCorrectly() throws IOException {
        CardGame runGame = new CardGame();
        Deck runDeck = new Deck(1);
        Player runPlayer = new Player(1, runDeck, runDeck, runGame);
        
        runGame.setNumOfPlayers(1);

        // Ensures runPlayer has a hand that can't win after one turn
        Card card1 = new Card(1); runPlayer.getCardsInHand().add(card1); 
        Card card2 = new Card(2); runPlayer.getCardsInHand().add(card2); 
        Card card3 = new Card(1); runPlayer.getCardsInHand().add(card3); 
        Card card4 = new Card(1); runPlayer.getCardsInHand().add(card4); 

        // Ensures deck only has three cards at the end of turn
        Card card5 = new Card(2); runDeck.getDeckCards().add(card5);
        Card card6 = new Card(5); runDeck.getDeckCards().add(card6);
        Card card7 = new Card(7); runDeck.getDeckCards().add(card7);

        // Performs method to see if a IllegalStateException is thrown
        assertThrows("It should throw an IllegalStateException", IllegalStateException.class, () -> runPlayer.run());
    }

    @Test
    /**
     * Tests the setupOutputFile method to make sure that it creates a file with the 
     * correct name, and that it is empty, even if it already has exists.
     * 
     * @throws IOException
     */
    public void testSetupOutputFile_WritesCorrectTextToOutputFile() throws IOException {
        // Performs the method to test
        try {
            player1.setupOutputFile(1);
        } catch (IOException e) {
            fail("Failed to create/overwrite output file");
        }
        // Creates a file object for the expected file
        File file = new File("player1_output.txt");

        // Assert that the file exists
        assertTrue("The file should exist after setupOutputFile method is executed.", file.exists());

        // Assert that the file is empty after method is called
        assertEquals("The file should be empty.", 0, file.length());
    }

    @Test
    /**
     * Tests the writeInitialHand method to make sure that it writes the correct
     * text to the players output file. 
     * 
     * @throws IOException
     */
    public void testWriteInitialHand_WritesCorrectTextToOutputFile() throws IOException {
        // Ensures player 1 has cards to write to their output file for test
        Card card1 = new Card(2); player1.getCardsInHand().add(card1);
        Card card2 = new Card(4); player1.getCardsInHand().add(card2);
        Card card3 = new Card(3); player1.getCardsInHand().add(card3);
        Card card4 = new Card(6); player1.getCardsInHand().add(card4);

        // Performs method to test
        try {
            player1.writeInitialHand();
        } catch (IOException e) { 
            fail("Failed to write to output file");
        }

        try (Scanner scanner = new Scanner(new File("player1_output.txt"))) {
            assertEquals("Player 1 output file contents are not correct", "player 1 initial hand: [2, 4, 3, 6]", scanner.nextLine());
        } catch (IOException e) {
            fail("Could not find output");
        }
    }

    @Test
    /**
     * Tests the writeCurrentHand method to make sure that it writes the correct
     * text to the players output file. 
     * 
     * @throws IOException
     */
    public void testWriteCurrentHand_WritesCorrectTextToOutputFile() throws IOException {
        // Ensures player 1 has cards to write to their output file for test
        Card card1 = new Card(3); player1.getCardsInHand().add(card1);
        Card card2 = new Card(1); player1.getCardsInHand().add(card2);
        Card card3 = new Card(8); player1.getCardsInHand().add(card3);
        Card card4 = new Card(4); player1.getCardsInHand().add(card4);

        // Performs method to test
        player1.writeCurrentHand();

        try (Scanner scanner = new Scanner(new File("player1_output.txt"))) {
            assertEquals("Player 1 output file contents are not correct", "player 1 current hand: [3, 1, 8, 4]", scanner.nextLine());
        } catch (IOException e) {
            fail("Could not find output");
        }
    }

    @Test
    /**
     * Tests the drawCard method to make sure that it correctly retrieves
     * a card from queue's front stored in the drawDeck and adds it to the list
     * of cardsInHand. The test will check:
     * 
     * 1. The size of cardsInHand increases by one after drawing a card.
     * 2. The card value drawn from the queue matches the value added to cardsInHand.
     * 3. The queue still has all its original cards, in order, minus the 
     * card drawn from it.
     * 4. The method writes the correct message to the player output file.
     * 
     * @throws IOException
     */
    public void testDrawCard_AddsCardToHandAndCorrectFileOutput() throws IOException {
        // Ensures player has 1 card with the content: [1]
        Card card1 = new Card(1); player1.getCardsInHand().add(card1); 

        // Stores the player hand content after method performed
        int initialHandSize = player1.getCardsInHand().size(); // Size = 1

        // Ensures deck has 2 cards with the content: [2, 3]
        Card card2 = new Card(2); deck1.getDeckCards().add(card2); 
        Card card3 = new Card(3); deck1.getDeckCards().add(card3);

        // Stores the Queue LinkedList's old values in order as a List.
        List<Integer> oldQueueList = new ArrayList<>(deck1.getCardValues());

        // Performs tested method
        try {
            player1.drawCard();
        } catch (IOException e) {
            fail("Failed to write draw card to output file");
        }

        // Stores the player hand content after method performed
        List<Integer> finalHand = player1.getCardValues();

        // 1.
        assertTrue("Drawing a card must increase the size of the hand list by 1.", finalHand.size() == initialHandSize + 1);

        // 2.
        // Finds the card added to the Player hand, comparing it to the card that was at the queue's front.
        int lastIndex = finalHand.size() - 1;
        int drawnCard = finalHand.get(lastIndex);
        assertEquals("Drawn card should be of equal value to deck's front card.", 2, drawnCard);

        // 3.
        // Stores the Queue LinkedList's new values in order as a List.
        List<Integer> newQueueList = new ArrayList<>(deck1.getCardValues());
        List<Integer> oldQueueListMinusDrawCard = new ArrayList<>(oldQueueList);
        oldQueueListMinusDrawCard.remove(0);
        assertEquals("Drawing a card from the deck should remove the card from the Deck queue's front, and preserve its existing order.", newQueueList, oldQueueListMinusDrawCard);

        // 4.
        // Expected and actual contents of the file
        String expectedContents = "player " + player1.getPlayerNumber() + " draws a " + drawnCard + " from deck " + deck1.getDeckNumber();

        String actualContents = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(player1.getOutputFile()))) {
            actualContents = reader.readLine();
        } catch (IOException e) {
            fail("Output file could not be read");
        }

        assertEquals("The file contents are incorrect.", expectedContents, actualContents);
    }

    @Test
    /**
     * Tests the discardCard method to make sure that it correctly removes an
     * integer from list the referred to as cardsInHand and adds it to the queue stored
     * in the discardDeck. The test will check:
     * 
     * 1. It removes a card that isn't preferred by the player, i.e.,
     * it has a different value to the player's index number.
     * 2. The size of cardsInHand decreases by one after discarding a card.
     * 3. The card discarded from list cardsInHand matches the last item in the
     * queue stored at discardDeck.
     * 4. The written text to the output file is correct.
     * 
     * @throws IOException
     */
    public void testDiscardCard_RemovesCardFromHandAndAddsToDeckAndCorrectFileOutput() throws IOException {
        // Player 1 hand contents: [1, 2]
        Card card1 = new Card(1); player1.getCardsInHand().add(card1);
        Card card2 = new Card(2); player1.getCardsInHand().add(card2);

        // player 1 discard deck contents: [3, 4]
        Card card3 = new Card(3); deck2.getDeckCards().add(card3);
        Card card4 = new Card(4); deck2.getDeckCards().add(card4);

        int initialHandSize = player1.getCardsInHand().size(); // size = 2

        // 1.
        List<Card> finalHand = null;
        List<Integer> expectedContents = new ArrayList<>();
        expectedContents.add(1);
        
        // Ensures it does not discard the preferred card from the players hand, by performing test 100 times
        for (int i = 0; i < 100; i++) {
            finalHand = player1.getCardsInHand();   
            player1.discardCard();  // Performs tested method

            // Checks if card discarded is the unpreferred one
            List<Integer> finalHandValues = player1.getCardValues();
            assertEquals("The discarded card had the unpreferred value (1); discarded cards should be of a prefered value.", expectedContents, finalHandValues);
            
            // Adds unpreferred card back to hand, to try again
            Card card5 = new Card(2); 
            finalHand.add(card5);
        }

        player1.discardCard(); // Ensures Player 1 final hand contents is [1]

        // 2.
        assertEquals("Discarding a card must decrease the size of the hand list by 1.", finalHand.size(), initialHandSize - 1);
        
        // 3.
        int newBackOfQueueCard = ((LinkedList<Integer>) deck2.getCardValues()).getLast();
        assertEquals("The card added to the Deck queue should be equal in value to the discarded card; 2.", 2, newBackOfQueueCard);

        // 4
        String expectedText = "player 1 discards a 2 to deck 2";

        try(Scanner scanner = new Scanner(new File("player1_output.txt"))) {            
            String actualText = scanner.nextLine().trim();
            assertEquals("The text written to the output file is incorrect", expectedText, actualText);
        } catch (IOException e) {
            fail();
        }

    }

    @Test
    /**
     * Tests the winCondition method to make sure that it correctly sends
     * a notification to the CardGame class correctly, and starts to end 
     * thread:
     *
     * 1. Successfully notifies CardGame class of its victory (by setting the value of 
     * its winningPlayerNumber attribute to the player number).
     * 2. Writes the correct message to the player's output file, stating it's
     * won, and the player's final hand. 
     * 3. Player 1 variable gameInProgress should be equal to false when player 1 
     * has a winning hand
     * 
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    public void testWinCondition_PerformsCorrectActionsForWinningPlayer() throws FileNotFoundException, InterruptedException  {
        // Verifies winningPlayerNumber's value is initially 0 (no winner)
        assertEquals("The initial value of winningPlayerNumber must be 0.", game.getWinningPlayerNumber(), 0);
        
        // Ensures Player 1 has a winner hand
        Card card1 = new Card(1); player1.getCardsInHand().add(card1); 
        Card card2 = new Card(1); player1.getCardsInHand().add(card2); 
        Card card3 = new Card(1); player1.getCardsInHand().add(card3); 
        Card card4 = new Card(1); player1.getCardsInHand().add(card4); 
        

        // Performs tested method
        try {
            player1.winCondition();
        } catch (IOException e) {
            fail("IOException occurred during winCondition: " + e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Thread was interrupted during winCondition");
        }

        // 1.
        assertNotEquals("The value of winningPlayerNumber should change (becoming non-0) on a player victory.", game.getWinningPlayerNumber(), 0);
        assertEquals("On victory, the value of winningPlayerNumber should equal the winner's player number.", game.getWinningPlayerNumber(), 1);

        // 2.
        int lineNumber = 0;
        try(Scanner scanner = new Scanner(new File("player1_output.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;

                switch (lineNumber) {
                    case 1:
                        assertEquals("First line in player output file incorrect", "player 1 wins", line);
                        break;    
                    case 2:
                        assertEquals("Second line in player output file incorrect", "player 1 exits", line);
                        break; 
                    case 3:
                        assertEquals("Third line in player output file incorrect", "player 1 final hand: [1, 1, 1, 1]", line);
                        break; 
                    default:
                        fail("Additional line in output file unexpected");                        
                }
            }
        } catch (FileNotFoundException e) {
            fail("Output file not found");
        }
        

        // 3.
        assertEquals("Boolean variable gameInProgress should be false", false, player1.getGameInProgress());
    }

    @Test
    /**
     * Tests the winCondition method to make sure that it performs the correct 
     * course of actions when a player loses. This will test:
     * 
     * 1. Successfully tells the losing player that another won by changing the 
     * winningPlayerNumber 
     * 2. Writes the correct message to the player's output file, including the 
     * winning player informing the losing player they won, and the losing player
     * final hand
     * 3. Player 2 variable gameInProgress should be equal to false when player 1 
     * has a winning hand
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void testWinCondition_PerformsCorrectActionsForLosingPlayer() throws IOException, InterruptedException {
        // Verifies winningPlayerNumber's value is initially 0 (no winner)
        assertEquals("The initial value of winningPlayerNumber must be 0.", game.getWinningPlayerNumber(), 0);
        
        // Ensures Player 1 has a winner hand
        Card card1 = new Card(1); player1.getCardsInHand().add(card1); 
        Card card2 = new Card(1); player1.getCardsInHand().add(card2); 
        Card card3 = new Card(1); player1.getCardsInHand().add(card3); 
        Card card4 = new Card(1); player1.getCardsInHand().add(card4); 

        // Ensures Player 2 does not have a winner hand
        Card card5 = new Card(2); player2.getCardsInHand().add(card5); 
        Card card6 = new Card(2); player2.getCardsInHand().add(card6); 
        Card card7 = new Card(2); player2.getCardsInHand().add(card7); 
        Card card8 = new Card(3); player2.getCardsInHand().add(card8); 

        // Performs tested method

        try {
            player1.winCondition();
        } catch (IOException e) {
            fail("IOException occurred during winCondition: " + e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Thread was interrupted during winCondition");
        }

        try {
            player2.winCondition();
        } catch (IOException e) {
            fail("IOException occurred during winCondition: " + e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Thread was interrupted during winCondition");
        }

        // 1 
        assertEquals("The value of winningPlayerNumber should equal the winner's player number.", game.getWinningPlayerNumber(), 1);

        // 2
        int lineNumber = 0;
        try(Scanner scanner = new Scanner(new File("player2_output.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;

                switch (lineNumber) {
                    case 1:
                        assertEquals("First line in player output file incorrect", "player 1 has informed player 2 that player 1 has won", line);
                        break;    
                    case 2:
                        assertEquals("Second line in player output file incorrect", "player 2 exits", line);
                        break; 
                    case 3:
                        assertEquals("Third line in player output file incorrect", "player 2 hand: [2, 2, 2, 3]", line);
                        break;      
                    default:
                        fail("Additional line in output file unexpected");                       
                }
            }
        } catch (FileNotFoundException e) {
            fail("Output file not found"); 
        }

        // 3.
        assertEquals("Boolean variable gameInProgress should be false", false, player2.getGameInProgress());
    }

    @Test
    /**
     * Tests the winCondition method to make sure that it performs the correct 
     * course of actions when no players have won. This will test:
     * 
     * 1. The CardGame class variable winningPlayerNumber does not change
     * 2. The output files for player 1 and 2 are empty
     * 3. Player 1 and 2 variable gameInProgress should be equal to true as 
     * no player has won
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void testWinCondition_PerformsCorrectActionsForNoPlayerWin() throws IOException, InterruptedException {
        // Verifies winningPlayerNumber's value is initially 0 (no winner)
        assertEquals("The initial value of winningPlayerNumber must be 0.", game.getWinningPlayerNumber(), 0);
        game.setNumOfPlayers(1);
        // Ensures Player 1 does not have a winner hand
        Card card1 = new Card(1); player1.getCardsInHand().add(card1); 
        Card card2 = new Card(3); player1.getCardsInHand().add(card2); 
        Card card3 = new Card(1); player1.getCardsInHand().add(card3); 
        Card card4 = new Card(2); player1.getCardsInHand().add(card4); 

        // Performs tested method
        try {
            player1.winCondition();
        } catch (IOException e) {
            fail("IOException occurred during winCondition: " + e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Thread was interrupted during winCondition");
        }


        // 1
        assertEquals("The value of winningPlayerNumber should equal be equal to 0", 0, game.getWinningPlayerNumber());

        // 2 
        assertEquals("There shouldn't be any text in the player 1 output file", 0, player1.getOutputFile().length());

        // 3
        assertTrue("The player 1 variable gameInProgress should be true", player1.getGameInProgress());
        
    }
}
