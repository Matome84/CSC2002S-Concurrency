
package com.company;

public class WordMover implements Runnable {

WordPanel wp;
int wordNum;
Score score;
boolean running;

	WordMover (WordPanel wp, int wordNum, Score score) {
		this.wp = wp;
		this.wordNum = wordNum;
		this.score = score;
		this.running = true;
	}

	public String getThreadWord () { return wp.getWords()[wordNum].getWord(); }

	public void stopRunning () {
		running = false;
	}

	public void startRunning () {
		running = true;
	}

	public void run () { 

		while (running) {

			for (int delay=0; delay<wp.getWords()[wordNum].getSpeed()*500; delay++) {
				// System.out.println("word: " + wordNum + " " + wp.getWords()[wordNum].getWord() + " delay " + delay);
			}

			wp.getWords()[wordNum].drop(1);
			// repaint();
			if (wp.getWords()[wordNum].dropped()==true) {
				wp.getWords()[wordNum].resetWord();
				score.missedWord();
			}
		}

	}

}