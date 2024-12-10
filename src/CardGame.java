import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Serves as the entry point of the game, and represents the functionality of the card game
 */
public class CardGame {

    // Attributes

    /**
     * List of references to Player objects representing all players in the game.
     * Used to manage each player's actions, turns, and interactions with other 
     * players.  
     */
    private List<Player> players = new ArrayList<>(); 

    /**
     * List of references to Deck objects representing all available decks in the game.
     */ 
    private List<Deck> decks = new ArrayList<>();

    /**
     * List of references to card objects used to store all cards used in the game. 
     */ 
    private List<Card> pack = new ArrayList<>();

    /**
     * Boolean representing whether or not a user input pack is valid.
     * Used for testing main method
     */ 
    private boolean packValid = false;

    /**
     * Integer that stores the number of players in this game
     */ 
    private int numOfPlayers;

    /**
     * Stores the cardGame object
     */ 
    private static CardGame cardGame;

    /**
     * Int storing the player number of the winning Player, is set as 0 if
     * there are no winners yet
     */ 
    private int winningPlayerNumber;

    /**
     * Object is used to synchronise the different player threads within the awaitAllPlayers method
     */ 
    private final Object lock = new Object();
    
    /**
     * Used by awaitAllPlayers to count the number of players that have started the method
     */
    private int playersReady = 0;

    // Methods

    // Getter methods
    
    /**
     * Getter method for pack
     * 
     * @return pack
     */
    public List<Card> getPack() {
        return pack;
    }

    /**
     * Getter method for decks
     * 
     * @return decks
     */
    public List<Deck> getDecks() {
        return decks;
    }

    /**
     * Getter method for players
     * 
     * @return players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Getter method for packValid
     * 
     * @return packValid
     */
    public boolean getPackValid() {
        return packValid;
    }

    /**
     * Getter method for numOfPlayers
     * 
     * @return numOfPlayers
     */
    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    /**
     * Getter method for cardGame
     * 
     * @return cardGame
     */
    public static CardGame getCardGame() {
        return cardGame;
    }

    /**
     * Getter method for winningPlayerNumber
     * 
     * @return winningPlayerNumber
     */
    public int getWinningPlayerNumber() {
        return winningPlayerNumber;
    }

    /**
     * Method used to get the card values of all cards stored in the pack. Used in tests
     * 
     * @param pack 
     * @return packCardValues    Holds all the card values from the cards stored in the pack
     */
    public List<Integer> getPackCardValues() {
        List<Integer> packCardValues = new ArrayList<>();
        
        // Iterate through the pack of cards and add their values to the list
        for (Card card : pack) {
            packCardValues.add(card.getCardValue());
        }
        
        return packCardValues;
    }

    // Setter methods

    /**
     * Setter method for packValid
     * 
     * @param packValid
     */
    public void setPackValid(boolean packValid) {
        this.packValid = packValid;
    }

    /**
     * Setter method for numOfPlayer
     * 
     * @param numOfPlayer
     */
    public void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    /**
     * Setter method for winningPlayerNumber
     * 
     * @param winningPlayerNumber
     */
    public void setWinningPlayerNumber(int winningPlayerNumber) {
        this.winningPlayerNumber = winningPlayerNumber;
    }

    /**
     * Setter method for pack
     * 
     * @param pack
     */
    public void setPack(List<Card> pack) {
        this.pack = pack;
    }

    /**
     * Setter method for players
     * 
     * @param players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Setter method for decks
     * 
     * @param decks
     */
    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    // Other methods

    /**
     * Creates a list of card objects that represent the cards within the game. This method also
     * ensures that if the pack file given contains a non-integer or negative integer, it 
     * will not create a pack, and the player will have to try again. 
     * 
     * In the pack file, each line with a number represents a new card within the deck. 
     * This functions works by reading and then writing these numbers into a list of 
     * integers called pack. 
     * 
     * @param packFileLocation The location of the card pack file to be used
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public void createPack(String packFileLocation) throws IOException, NumberFormatException {
        // Creates a BufferedReader object to read the pack file
        try (BufferedReader reader = new BufferedReader(new FileReader(packFileLocation))) {
            // String variable used to hold each line read from file
            String line;

            // Reads each line in the file and adds them to the list of cards defined as pack
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                int cardNumber;

                // Checks if line in file is an integer. If not, clears pack and ends method
                try {
                    cardNumber = Integer.parseInt(line.trim()); // Remove whitespace, and converts the line to int
                } catch (NumberFormatException e) {
                    System.out.println("Pack file contains a non-integer");
                    pack.clear();
                    return;
                }

                // Checks if card number is positive. If not, clears pack and ends method
                if (cardNumber < 0) {
                    System.out.println("Pack file contains a 0 or negative integer");
                    pack.clear();
                    return;
                }
                
                // Initializes card object with valid card number, and adds it to pack 
                Card card = new Card(cardNumber);
                pack.add(card);
            }
        } catch (IOException e) {
            throw new IOException("Failed to read pack file at " + packFileLocation, e);
        }
    }


    /**
     * Distributes cards to players and decks from the provided card pack.
     * Distributes 4 cards to each player in a round-robin fashion, 
     * followed by 4 cards to each deck in the same manner.
     */
    public void distributeCards() {
        // Distributes cards to players
        for (int i = 0; i < 4 * numOfPlayers; i++) {
            // Get the player based on the index
            Player player = players.get(i % numOfPlayers);
            // Assign card from the pack
            player.getCardsInHand().add(pack.get(i));
        }

        // Distribute cards to decks
        for (int i = 4 * numOfPlayers; i < 8 * numOfPlayers; i++) {
            // Get the deck based on index
            Deck deck = decks.get((i - 4 * numOfPlayers) % numOfPlayers);
            // Assign card from the pack
            deck.addCardToTop(pack.get(i));
        }
    }

