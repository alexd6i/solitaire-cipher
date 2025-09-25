package solitaire-cipher;

public class SolitaireCipher {
	public Deck key;

	public SolitaireCipher (Deck key) {
		this.key = new Deck(key); // deep copy of the deck
	}

	/* 
	 * TODO: Generates a keystream of the given size
	 */
	public int[] getKeystream(int size) {
		int[] keystream = new int[size];

		if (size != 0) {
			for (int i = 0; i < size; i++) {
				keystream[i] = key.generateNextKeystreamValue();
			}
			return keystream;
		}
		return null;
	}

	/* 
	 * TODO: Encodes the input message using the algorithm described in the pdf.
	 */
	public String encode(String msg) {
		if (msg == null) return null;
		String stripedMsg = msg.replaceAll("[^a-zA-Z]", "").toUpperCase();
		int[] keystream = getKeystream(stripedMsg.length());

		String encoded = "";
		for (int i = 0; i < stripedMsg.length(); i++) {
			char base = 'A';
			char c = (char) (base + (stripedMsg.charAt(i) - base + keystream[i]) % 26);
			encoded += c;
		}
		return encoded;
	}

	/* 
	 * TODO: Decodes the input message using the algorithm described in the pdf.
	 */
	public String decode(String msg) {
        if (msg == null) {
            return null;
        }

        // Remove non-alphabetic characters and convert to uppercase
        msg = msg.replaceAll("[^a-zA-Z]", "").toUpperCase();

        // Check if the message is empty after processing
        if (msg.isEmpty()) {
            return "";
        }

        // Generate keystream
        int[] keystream = getKeystream(msg.length());

        // Decode the message
        char[] decodedChars = new char[msg.length()];
        for (int i = 0; i < msg.length(); i++) {
            int charValue = msg.charAt(i) - 'A' + 1; // A=1, B=2, ..., Z=26
            int decodedValue = (charValue - keystream[i] + 25) % 26; // Wrap around using modulo 26
            if (decodedValue < 0) {
                decodedValue += 26; // Ensure positive value
            }
            char decodedChar = (char) (decodedValue + 'A'); // Convert back to char
            decodedChars[i] = decodedChar;
        }

        return new String(decodedChars);
    }
}
