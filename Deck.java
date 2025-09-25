package solitaire-cipher;
import java.util.Random;

public class Deck {
	public static String[] suitsInOrder = {"clubs", "diamonds", "hearts", "spades"};
	public static Random gen = new Random();

	public int numOfCards; // contains the total number of cards in the deck
	public Card head; // contains a pointer to the card on the top of the deck

	/* 
	 * TODO: Initializes a Deck object using the inputs provided
	 */
	public Deck(int numOfCardsPerSuit, int numOfSuits) {
		if (numOfCardsPerSuit < 1 || numOfCardsPerSuit > 13 || numOfSuits < 1 || numOfSuits > this.suitsInOrder.length) {
			throw new IllegalArgumentException("Invalid number of suits");
		}

		this.numOfCards = numOfCardsPerSuit * numOfSuits + 2;

		Card prev = null;
		Card redJoker = new Joker("red");
		Card blackJoker = new Joker("black");

		for (int i = 0; i < numOfSuits; i++) {
			for (int j = 0; j < numOfCardsPerSuit; j++) {
				Card newCard = new PlayingCard(suitsInOrder[i], j);

				if (head == null) {
					head = newCard;
				}
				if (prev != null) {
					prev.next = newCard;
					newCard.prev = prev;
				}
				prev = newCard;
			}
		}
		prev.next = redJoker;
		redJoker.prev = prev;
		redJoker.next = blackJoker;
		blackJoker.prev = redJoker;
		blackJoker.next = head;
		head.prev = blackJoker;
	}

	/* 
	 * TODO: Implements a copy constructor for Deck using Card.getCopy().
	 * This method runs in O(n), where n is the number of cards in d.
	 */
	public Deck(Deck d) {
		if (d.head == null) return; // Edge case: Empty deck

		this.numOfCards = d.numOfCards;

		//First
		this.head = d.head.getCopy();
		Card newCurr = head;
		Card oldCurr = d.head.next;

		//Remaining cards
		while (oldCurr != d.head) { // Loop until we complete a full circle
			Card copiedCard = oldCurr.getCopy();
			newCurr.next = copiedCard;
			copiedCard.prev = newCurr;
			newCurr = copiedCard;
			oldCurr = oldCurr.next;
		}

		newCurr.next = this.head;
		this.head.prev = newCurr;
	}

	/*
	 * For testing purposes we need a default constructor.
	 */
	public Deck() {}

	/* 
	 * TODO: Adds the specified card at the bottom of the deck. This 
	 * method runs in $O(1)$. 
	 */
	public void addCard(Card c) {
		if (this.head == null) {
			this.head = c;
			this.head.next = head;
			this.head.prev = head;
		}
		else {
			Card tail = this.head.prev;
			tail.next = c;
			c.prev = tail;
			c.next = head;
			head.prev = c;
		}
		numOfCards++;
	}

	/*
	 * TODO: Shuffles the deck using the algorithm described in the pdf. 
	 * This method runs in O(n) and uses O(n) space, where n is the total 
	 * number of cards in the deck.
	 */
	public void shuffle() {
		if (this.head == null || this.numOfCards <= 1) return;

		Card[] array= new Card[numOfCards];
		Card current = this.head;

		for (int i = 0; i < this.numOfCards; i++) {
			array[i] = current;
			current = current.next;
		}
		for (int i = this.numOfCards - 1; i > 0; i--) {
			int j = gen.nextInt(i + 1);
			Card temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}

		for (int i = 0; i < this.numOfCards; i++) {
			array[i].next = array[(i + 1) % this.numOfCards];
			array[i].prev = array[(i - 1 + this.numOfCards) % this.numOfCards];
		}
		this.head = array[0];
	}

	/*
	 * TODO: Returns a reference to the joker with the specified color in 
	 * the deck. This method runs in O(n), where n is the total number of 
	 * cards in the deck. 
	 */
	public Joker locateJoker(String color) {
		Card current = this.head;

		do {
			if (current instanceof Joker && ((Joker) current).getColor().equalsIgnoreCase(color)) {
				return (Joker) current;
			}
			current = current.next;
		}
		while (current != this.head); //Iteration of the whole circle
		return null;
	}

	/*
	 * TODO: Moved the specified Card, p positions down the deck. You can 
	 * assume that the input Card does belong to the deck (hence the deck is
	 * not empty). This method runs in O(p).
	 */
	public void moveCard(Card c, int p) {
		if (p == 0 || numOfCards <= 1 || c == null)
			return;

		//break link
		c.next.prev = c.prev;
		c.prev.next = c.next;

		Card current = c;
		for (int i = 0; i < p; i++) {
			current = current.next;
		}

		c.next = current.next;
		c.prev = current;
		current.next.prev = c;
		current.next = c;

		if (this.head == c)
			this.head = this.head.next;
	}

