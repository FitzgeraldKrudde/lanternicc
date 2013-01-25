package lantern;
/*
*  Copyright (C) 2010 Michael Ronald Adams.
*  All rights reserved.
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
*  This code is distributed in the hope that it will
*  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  General Public License for more details.
*/

import java.awt.*;
//import java.awt.event.*;
import javax.swing.*;
//import javax.swing.JDialog;
import java.io.*;
import java.net.*;
import java.lang.Thread.*;
//import java.applet.*;
//import javax.swing.GroupLayout.*;
//import javax.swing.colorchooser.*;
//import javax.swing.event.*;
import java.lang.Integer;
import javax.swing.text.*;
//import java.awt.geom.*;
//import java.awt.image.BufferedImage;
//import java.applet.*;
//import java.awt.event.*;
//import java.awt.image.*;
//import javax.imageio.ImageIO;
import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.StringTokenizer;
//import java.util.concurrent.locks.*;
//import java.util.Timer;
//import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
//import javax.swing.event.ChangeEvent.*;



class runningengine implements Runnable
{
channels sharedVariables;
int BoardIndex;
JTextPane [] gameconsoles;
gamestuff gameData;
String pretext="";
runningengine(channels sharedVariables1, int board, JTextPane [] gameconsoles1, gamestuff gameData1)
{
	gameconsoles=gameconsoles1;
	sharedVariables=sharedVariables1;
	BoardIndex=board;
	gameData=gameData1;
}


void sendToEngine(String output)
{
	byte [] b2 = new byte[2500];
	try {
		for(int a=0; a< output.length(); a++)
	b2[a]=(byte) output.charAt(a);
	sharedVariables.engineOut.write(b2,0, output.length());
sharedVariables.engineOut.flush();

//engineOut.write(b2,0, output.length());
//engineOut.flush();
}
catch(Exception e) {}
}


Process engine;
void sendWildVariant()
{
/*
wildcastle	Shuffle chess where king can castle from d file
nocastle	Shuffle chess with no castling at all
fischerandom	Fischer Random
bughouse	Bughouse, ICC/FICS rules
crazyhouse	Crazyhouse, ICC/FICS rules
losers	Win by losing all pieces or getting mated (ICC)
suicide	Win by losing all pieces including king, or by having fewer pieces when one player has no legal moves (FICS)
giveaway	Win by losing all pieces including king, or by having no legal moves (ICC)
twokings	Weird ICC wild 9
kriegspiel	Kriegspiel (engines not supported)
atomic	Atomic
3check	Win by giving check 3 times
*/


if(sharedVariables.mygame[BoardIndex].wild == 28)
sendToEngine("variant shatranj\n");
else if(sharedVariables.mygame[BoardIndex].wild == 27)
sendToEngine("variant atomic\n");
else if(sharedVariables.mygame[BoardIndex].wild == 26)
sendToEngine("variant giveaway\n");
else if(sharedVariables.mygame[BoardIndex].wild == 25)
sendToEngine("variant 3check\n");
else if(sharedVariables.mygame[BoardIndex].wild == 23)
sendToEngine("variant crazyhouse\n");
else if(sharedVariables.mygame[BoardIndex].wild == 22)
sendToEngine("variant fischerandom\n");
else if(sharedVariables.mygame[BoardIndex].wild == 17)
sendToEngine("variant losers\n");

}
void intializeNewEngineGame()
{
	sendToEngine("new\n");
	sendToEngine("level 0 1 1\n");
	sendToEngine("post\n");
sendToEngine("hard\n");
sendWildVariant();
	sendToEngine("force\n");
	sendToEngine("analyze\n");

for(int a=0; a< sharedVariables.mygame[BoardIndex].engineTop; a++)// if they start analyzing in the middle of an examined game
sendToEngine(sharedVariables.mygame[BoardIndex].getEngineMove(a));
//sendToEngine(".\n");

//sendToEngine("e4\n");
}
OutputStream engineOut;

public void run()
	{


try {
//InputStream is= sharedVariables.engine.getInputStream();

			 		Runtime rt;
			 		rt = Runtime.getRuntime();
			 	//sharedVariables.engine = rt.exec(file.toString());
			 	engine = rt.exec(sharedVariables.engineFile.toString());

			 	sharedVariables.engineOn = true;
if(sharedVariables.uci == false)
runWinboard();
else
runUci();


} // end try
catch(Exception e)
{ }




}// end method


void runWinboard()
{
try {

int go=1;
InputStream is= engine.getInputStream();

byte [] b = new byte[15000];


 InputStreamReader converter = new InputStreamReader(is);
BufferedReader in = new BufferedReader(converter);
//sharedVariables.engineOut=sharedVariables.engine.getOutputStream();
sharedVariables.engineOut=engine.getOutputStream();
//engineOut=engine.getOutputStream();
sendToEngine("xboard\nprotover 2\n");




//sendToEngine("move e2e4\n");
//sendToEngine("move d7d5\n");
String text="";
do {

if(in.ready())
text=in.readLine();


if(text.contains("feature"))
{
	int i=0, k=-1;
	while(i>-1)
	{

	i=text.indexOf(" ", i);
	if(i>-1)
	{
		int j=text.indexOf("=", i);
		if(j>-1)
		{
				// accept feature
			String temp = "";
			temp=text.substring(i+1, j);
			//if(temp.contains("ping") || temp.contains("sans"))
			//sendToEngine("rejected " + temp + "\n");
			//else
			sendToEngine("accepted " + temp + "\n");
			i=j;

		}
	}

	}


}

	if(text.contains("done"))
	intializeNewEngineGame();

myoutput tosend = new myoutput();
try {
tosend=sharedVariables.engineQueue.poll();// we look for data from other areas of the program
if(tosend!=null)
{
	sendToEngine(tosend.data);
	if(tosend.data.contains("quit"))
	go=0;
}
}
catch(Exception e) {

}
if(text.length() > 0 && !text.startsWith("#") && !text.startsWith("stat"))
{

try {
//StyledDocument doc = sharedVariables.mygamedocs[BoardIndex];
 StyledDocument doc = sharedVariables.engineDoc;

doc.insertString(doc.getEndPosition().getOffset(), text + "\n", null);
for(int a=0; a<sharedVariables.openBoardCount; a++)
if(sharedVariables.gamelooking[a]==BoardIndex)
{
  
 if((sharedVariables.mygame[sharedVariables.gamelooking[a]].state == sharedVariables.STATE_EXAMINING || sharedVariables.mygame[sharedVariables.gamelooking[a]].state == sharedVariables.STATE_OBSERVING) && sharedVariables.engineOn == true)
 if(sharedVariables.mygame[sharedVariables.gamelooking[a]].clickCount %2 == 0)
setEngineDoc(doc, a);
}

}
catch(Exception e)
{}
}// end if








Thread.sleep(35);
}
while(go==1);
}// end try
catch(Exception e){}
}// end run winboard

void setEngineDoc(StyledDocument doc, int a)
{
gameconsoles[a].setStyledDocument(doc);
gameconsoles[a].setFont(sharedVariables.analysisFont);
gameconsoles[a].setForeground(sharedVariables.analysisForegroundColor);
gameconsoles[a].setBackground(sharedVariables.analysisBackgroundColor);

}
void runUci()
{
try {

int go=1;
InputStream is= engine.getInputStream();

byte [] b = new byte[15000];


 InputStreamReader converter = new InputStreamReader(is);
BufferedReader in = new BufferedReader(converter);
//sharedVariables.engineOut=sharedVariables.engine.getOutputStream();
sharedVariables.engineOut=engine.getOutputStream();
//engineOut=engine.getOutputStream();

pgnWriter pgnGetter = new pgnWriter();


int stage=0;

//sendToEngine("move e2e4\n");
//sendToEngine("move d7d5\n");
String text="";
do {

if(in.ready())
text=in.readLine();


if(stage == 0)
{
	sendToEngine("uci\n");
	sendToEngine("setoption UCI_ShowCurrLine 1\n");
	stage++;
}

if(stage == 1 && text.contains("uciok"))
{
	sendToEngine("isready\n");
	stage++;

}
if(stage == 2 && text.contains("readyok"))
{


	sendToEngine("setoption name UCI_AnalyseMode value true\n");
if(sharedVariables.mygame[BoardIndex].engineFen.length()>2)
{

    sendToEngine("position fen " + sharedVariables.mygame[sharedVariables.gamelooking[BoardIndex]].engineFen + "\n");
	writeOut("position fen " + sharedVariables.mygame[sharedVariables.gamelooking[BoardIndex]].engineFen + "\n");

}
else
{
  sendToEngine("position startpos\n");
}
	sendToEngine("go infinite\n");

// if they start analyzing in the middle of an examined game
writeOut("Engine Top is " + sharedVariables.mygame[BoardIndex].engineTop);
if(sharedVariables.mygame[BoardIndex].engineTop > 0)
{
	sendToEngine("stop\n");
	writeOut("stop\n");

if(sharedVariables.mygame[BoardIndex].engineFen.length()>2)
{
   if(!sharedVariables.mygame[sharedVariables.gamelooking[BoardIndex]].engineFen.startsWith("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"))
   sendToEngine("ucinewgame\n");
    sendToEngine("position fen " + sharedVariables.mygame[sharedVariables.gamelooking[BoardIndex]].engineFen + sharedVariables.mygame[BoardIndex].getUciMoves());
	writeOut("position fen " + sharedVariables.mygame[sharedVariables.gamelooking[BoardIndex]].engineFen + sharedVariables.mygame[BoardIndex].getUciMoves());

}
else
{
    sendToEngine("position startpos" + sharedVariables.mygame[BoardIndex].getUciMoves());
	writeOut("position startpos" + sharedVariables.mygame[BoardIndex].getUciMoves());
}


	sendToEngine("go infinite\n");
	writeOut("go infinite\n");
	writeOut("engine fen is " + sharedVariables.mygame[BoardIndex].engineFen + " and board index is " + BoardIndex + " \n");
}



	stage++;
}


if(stage == 3)
{
myoutput tosend = new myoutput();
String finalStuff="";

try {
tosend=sharedVariables.engineQueue.poll();// we look for data from other areas of the program
while(tosend!=null)
{
//	sendToEngine(tosend.data);
finalStuff=tosend.data;

	if(tosend.data.contains("quit"))
	{
			sendToEngine(finalStuff);
			finalStuff="";
			go=0;
			writeOut("sent quit");

		break;
	}
if(finalStuff.length() > 0)
{
	try {
		writeOut("final stuff lenght > 0 and sending " + finalStuff);

	}
	catch(Exception badright){}
	sendToEngine(finalStuff);
}
tosend=sharedVariables.engineQueue.poll();// we look for data from other areas of the program





/*if(!sharedVariables.engineQueue.isEmpty())

{
	tosend = new myoutput();
	tosend=sharedVariables.engineQueue.poll();// we look for data from other areas of the program
	String lastsent="";




	while(tosend != null) // check if we are forwarding a bunch of moves and get last one
	{


			if(tosend.data.contains("quit"))
			{sendToEngine(tosend.data);
			go=0;
			lastsent = tosend.data;
			break;
			}

	        lastsent=tosend.data.substring(0, tosend.data.length());
	        if(!sharedVariables.engineQueue.isEmpty())
	        {
				tosend = new myoutput();
	        	tosend=sharedVariables.engineQueue.poll();// we look for data from other areas of the program
			}
			else
			break;

			}// end while


sendToEngine(lastsent);
writeOut("last sent is " + lastsent);
}// end if not empty
*/

}// end if tosent not null first time


}
catch(Exception e) {}
}//end if stage 3

if(text.length() > 0 && ((text.contains("pv") && stage ==3) || stage<3))
{
try {
if(text.startsWith("info") && (text.contains("pv") && !text.contains("info currmove") && stage ==3))
{
	
        // routine for those who print pv twice
        int tryone=text.indexOf("pv");
        int trytwo = text.indexOf("pv", tryone + 1);

        String line1="";
        String line2="";
        if(trytwo != -1)
        {
       line1 = text.substring(0, trytwo);
       line2 = text.substring(trytwo + 3, text.length());


        }
        else
        {
        line1 = text.substring(0, text.indexOf("pv"));
	line2 = text.substring(text.indexOf("pv") + 3, text.length());
         }
        line2=pgnGetter.getPgn(line2, sharedVariables.mygame[gameData.BoardIndex].iflipped, sharedVariables.mygame[gameData.BoardIndex].board);
	writeOut2( line1 + "\n" + line2 + "\n");
}
else
writeOut(text);
}
catch(Exception badone){}
}// end if







try {
Thread.sleep(30);
}
catch(Exception E5){}



}
while(go==1);
}// end try
catch(Exception e){}
}// end run uci


void writeOut(String text)
{
try {

// if(!sharedVariables.mygame[sharedVariables.gamelooking[BoardIndex]].engineFen.equals(""))
// text+="\n" + sharedVariables.mygame[sharedVariables.gamelooking[BoardIndex]].engineFen  + "\n";
//StyledDocument doc = sharedVariables.mygamedocs[BoardIndex];
 StyledDocument doc = sharedVariables.engineDoc;

doc.insertString(doc.getEndPosition().getOffset(), text + "\n", null);
for(int a=0; a<sharedVariables.openBoardCount; a++)
if(sharedVariables.gamelooking[a]==BoardIndex)
{
 if((sharedVariables.mygame[sharedVariables.gamelooking[a]].state == sharedVariables.STATE_EXAMINING || sharedVariables.mygame[sharedVariables.gamelooking[a]].state == sharedVariables.STATE_OBSERVING) && sharedVariables.engineOn == true)
 if(sharedVariables.mygame[sharedVariables.gamelooking[a]].clickCount %2 == 1)
setEngineDoc(doc, a);

//gameconsoles[a].setStyledDocument(doc);
}

}
catch(Exception e)
{}

}// end writeout method
void writeOut2(String text)
{
try {

// if(!sharedVariables.mygame[sharedVariables.gamelooking[BoardIndex]].engineFen.equals(""))
// text+="\n" + sharedVariables.mygame[sharedVariables.gamelooking[BoardIndex]].engineFen  + "\n";
//StyledDocument doc = sharedVariables.mygamedocs[BoardIndex];
 //JTextPane me = new JTextPane();
  StyledDocument doc = sharedVariables.engineDoc;
 doc.remove(0, doc.getLength());
 // doc.remove(0,doc.toString().length());
doc.insertString(doc.getEndPosition().getOffset(), text + "\n", null);
for(int a=0; a<sharedVariables.openBoardCount; a++)
if(sharedVariables.gamelooking[a]==BoardIndex)
{
 if((sharedVariables.mygame[sharedVariables.gamelooking[a]].state == sharedVariables.STATE_EXAMINING || sharedVariables.mygame[sharedVariables.gamelooking[a]].state == sharedVariables.STATE_OBSERVING) && sharedVariables.engineOn == true)
 if(sharedVariables.mygame[sharedVariables.gamelooking[a]].clickCount %2 == 1)
setEngineDoc(doc, a);

//gameconsoles[a].setStyledDocument(doc);
}

}
catch(Exception e)
{}

}// end writeout2 method



}// end run time class