    /**
     * Initializes all player objects to be used within the game. 
     * 
     * @param numOfPlayers The number of players within the game
     * @param game         The game instance
     * @throws IOException
     */
    public void createPlayers(int numOfPlayers, CardGame game) throws IOException {
        // Creating players from 1 up to numOfPlayers
        for (int i = 1; i <= numOfPlayers; i++) {
            // Finds the draw deck for the current player
            Deck drawDeck = null;
            for (Deck deck : decks) {
                if (deck.getDeckNumber() == i) {
                    drawDeck = deck;
                    break;
                }
            }
            
            // Determine the discard deck number for the current player
            int discardDeckNumber;
            if (i < numOfPlayers) {
                discardDeckNumber = i + 1;
            } else {
                discardDeckNumber = 1;
            }

            // Find the discard deck for the current player
            Deck discardDeck = null;
            for (Deck deck : decks) {
                if (deck.getDeckNumber() == discardDeckNumber) {
                    discardDeck = deck;
                    break;
                }
            }

            // Create a new player instance with there player number, and the decks found above
            Player player = new Player(i, drawDeck, discardDeck, game);

            // Adds new player instance to the list of players
            players.add(player);
        }      
    } 

    /**
     * Initializes all deck objects to be used within the game. 
     * 
     * @param numOfPlayers The number of players within the game
     * @throws IOException 
     */
    public void createDecks(int numOfPlayers) throws IOException {
        try { 
            // Creates amount of decks, equal to numOfPlayers
            for (int i = 1; i <= numOfPlayers; i++) {
                Deck deck = new Deck(i); 
                decks.add(deck);
            }
        } catch (IOException e) {
            throw new IOException("Failed to create required decks, due to file creation error", e);
        }
    } 

    /**
     * Starts the threads for all players to enable simultaneous gameplay.
     */
    public void playerThreadsStart() {
        for (Player player : players) {
            Thread playerThread = player.getPlayerThread();
            playerThread.start(); 
        }
    }

    /**
     * This method ensures synchronization among all player threads at key points during the
     * game, by making sure certain variables are correctly updated and specific actions are
     * completed before proceeding. This method works using a shared, synchronized object as
     * a lock. By ensuring only one player can access the method at a time, a thread is told 
     * to wait if all other threads have not reached this method. When the last thread 
     * increases the counter to the required amount, all threads in the synchronized object
     * are notified to continue using the notifyAll().
     * 
     * @throws InterruptedException
     */
    public void awaitAllPlayers() throws InterruptedException {
        synchronized (lock) {
            try {
                playersReady++;
                if (playersReady == numOfPlayers) {
                    lock.notifyAll();
                    playersReady = 0;
                } else if (winningPlayerNumber == 0){
                    lock.wait();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Main entry point of the CardGame program.
     * 
     * @param args
     * @throws NumberFormatException
     * @throws IOException
     */
    public static void main(String[] args) throws NumberFormatException, IOException {
        // Creates a scanner object. Used for allowing user input
        Scanner scanner = new Scanner(System.in);

        cardGame = new CardGame();
        int numOfPlayers = 0;

        // Asks user for number of players. Loops until valid number is given
        while (numOfPlayers < 1) {
            System.out.print("Please enter the number of players: ");
            try {
                numOfPlayers = Integer.parseInt(scanner.nextLine()); // Tries to parse the input into an integer
                if (numOfPlayers > 0) {
                    cardGame.setNumOfPlayers(numOfPlayers);
                } else {
                    System.out.println("Number of players need to be greater than 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Number of players need to be a positive integer");
            }
        }

        // Initialize packValid to track the validity of the pack
        cardGame.setPackValid(false);

        // Loop until a valid pack is loaded
        while (!cardGame.getPackValid()) {
            String packFileLocation = "";
            boolean fileValid = false;
            
            // Loops until a file is provided.  
            while (!fileValid) {
                System.out.print("Please enter location of pack to load: ");
                packFileLocation = scanner.nextLine();

                // Checks if the file exists
                File file = new File(packFileLocation);
                if (!file.exists() || !file.isFile()) {
                    System.out.println("The file path provided does not lead to a file, please try again.");
                } else {
                    fileValid = true; // valid file path entered
                }
            }

            // If pack is invalid, Pack variable will be empty
            cardGame.createPack(packFileLocation);

            // Check if the size of the created pack is valid. Must be 8n, where n is number of players
            if (cardGame.getPack().size() != (numOfPlayers * 8)) {
                // If pack is invalid, informs the user and clears the pack for another attempt
                System.out.print("Pack given does not meet the required amount of cards for the number of players, please try again. \n");
                cardGame.getPack().clear(); 
            } else {
                // If pack is valid, it informs the user, and exits the loops
                System.out.print("Pack is valid. \n");
                cardGame.setPackValid(true);
                scanner.close();
            }
        }

        // Creates required number of decks
        cardGame.createDecks(numOfPlayers);

        // Creates required number of players
        cardGame.createPlayers(numOfPlayers, cardGame);

        // Distributes 4 cards to each player, then 4 to each deck in round robin fashion.
        cardGame.distributeCards();

        // Starts the game by starting the player threads. 
        cardGame.playerThreadsStart();
    }
}
