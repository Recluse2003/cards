import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/*
* Testing class for CardGame
*/
public class CardGameTest {

    private CardGame game;

    private Player player1;
    private Player player2;

    private Deck deck1;
    private Deck deck2;

    
    @Before
    /**
     * Constructs a test CardGame instance alongside test Decks and Players with the 
     * specified attributes.
     * Used to test CardGame methods.
     * 
     * @throws IOException
     */
    public void setup() throws IOException {
        // Sets up CardGame object
        game = new CardGame();

        // Sets up player 1 draw deck, and player 2 discard deck objects
        deck1 = new Deck(1);

        deck2 = new Deck(2);        

        // Sets player 1 and 2 objects
        player1 = new Player(1, deck1, deck2, game);

        player2 = new Player(2, deck2, deck1, game);

        // Assigns decks to CardGame game object's decks list
        Deck[] deckArray = {deck1, deck2};
        List<Deck> decks = Arrays.asList(deckArray);
        game.setDecks(decks);
        
        // Assigns player to CardGame game object's players list
        Player[] playerArray = {player1, player2};
        List<Player> players = Arrays.asList(playerArray);
        game.setPlayers(players);

        game.setNumOfPlayers(2);

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

    @Test 
    /**
     * Tests the getPackCardValues method to see if it returns all card values in a pack. 
     * 
     * @throws IOException
     */
    public void testGetPackCardValues_ReturnsCorrectCardValues() throws IOException {
        game.createPack("validTestPack.txt");
        List<Integer> actualContents = game.getPackCardValues();
        List<Integer> expectedContents = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

        assertEquals("The pack card values are not what is expected", expectedContents, actualContents);
    }

    @Test
    /** 
     * Test the createPack method to make sure that it creates a pack with the correct 
     * contents. This test will check:
     * 
     * 1. List of ints variable called pack is empty before method is called
     * 2. pack is empty if the file given contains a non-integer
     * 3. pack is empty if the file given contains a negative integer
     * 4. pack has the correct contents if the file is correct
     * 
     * @throws IOException
     */
    public void testCreatePack_PackContentsAreCorrect() throws IOException {
        // 1 
        assertEquals("Pack is not empty before createPack method is called", 0, game.getPack().size());

        // 2 
        game.createPack("invalidTestPack1.txt");
        assertEquals("Pack is not empty when given a file with a non-integer", 0, game.getPack().size());

        // 3
        game.createPack("invalidTestPack2.txt");
        assertEquals("Pack is not empty when given a file with a 0 or negative integer", 0, game.getPack().size());

        // 4
        game.createPack("validTestPack.txt");
        assertEquals("Pack is not empty when given a file with a 0 or negative integer", Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), game.getPackCardValues());
    }


    @Test
    /** 
     * Tests the distributeCards method to make sure it distributes cards correctly to
     * players and decks. This will test:
     * 
     * 1. The players' hands have the correct cards after distribution
     * 2. The decks have the correct cards after distribution 
     * 
     * @throws IOException
     */
    public void testDistributeCards_CardsDistributedCorrectly() throws IOException {
        // Assigns fake test pack to CardGame game object
        game.createPack("distributionTestPack.txt"); 

        // Performs method to be test
        game.distributeCards();

        // 1 
        List<Integer> player1ExpectedHand = Arrays.asList(1, 3, 1, 3);
        List<Integer> player2ExpectedHand = Arrays.asList(2, 4, 2, 4);

        assertEquals("The cards in the player 1 hand is not the expected result", player1ExpectedHand, player1.getCardValues());
        assertEquals("The cards in the player 2 hand is not the expected result", player2ExpectedHand, player2.getCardValues());

        // 2 
        List<Integer> deck1ExpectedHand = Arrays.asList(3, 1, 3, 1);
        List<Integer> deck2ExpectedHand = Arrays.asList(4, 2, 4, 2);

        assertEquals("The cards in the deck 1 queue is not the expected result", deck1ExpectedHand, deck1.getCardValues());
        assertEquals("The cards in the deck 2 queue is not the expected result", deck2ExpectedHand, deck2.getCardValues());
    }

    @Test 
    /**
     * Tests the createDecks method. This will test:
     * 
     * 1. The correct number of decks were created
     * 2. Each deck instance is assigned the correct deck number
     * 
     * @throws IOException
     */
    public void testCreateDecks_DecksCreatedWithCorrectNumbers() throws IOException {
        // Seperate CardGame instance to ensure the correct number of decks
        CardGame testGame = new CardGame();
        testGame.setNumOfPlayers(4);

        testGame.createDecks(4);

        // 1
        assertEquals("The number of decks created was not correct", 4, testGame.getDecks().size());

        // 2
        assertEquals("Deck 1 did not have the correct number", 1, testGame.getDecks().get(0).getDeckNumber());
        assertEquals("Deck 2 did not have the correct number", 2, testGame.getDecks().get(1).getDeckNumber());
        assertEquals("Deck 3 did not have the correct number", 3, testGame.getDecks().get(2).getDeckNumber());
        assertEquals("Deck 4 did not have the correct number", 4, testGame.getDecks().get(3).getDeckNumber());
    }