	/*
	 * TODO: Performs a triple cut on the deck using the two input cards. You 
	 * can assume that the input cards belong to the deck and the first one is 
	 * nearest to the top of the deck. This method runs in O(1)
	 */
	public void tripleCut(Card firstCard, Card secondCard) {
    	if (head == null || firstCard == null || secondCard == null) return;

    	if (firstCard.prev == secondCard)
			return;

		Card topStart = head;
    	Card topEnd = firstCard.prev;
    	Card botStart = secondCard.next;
    	Card botEnd = head.prev;
    
    	if (topStart != firstCard) {
        	if (botStart == topStart) {
            	head = firstCard;
        	}
			else {
        		botEnd.next = firstCard;
            	firstCard.prev = botEnd;
            
            	secondCard.next = topStart;
            	topStart.prev = secondCard;
            
            	topEnd.next = botStart;
            	botStart.prev = topEnd;
            
            	head = botStart;
        	}
    	}

		else {
        	head = botStart;
			
        	botEnd.next = firstCard;
        	firstCard.prev = botEnd;
        	secondCard.next = topStart;
        	topStart.prev = secondCard;
    	}
	}


	/*
	 * TODO: Performs a count cut on the deck. Note that if the value of the 
	 * bottom card is equal to a multiple of the number of cards in the deck, 
	 * then the method should not do anything. This method runs in O(n).
	 */
	public void countCut() {
    	if (head == null || numOfCards <= 1) return;

    	int numOfCuts = head.prev.getValue() % numOfCards;
    	if (numOfCuts == 0 || numOfCuts >= numOfCards) return;

    	Card firstCardCut = head;
    	Card lastCardCut = head;
    	for (int i = 0; i < numOfCuts; i++) {
        	lastCardCut = lastCardCut.next;
    	}

    	Card newHead = lastCardCut.next;
    	Card lastCard = head.prev;

    	lastCard.prev.next = firstCardCut;
    	firstCardCut.prev = lastCard.prev;
    	lastCardCut.next = lastCard;
    	lastCard.prev = lastCardCut;

    	head = newHead;
	}

	/*
	 * TODO: Returns the card that can be found by looking at the value of the 
	 * card on the top of the deck, and counting down that many cards. If the 
	 * card found is a Joker, then the method returns null, otherwise it returns
	 * the Card found. This method runs in O(n).
	 */
	public Card lookUpCard() {
    	if (head == null) return null;

    	int countDown = head.getValue();
    	Card current = head;

    	for (int i = 0; i < countDown; i++) {
        	current = current.next;
    	}

    	if (current instanceof Joker) {
        	return null;
    	} else {
        	return current;
    	}
	}

	/*
	 * TODO: Uses the Solitaire algorithm to generate one value for the keystream 
	 * using this deck. This method runs in O(n).
	 */ //CHANGE, CHECK IDE
	public int generateNextKeystreamValue() {
    	if (head == null) return -1;

    	// Move the red joker down by 1 or 2 cards
    	Joker redJoker = locateJoker("red");
    	if (redJoker == null) return -1;
    	moveCard(redJoker, redJoker.next == head ? 2 : 1);

    	// Move the black joker down by 1, 2, or 3 cards
    	Joker blackJoker = locateJoker("black");
    	if (blackJoker == null) return -1;
    	if (blackJoker.next == head) {
        	moveCard(blackJoker, 2);
    	} else if (blackJoker.next.next == head) {
        	moveCard(blackJoker, 3);
    	} else {
        	moveCard(blackJoker, 1);
    	}

    	// Perform a triple cut around the two jokers
    	Card firstJoker = locateJoker("red");
    	Card secondJoker = locateJoker("black");
    	if (firstJoker == null || secondJoker == null) return -1;
    	tripleCut(firstJoker, secondJoker);

    	// Perform a count cut
    	countCut();

    	// Look up the next keystream value
    	Card keystreamCard = lookUpCard();
    	if (keystreamCard == null || keystreamCard instanceof Joker) {
        	return generateNextKeystreamValue(); // Recursively generate the next value if a joker is found
    	} else {
        	return keystreamCard.getValue();
    	}
	}


	public abstract class Card { 
		public Card next;
		public Card prev;

		public abstract Card getCopy();
		public abstract int getValue();

	}

	public class PlayingCard extends Card {
		public String suit;
		public int rank;

		public PlayingCard(String s, int r) {
			this.suit = s.toLowerCase();
			this.rank = r;
		}

		public String toString() {
			String info = "";
			if (this.rank == 1) {
				//info += "Ace";
				info += "A";
			} else if (this.rank > 10) {
				String[] cards = {"Jack", "Queen", "King"};
				//info += cards[this.rank - 11];
				info += cards[this.rank - 11].charAt(0);
			} else {
				info += this.rank;
			}
			//info += " of " + this.suit;
			info = (info + this.suit.charAt(0)).toUpperCase();
			return info;
		}

		public PlayingCard getCopy() {
			return new PlayingCard(this.suit, this.rank);   
		}

		public int getValue() {
			int i;
			for (i = 0; i < suitsInOrder.length; i++) {
				if (this.suit.equals(suitsInOrder[i]))
					break;
			}

			return this.rank + 13*i;
		}

	}

	public class Joker extends Card{
		public String redOrBlack;

		public Joker(String c) {
			if (!c.equalsIgnoreCase("red") && !c.equalsIgnoreCase("black")) 
				throw new IllegalArgumentException("Jokers can only be red or black"); 

			this.redOrBlack = c.toLowerCase();
		}

		public String toString() {
			//return this.redOrBlack + " Joker";
			return (this.redOrBlack.charAt(0) + "J").toUpperCase();
		}

		public Joker getCopy() {
			return new Joker(this.redOrBlack);
		}

		public int getValue() {
			return numOfCards - 1;
		}

		public String getColor() {
			return this.redOrBlack;
		}
	}

}
