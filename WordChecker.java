package com.company;

public class WordChecker implements Runnable {

	String text;
	WordPanel wp;
	Score score;

	WordChecker (String text, WordPanel wp, Score score) {
		this.text = text;
		this.wp = wp;
		this.score = score;
	}


	public void run () {
		for (WordRecord word : wp.getWords()) {
			if (word.matchWord(text)) {
				// System.out.println("word matched!");
				score.caughtWord(word.getWord().length());
			}
		}

	}

}