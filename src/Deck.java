import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents the decks used to store cards in the game
 */
public class Deck {

    // Attributes
    
    /**
     * Indicates the deck object’s number
     */
    private int deckNumber;

    /**
     * Stores card objects associated with the deck
     */
    private Queue<Card> deckCards;
    
    /**
     * Unique file to which the deck's output data is written.
    */
    private File outputFile;

    // Constructor

    /**
     * Deck class constructor
     *
     * @throws IOException 
     */
    public Deck(int deckNumber) throws IOException {
        //Initiates attributes
        this.deckNumber = deckNumber;
        this.deckCards = new LinkedList<>();
        this.outputFile = new File("deck" + deckNumber + "_output.txt");

        // Overwrites the file, and ensures it empty
        try (FileWriter writer = new FileWriter(outputFile, false)) { // false ensures overwriting
        
        } catch (IOException e) {
            throw new IOException("Failed to create or overwrite deck " + deckNumber + " output file: " + outputFile.getPath(), e);
        }
    }

    // Methods

    // Getter methods

    /**
     * Getter method for deckNumber
     * 
     * @returns deckNumber The deck object’s number
     */
    public int getDeckNumber() {
        return deckNumber;
    }

    /**
     * Getter method for deckCards
     * 
     * @returns deckCards Ordered card numbers associated with the deck
     */
    public Queue<Card> getDeckCards() {
        return deckCards;
    }

    /**
     * Getter method for outputFile
     * 
     * @returns outputFile    File to which the game's final contents is written
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * This method returns all the card values within the deckCards list
     * 
     * @return cardNumbers a list of integers that hold the card values of all cards
     *         in the players hand. 
     */
    public Queue<Integer> getCardValues() {
        Queue<Integer> cardValues = new LinkedList<>();
        
        // Iterates through the cards in the deck and adds their values to the list
        for (Card card : deckCards) {
            cardValues.add(card.getCardValue());
        }

        return cardValues;
    }

    // Other methods

    /**
     * Allocates a card (from the pack) to the top of the deck
     * 
     * @param cardNumber Number of the card allocated by the pack
     */
    public void addCardToTop(Card card) {
        ((LinkedList<Card>) deckCards).addFirst(card); 
    }
    
    /**
     * Allocates a card held by the LH player to the bottom of the deck/
     * back of the queue (thread-safe)
     * 
     * @param cardNumber Number of the card discarded by LH player
     */
    public synchronized void addCardToBottom(Card card) {
        deckCards.add(card);
    }

    /**
     * Draws a card from the top. It is used to give the RH player a card (thread-safe)
     * 
     * @return The card drawn from the top of the deck
     * @throws IllegalStateException
     */
    public synchronized Card removeCardFromTop() throws IllegalStateException {
        // Throws exception if deck is empty
        if (deckCards.isEmpty()) {
            throw new IllegalStateException("Failed to draw a card from deck, as deck " + deckNumber + " was empty");
        }

        Card card = deckCards.poll();

        return card;
    }

    /**
     * Writes the deck's final contents to the ouptut file (called at the end of the game)
     * 
     * @throws IOException 
     */
    public void writeFinalDeckContentsToFile() throws IOException {
        try {
            FileWriter writer = new FileWriter(outputFile, true); // 'true' appends to file
            writer.write("deck " + deckNumber + " contents: " + getCardValues());
            writer.close(); 
        } catch (IOException e) {
            throw new IOException("Failed to write final contents to deck " + deckNumber + " output file: " + outputFile.getPath(), e);
        }
    } 
}
