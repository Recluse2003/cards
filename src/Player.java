import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Represents players within the game, including attributes such as their hand
 */
public class Player implements Runnable {

    // Attributes
    
    /**
 	 * Indicates the player object's number.
 	 */
    private int playerNumber;

    /**
 	 * This player's associated draw deck. Note: All player draw decks are also stored 
     * in a central collection in CardGame class.
 	 */
    private Deck drawDeck;

    /**
 	 * This player's associated discard deck. Note: All player discard decks are also stored 
     * in a central collection in CardGame class.
 	 */
    private Deck discardDeck;

    /**
 	 * This is a reference to the CardGame instance the player object is associated with. 
 	 */
    private CardGame game;

    /**
     * List of references to Card objects representing all available decks in the game.
     */
    private List<Card> cardsInHand; 

    /**
     * Unique file to which the player's output data is written.
     */
    private File outputFile;

    /**
     * Stores the thread object associated with the player.
     */
    private Thread playerThread;

    /**
     * Indicates whether the game is currently in progress.
     * Used to control the main game loop for the player.
     */
    private boolean gameInProgress;

    // Constructors

    /**
     * Constructs a new Player instance with the specified attributes.
     *
     * @param playerNumber    The unique identifier for the player. 
     * @param drawDeck        The Deck object that the player draw cards from.
     * @param discardDeck     The Deck object that the player discard cards to.
     * @throws IOException
     */
    public Player(int playerNumber, Deck drawDeck, Deck discardDeck, CardGame game) throws IOException {
        this.playerNumber = playerNumber;
        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;
        this.game = game;

        this.outputFile = setupOutputFile(playerNumber);
        this.playerThread = new Thread(this, "player" + playerNumber);
        this.gameInProgress = true;

        /**
         * Initializes the list to be used to store the player's cards. 
         */ 
        cardsInHand = new ArrayList<>();
    }

    // Methods

    // Getter methods

    /**
     * Getter method for playerNumber
     * 
     * @returns playerNumber    The Player object’s number.
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Getter method for drawDeck
     * 
     * @returns drawDeck    The Player object’s draw deck.
     */
    public Deck getDrawDeck() {
        return drawDeck;
    }

    /**
     * Getter method for drawDeck
     * 
     * @returns discardDeck    The Player object’s discard deck.
     */
    public Deck getDiscardDeck() {
        return discardDeck;
    }

    /**
     * Getter method for game
     * 
     * @returns game   The CardGame the player instance is a part of
     */
    public CardGame getGame() {
        return game;
    }

    /**
     * Getter method for cardsInHand
     * 
     * @returns cardsInHand    cards associated with the player.
     */
    public List<Card> getCardsInHand() {
        return cardsInHand;
    }

    /**
     * Getter method for outputFile
     * 
     * @returns outputFile    Unique file to which the player's output data is written.
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * Getter method for gameInProgress
     * 
     * @returns gameInProgress    Boolean which states if the game is finished
     */
    public boolean getGameInProgress() {
        return gameInProgress;
    }

    /**
     * Getter method for the player's thread.
     *
     * @return playerThread    The thread object associated with the player.
     */
    public Thread getPlayerThread() {
        return playerThread;
    }

    /**
     * This method returns all the card values within the cardsInHand list
     * 
     * @return cardValues a list of integers that hold the card values of all cards
     *         in the players hand. 
     */
    public List<Integer> getCardValues() {
        List<Integer> cardValues = new ArrayList<>();
        
        // Iterate through the cards in hand and add their values to the list
        for (Card card : cardsInHand) {
            cardValues.add(card.getCardValue());
        }

        return cardValues;
    }

    // Setter methods