    @Test
    /**
     * Tests the createPlayers method. This will test: 
     * 
     * 1. The correct number of players were created
     * 2. Each player instance is assigned the correct player number
     * 3. Each player instance is assigned the correct draw deck
     * 4. Each player instance is assigned the correct discard deck
     * 5. Each player instance game variable holds the correct CardGame
     *    they a part of
     * 
     * @throws IOException
     */
    public void testCreatePlayers_PlayersCreatedWithCorrectAttributes() throws IOException {
        CardGame testGame = new CardGame();
        testGame.setNumOfPlayers(3);

        // Creates test decks
        Deck testDeck1 = new Deck(1);
        Deck testDeck2 = new Deck(2);    
        Deck testDeck3 = new Deck(3);   

        // Assigns testDecks to CardGame game object
        Deck[] deckArray = {testDeck1, testDeck2, testDeck3};
        List<Deck> decks = Arrays.asList(deckArray);
        testGame.setDecks(decks);

        // Performs method to test
        try {
            testGame.createPlayers(3, testGame);
        } catch (IOException e) {
            fail("Failed to create/overwrite output files to player objects during createPlayers method");
        }

        // 1
        assertEquals("The number of players were not created", 3, testGame.getPlayers().size());

        // 2 
        assertEquals("Player 1 did not have the correct number", 1, testGame.getPlayers().get(0).getPlayerNumber());
        assertEquals("Player 2 did not have the correct number", 2, testGame.getPlayers().get(1).getPlayerNumber());
        assertEquals("Player 3 did not have the correct number", 3, testGame.getPlayers().get(2).getPlayerNumber());

        // 3 
        assertEquals("Player 1 did not have the correct draw deck", testDeck1, testGame.getPlayers().get(0).getDrawDeck());
        assertEquals("Player 2 did not have the correct draw deck", testDeck2, testGame.getPlayers().get(1).getDrawDeck());
        assertEquals("Player 3 did not have the correct draw deck", testDeck3, testGame.getPlayers().get(2).getDrawDeck());

        // 4
        assertEquals("Player 1 did not have the correct discard deck", testDeck2, testGame.getPlayers().get(0).getDiscardDeck());
        assertEquals("Player 2 did not have the correct discard deck", testDeck3, testGame.getPlayers().get(1).getDiscardDeck());
        assertEquals("Player 3 did not have the correct discard deck", testDeck1, testGame.getPlayers().get(2).getDiscardDeck());

        // 5
        assertEquals("Player 1 did not have the correct CardGame instance", testGame, testGame.getPlayers().get(0).getGame());
        assertEquals("Player 2 did not have the correct CardGame instance", testGame, testGame.getPlayers().get(1).getGame());
        assertEquals("Player 3 did not have the correct CardGame instance", testGame, testGame.getPlayers().get(2).getGame());
    }

    @Test
    /** 
     * Tests the playerThreadsStart method to make sure that it does not attempt to start threads that are already started,
     * and that it successfully starts a thread. Specifically, this will test:
     * 
     * 1. The thread is not alive before the method is called
     * 2. The thread is alive after the start method is called
     */
    public void testPlayerThreadStart_ThreadsAreStartedSuccessfully() {
        for (Player player : game.getPlayers()) {
            assertFalse("Player " + player.getPlayerNumber() + " thread is alive before the start method is called", player.getPlayerThread().isAlive());
        }

        // Method to test is called
        game.playerThreadsStart();
        
        for (Player player : game.getPlayers()) {
            assertTrue("Player " + player.getPlayerNumber() + " thread is not alive after the start method is called", player.getPlayerThread().isAlive());
        }
    }

