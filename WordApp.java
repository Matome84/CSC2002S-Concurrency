// package skeletonCodeAssgnmt2;
package com.company;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;


import java.util.Scanner;
import java.util.concurrent.*;
//model is separate from the view.

public class WordApp {
//shared variables
	static int noWords=4;
	static int totalWords;

   	static int frameX=1000;
	static int frameY=600;
	static int yLimit=480;

	static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

	static WordRecord[] words;
	static Thread[] threadArray;
	static WordMover[] wordMoverArray;
	static volatile boolean done;  //must be volatile
	static 	Score score = new Score();

	static WordPanel w;
	static JLabel caught;
	static JLabel missed;
	static JLabel scr;

	static boolean running = false;
	static boolean restart = false;
	static JFrame frame;
	static boolean shown = false;
	
	public static void setupGUI(int frameX,int frameY,int yLimit) {
		// Frame init and dimensions
    	frame = new JFrame("WordGame"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(frameX, frameY);
    	
      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
      	g.setSize(frameX,frameY);
 
    	
		w = new WordPanel(words,yLimit);
		w.setSize(frameX,yLimit+100);
	    g.add(w);
	    
	    
	    JPanel txt = new JPanel();
	    txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS)); 
	    caught =new JLabel("Caught: " + score.getCaught() + "    ");
	    missed =new JLabel("Missed:" + score.getMissed()+ "    ");
	    scr =new JLabel("Score:" + score.getScore()+ "    ");    
	    txt.add(caught);
	    txt.add(missed);
	    txt.add(scr);
    
	    //[snip]
  
	    final JTextField textEntry = new JTextField("",20);
	   textEntry.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent evt) {
	          String text = textEntry.getText();
	          //[snip]
	          (new Thread(new WordChecker(text, w, score))).start();

	          textEntry.setText("");
	          textEntry.requestFocus();
	      }
	    });
	   
	   txt.add(textEntry);
	   txt.setMaximumSize( txt.getPreferredSize() );
	   g.add(txt);
	    
	    JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS)); 
	   	JButton startB = new JButton("Start");;
		
			// add the listener to the jbutton to handle the "pressed" event
			startB.addActionListener(new ActionListener()
		    {
		      public void actionPerformed(ActionEvent e)
		      {
		    	  //[snip]
		      	// System.out.println ("Start pressed");
		      	if (running != true) {

		      		if (restart) {

		      			score.resetScore();
		      			shown = false;
		      			w.startRunning();
		      			(new Thread(w)).start();
			      		for (WordRecord word : w.getWords()) {word.resetWord();}
		      			for (WordMover mover : wordMoverArray) {mover.startRunning();}
		      			for (int i=0; i<noWords;i++) {threadArray[i] = new Thread(wordMoverArray[i]);}
		      		}
		      		if (restart==false) {restart = true;}
		      		score.resetScore();
			      	running = true;
			      	for (Thread thread : threadArray) {thread.start();}
		    	    textEntry.requestFocus();  //return focus to the text entry field
		    	}
		    	textEntry.requestFocus();
		      }
		    });
		JButton endB = new JButton("End");;
			
				// add the listener to the jbutton to handle the "pressed" event
				endB.addActionListener(new ActionListener()
			    {
			      public void actionPerformed(ActionEvent e)
			      {
			    	  //[snip]
			      	// System.out.println ("End pressed");
			      	running = false;
			      	for (WordMover mover : wordMoverArray) {mover.stopRunning();}
			      		w.stopRunning();
			      	score.resetScore();
			      }
			    });
		
		b.add(startB);
		b.add(endB);

		JButton quitB = new JButton("Quit");;
		quitB.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}
			});

		b.add(quitB);
		
		g.add(b);
    	
      	frame.setLocationRelativeTo(null);  // Center window on screen.
      	frame.add(g); //add contents to window
        frame.setContentPane(g);     
       	//frame.pack();  // don't do this - packs it into small space
        frame.setVisible(true);

		
	}

	
public static String[] getDictFromFile(String filename) {
		String [] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLength = dictReader.nextInt();
			//System.out.println("read '" + dictLength+"'");

			dictStr=new String[dictLength];
			for (int i=0;i<dictLength;i++) {
				dictStr[i]=new String(dictReader.next());
				//System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
			}
			dictReader.close();
		} catch (IOException e) {
	        System.err.println("Problem reading file " + filename + " default dictionary will be used");
	    }
		return dictStr;

	}

	public static void main(String[] args) {
    	
		//deal with command line arguments
		totalWords=Integer.parseInt(args[0]);  //total words to fall
		noWords=Integer.parseInt(args[1]); // total words falling at any point
		assert(totalWords>=noWords); // this could be done more neatly
		String[] tmpDict=getDictFromFile(args[2]); //file of words
		if (tmpDict!=null)
			dict= new WordDictionary(tmpDict);
		
		WordRecord.dict=dict; //set the class dictionary for the words.
		
		words = new WordRecord[noWords];  //shared array of current words
		
		//[snip]
			
				setupGUI(frameX, frameY, yLimit);  
		    	//Start WordPanel thread - for redrawing animation
				(new Thread(w)).start();

				int x_inc=(int)frameX/noWords;
			  	//initialize shared array of current words

				for (int i=0;i<noWords;i++) {
					words[i]=new WordRecord(dict.getNewWord(),i*x_inc,yLimit);
				}

				threadArray = new Thread[noWords];
				wordMoverArray = new WordMover[noWords];
				for (int i=0; i<noWords;i++) {wordMoverArray[i] = new WordMover(w, i, score);}
				for (int i=0; i<noWords;i++) {threadArray[i] = new Thread(wordMoverArray[i]);}
				// for (int i=0; i<noWords;i++) {threadArray[i] = new Thread(new WordMover(w, i, score));}
				// for (Thread thread : threadArray) {thread.start();}
				// (new Thread(w)).start();

				while (true) {
					caught.setText("Caught: " + score.getCaught() + "    ");
					missed.setText("Missed:" + score.getMissed()+ "    ");
					scr.setText("Score:" + score.getScore()+ "    ");
				
				if (score.getCaught()>=totalWords) {
					caught.setText("Caught: " + score.getCaught() + "    ");
					missed.setText("Missed:" + score.getMissed()+ "    ");
					scr.setText("Score:" + score.getScore()+ "    ");
					// same functionality as pressing the end button
					running = false;
			      	for (WordMover mover : wordMoverArray) {mover.stopRunning();}
		      		w.stopRunning();
		      		if (shown == false) {
			      		JOptionPane.showMessageDialog(frame, "You win!");
		      			shown = true;
		      			// score.resetScore();
		      		}
	      		}
	      	}
	}

}
