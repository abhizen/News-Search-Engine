package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;

public class AccentRemoval {
	char[] chBuffer;
	String termText;
	HashMap<String, String> codeMap;
	Token inputToken;

	AccentRemoval(Token tempToken) {
		chBuffer = tempToken.getTermBuffer();
		termText = tempToken.getTermText();
		inputToken = tempToken;
		codeMap = new HashMap<String, String>();
		codeMap.put("\\u00c0", "A");
		codeMap.put("\\u00c1", "A");
		codeMap.put("\\u00c2", "A");
		codeMap.put("\\u00c3", "A");
		codeMap.put("\\u00c4", "A");
		codeMap.put("\\u00c5", "A");
		codeMap.put("\\u00c6", "AE");
		codeMap.put("\\u00c7", "C");
		codeMap.put("\\u00c8", "E");
		codeMap.put("\\u00c9", "E");
		codeMap.put("\\u00ca", "E");
		codeMap.put("\\u00cb", "E");
		codeMap.put("\\u00cc", "I");
		codeMap.put("\\u00cd", "I");
		codeMap.put("\\u00ce", "I");
		codeMap.put("\\u00cf", "I");
		codeMap.put("\\u0132", "IJ");
		codeMap.put("\\u00d0", "D");
		codeMap.put("\\u00d1", "N");
		codeMap.put("\\u00d2", "O");
		codeMap.put("\\u00d3", "O");
		codeMap.put("\\u00d4", "O");
		codeMap.put("\\u00d5", "O");
		codeMap.put("\\u00d6", "O");
		codeMap.put("\\u00d8", "O");
		codeMap.put("\\u0152", "OE");
		codeMap.put("\\u00de", "TH");
		codeMap.put("\\u00d9", "U");
		codeMap.put("\\u00da", "U");
		codeMap.put("\\u00db", "U");
		codeMap.put("\\u00dc", "U");
		codeMap.put("\\u00dd", "Y");
		codeMap.put("\\u0178", "Y");
		codeMap.put("\\u00e0", "a");
		codeMap.put("\\u00e1", "a");
		codeMap.put("\\u00e2", "a");
		codeMap.put("\\u00e3", "a");
		codeMap.put("\\u00e4", "a");
		codeMap.put("\\u00e5", "a");
		codeMap.put("\\u00e6", "ae");
		codeMap.put("\\u00e7", "c");
		codeMap.put("\\u00e8", "e");
		codeMap.put("\\u00e9", "e");
		codeMap.put("\\u00ea", "e");
		codeMap.put("\\u00eb", "e");
		codeMap.put("\\u00ec", "i");
		codeMap.put("\\u00ed", "i");
		codeMap.put("\\u00ee", "i");
		codeMap.put("\\u00ef", "i");
		codeMap.put("\\u0133", "ij");
		codeMap.put("\\u00f0", "d");
		codeMap.put("\\u00f1", "n");
		codeMap.put("\\u00f2", "o");
		codeMap.put("\\u00f3", "o");
		codeMap.put("\\u00f4", "o");
		codeMap.put("\\u00f5", "o");
		codeMap.put("\\u00f6", "o");
		codeMap.put("\\u00f8", "o");
		codeMap.put("\\u0153", "oe");
		codeMap.put("\\u00df", "ss");
		codeMap.put("\\u00fe", "th");
		codeMap.put("\\u00f9", "u");
		codeMap.put("\\u00fa", "u");
		codeMap.put("\\u00fb", "u");
		codeMap.put("\\u00fc", "u");
		codeMap.put("\\u00fd", "y");
		codeMap.put("\\u00ff", "y");
		codeMap.put("\\uFB00", "ff");
		codeMap.put("\\uFB01", "fi");
		codeMap.put("\\uFB02", "fl");
		codeMap.put("\\uFB03", "ffi");
		codeMap.put("\\uFB04", "ffl");
		codeMap.put("\\uFB05", "ft");
		codeMap.put("\\uFB06", "st");
	}

	public String charAt(char ch) {
		// if ch>="\u00C0"
		String unicodeChar = String.format("\\u%04x", (int) ch);
		if (codeMap.get(unicodeChar) != null)
			return codeMap.get(unicodeChar).toString();
		return null;
	}

	public Token remove() {
		String temp;
		CharSequence seqSource;
		CharSequence seqReplacement;
		for (char ch : chBuffer) {
			temp = charAt(ch);
			if (temp != null) {
				seqSource = Character.toString(ch);
				seqReplacement = temp;
				termText = termText.replace(seqSource, seqReplacement);
			}
		}
		inputToken.setTermText(termText);
		return inputToken;
	}
}
