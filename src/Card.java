/**
 * Represents the cards used within the game
 */
public class Card {
    
    /**
     * Stores the card value of the object
     */
    private int cardValue;
    
    /**
     * Getter method for cardValue
     * 
     * @return cardValue    The value of this card
     */
    public int getCardValue() {
        return cardValue;
    }

    /**
     * Constructor for the Card class
     * 
     * @param cardValue    The value for this card
     */
    public Card(int cardValue) {
        this.cardValue = cardValue;
    }
}