    @Test
    /** 
     * Tests the main method. 
     * Note the file testPack.txt only has the cards required for player 1 to win. This tests:
     * 
     * 1. User input for number of players is correctly assigned as a variable.
     * 2. Checks packValid is correctly set as true if the pack size exceeds eight times the number of players.
     * 3. Prints the correct messages to the terminal 
     * 
     * @throws NumberFormatException
     * @throws IOException
     * @throws InterruptedException
     */
    public void testMain_CorrectInputsStartsGame() throws NumberFormatException, IOException, InterruptedException {
        // Test user input for no. of players, and a valid test pack.
        System.setIn(new ByteArrayInputStream("3\ntestPack.txt\n".getBytes()));

        // Creates a ByteArrayOutputStream to capture the output from System.out
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Saves the orginal System.out to return later, and ensures other tests aren't affected
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outputStream));


        try {
            // Runs method
            CardGame.main(new String[]{});
            CardGame cardGameForMainTesting = CardGame.getCardGame();

            // Make sure method is performed fully before checking output
            Thread.sleep(1000);

            // 1
            int numOfPlayers = cardGameForMainTesting.getNumOfPlayers();
            assertEquals("The simulated user input for number of players (3) was not correctly stored as a variable", 3, numOfPlayers);
            
            // 2
            assertEquals("The size of the pack was incorrectly flagged as invalid", true, cardGameForMainTesting.getPackValid());

            // 3
            String output = outputStream.toString();
            assertTrue("Output should prompt for the number of players", output.contains("Please enter the number of players:"));
            assertTrue("Output should indicate valid pack", output.contains("Pack is valid."));
            assertTrue("Output should indicate player 1 wins", output.contains("player 1 wins"));

        } finally {
            // ensures system output does not affect other tests
            System.setOut(originalSystemOut);
        }
    }

    @Test
    /** 
     * Tests the main method to ensure it correctly allows the user to input invalid number of players, 
     * and then allow the input of a valid number of players, and does not crash program if given 
     * invalid number of players. This will test
     * 
     * 1. User input for number of players is correctly assigned as a variable.
     * 2. Prints the correct messages to the terminal when given invalid numbers for number of players. 
     * 3. Checks if packValid is set as true, even when invalid numbers were given before a valid number of 
     * players were provided.
     * 
     * @throws NumberFormatException
     * @throws IOException
     * @throws InterruptedException
     */
    public void testMain_InvalidPlayerNumInputsAreHandledCorrectly() throws NumberFormatException, IOException, InterruptedException {
        // Test user input for no. of players, then an invalid file path, invalid test pack
        // with a non-int, then another with a negative int, and finally a valid test pack.
        System.setIn(new ByteArrayInputStream("r\n-2\n3\ntestPack.txt\n".getBytes()));

        // Creates a ByteArrayOutputStream to capture the output from System.out
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Saves the orginal System.out to return later, and ensure other tests aint affected
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outputStream));


        try {

            // Runs method
            CardGame.main(new String[]{});
            CardGame cardGameForMainTesting = CardGame.getCardGame();

            // Make sure method is performed fully before checking output
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } 

            // 1
            int numOfPlayers = cardGameForMainTesting.getNumOfPlayers();
            assertEquals("The simulated user input for number of players (3) was not correctly stored as a variable", 3, numOfPlayers);
            
            // 2
            String output = outputStream.toString();
            assertTrue("Output should prompt for the number of players", output.contains("Please enter the number of players:"));
            assertTrue("Output should indicate invalid input for negative number of players", output.contains("Number of players need to be greater than 0"));
            assertTrue("Output should indicate invalid input for non-integer number of players", output.contains("Number of players need to be a positive integer"));
            assertTrue("Output should indicate valid pack", output.contains("Pack is valid."));
            assertTrue("Output should indicate player 1 wins", output.contains("player 1 wins"));

            // 3
            assertEquals("The size of the pack was incorrectly flagged as invalid", true, cardGameForMainTesting.getPackValid());
            
        } finally {
            // ensures system output does not affect other tests
            System.setOut(originalSystemOut);
        }
    }

    @Test
    /** 
     * Tests the main method to ensure it correctly allows the user to input invalid packs, and then allows 
     * the user to input a valid pack. This will test
     * 
     * 1. User input for number of players is correctly assigned as a variable.
     * 2. Prints the correct messages to the terminal when an invalid pack and invalid file path is given. 
     * 3. Checks if packValid is set as true, even when invalid packs were given before a valid file path.
     * 
     * @throws NumberFormatException
     * @throws IOException
     */
    public void testMain_InvalidFilePathInputsAreHandledCorrectly() throws NumberFormatException, IOException {
        // Test user input for no. of players, then an invalid file path, invalid test pack
        // with a non-int, then another with a negative int, and finally a valid test pack.
        System.setIn(new ByteArrayInputStream("3\nnotAPath\ninvalidTestPack1.txt\ninvalidTestPack2.txt\ntestPack.txt\n".getBytes()));

        // Creates a ByteArrayOutputStream to capture the output from System.out
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Saves the orginal System.out to return later, and ensure other tests aint affected
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {

            // Runs method
            CardGame.main(new String[]{});
            CardGame cardGameForMainTesting = CardGame.getCardGame();

            // Make sure method is performed fully before checking output
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } 

            // 1
            int numOfPlayers = cardGameForMainTesting.getNumOfPlayers();
            assertEquals("The simulated user input for number of players (3) was not correctly stored as a variable", 3, numOfPlayers);
            
            // 2
            String output = outputStream.toString();
            assertTrue("Output should prompt for the number of players", output.contains("Please enter the number of players:"));
            assertTrue("Output should prompt for the pack path file", output.contains("Please enter location of pack to load: "));
            assertTrue("Output should indicate invalid file path", output.contains("The file path provided does not lead to a file, please try again."));
            assertTrue("Output should indicate invalid pack, that contains a non-integer", output.contains("Pack file contains a non-integer"));
            assertTrue("Output should indicate invalid pack, that contains an interger less than 1", output.contains("Pack file contains a 0 or negative integer"));
            assertTrue("Output should indicate valid pack", output.contains("Pack is valid."));
            assertTrue("Output should indicate player 1 wins", output.contains("player 1 wins"));

            // 3
            assertEquals("The size of the pack was incorrectly flagged as invalid", true, cardGameForMainTesting.getPackValid());

        } finally {
            System.setOut(originalSystemOut);
        }
    }
}