    /**
     * Setter method for playerNumber
     * 
     * @param playerNumber The Player object’s number.
    */
    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    /**
     * Setter method for cardsInHand
     * 
     * @param cardsInHand card numbers associated with the player.
    */
    public void setCardsInHand(List<Card> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    /**
     * Setter method for outputFile
     * 
     * @param outputFile Unique file to which the player's output data is written.
    */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Setter method for gameInProgress
     * 
     * @param gameInProgress    Boolean storing the game's running status.
    */
    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    // Other methods

    /**
     * Creates the output file used to store the players actions, and ensures any file with the 
     * same name is wiped clean for the new game. 
     * 
     * @param playerNumber
     * @returns the outputFile of the player 
     * @throws IOException
     */
    public File setupOutputFile(int playerNumber) throws IOException {
        try {
            File outputFile = new File("player" + playerNumber + "_output.txt");
            FileWriter writer = new FileWriter(outputFile, false);
            writer.close();
            return outputFile;
        } catch (IOException e) {
            throw new IOException("Failed to create or overwrite player " + playerNumber + "output file: " + outputFile, e);
        }
    }

    /**
     * Runs the thread for the Player object.
     */
    @Override
    public void run() {
        try {
            // Writes initial hand of player to their output file
            writeInitialHand();

            // Checks if a player's starting hand meets the win condition
            winCondition();

            while (gameInProgress == true) {
                // Ensures drawing and discarding a card is treated a single atomic action
                synchronized (this) {
                    // Draws a card
                    drawCard();

                    // Discards a card
                    discardCard();
                }

                // Writes current hand of player to their output file 
                writeCurrentHand();

                // Verifies if this Player object has won
                winCondition();
                
                // Verifies no cards have been duplicated or deleted due to race conditions.
                // Used for testing and debugging purposes. 
                if (cardsInHand.size() != 4 || drawDeck.getDeckCards().size() != 4) {
                    this.gameInProgress = false;
                    System.err.println("Inconsistency detected for Player " + playerNumber);
                    System.err.println("Player's hand size: " + cardsInHand.size());
                    System.err.println("Player's hand contents: " + getCardValues());
                    System.err.println("Draw Deck's size: " + drawDeck.getDeckCards().size());
                    System.err.println("Draw Deck's contents: " + drawDeck.getCardValues());
                    throw new IllegalStateException("Card count mismatch detected for Player " + playerNumber);
                }

                // Ensures all threads have finished previous actions, before they start their next turn
                game.awaitAllPlayers();

            }     
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new RuntimeException("Error occured during player " + playerNumber + " thread.", e);
        }
    }

    /**
     * Writes the initial hand of the player to their output file.
     *
     * @throws IOException
     */
    public void writeInitialHand() throws IOException {
        try (FileWriter writer = new FileWriter(outputFile, true)) { // 'true' appends to file
            writer.write("player " + playerNumber + " initial hand: " + getCardValues() + "\n\n");
        } catch (IOException e) {
            throw new IOException("Failed to write to output file as Player " + playerNumber + " output file not found", e);
        }
    }

    /**
     * Writes the current hand of the player to their output file.
     *
     * @throws IOException
     */
    public void writeCurrentHand() throws IOException {
        try (FileWriter writer = new FileWriter(outputFile, true)) { // 'true' appends to file
            writer.write("player " + playerNumber + " current hand: " + getCardValues() + "\n\n");
        } catch (IOException e) {
            throw new IOException("Failed to write to output file as player " + playerNumber + " output file not found", e);
        }  
    }

    /**
     * Retrieves an integer from the front of the queue stored on the object referenced
     * at drawDeck, and stores it in the list, cardsInHand. Writes action to outputFile.
     * 
     * @throws IOException
     */ 
    public void drawCard() throws IOException {
        // Retrieves card from top of drawDeck
        Card cardDrawn = drawDeck.removeCardFromTop();

        // Finds number of drawDeck
        int drawDeckNumber = drawDeck.getDeckNumber();

        // Adds card/integer to cardsInHand list
        cardsInHand.add(cardDrawn);

        // Write to file 
        try (FileWriter writer = new FileWriter(outputFile, true)) { // 'true' appends to file
            writer.write("player " + playerNumber + " draws a " + cardDrawn.getCardValue() + " from deck " + drawDeckNumber + "\n"); 
        } catch (IOException e) {
            throw new IOException("Failed to write card drawed by player " + playerNumber + " to output file: " + outputFile, e);
        }
    }

    /**
     * Removes a selected integer from the list, cardsInHand, and sends it to the 
     * object referenced at discardCard, and stores it in its queue. Writes action to outputFile.
     * 
     * @param deckNumber    The Deck object that the player discards cards to.
     * @throws IOException
     */ 
    public void discardCard() throws IOException {
        // Creates a temporary list of the player's cards whose values don't equal the player number.
        List<Card> nonPreferredCards = new ArrayList<>();
        for (Card card : cardsInHand) {
            if (card.getCardValue() != playerNumber) {
                nonPreferredCards.add(card);
            }
        }
        
        // Random number generator chooses an unprefered card to discard
        Random random = new Random();
        int randomIndex = random.nextInt(nonPreferredCards.size());
        Card chosenCard = nonPreferredCards.get(randomIndex);

        // Removes chosen card from hand, and allocates it to the bottom of the discard deck
        discardDeck.addCardToBottom(chosenCard);
        cardsInHand.remove(chosenCard);

        // Finds number of discardDeck
        int discardDeckNumber = discardDeck.getDeckNumber();

        // Writes to file using a new writer object
        try (FileWriter writer = new FileWriter(outputFile, true)) {
            writer.write("player " + playerNumber + " discards a " + chosenCard.getCardValue() + " to deck " + discardDeckNumber + "\n");
        } catch (IOException e) {
            throw new IOException("Failed to write card discarded by player " + playerNumber + " to output file: " + outputFile, e);
        }
    }

    /** 
     * Used after each time a card is drawn. If the player holds four integers with the
     * same value, at cardsInHand, it will start a sequence of steps to end the game. Writes action to outputFile.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void winCondition() throws IOException, InterruptedException {
        try {
            // Verifies if player has won; Is true if the set of denominations in the player's hand has length 1. 
            Set<Integer> denominationsInHand = new HashSet<>(getCardValues());
            if (denominationsInHand.size() == 1) {
                game.setWinningPlayerNumber(playerNumber);
            }

            // this ensures all players have checked that they may have won or not, before moving on
            game.awaitAllPlayers();
            int winningPlayerNumber = game.getWinningPlayerNumber();

            if (winningPlayerNumber == playerNumber) {
                // The victory and exit messages are written to the output file, followed by the Player's final hand.
                try (FileWriter writer = new FileWriter(outputFile, true)) {
                    writer.write("player " + playerNumber + " wins" + "\n");
                    writer.write("player " + playerNumber + " exits" + "\n");
                    writer.write("player " + playerNumber + " final hand: " + getCardValues());
                } catch (IOException e) {
                    throw new IOException("Failed to write winning message to player " + playerNumber + " output file: " + outputFile, e);
                }

                // Stops game loop
                this.gameInProgress = false;

                // Writes winning player to terminal
                System.out.print("player " + playerNumber + " wins");

                // Writes final contents of player's draw deck to the deck output file
                drawDeck.writeFinalDeckContentsToFile();

            } else if (winningPlayerNumber != 0) {
                try (FileWriter writer = new FileWriter(outputFile, true)) {
                    writer.write("player " + winningPlayerNumber + " has informed player " + playerNumber + " that player " + winningPlayerNumber + " has won" + "\n");
                    writer.write("player " + playerNumber + " exits" + "\n");
                    writer.write("player " + playerNumber + " hand: " + getCardValues());
                } catch (IOException e) {
                    throw new IOException("Failed to write losing message to player " + playerNumber + " output file: " + outputFile, e);
                }

                // Stops game loop
                this.gameInProgress = false;

                // Writes final contents of player's draw deck to the deck output file
                drawDeck.writeFinalDeckContentsToFile();
            } 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
