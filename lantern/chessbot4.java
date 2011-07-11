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
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JDialog;
import java.io.*;
import java.net.*;
import java.lang.Thread.*;
import java.applet.*;
import javax.swing.GroupLayout.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import java.lang.Integer;
import javax.swing.text.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.applet.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.text.html.HTML.Attribute.*;
import java.util.StringTokenizer;
import java.lang.reflect.Constructor;
import java.util.Vector;
import free.freechess.*;
import free.util.*;
import java.util.concurrent.locks.*;
import java.util.Calendar;
import java.util.StringTokenizer;

 class chessbot4 implements Runnable
{
	Socket requestSocket;
	InputStream tempinput;
 	OutputStream outStream;
	String message;
    String myinput;// persistent beyond getdata becaue we may call more than once
	String n;
	String p;
	int hits=0;
	long thetime;
	int counter=0;
	Thread t;
	int lastConsoleNumber;
	int linkID;
	int lastMoveGame;
	createWindows mycreator;
	seekGraphFrame seekGraph;
	int SUBFRAME_CONSOLES;
	int GAME_CONSOLES;
	String DG_GAME_NOTIFY;
	gameFrame myGameList;
	boolean dummyResponse;
	// these two lines below are persistent beyond the method geticcdata where they are used because
	// we may exit geticcdata on no data waiting but must resume
	int level1openings, level1closings, level2, icc_num;
	boolean startedParsing;
	// this next one is a hack that we dont start strict parsing tell after we receive the prompt
	int fullyConnected;
	docWriter myDocWriter;
	int maxLinks=75;
	long idleTime;
	qsuggest qsuggestDialog;
	mymultiframe theMainFrame;
	connectionDialog myConnection;
	Datagram1 masterDatagram = new Datagram1();
	boolean bellSet;

ConcurrentLinkedQueue<myoutput> queue;
ConcurrentLinkedQueue<newBoardData> gamequeue;
ConcurrentLinkedQueue<newBoardData> listqueue = new ConcurrentLinkedQueue();
JTextPane consoles[];
gameboard myboards[];
channels sharedVariables;
subframe [] consoleSubframes;
chatframe [] consoleChatframes;
JTextPane gameconsoles[];
resourceClass graphics;
listClass eventsList;
listClass seeksList;
listClass computerSeeksList;
listClass notifyList;
tableClass gameList;
JFrame masterFrame;

	chessbot4(JTextPane gameconsoles1[], ConcurrentLinkedQueue<newBoardData> gamequeue1, ConcurrentLinkedQueue<myoutput> queue1, JTextPane consoles1[], channels sharedVariables1, gameboard myboards1[], subframe consoleSubframes1[], createWindows mycreator1, resourceClass graphics1, listClass eventsList1, listClass seeksList1, listClass computerSeeksList1, listClass notifyList1, tableClass gameList1, gameFrame myGameList1, JFrame masterFrame1, chatframe [] consoleChatframes1, seekGraphFrame seekGraph1, mymultiframe theMainFrame1, connectionDialog myConnection1)
	{

SUBFRAME_CONSOLES=0;
GAME_CONSOLES=1;
DG_GAME_NOTIFY = "3000";
startedParsing= false;
icc_num= 0;
fullyConnected=-1;// set to 0 on prompt set to 1 message after prompt
theMainFrame=theMainFrame1;
myConnection= myConnection1;

queue=queue1;
consoles = new JTextPane[100];
gameconsoles=gameconsoles1;
graphics=graphics1;
//for(int a=0; a<100; a++)
seekGraph=seekGraph1;
consoles=consoles1;
myboards=myboards1;
sharedVariables=sharedVariables1;
linkID=0;
consoleSubframes=consoleSubframes1;
consoleChatframes=consoleChatframes1;
gamequeue=gamequeue1;
dummyResponse=false;
newBoardCreator client = new newBoardCreator();
newListAdder client3 = new newListAdder();
eventsList = eventsList1;
seeksList = seeksList1;
computerSeeksList = computerSeeksList1;
notifyList = notifyList1;
gameList=gameList1;
myGameList=myGameList1;
mycreator = mycreator1;
myDocWriter = new docWriter(sharedVariables, consoleSubframes, consoles, gameconsoles, myboards, consoleChatframes);
masterFrame=masterFrame1;

Thread t = new Thread(client);
t.start();
sendToIcs client2 = new sendToIcs();

Thread t3 = new Thread(client3);
t3.start();


lastMoveGame=-1;
Thread t1 = new Thread(client2);
t1.start();
}



chessbot4(){}
void startit()
{

chessbot4 r= new chessbot4();
t = new Thread(r);
t.start();

}
	public void run()
	{
		try{

			// System.out.println("trying to connect");


			lastConsoleNumber=0;
			connect();


			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String myText= null;





                        //3: Communicating with the server
				int keepgoing=1;
				do{
				try{
					//System.out.println("trying to read");
					int message2 = 0;

					// we may call getICCdata more than once before we finish a level 1 event
					// this is because it exits when no data is waiting, i.e can happen in lag no data, but havent finished
					if(startedParsing == false && !sharedVariables.myServer.equals("FICS"))
					{
						myinput="";
						icc_num=0;
					}
					else if(sharedVariables.myServer.equals("FICS"))
					{
						myinput=""; // fics-- not yet impletment multiple calls to getdata with same myinput being loaded
					}
					n="";
					p="";
					if(idleTime + 45 * 60 * 1000 < System.currentTimeMillis())
					{
						idleTime = System.currentTimeMillis();
						if(sharedVariables.noidle == true)
						{

						 myoutput output = new myoutput();
						 output.data = "DATE\n";
						 queue.add(output);
						}
					}
					int got;

					if(sharedVariables.myServer.equals("FICS"))
					got=getdata();
					else
					got = getIccData();
					if(got==0)
					Thread.sleep(1);
					if(got==1)
					{

					int istell=isitatell();
					if(istell==1)
					{
					processtell();

					}

					}// end if got=1, got data



					if(sharedVariables.doreconnect==true) // this would forcibly be set by user in menu if he chose reconnect to fics or icc
					{
									try{

										sendMessage("exit\n");
										//requestSocket.close();
										writeToConsole("attempting to reconnect");
										seeksList.resetList();
										computerSeeksList.resetList();
										eventsList.resetList();
										notifyList.resetList();
										Thread.sleep(1000);
										sharedVariables.timestamp=null;
										enabletimestamp();
										connect();
										sharedVariables.doreconnect=false;

									}
									catch(Exception e){
										e.printStackTrace();
										writeToConsole("exception in reconnect will try to write and reconnect");
										try {
										byte b = (byte) '\n';
										// i think we need to set a socket timeout
										// so write will fail, or it hangs
										requestSocket.setSoTimeout(1500);
										outStream.write(b); // we just send enter

											}
										catch(Exception ee)
										{ // write failed try to reconnect


										writeToConsole("attempting second reconnect in catch");
										connect();
										sharedVariables.doreconnect=false;
										writeToConsole("completed second reconnect in catch");
										Thread.sleep(1000);

                                        }


										}

					}

					if(sharedVariables.updateTellConsole==1)
					updateTellConsole();// in this loop we handle the checking and unchecking of the conosles tell check box;  subframe cant seem to control other subframes;
					if(sharedVariables.lastButton != -1)
					updateBoard();

				}
				catch(Exception classNot){
					try {Thread.sleep(500);}catch(Exception d){}
				}
			}while(keepgoing==1);
		}
		catch(Exception e){
			//System.out.println("You are trying to connect to an unknown host!");
		}
				finally{
			//4: Closing connection
			try{

				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}




void updateBoard()
{

	try {
	if(sharedVariables.lastButton > -1 && sharedVariables.lastButton< sharedVariables.maxBoardTabs)
	{
	    if(myboards[sharedVariables.lastButton]!=null)
			if(myboards[sharedVariables.lastButton].isVisible() == true)
		{

			myboards[sharedVariables.lastButton].repaint();


		}



		if(myboards[sharedVariables.lastButton]!=null)
		if(myboards[sharedVariables.lastButton].isVisible() == true)
		myboards[sharedVariables.lastButton].setTitle(sharedVariables.mygame[myboards[sharedVariables.lastButton].gameData.LookingAt].title);

}




		sharedVariables.lastButton=-1;
	}
catch(Exception e)
{}
}


 void updateTellConsole()
{


	for(int a=0; a< sharedVariables.maxConsoleTabs; a++)
    if(consoleSubframes[a]!=null && a!=sharedVariables.tellconsole)
    consoleSubframes[a].tellCheckbox.setSelected(false);

    if(consoleSubframes[sharedVariables.tellconsole]!=null)
    consoleSubframes[sharedVariables.tellconsole].tellCheckbox.setSelected(true);
	else
    consoleSubframes[0].tellCheckbox.setSelected(true);

    sharedVariables.updateTellConsole=0;

}

	void connect()
{
//1. creating a socket to connect to the server
		try {


			if(sharedVariables.reconnectTimestamp == true)
				writeDateStamps();

			startedParsing = false;
			fullyConnected = -1;
			if(sharedVariables.myServer.equals("ICC"))
			{


try {

computerSeeksList.resetList();
seeksList.resetList();
notifyList.resetList();
eventsList.resetList();
}
catch(Exception listException) { }






/*

 protected Socket connectImpl(String hostname, int port) throws IOException{
    Socket result = null;
    try{
      Class tsSocketClass = Class.forName("free.chessclub.timestamp.TimestampingSocket");
      Constructor tsSocketConstructor = tsSocketClass.getConstructor(new Class[]{String.class, int.class});
      result = (Socket)tsSocketConstructor.newInstance(new Object[]{hostname, new Integer(port)});
    } catch (ClassNotFoundException e){}
      catch (SecurityException e){}
      catch (NoSuchMethodException e){}
      catch (IllegalArgumentException e){}
      catch (InstantiationException e){}
      catch (IllegalAccessException e){}
      catch (InvocationTargetException e){
        Throwable targetException = e.getTargetException();
        if (targetException instanceof IOException)
          throw (IOException)targetException;
        else if (targetException instanceof RuntimeException)
          throw (RuntimeException)targetException;
        else if (targetException instanceof Error)
          throw (Error)targetException;
        else
          e.printStackTrace(); // Shouldn't happen, I think
      }

    if (result == null)
      result = new Socket(hostname, port);

    return result;
  }



*/



/****** we will set some stuff off **************/
sharedVariables.autoexam=0;
sharedVariables.toldTabNames.clear();
bellSet=false;

// set all games to was
try {
sharedVariables.graphData = new seekGraphData();
for(int i=0; i< sharedVariables.openBoardCount; i++)
if(myboards[i] != null)
{
	myboards[i].gameEnded("" + sharedVariables.mygame[i].myGameNumber);
	myboards[i].resetMoveList();
	updateGameTabs(sharedVariables.tabTitle[i], i);


}
for(int i=sharedVariables.openBoardCount-1; i>=0; i--)
if(myboards[i] != null)
{

   //myoutput data = new myoutput(); // we set it up to close all these tabs now. we end the game first so no resign or unob etc is sent in closetab method
    closeGameTab(i);
   // queue.add(data);
}
}// end try
catch(Exception badgameclose)
{// do nothing
}

// set noidle time
try {
	idleTime = System.currentTimeMillis();
}catch(Exception timedOut){}

    Socket result = null;
 			if(sharedVariables.timestamp!=null)
			requestSocket = new Socket("127.0.0.1", 5500);// 127.0.0.1 or 207.99.83.228
	else
{
	try{
      Class tsSocketClass = Class.forName("free.chessclub.timestamp.TimestampingSocket");
      Constructor tsSocketConstructor = tsSocketClass.getConstructor(new Class[]{String.class, int.class});
      requestSocket = (Socket)tsSocketConstructor.newInstance(new Object[]{sharedVariables.chessclubIP, new Integer(5000)});
    } catch(Exception d){ }
}
	if(requestSocket== null)
	requestSocket = new Socket("207.99.83.228", 23);// 127.0.0.1 or







/*
			if(sharedVariables.timestamp!=null)
			requestSocket = new Socket("127.0.0.1", 5500);// 127.0.0.1 or 207.99.83.228
			else
			requestSocket = new Socket("207.99.83.228", 23);// 127.0.0.1 or
*/

		}
			else
			{
			if(sharedVariables.timestamp != null)
			requestSocket = new Socket("127.0.0.1", 5499);// 127.0.0.1 or 207.99.83.228
			else
			requestSocket = new Socket("69.36.243.188", 23);// 127.0.0.1 or	FICS by IP
			}

			//System.out.println("Connected to chessclub.com on port 23");
			//2. get Input and Output streams



			outStream = requestSocket.getOutputStream();


			tempinput = requestSocket.getInputStream();
						// we login





// 13 16 18 24 25

			if(sharedVariables.myServer.equals("ICC"))
			{
			sharedVariables.myname="";// reset our name at reconnect.  having a name means we can use level1
			String  dgs= "0000000000000100101000001100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
			// turn on 0 ( who am i ) and 32 ( shout)
			String dgs2="";
			String newdgs="";
			for(int a = 0; a< dgs.length(); a++)// 79 80 string list
			{// 50 and 51 seeks
			if(a!= 0 && a != 32 && a != 31 && a != 28 && a != 26 && a != 12 && a != 13 && a != 14 && a != 15 && a!= 16 && a != 17 && a != 18 && a != 19 && a != 21 && a != 22 && a != 23 && a != 24 && a != 25 /*&& a != 27 */ && a != 33 && a != 34 && a != 37 && a != 39 && a != 40 && a != 41 && a != 42 && a!= 43 && a!= 44 /*&& a!= 46*/ && a != 47 && a!= 48 &&  a != 50 && a!= 51 && a!= 56 && a!=58 &&  a!= 59 &&  a!=60 && a!= 62 && a!=63 && a!= 64 && a!=65  && a!= 67 && a!= 69 && a!= 70 && a!= 72 && a!= 73 && a!=77 && a!=79 && a!=80 && a!=82 && a!= 83 && a!=86 && a!=91 && a!=99 && /* a!= 103 && */a!= 104 && a!=132 && a!= 152)
			dgs2= dgs2 + "0";
			else
			{
				  /*if(a==64)
				{
					newdgs = newdgs + "multi Set-2 81 1; Set-2 " + a + " 1; Set-2 81 1\n";
				}
				else*/
				dgs2=dgs2 + "1";
				//newdgs = newdgs + "Set-2 " + a + " 1\n";
			}
		    }
			sendMessage("level2settings=" + dgs2 + "\n");
			// see if we can load a script
			for(int ss=0; ss<sharedVariables.iccLoginScript.size(); ss++)
				sendMessage(sharedVariables.iccLoginScript.get(ss) + "\n");
			}// end if icc server and we just sent level 2 settings

			else if(sharedVariables.myServer.equals("FICS"))
			{for(int ss=0; ss<sharedVariables.ficsLoginScript.size(); ss++)
				sendMessage(sharedVariables.ficsLoginScript.get(ss) + "\n");
			}// end if icc server and we just sent level 2 settings

			try {for(int li=0; li<sharedVariables.channelNamesList.size(); li++)
				sharedVariables.channelNamesList.get(li).clearList();
			}catch(Exception listClear){}
			}// end try
		catch(Exception e)
		{
			//System.out.println("exception in connect\n");
			//JFrame frame1 = new JFrame("Connect exception");
			//frame1.setSize(100, 100);
   			//frame1.setVisible(true);
   			writeToConsole("exception in connect()");
		}
}


void updateGameTabs(String title, int num)
{

	for(int a=0; a < sharedVariables.maxBoardTabs; a++)
	{
		if(myboards[a]!=null)
		if(myboards[a].isVisible() == true )
		if(myboards[a].myconsolepanel!=null)
		if(myboards[a].myconsolepanel.channelTabs[num]!=null)// adding these extra checks for safety. in case this is called before the board fully creates
		{myboards[a].myconsolepanel.channelTabs[num].setText(title, num);
		myboards[a].myconsolepanel.channelTabs[num].setVisible(true);
		}
	}
	sharedVariables.mygame[num].tabtitle= title;

}







	int getIccData()
	{

		try {

		if(startedParsing != true || fullyConnected < 1)
		{
			myinput="";
			icc_num=0;
			level1openings =0;
			level1closings=0;
			level2=0;
		}




while(tempinput.available() > 0)
		{
					// read returns byte type we cast as char
					requestSocket.setSoTimeout(0);
// we have data, untill we hit a break, we are going to be reading the same data
// even if we exit this function and come back to it when data is again ready
// variables level1openings, level1closings and level2 are persistent beyond one call to this function
// and they are reset to 0 on reconnect or when we enter this function and startedparsing = false( which happens if we exit on a break)
startedParsing = true;
char c=(char) tempinput.read();
if((c== '\r' || c=='\n') && icc_num == 0)
{
	myinput="";
	icc_num=0;
	startedParsing=false;
	return 0;

}
/*if(c == '\031')
writeToSubConsole("\\031", sharedVariables.openConsoleCount-1);
else
*/
//writeToSubConsole("" + c, sharedVariables.openConsoleCount-1);
//if(bellSet == false)
//writeToSubConsole("" + c, sharedVariables.openConsoleCount-1);
// try to parse if we are level 1 or level 2 here

String ccc="" + c;

myinput+= ccc;



if(myinput.equals("aics% ") && level1openings == 0)
{
	myinput="";
	icc_num=0;
	startedParsing=false;
	//if(fullyConnected==0)
	//writeToConsole("on icc\n");

	fullyConnected=0;
	return 0;
}

if(myinput.equals("login: ") && level1openings == 0)
{

	startedParsing=false;
	break;
}
if(myinput.equals("password: ") && level1openings == 0)
{

	startedParsing=false;
	break;
}

if(icc_num > 0)
{


// garbage collection because of the aics
if(myinput.charAt(icc_num-1) == '\031' && icc_num > 1)
{

	boolean allSpaces=true;
	for(int z=0; z<icc_num-1; z++)
		if(myinput.charAt(z) != ' ')
			allSpaces=false;
	if(allSpaces==true)
	{
		myinput = "" + '\031' + c;
		icc_num=1;
	}
}



if( c == '[')
if(myinput.charAt(icc_num-1) == '\031')
	level1openings ++;

if(c == ']')
if(myinput.charAt(icc_num-1) == '\031')
level1closings++;

if(icc_num == 1 )
if(c == '(')
if(myinput.charAt(icc_num-1) == '\031')
	level2 =1;

if(icc_num == 2 )
if(c == '(')
if(myinput.charAt(icc_num-1) == '\031')
{	level2 =1;
myinput="" + '\031' + '(';
icc_num--;
}

}// end if i > 0

icc_num++;


					//myglobalinput = myglobalinput + c;
					// end of line condition
					// if more data is in socket,
					// we will read it later after
					// we process this line
					/*if(myinput.charAt(0) == '\031')
					{String tzy = "";
					tzy = tzy + myinput.charAt(i-1);
					newbox.append(tzy);
					}*/
					// for fics '\n\rfics% '

					// started parsing set to false on each break. next time we enter we know we are starting fresh not continuing, we could also have exited because of lack of data
					if(myinput.charAt(icc_num - 1) == '\n' && myinput.charAt(0) != '\031') // or '\031' and ')'
					{
						startedParsing = false;
						break;
					}
					else if(icc_num > 1 && level2 ==1)
					{	if(myinput.charAt(icc_num-2) == '\031' && myinput.charAt(icc_num-1) == ')')
						{
						startedParsing=false;
						break;
						}
					}
					else if(level1openings > 0 && level1openings == level1closings)
					{
						startedParsing=false;
						break;
					}

		}// end while


if(startedParsing == true)
return 1;

if(fullyConnected == 0)// message after prompt
fullyConnected=1;

if(level2 == 1)
{

	try {
		Datagram1 dg;
		dg= new Datagram1(myinput);

		processDatagram(dg, new routing());
	    }
		catch(Exception e)
		{}
	return 1;
}
else
{ // not level 2
	int go=0;
	if(icc_num>0)
	{

	if(myinput.charAt(0)=='\031')
	{routing console = new routing();
	//writeToSubConsole("process level 1 call and i.length is " + i + "\n", sharedVariables.openConsoleCount-1);

	go= processLevel1(myinput, 0, console);
	}
	else
	{
writeLevel1(new routing(), myinput);
	}
	}
	else
		Thread.sleep(10);
return 1;

}// not level 2
	}// end try
	catch(Exception e) {try {Thread.sleep(10);
	}catch(Exception dd){}}
	return 0;
}

class routing {

	int type;
	int number;
	int found;

	routing()
	{
		type=0;// subframe not game, game is 1
		number=0; // first subframe or first game console
		found=0;

	}

}

void writeLevel1(routing console, String thetell)
{
if(thetell.startsWith("bell set to"))
{
	bellSet=true;

}
if(!thetell.startsWith("aics"))
{

if(thetell.length() == 2)
  return;
else if(thetell.length() > 2)
{
	thetell=thetell.substring(0, thetell.length()-2);
	
       /* if(thetell.indexOf("\n") > -1)
        {
        JFrame framer = new JFrame("" + thetell.indexOf("\n") + " and " + thetell.indexOf("\r") + " and " +  thetell.length());
         framer.setSize(200,50);
         framer.setVisible(true);

        }
        */
        thetell = thetell + "\n";
}
else
return;
}
if(thetell.startsWith("Game notification: "))
{
	processGnotify(thetell);
	return;
}

if(console.found==0)
{

	normalLineProcessing(thetell);
	return;
}

StyledDocument doc;
int gameornot=0;
try {
	int index=0;
	if(console.type == 0)
	{
		if(consoles[console.number]==null)
		index=0;
		else
		index=console.number;

		gameornot=0;
		doc=sharedVariables.mydocs[index];
	}
	else
	{
		if(sharedVariables.mygamedocs[console.number]==null)
		{
			doc=sharedVariables.mydocs[0];
			gameornot=0;
			index=0;
		}
		else
		{
			doc = sharedVariables.mygamedocs[console.number];
			gameornot=1;
			index=console.number;
		}

	}
	Color mycolor;


	mycolor=sharedVariables.ForColor;
	if(sharedVariables.tabStuff[index].ForColor!=null)
	mycolor=sharedVariables.tabStuff[index].ForColor;

SimpleAttributeSet attrs = new SimpleAttributeSet();


	if(console.found == 1)
	{
		mycolor=sharedVariables.responseColor;
	if(gameornot == 0)
	if(sharedVariables.tabStuff[index].responseColor!=null)
	mycolor=sharedVariables.tabStuff[index].responseColor;
	if(sharedVariables.responseStyle == 1 || sharedVariables.responseStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.responseStyle == 2 || sharedVariables.responseStyle == 3)
		StyleConstants.setBold(attrs, true);


	}
	else
	{
	if(sharedVariables.nonResponseStyle == 1 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.nonResponseStyle == 2 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setBold(attrs, true);


	}


if(gameornot == 0)
{
	if(!(thetell.startsWith("(told ") && sharedVariables.tabStuff[index].told == false))// surpress typed text ability
	processLink(doc, thetell, mycolor, index, maxLinks, SUBFRAME_CONSOLES, attrs, null);
}
else
processLink(doc, thetell, mycolor, index, maxLinks, GAME_CONSOLES, attrs, null);
} catch(Exception d){}



}



void processGnotify(String s)
{
		try {
		Datagram1 dg;


		dg= new Datagram1("");

		dg.arg[1]=s;
	    dg.arg[0]= DG_GAME_NOTIFY;
	    dg.argc=2;

	    processDatagram(dg, new routing());
	}catch(Exception e){}

}
int processLevel1(String myinput, int depth, routing console)
{

	// [ 11 me\n
	// text
	// '031' ] [ (
		int go=0;
		int i=myinput.indexOf("\n");
		if(depth > 0)
		i=-1;


		if(depth==0)
		{
			int c=myinput.indexOf("*");
			if(c > -1 && c + 2 < i && i > -1)
			{

						c=c+2;
						try {
						String consoleString=myinput.substring(c, i);
						if(consoleString.length()>1)
						{




						//writeToConsole("found phrase:" + consoleString + ":\n");
						char consoleChar=consoleString.charAt(0);
						if(consoleChar == 'g')
						console.type=1;// defaults to 0
						if(consoleChar == 'p')
						console.type=2;// logpgn
						if(consoleChar == 's')
						console.type=3;// save pgn

                                                String myConNumber="";

						// we assume its c now. could be g for game, c is subframe console
						try {myConNumber = consoleString.substring(1, consoleString.length()-1); // not sure what character i'm elinimating with -1 but not a number if not minus1
						console.number= Integer.parseInt(myConNumber);
						console.found=1;}
						catch(Exception badnumber){//writeToConsole("exception and mycon number is " + myConNumber);
						}
						// writeToConsole("type is " + console.type + " and number is " + console.number + "\n");

				}// end if not empty string
				}catch(Exception z1){}
			}
		}

		if(i>-1 )// we move passed \n but on depth 0 we check for our arbitrary phrase to say what console to go to
		myinput= myinput.substring(i+1, myinput.length());
		else
		{
			while(myinput.charAt(0) == ' ')
			myinput=myinput.substring(1, myinput.length());
		}

		if(myinput.charAt(0) == '\031')
		if(myinput.charAt(1) == ']')
		 return 1;

		if(myinput.length()>1)
		{
			if(myinput.charAt(0) == '\031')
			{//writeToConsole("inside charat(0) 031 and lenght is " + myinput.length() + "\n");
				if(myinput.charAt(1)== '[')// another level 1
				{ try {
					int next = myinput.indexOf("\n") + 1;
					//writeToConsole("another level 1 and myinput lenght is " +  myinput.length() + "\n");
				processLevel1(myinput.substring(next, myinput.length()), depth + 1, console);
				}catch(Exception d1){}
					}
				if(myinput.charAt(1)=='(')// level 2
				{
					int j=myinput.indexOf("\031(");
					if(j > -1)
					{
					int k = myinput.indexOf("\031)");
						if(k > j)
						{
							try {

						int oldj=j;
						int oldk=k;
						int headcount=0;
						while(j > -1 && k > -1 && k>j)
					{
						String stuff = myinput.substring(j, k+2);



								stuff = stripLevel1(stuff);
								masterDatagram.makeDatagram(stuff);
								//writeToConsole("stuff is " + stuff + "\n");
								try { processDatagram(masterDatagram, console);} catch(Exception e3){}

								go=1;

						oldj=j;
						oldk=k;
						j=myinput.indexOf("\031(", j+1);
						k = myinput.indexOf("\031)", k+1);


					}// end while

myinput=myinput.substring(oldk+2, myinput.length());// stuff after datagram

								processLevel1(myinput, depth + 1, console);

								}

								catch(Exception e)
								{}
						}
					}	// if jj > -1
				}// end if level 2
			}// end if starts with '031'
			else
			{
				int m=myinput.indexOf("\031");
				String s = myinput.substring(0, m);
				writeLevel1(console, s);
				if(myinput.charAt(m+1)=='[')
				{

				processLevel1(myinput.substring(m, myinput.length()), depth + 1, console);

				}
				if(myinput.charAt(m+1)=='(')
				processLevel1(myinput.substring(m, myinput.length()), depth + 1, console);

			}

		}// end if lenght > 1


	return go;

}




String stripLevel1(String stuff)
{

	int j=stuff.indexOf("\031[");
	if(j > -1)// we have a level 1 embedded
	{
		int i=stuff.indexOf("\031]");
		if(i > -1)
		{
			String beginning=stuff.substring(0, j);
			String ending=stuff.substring(i+3, stuff.length());
			stuff=beginning + ending;

		}

	}


	return stuff;

}







	int getdata()
	{

		try {
		int i=0;

while(tempinput.available() > 0)
		{
					// read returns byte type we cast as char
					requestSocket.setSoTimeout(0);
char c=(char) tempinput.read();
					i++;
					myinput=myinput + c;


					//myglobalinput = myglobalinput + c;
					// end of line condition
					// if more data is in socket,
					// we will read it later after
					// we process this line
					/*if(myinput.charAt(0) == '\031')
					{String tzy = "";
					tzy = tzy + myinput.charAt(i-1);
					newbox.append(tzy);
					}*/
					// for fics '\n\rfics% '

					if(sharedVariables.myServer.equals("FICS"))
					{
						if(i>=8)
						{if(myinput.charAt(i-1) == ' ' && myinput.charAt(i-2) == '%' && myinput.charAt(i-3) == 's' && myinput.charAt(i-4) == 'c' && myinput.charAt(i-5) == 'i' && myinput.charAt(i-6) == 's' && myinput.charAt(i-7) == '\r' && myinput.charAt(i-8) == '\n')
							break;}
					}
					else
					{
					if(myinput.charAt(i-1) == '\n' && myinput.charAt(0) != '\031') // or '\031' and ')'
						break;
					else if(i > 1)
					{if(myinput.charAt(i-2) == '\031' && myinput.charAt(i-1) == ')')
					break;
					}
				   }// end else

		}

		if(sharedVariables.myServer.equals("ICC"))
		myinput=myinput + '\0';
		if(i>0)
		{

						if(myinput.charAt(i-2) == '\031')
						{
						//newbox.setText(newbox.getText() + "found datagrame trying to parse\n");

						//StyledDocument doc=newbox.getStyledDocument();
						//doc.insertString(doc.getEndPosition().getOffset(), "found datagram trying to parse", null);
						//newbox.setStyledDocument(doc);


						try {
						Datagram1 dg;
						dg= new Datagram1(myinput);

						//if(!dg.getArg(0).equals("28") && !dg.getArg(0).equals("32"))
						//writedg("Datagram: type " + dg.type + " args " + dg.argc + " spot 0: " + dg.getArg(0));
						//newbox.setText(newbox.getText() + "Datagram: type " + dg.type + " args " + dg.argc + " spot 0: " + dg.getArg(0) + "\n");
						/*StyledDocument doc=consoles[0].getStyledDocument();
						doc.insertString(doc.getEndPosition().getOffset(), "Datagram: type " + dg.type + " args " + dg.argc + " spot 0: " + dg.getArg(0) + "\n", null);
						consoles[0].setStyledDocument(doc);
						*/
						processDatagram(dg,  new routing());
						}
						catch(Exception e)
						{}
						}
						else //process line
						{

						int nodispaly =0;
						if(sharedVariables.myServer.equals("ICC"))
						{
							myinput=myinput.substring(0, myinput.length()-2);
							normalLineProcessing(myinput);
						}
						else// fics
						ficsParsing(myinput);

						}
						return 1; // we must have read something
		}
		Thread.sleep(3);

		}
		catch(Exception e)
		{
			//System.out.println("caught exception in getdata\n");
		}

		return 0;


	}

void ficsParsing(String myinput)

{



							myinput=myinput.substring(0, myinput.length()-8); // fics

							String myinput2;
							boolean keepGoing = true;
							while(keepGoing == true)
							{


							int nextreturn=myinput.indexOf("\n\r");

							// we grab the line up to the \n\r and put it in myinput2 and set myinput to rest of string. when no more \n\r we just process last line
							if(nextreturn != -1)
							{
								myinput2=myinput.substring(0, nextreturn);

								if(myinput.length() == myinput2.length() + 2)
								keepGoing = false;
								else
								myinput = myinput.substring(nextreturn + 2, myinput.length());
							}
							else
							{
								myinput2=myinput;
								keepGoing = false;
							}

							if(myinput.equals("\n\r"))
							break;


							myinput2=myinput2.replace("\n", "");
							myinput2=myinput2.replace("\r", "");
							if(myinput2.length() == 0)
							continue;
						try{
							DeltaBoardStruct deltaline;



							deltaline=DeltaBoardStruct.parseDeltaBoardLine(" " + myinput2 + " ");
							String thenumber = "" + deltaline.getGameNumber();

							newBoardData temp = new newBoardData();
							temp.dg=24;
							temp.arg1=thenumber;
							temp.arg2=deltaline.getMoveSmith();
							gamequeue.add(temp);
							continue;
						}
						catch(Exception e){}



						try{
							Style12Struct style12line;
							//writeToConsole("looking for style 12 struct and myinput2 is now:" + myinput2 + ":::end myinput2\n");


							style12line=Style12Struct.parseStyle12Line(" " + myinput2 + " ");
							//writeToConsole("Style12 struct with game number " +  style12line.getGameNumber()  +  " and move " + style12line.getMoveSAN());

							// now we set the game info.
							// the method in gameboard will only act once. setting ficsSet to 1 and returning on future calls to set
							String thenumber = "" + style12line.getGameNumber();

							newBoardData temp = new newBoardData();
							temp.dg=250;
							temp.arg1=thenumber;
							try {
								temp.arg2=style12line.getWhiteName();
							}
							catch(Exception e){temp.arg2="somebody";}

							try {
								temp.arg3=style12line.getBlackName();
							}
							catch(Exception e) { temp.arg3="somebody";}
							temp.arg4="" + style12line.getGameType(); // MY_GAME=1 OBSERVED_GAME=2 ISOLATED_BOARD
							String played = "False";
							if(style12line.isPlayedGame())
							played= "True";
							temp.arg5= played;

							// we do a check here for if the game is examined, if so we try to set it up because we dont get game info on it
							if(played.equals("False") && temp.arg4.equals("1"))
							{
								setupExaminingFics(thenumber);
							}

							temp.arg6= "" + style12line.getPlayedPlyCount();

							String myturn = "True";
							try {
								if(!style12line.isMyTurn())
									myturn = "False";

								}
							catch(Exception e){}
							temp.arg7= myturn;

							gamequeue.add(temp);

							temp = new newBoardData();
							temp.dg=25;
							temp.arg1=thenumber;
							temp.arg2=style12line.getBoardLexigraphic();
							gamequeue.add(temp);

							// now we set the clocks
							temp = new newBoardData();
							temp.dg=56;
							temp.arg1=thenumber;
							temp.arg2= "" + style12line.getWhiteTime();
							temp.arg3= "" + style12line.getBlackTime();
							gamequeue.add(temp);


							continue;
						}
						catch(Exception e)
						{
							//if(!(e.toString().contains("Missing \"<12>\" identifier")))
							//writeToConsole(" not a style 12 struct and error is: " + e.toString()       );
						}

						try{
							GameInfoStruct gameinfolineline;
							gameinfolineline=GameInfoStruct.parseGameInfoLine(" " + myinput2 + " ");

								try {
									writeToConsole("Game info struct with game number " +  gameinfolineline.getGameNumber());
								}
								catch(Exception e){writeToConsole("game info struct with no game number.");}

							newBoardData temp = new newBoardData();
							temp.dg=12;
							String thenumber = "" + gameinfolineline.getGameNumber();
							temp.arg1=thenumber;

							gamequeue.add(temp);

							continue;
						}
						catch(Exception e)
						{
							// not a game info line
						}



							if(myinput2.indexOf("\n") > -1)
							myinput2=myinput2.substring(0, myinput2.indexOf("\n"));
							boolean ficsdatagram=proccessFicsDatgram(myinput2);
							if(ficsdatagram == true)
							continue;

							normalLineProcessing(myinput2);

							String removing = "Removing game";
							if(myinput2.startsWith("Removing game"))
							{
								StringTokenizer tokens = new StringTokenizer(myinput2, " ");
								tokens.nextToken();
								tokens.nextToken();


								newBoardData temp = new newBoardData();
								temp.dg=13;
								temp.arg1=tokens.nextToken();
								gamequeue.add(temp);

							}
							if(myinput2.startsWith("**** Starting FICS session"))
	                        {
								sendMessage("set style 12\n");
								sendMessage("set interface Lantern Chess " + sharedVariables.version + "\n");
								sendMessage("iset gameinfo 1\n");
								sendMessage("iset MS 1\n");
								sendMessage("iset compressmove 1\n");
							}

							// You are no longer examining game 81.
							if(myinput2.startsWith("You are no longer examining game"))
							{
								String number;
								StringTokenizer tokens = new StringTokenizer(myinput2, " ");
								tokens.nextToken();
								tokens.nextToken();
								tokens.nextToken();
								tokens.nextToken();
								tokens.nextToken();
								tokens.nextToken();
								number=tokens.nextToken();
								number = number.substring(0, number.length() -1);
								newBoardData temp = new newBoardData();
								temp.dg=13;
								temp.arg1=number;
								gamequeue.add(temp);


							}

						}// end while
}



void setupExaminingFics(String number)
{
   int gamenum=getGameBoard(number);
	if(gamenum!=-1) // if its -1 i.e. doesnt exist yet we set it up
	return; // we expect not to find the examine game has a game number if we havent set it up yet

	newBoardData temp = new newBoardData();
    temp.dg=12;
	temp.arg1=number;
	gamequeue.add(temp);

}
boolean proccessFicsDatgram(String myinput)
{
	// channel adammr(40): hi
	// or
	// pulsar(C)(40): hi

	int firstspace = myinput.indexOf(" ");

	if(firstspace > -1)
	{

		if(firstspace > 6) // could be channel
		{
			String temp1=myinput.substring(firstspace - 2, firstspace);
			if(temp1.equals("):"))// it could be a channel or tell now
			{


				//writeToConsole("could be a channel or tell now\n");

				// we hunt for the first "(" prior to firstspace -2 or what we found ")"
				int i=myinput.indexOf("(");
				while(i > -1)
				{
					int j=myinput.indexOf("(", i + 1);// check this methods parameters
					if(j == -1)
						break;
					if(j >= firstspace - 2)
					 	break;
					 i=j;
				}


				//writeToConsole("first space is " + firstspace + " and found i or ( at " + i + "\n");

				if(i > -1)// its a channel if between firstspace - 3 spot and i + 1 spot its a number
				{
					String temp2 = myinput.substring(i+1, firstspace-2);
					try {
						temp2.trim();
						int num=Integer.parseInt(temp2);
						// if we didnt throw an exception its channel num.
						Datagram1 dg;
						//writeToConsole("creating datagram\n");
						dg= new Datagram1("");
						dg.arg[2]=myinput.substring(0, i);
						dg.arg[1]=temp2;
						dg.arg[3]="";
						dg.arg[4]=myinput.substring(firstspace + 1, myinput.length());
						dg.arg[0]="28";
						dg.argc=5;
						//String thetell= dg.getArg(2) + "(" + dg.getArg(1) + "): " + dg.getArg(4) + "\n";
						//writeToConsole("proccesing datagram\n");
						processDatagram(dg, new routing());
						return true;


					}
					catch(Exception e){
						// its not a channel
						//writeToConsole("exception on channel number its :" + temp2 + ":\n");
					}
				}
			}
		}

	}// end if first space > -1


	StringTokenizer tokens = new StringTokenizer(myinput, " ");
	int j = tokens.countTokens();

	if(j >= 3)
	{
		// adammr tells you:
		// adammr shouts:
		// --> adammr something

		String token1= tokens.nextToken();
		String token2= tokens.nextToken();
		String token3= tokens.nextToken();

		String name;
		String body;
try {

		Datagram1 dg;
		dg= new Datagram1("");


	if(token2.equals("tells") && token3.equals("you:")) // its a tell
		{
			name=token1;  // 1 is name 3 is body and its 31
			int c=myinput.indexOf(":");
			body=myinput.substring(c+2, myinput.length());
			dg.arg[0]="31";
			dg.arg[2]=""; // titles we ar parsing them in the name
			dg.arg[1]=name;
			dg.arg[3]=body;
			dg.argc=4;
			processDatagram(dg, new routing());
			return true;
		}
		if(token2.equals("shouts:")) // shout
		{
			// arg 3 is 0 or 1 and 1 is i. dg is 32 arg1 is name and arg 4 is body
			name=token1;
			int c=myinput.indexOf(":");
			body=myinput.substring(c+2, myinput.length());
			dg.arg[0]="32";
			dg.arg[2]=""; // titles we ar parsing them in the name
			dg.arg[3]="0";
			dg.arg[1]=name;
			dg.arg[4]=body;
			dg.argc=5;
			processDatagram(dg, new routing());
			return true;
		}
		if(token1.equals("-->")) // its an i shout
		{
			name=token2;
			int c=myinput.indexOf(token2);
			int d=myinput.indexOf(" ", c);
			body=myinput.substring(d+1, myinput.length());
			dg.arg[0]="32";
			dg.arg[3]="1";
			dg.arg[2]="";
			dg.arg[1]=name;
			dg.arg[4]=body;
			dg.argc=5;
			processDatagram(dg, new routing());

			return true;
		}



	}// end try
	catch(Exception e){}


	}

return false;
}


void normalLineProcessing(String myinput)
{
						try {
						if(myinput.contains("You're at the end of the game.") && myinput.length()<35)
						sharedVariables.autoexam=0;

						if(myinput.contains("goes forward 1") && myinput.contains("Game") && myinput.length()<35)
						{
							if(sharedVariables.autoexam==1 && sharedVariables.autoexamnoshow == 1)
							  return;
						}

						//{Game 1358
					if(myinput.startsWith("{Game ") || myinput.startsWith("Game "))// && (myinput.contains("goes forward") || myinput.contains("backs up") || myinput.contains("moves:")))// next case any forward
						{
							// we want this to go to thte game console
							// find 1 spaces and go one index up and substring to index of :
							//Game 312: Mike goes forward 1.
							int j = myinput.indexOf(" ");
							if(j > -1)
							{
								int k=myinput.indexOf(" ", j+1);
								int l=myinput.indexOf(":", j+1);// catches game 555: something
								if(l==k-1)
								k=l;

								if(k > -1) // j+1 because we want char after space
								{
			                            newBoardData temp = new newBoardData();
			                            temp.dg=900; // to high to really be a datagram we make this up
										temp.arg1=myinput.substring(j+1, k);
										// lets see if arg1 is a number. if not we pass on adding to queue
										int aNum=1;
										try { int anumber=Integer.parseInt(temp.arg1);}
										catch(Exception e) { aNum=0; // not a number
										}

										temp.arg2=myinput + "\n";
										if(aNum == 1)
										{
											gamequeue.add(temp);
											return;
										}

								}
							}


						}


						// we want all game lines to go to game console
						if(myinput.startsWith("Game:"))// && (myinput.contains("goes forward") || myinput.contains("backs up") || myinput.contains("moves:")))// next case any forward
						{
							// we want this to go to thte game console
							// find 1 spaces and go one index up and substring to index of :
							//Game 312: Mike goes forward 1.
							int j = myinput.indexOf(" ");
							if(j > -1)
							{
								int k=myinput.indexOf(":");
								if(k > -1 && k > j+1) // j+1 because we want char after space
								{
			                            newBoardData temp = new newBoardData();
			                            temp.dg=900; // to high to really be a datagram we make this up
										temp.arg1=myinput.substring(j+1, k);
										temp.arg2=myinput + "\n";
										gamequeue.add(temp);
										return;

								}
							}


						}
						if(myinput.startsWith("password:") && myinput.length()<15)
						sharedVariables.password=1;


						myinput = myinput + "\n";
						String temp1;
						String temp2;
						int special=0;
					 	if(myinput.length() > 10 && myinput.length() < 21)
					 	{
							temp1=myinput.substring(0,5);
							temp2=myinput.substring(1,6);
							if(temp1.equals("(told") || temp2.equals("(told"))
							{
								if(myinput.contains(")"))
								{
									StyledDocument doc=sharedVariables.mydocs[sharedVariables.looking[lastConsoleNumber]];
									doc.insertString(doc.getEndPosition().getOffset(), myinput, null);
									special=1;
									myDocWriter.writeToConsole(doc, sharedVariables.looking[lastConsoleNumber]);

	}
							}


						}

						if(myinput.startsWith("Black gives") || myinput.startsWith("White gives"))
						{
							special=1;
					    StyledDocument doc;
						if(lastMoveGame != -1)
						doc = sharedVariables.mygamedocs[lastMoveGame];
						else
						doc = sharedVariables.mydocs[0];
SimpleAttributeSet attrs = new SimpleAttributeSet();
	if(sharedVariables.nonResponseStyle == 1 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.nonResponseStyle == 2 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setBold(attrs, true);

		if(lastMoveGame != -1)
					processLink(doc, myinput + "\n", sharedVariables.ForColor, lastMoveGame, maxLinks, GAME_CONSOLES, attrs, null);// 1 at end means go to game console
					else
					processLink(doc, myinput + "\n", sharedVariables.ForColor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, null);// console 0 and last 0 is not a game console

//writeToConsole("got a starts with and lastMoveGame is " + lastMoveGame)

						}
						if(special == 0)
						{

SimpleAttributeSet attrs = new SimpleAttributeSet();
	if(sharedVariables.nonResponseStyle == 1 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.nonResponseStyle == 2 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setBold(attrs, true);

							StyledDocument doc=sharedVariables.mydocs[0];
							//doc.insertString(doc.getEndPosition().getOffset(), myinput, null);
							processLink(doc, myinput, sharedVariables.ForColor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, null);
							//writeToConsole(doc, 0);
							if(myinput.startsWith("style set to 13."))
							if(sharedVariables.myname.startsWith("guest"))
								 writeGuestLogin();
						}

					}// end try
						catch(Exception e){}

}

void writedg(String mydg)
{

	StyledDocument doc=consoles[0].getStyledDocument();
							try {
								doc.insertString(doc.getEndPosition().getOffset(), mydg + "\n", null);


							consoles[0].setStyledDocument(doc);
							}
							catch(Exception e)
							{
							}

}

void processLink(StyledDocument doc, String thetell, Color col, int index, int attempt, int game, SimpleAttributeSet attrs, messageStyles myStyles)
{
	myDocWriter.processLink(doc, thetell, col, index, attempt, game, attrs, myStyles);
}
void processLink2(StyledDocument doc, String thetell, Color col, int index, int attempt, int game, SimpleAttributeSet attrs, int [] allTabs, messageStyles myStyles)
{
	myDocWriter.processLink2(doc, thetell, col, index, attempt, game, attrs, allTabs, myStyles);
}



	void writeGuestLogin()
	{
		String s1= "Games guests can observe can be seen by typing ";
		String s2= "\"games *-r-T-e\"\n";
try{

StyledDocument doc=sharedVariables.mydocs[0];// 0 for main console
SimpleAttributeSet attrs = new SimpleAttributeSet();
StyleConstants.setForeground(attrs, sharedVariables.ForColor);
doc.insertString(doc.getEndPosition().getOffset(), s1, attrs);
StyleConstants.setUnderline(attrs, true);
attrs.addAttribute(javax.swing.text.html.HTML.Attribute.HREF, "games *-r-T-e");
doc.insertString(doc.getEndPosition().getOffset(), s2, attrs);
myDocWriter.writeToConsole(doc, 0);// o for main console
}
catch(Exception e)
{}

	}
void testQsuggest()// used for debugging
{

try {
	if(qsuggestDialog!=null)
	{

		qsuggestDialog.dispose();
		qsuggestDialog=null;
	}
}
catch(Exception qsug){}
try{
qsuggestDialog=new qsuggest(masterFrame, false, queue);
qsuggestDialog.suggestion("Test 1", "Test 1 this is a qsuggest", "jack", "Tom");// text command id (2,1,6)
qsuggestDialog.setVisible(true);
}
catch(Exception badq){}


}
	void processDatagram(Datagram1 dg, routing console)
	{
		try {
		int gamenum=0;

if( dg.getArg(0).equals("152"))
{
 // dg.getArg(0).equals("58") || // ip doenst do anything found out its 152
  String arg58="";
        for(int a= 0; a < dg.argc; a++)
        {
                      arg58 = " " + dg.getArg(a);
                      //writeToConsole(a + " " +  arg58 + "\n");
        }



			newBoardData temp = new newBoardData();
			temp.dg=152;
			temp.arg1=dg.getArg(2);
                        temp.arg2=dg.getArg(1);
                        temp.arg3=dg.getArg(3);
                        try {
                        if(Integer.parseInt(dg.getArg(2)) > 0)
                        gamequeue.add(temp);
                        else
                        {
                       if(console.type==0)
                         {if(console.number >=0 && console.number <sharedVariables.maxConsoleTabs)
                        {
                         
                         String uniqueName="";
                         int bb=sharedVariables.countryNames.indexOf(";" + dg.getArg(3) + ";");
                         if(bb > -1)
                         {
                          int bbb=sharedVariables.countryNames.indexOf( ";", bb + 4);
                          if(bbb > -1)
                          uniqueName=sharedVariables.countryNames.substring(bb+ dg.getArg(3).length() + 2, bbb);

                         }
                          if(uniqueName.equals(""))
                          writeToSubConsole(dg.getArg(1) + " " + dg.getArg(3) + "\n",console.number);
                          else
                           writeToSubConsole(dg.getArg(1) + " " + dg.getArg(3) + " " + uniqueName + "\n",console.number);

                        }

                        }
                             }    // end else

                        }
                        catch(Exception country){
                     }





        return;
}

if(dg.getArg(0).equals("46"))
{
 try {
         String user = dg.getArg(1);

          /* if(bellSet == true)
           for(int d=0; d<sharedVariables.lanternNotifyList.size(); d++)
           if(sharedVariables.lanternNotifyList.get(d).name.toLowerCase().equals(user.toLowerCase()))
           {
            globalNotifyAlert(user, true);
            break;
           }
            */
        for(int a= 2; a < dg.argc; a++)
        {
                        newBoardData temp = new newBoardData();

                	temp.dg=27;
			temp.arg1=dg.getArg(a);
			temp.arg2=user;
			temp.arg3="1";
                 // writeToSubConsole("S" + temp.arg1 + " " + temp.arg2 + " " +  temp.arg3 + "\n", sharedVariables.openConsoleCount-1);

			listqueue.add(temp);

        }



 }// end try
 catch(Exception dui){}

}

if(dg.getArg(0).equals("27"))
{
	// (channel playername come/go)

			newBoardData temp = new newBoardData();
			temp.dg=27;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);
			temp.arg3=dg.getArg(3);


			listqueue.add(temp);
			return;




}

		if(dg.getArg(0).equals("91")){
		mycreator.createWebFrame(dg.getArg(1));

		}

		if(dg.getArg(0).equals("0"))
		{sendMessage("multi set-quietly prompt 0\n");

		 sendMessage("multi set-quietly style 13\n");
		sendMessage("multi Set-2 46 1\n");// dg  channels shared
                 sendMessage("multi Set-2 27 1\n");// dg people in channel
		sendMessage("multi set-quietly interface Lantern Chess " + sharedVariables.version + "\n");

		sendMessage("set-quietly level1 5\n");

				sendMessage("multi Set-2 81 1; Set-2 64 1; Set-2 65 1; Set-2 81 1\n");// notify
					sendMessage("multi Set-2 103 1\n");// dg tourney events list
				sendMessage("multi set bell 0\n");
								if(sharedVariables.iloggedon == true)
								sendMessage("iloggedon\n");

		 sharedVariables.myname=dg.getArg(1);
		 sharedVariables.myopponent=dg.getArg(1);


		 try { myConnection.dispose(); } catch(Exception done){};
	 	}

		if(dg.getArg(0).equals("47"))   // DG_MY_VARIABLE
		{

                  try {
                  // writeToConsole("arg 1 " + dg.getArg(1) + " and arg 2 "  + dg.getArg(2) + " \n");
                  if(dg.getArg(1).equals("time"))
                  sharedVariables.myseek.time=Integer.parseInt(dg.getArg(2));
                  if(dg.getArg(1).equals("increment"))
                  sharedVariables.myseek.inc=Integer.parseInt(dg.getArg(2));
                  if(dg.getArg(1).equals("wild"))
                  sharedVariables.myseek.wild=Integer.parseInt(dg.getArg(2));

                  if(dg.getArg(1).equals("rated"))
                  {
                   if(Integer.parseInt(dg.getArg(2)) == 0)
                    sharedVariables.myseek.rated=false;
                    else
                    sharedVariables.myseek.rated=true;
                  }
                  if(dg.getArg(1).equals("useformula"))
                   {
                   if(Integer.parseInt(dg.getArg(2)) == 0)
                    sharedVariables.myseek.formula=false;
                    else
                    sharedVariables.myseek.formula=true;
                  }
                  if(dg.getArg(1).equals("manualaccept"))
                  {
                   if(Integer.parseInt(dg.getArg(2)) == 0)
                    sharedVariables.myseek.manual=false;
                    else
                    sharedVariables.myseek.manual=true;
                  }
                  if(dg.getArg(1).equals("minseek"))
                  sharedVariables.myseek.minseek=Integer.parseInt(dg.getArg(2));
                  if(dg.getArg(1).equals("maxseek"))
                  sharedVariables.myseek.maxseek=Integer.parseInt(dg.getArg(2));
                  if(dg.getArg(1).equals("color"))
                  sharedVariables.myseek.color=Integer.parseInt(dg.getArg(2));

                  }
                  catch(Exception duiy){}
                  return;
                }
		if(dg.getArg(0).equals("69"))
		{
			try {
				if(myConnection == null)
			 myConnection = new connectionDialog(theMainFrame, sharedVariables, queue, false);
			 else if(!myConnection.isVisible())
			 myConnection = new connectionDialog(theMainFrame, sharedVariables, queue, false);

			 myConnection.setVisible(true);
		 }catch(Exception dduu){}
		}
	 	/*routing console
	 	type=0;// subframe not game, game is 1
		number=0; // first subframe or first game console
	 	*/
	 	if(dg.getArg(0).equals("62"))// told, add person to list with their console type and number for directing pqtells
	 	{
			if(console.type == 0)
			{

			String toldName = dg.getArg(1);
			sharedVariables.F9Manager.addName(dg.getArg(1));
			boolean found = false;
			for(int a=0; a<sharedVariables.toldNames.size(); a++)
				if(sharedVariables.toldNames.get(a).name.equals(toldName))
				{
					found=true;
					sharedVariables.toldNames.get(a).tab=console.number;
					//writeToConsole("updated told\n");
					break;

				}

			if(found == false)
			{
				told newTold = new told();
				newTold.name=toldName;
				newTold.tab=console.number;
				newTold.console=0;

				sharedVariables.toldNames.add(newTold);
			//	writeToConsole("added told\n");

			}

			}// console.type==0;

		}

		if(dg.getArg(0).equals("28"))
		{

			String chatTime = "";
			String chatTime2 = "";

			if(sharedVariables.channelTimestamp == true)
			{
				if(sharedVariables.leftTimestamp == false)
				chatTime=getATimestamp();
				else
				chatTime2=getATimestamp();


			}
String thetell = "";
String extraSpacing = "";
if(sharedVariables.channelNumberLeft == false)
{
// old format channel after name
			thetell= chatTime2 + dg.getArg(2) + chatTime + "(" + dg.getArg(1) + ")"  + ": " + dg.getArg(4) + "\n";
			if(!dg.getArg(3).equals(""))
				thetell= chatTime2 + dg.getArg(2) + chatTime + "(" + dg.getArg(3) + ")"+ "(" + dg.getArg(1) + ")"  + ": " + dg.getArg(4) + "\n";
}
else
{


			if(dg.getArg(1).length() == 1)
				extraSpacing = "  ";
			else if(dg.getArg(1).length() == 2)
				extraSpacing = " ";

			 thetell= chatTime2  + extraSpacing +  dg.getArg(1) + " " + dg.getArg(2) + chatTime  + ": " + dg.getArg(4) + "\n";
			if(!dg.getArg(3).equals(""))
				thetell= chatTime2  + extraSpacing  + dg.getArg(1) + " " + dg.getArg(2) + chatTime + "(" + dg.getArg(3) + ")"  + ": " + dg.getArg(4) + "\n";
}

	int [] cindex2 = new int[sharedVariables.maxConsoleTabs];
		cindex2[0]=0; // default till we know more is its not going to main

		int tempInt=Integer.parseInt(dg.getArg(1));
		boolean goTab=false;
		for(int b=1; b< sharedVariables.maxConsoleTabs; b++)
		{

			if(sharedVariables.console[b][tempInt]==1)
			{	cindex2[b]=1;

				goTab=true;
			}
			else
				cindex2[b]=0;


		}

					Color channelcolor;
					Integer num= new Integer(dg.getArg(1));



					int num1 = num.intValue();

SimpleAttributeSet attrs = new SimpleAttributeSet();


if(sharedVariables.style[num1] > 0)
{


	if(sharedVariables.style[num1] == 1 || sharedVariables.style[num1] == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.style[num1] == 2 || sharedVariables.style[num1] == 3)
		StyleConstants.setBold(attrs, true);

}


if(sharedVariables.channelOn[num1]==1)
{	channelcolor=sharedVariables.channelColor[num1];

	//StyleConstants.setForeground(attrs, channelcolor);
}
else
{	channelcolor=sharedVariables.defaultChannelColor;

	//StyleConstants.setForeground(attrs, channelcolor);
}


messageStyles myStyles = new messageStyles();
if(chatTime2.length() > 0)
{
	myStyles.top=4;
	myStyles.blocks[0]=chatTime2.length() ;
	myStyles.blocks[1]=dg.getArg(1).length() + 1 + chatTime2.length() + extraSpacing.length();
	myStyles.blocks[2]=myStyles.blocks[1] + dg.getArg(2).length();
	myStyles.colors[0] = sharedVariables.chatTimestampColor;
	myStyles.colors[1] = channelcolor;
	//myStyles.colors[2] = sharedVariables.qtellChannelNumberColor;
	myStyles.colors[2] = getNameColor(channelcolor);
	if(dg.getArg(3).equals(""))
	{
	myStyles.blocks[3]=thetell.length();
	myStyles.colors[3] = channelcolor;

	}
	else
	{	myStyles.blocks[3]=myStyles.blocks[2] + dg.getArg(3).length() + 2;
		//myStyles.colors[3] = sharedVariables.channelTitlesColor;
		myStyles.colors[3]=channelcolor.brighter();
		myStyles.blocks[4]=thetell.length();
		myStyles.colors[4] = channelcolor;
		myStyles.top=5;

	}
}
else
{
	myStyles.top=3;
	myStyles.blocks[0]=dg.getArg(1).length() + 1 + extraSpacing.length();
	myStyles.blocks[1]=myStyles.blocks[0] + dg.getArg(2).length();
	myStyles.colors[0] = channelcolor;
	//myStyles.colors[1]= sharedVariables.qtellChannelNumberColor;
	myStyles.colors[1] = getNameColor(channelcolor);
	if(dg.getArg(3).equals(""))
	{
		myStyles.blocks[2]=thetell.length();
		myStyles.colors[2] = channelcolor;
	}
	else
	{
		myStyles.blocks[2]=myStyles.blocks[1] + dg.getArg(3).length() + 2;
		//myStyles.colors[2]= sharedVariables.channelTitlesColor;
		myStyles.colors[2]=channelcolor.brighter();
		myStyles.blocks[3]=thetell.length();
		myStyles.colors[3]= channelcolor;
		myStyles.top=4;
	}


}

if(sharedVariables.channelNumberLeft == false)
myStyles=null;


if(goTab==true && sharedVariables.mainAlso[Integer.parseInt(dg.getArg(1))] == true)
	cindex2[0]=1;// its going to main and tab. we set this so we can pass cindex2 to docwriter letting it know all tabs things go to for new info updates

for(int b=1; b<sharedVariables.maxConsoleTabs; b++)
if(cindex2[b]==1)
{

if(chatTime2.length() > 0 && myStyles !=null && sharedVariables.tabStuff[b].timestampColor!=null)
myStyles.colors[0]=sharedVariables.tabStuff[b].timestampColor;

StyledDocument doc=sharedVariables.mydocs[b];

processLink2(doc, thetell, channelcolor, b, maxLinks, SUBFRAME_CONSOLES, attrs, cindex2, myStyles);
if(chatTime2.length() > 0 && myStyles !=null )
myStyles.colors[0]=sharedVariables.chatTimestampColor;

}

if(goTab==false || sharedVariables.mainAlso[Integer.parseInt(dg.getArg(1))] == true)
{
if(chatTime2.length() > 0 && myStyles !=null && sharedVariables.tabStuff[0].timestampColor!=null)
myStyles.colors[0]=sharedVariables.tabStuff[0].timestampColor;

StyledDocument	doc=sharedVariables.mydocs[0];
	processLink2(doc, thetell, channelcolor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, cindex2, myStyles);
	if(chatTime2.length() > 0 && myStyles !=null )
	myStyles.colors[0]=sharedVariables.chatTimestampColor;

}


}// end channel tell




if(dg.getArg(0).equals("31"))// tell
{
			String chatTime = "";
			String chatTime2 = "";

			if(sharedVariables.tellTimestamp == true)
			{
				if(sharedVariables.leftTimestamp == false)
				chatTime=getATimestamp();
				else
				chatTime2=getATimestamp();


			}

	String thetell="";
	// arg 4 The type is 0 for "say", 1 for "tell", 2 for "ptell"
	String tellType= ": "; // was " tells you: "
	if(dg.getArg(4).equals("0"))
		tellType = " says: ";
	if(dg.getArg(4).equals("2"))
		tellType= " ptells: ";
	if(dg.getArg(4).equals("4"))
		tellType= " atells: ";

	// debug for type -- tellType = tellType + " (" + dg.getArg(4) + ") ";

	if(dg.getArg(2).equals(""))
		thetell=chatTime2 +  dg.getArg(1) + chatTime + tellType + dg.getArg(3) + "\n";
	else
		thetell=chatTime2 +  dg.getArg(1) + chatTime + "(" + dg.getArg(2) + ")" + tellType + dg.getArg(3) + "\n";

	sharedVariables.lasttell=dg.getArg(1); // obsolete but why not leave the data
	sharedVariables.F9Manager.addName(dg.getArg(1));
	StyledDocument doc=sharedVariables.mydocs[sharedVariables.looking[sharedVariables.tellconsole]];
int direction = sharedVariables.looking[sharedVariables.tellconsole];
if(sharedVariables.tellsToTab == true)
{
	direction = sharedVariables.tellTab;
	doc=sharedVariables.mydocs[direction];
}
/*** check if forced to tab ****/
boolean him = false;
boolean makeASound=true;
for(int ab=0; ab<sharedVariables.toldTabNames.size(); ab++)
{
	if(sharedVariables.toldTabNames.get(ab).name.equals(dg.getArg(1)))
	{
		direction=sharedVariables.toldTabNames.get(ab).tab;
		doc=sharedVariables.mydocs[direction];
		him=true;
		makeASound=sharedVariables.toldTabNames.get(ab).sound;
		break;
	}
}


SimpleAttributeSet attrs = new SimpleAttributeSet();
	if(sharedVariables.tellStyle == 1 || sharedVariables.tellStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.tellStyle == 2 || sharedVariables.tellStyle == 3)
		StyleConstants.setBold(attrs, true);

messageStyles myStyles = new messageStyles();
if(!dg.getArg(4).equals("1"))
myStyles=null;
else
{


if(chatTime2.length() > 0)
{
	myStyles.top=3;
	myStyles.blocks[0]=chatTime2.length() ;
	myStyles.blocks[1]=dg.getArg(1).length()  + chatTime2.length();

	myStyles.colors[0] = sharedVariables.chatTimestampColor;
	myStyles.colors[1] = sharedVariables.tellNameColor;

	if(dg.getArg(2).equals(""))
	{
	myStyles.blocks[2]=thetell.length();
	if(sharedVariables.tabStuff[direction].tellcolor == null)
		myStyles.colors[2] = sharedVariables.tellcolor;

	else
	myStyles.colors[2] = sharedVariables.tabStuff[direction].tellcolor;
	}
	else
	{	myStyles.blocks[2]=myStyles.blocks[1] + dg.getArg(2).length() + 2;
		myStyles.colors[2] = sharedVariables.channelTitlesColor;
		myStyles.blocks[3]=thetell.length();
	if(sharedVariables.tabStuff[direction].tellcolor == null)
		myStyles.colors[3] = sharedVariables.tellcolor;

	else
	myStyles.colors[3] = sharedVariables.tabStuff[direction].tellcolor;
		myStyles.top=4;

	}
}
else
{
	myStyles.top=2;
	myStyles.blocks[0]=dg.getArg(1).length()  ;
	myStyles.colors[0] = sharedVariables.tellNameColor;

	if(dg.getArg(2).equals(""))
	{
		myStyles.blocks[1]=thetell.length();
	if(sharedVariables.tabStuff[direction].tellcolor == null)
		myStyles.colors[1] = sharedVariables.tellcolor;

	else
	myStyles.colors[1] = sharedVariables.tabStuff[direction].tellcolor;
	}
	else
	{
		myStyles.blocks[1]=myStyles.blocks[0] + dg.getArg(2).length() + 2;
		myStyles.colors[1]= sharedVariables.channelTitlesColor;
		myStyles.blocks[2]=thetell.length();
			if(sharedVariables.tabStuff[direction].tellcolor == null)
				myStyles.colors[2] = sharedVariables.tellcolor;

			else
			myStyles.colors[2] = sharedVariables.tabStuff[direction].tellcolor;

		myStyles.top=3;
	}


}
}// end if its type 1 a tell
if(chatTime2.length() > 0 && myStyles !=null && sharedVariables.tabStuff[direction].timestampColor!=null)
myStyles.colors[0]=sharedVariables.tabStuff[direction].timestampColor;

if(sharedVariables.tabStuff[direction].tellcolor == null)
	processLink(doc, thetell, sharedVariables.tellcolor, direction, maxLinks, SUBFRAME_CONSOLES, attrs, myStyles);
else
	processLink(doc, thetell, sharedVariables.tabStuff[direction].tellcolor, direction, maxLinks, SUBFRAME_CONSOLES, attrs, myStyles);

try
{

	for(int z=0; z< sharedVariables.openBoardCount; z++)
	{
		if(myboards[z]!=null)
		if(sharedVariables.mygame[z].realname1.equals(dg.getArg(1)) || sharedVariables.mygame[z].realname2.equals(dg.getArg(1)))
		{


		doc=sharedVariables.mygamedocs[z];
			processLink(doc, thetell, sharedVariables.tellcolor, z, maxLinks, GAME_CONSOLES, attrs, myStyles);
		}
	}
}catch(Exception dumb){}

try {
if(sharedVariables.tellsToTab == true && sharedVariables.switchOnTell == true && him == false)
{
	focusOwner whohasit = new focusOwner();
	consoleSubframes[sharedVariables.tellconsole].makeHappen(sharedVariables.tellTab);
	if(!sharedVariables.operatingSystem.equals("mac"))
	giveFocus(whohasit);
}
}catch(Exception donthave){}
Sound ptell;
if(sharedVariables.makeSounds == true && makeASound == true)
ptell=new Sound(sharedVariables.songs[0]);
if(sharedVariables.rotateAways == true)
{
try{

	Random generator = new Random( System.currentTimeMillis() );
	int randomIndex = generator.nextInt( sharedVariables.lanternAways.size());
	String myaway=sharedVariables.lanternAways.get(randomIndex);
	sendMessage("Away " + myaway + "\n");
}catch(Exception d){}
}
/************** remove debug code *************/

//if(dg.getArg(3).equals("make qsuggest"))
// testQsuggest();


/*************** end remove debug code ********/
}// end process tell


if(dg.getArg(0).equals(DG_GAME_NOTIFY))// game notification not really a datagram but i made up one to run it through dg routines
{
StyledDocument doc;
if(consoles[sharedVariables.gameNotifyConsole]== null)
	doc=sharedVariables.mydocs[0];
else
	doc=sharedVariables.mydocs[sharedVariables.gameNotifyConsole];
int index=0;
if(consoles[sharedVariables.gameNotifyConsole]!= null)
index = sharedVariables.gameNotifyConsole;

SimpleAttributeSet attrs = new SimpleAttributeSet();
	if(sharedVariables.nonResponseStyle == 1 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.nonResponseStyle == 2 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setBold(attrs, true);



if(sharedVariables.tabStuff[index].ForColor==null)

	processLink(doc, dg.getArg(1), sharedVariables.ForColor, index, maxLinks, SUBFRAME_CONSOLES, attrs, null);
else
	processLink(doc, dg.getArg(1), sharedVariables.tabStuff[index].ForColor, index, maxLinks, SUBFRAME_CONSOLES, attrs, null);// mike investigate calls against older code
}




		if(dg.getArg(0).equals("32"))// shout
				{

			String chatTime = "";
			String chatTime2 = "";

			if(sharedVariables.shoutTimestamp == true)
			{
				if(sharedVariables.leftTimestamp == false)
				chatTime=getATimestamp();
				else
				chatTime2=getATimestamp();


			}


	SimpleAttributeSet attrs = new SimpleAttributeSet();
					String thetell="";
					if(dg.getArg(3).equals("0"))
					{

							if(sharedVariables.shoutStyle == 1 || sharedVariables.shoutStyle == 3)
								StyleConstants.setItalic(attrs, true);
							if(sharedVariables.shoutStyle == 2 || sharedVariables.shoutStyle == 3)
								StyleConstants.setBold(attrs, true);


						if(dg.getArg(2).length() > 0)
						thetell= chatTime2 + dg.getArg(1) + chatTime + "(" + dg.getArg(2) + ") shouts: " + dg.getArg(4) + "\n";
						else
						thetell=chatTime2 +  dg.getArg(1) + chatTime + " shouts: " + dg.getArg(4) + "\n";

					}
					if(dg.getArg(3).equals("1")){
								if(sharedVariables.shoutStyle == 1 || sharedVariables.shoutStyle == 3)
								StyleConstants.setItalic(attrs, true);
							if(sharedVariables.shoutStyle == 2 || sharedVariables.shoutStyle == 3)
								StyleConstants.setBold(attrs, true);

					if(dg.getArg(4).startsWith("'s "))
						thetell= chatTime2 + "--> " +  dg.getArg(1) + chatTime + dg.getArg(4) + "\n";
					else
						thetell=chatTime2 +  "--> " +  dg.getArg(1) + chatTime + " " + dg.getArg(4) + "\n";
					}
					if(dg.getArg(3).equals("2"))
					{
							if(sharedVariables.sshoutStyle == 1 || sharedVariables.sshoutStyle == 3)
								StyleConstants.setItalic(attrs, true);
							if(sharedVariables.sshoutStyle == 2 || sharedVariables.sshoutStyle == 3)
								StyleConstants.setBold(attrs, true);

						if(dg.getArg(2).length() > 0)
						thetell=chatTime2 +  dg.getArg(1) + chatTime + "(" + dg.getArg(2) + ") s-shouts: " + dg.getArg(4) + "\n";
						else
						thetell=chatTime2 +  dg.getArg(1) + chatTime + " s-shouts: " + dg.getArg(4) + "\n";

					}

					if(dg.getArg(3).equals("3"))
					{
						if(dg.getArg(2).length() > 0)
						thetell= chatTime2 + "Announcement from " + dg.getArg(1) + chatTime + "(" + dg.getArg(2) + ") " + dg.getArg(4) + "\n";
						else
						thetell=chatTime2 +  "Announcement from " + dg.getArg(1) + chatTime + " " + dg.getArg(4) + "\n";

					}

					StyledDocument doc;


/* code to pass an array of consoles this goes to ( just needed if more than one and right now that can just be shouts*/
int [] cindex = new int[sharedVariables.maxConsoleTabs];
for(int z=0; z<sharedVariables.maxConsoleTabs; z++)
{
	if(z== sharedVariables.shoutRouter.shoutsConsole)
		cindex[z]=1;
	else
		cindex[z]=0;
}
if(sharedVariables.shoutsAlso == true)
	cindex[0]=1;
/*** end code for passing to process link where this is going */

if(dg.getArg(3).equals("0") || dg.getArg(3).equals("1"))
{
doc=sharedVariables.mydocs[sharedVariables.shoutRouter.shoutsConsole];
	processLink2(doc, thetell, sharedVariables.shoutcolor, sharedVariables.shoutRouter.shoutsConsole, maxLinks, SUBFRAME_CONSOLES, attrs, cindex, null);
	if(sharedVariables.shoutRouter.shoutsConsole>0 && sharedVariables.shoutsAlso == true)
	{
		doc=sharedVariables.mydocs[0];
			processLink2(doc, thetell, sharedVariables.shoutcolor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, cindex, null);

	}
}
else if(dg.getArg(3).equals("2"))
{
	doc=sharedVariables.mydocs[sharedVariables.shoutRouter.sshoutsConsole];
processLink(doc, thetell, sharedVariables.sshoutcolor, sharedVariables.shoutRouter.sshoutsConsole, maxLinks, SUBFRAME_CONSOLES, attrs, null);
}
else if(dg.getArg(3).equals("3"))
{
	doc=sharedVariables.mydocs[0];
	if(sharedVariables.tabStuff[0].ForColor == null)
	processLink(doc, thetell, sharedVariables.ForColor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, null);
	else
	processLink(doc, thetell, sharedVariables.tabStuff[0].ForColor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, null);

}




}


if(dg.getArg(0).equals("83"))// personal qtell
{
			String thetell= dg.getArg(3) + "\n";

SimpleAttributeSet attrs = new SimpleAttributeSet();
	if(sharedVariables.qtellStyle == 1 || sharedVariables.qtellStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.qtellStyle == 2 || sharedVariables.qtellStyle == 3)
		StyleConstants.setBold(attrs, true);



 		int cindex = sharedVariables.looking[sharedVariables.tellconsole];
			for(int a=0; a<sharedVariables.toldNames.size(); a++)
				if(sharedVariables.toldNames.get(a).name.equals(dg.getArg(1)))
				{
					cindex=sharedVariables.toldNames.get(a).tab;
					break;
				}


		StyledDocument doc=sharedVariables.mydocs[cindex];
							//Style styleQ = doc.addStyle(null, null);
		Color channelcolor;




if(sharedVariables.tabStuff[cindex].qtellcolor == null)
	channelcolor=sharedVariables.qtellcolor;
else
	channelcolor=sharedVariables.tabStuff[cindex].qtellcolor;

		processLink(doc, thetell.replaceAll("\\\\n", "\n"), channelcolor, cindex, maxLinks, SUBFRAME_CONSOLES, attrs, null);
}


















		if(dg.getArg(0).equals("82"))// channel qtell
		{

			String chatTime = "";
			String chatTime2 = "";

			if(sharedVariables.qtellTimestamp == true)
			{
				if(sharedVariables.leftTimestamp == false)
				{
					chatTime=  getATimestamp();
					chatTime = chatTime.replace("(", " ");
					chatTime = chatTime.replace(")", "");
				}
				else
				chatTime2=getATimestamp();


			}


			String mySpaces = "\n    ";
			String extraSpace ="";

for(int cu=0; cu<chatTime.length(); cu++)
	mySpaces = mySpaces + " ";

for(int cu=0; cu<chatTime2.length(); cu++)
	mySpaces = mySpaces + " ";


try {
int thenum=dg.getArg(1).length();
if(thenum==1)
extraSpace = "  ";
if(thenum == 2)
extraSpace = " ";
}
catch(Exception dui){}


			String thetell= chatTime2 +  extraSpace + dg.getArg(1) + chatTime + " " + dg.getArg(4) + "\n";



SimpleAttributeSet attrs = new SimpleAttributeSet();
	if(sharedVariables.qtellStyle == 1 || sharedVariables.qtellStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.qtellStyle == 2 || sharedVariables.qtellStyle == 3)
		StyleConstants.setBold(attrs, true);


		int [] cindex = new int[sharedVariables.maxConsoleTabs];
		cindex[0]=0;// default till we know more
		int tempInt=Integer.parseInt(dg.getArg(1));
		boolean goTab=false;
		for(int b=1; b< sharedVariables.maxConsoleTabs; b++)
		{

			if(sharedVariables.console[b][tempInt]==1)
			{	cindex[b]=1;

				goTab=true;
			}
			else
				cindex[b]=0;


		}



thetell=thetell.replaceAll("\\\\n", mySpaces);

Color channelnumbercolor = sharedVariables.qtellChannelNumberColor;
try
{
	int num1= Integer.parseInt(dg.getArg(1));
if(sharedVariables.channelOn[num1]==1)
	channelnumbercolor=sharedVariables.channelColor[num1];

else
	channelnumbercolor=sharedVariables.defaultChannelColor;

}
catch(Exception dui){}

messageStyles myStyles = new messageStyles();
if(chatTime2.length() > 0)
{
	myStyles.top=3;
	myStyles.blocks[0]=chatTime2.length() ;
	myStyles.blocks[1]=dg.getArg(1).length() + 1 + chatTime2.length() + extraSpace.length();
	myStyles.blocks[2]=thetell.length();
	myStyles.colors[0] = sharedVariables.chatTimestampColor;
	myStyles.colors[1] = channelnumbercolor;
}
else
{
	myStyles.top=2;
	myStyles.blocks[0]=dg.getArg(1).length() + 1 + extraSpace.length();
	myStyles.blocks[1]=thetell.length();
	myStyles.colors[0] = channelnumbercolor;
}



							//Style styleQ = doc.addStyle(null, null);
					Color channelcolor;
					Integer num= new Integer(dg.getArg(1));
					int num1 = num.intValue();

if(goTab == true && sharedVariables.mainAlso[Integer.parseInt(dg.getArg(1))] == true)
	cindex[0]=1;

for(int b=1; b<sharedVariables.maxConsoleTabs; b++)
if(cindex[b]==1)
{


StyledDocument doc=sharedVariables.mydocs[b];
if(chatTime2.length() > 0 && myStyles !=null && sharedVariables.tabStuff[b].timestampColor!=null)
myStyles.colors[0]=sharedVariables.tabStuff[b].timestampColor;




if(sharedVariables.tabStuff[b].qtellcolor == null)
{
	channelcolor=sharedVariables.qtellcolor;

}
else
{
	channelcolor=sharedVariables.tabStuff[b].qtellcolor;
}

if(chatTime2.length() > 0)
{
	myStyles.colors[2]=channelcolor;
}
else
{
	myStyles.colors[1]=channelcolor;
}
processLink2(doc, thetell, channelcolor, b, maxLinks, SUBFRAME_CONSOLES, attrs, cindex, myStyles);
if(chatTime2.length() > 0 && myStyles !=null )
myStyles.colors[0]=sharedVariables.chatTimestampColor;

}
if(goTab == false || sharedVariables.mainAlso[Integer.parseInt(dg.getArg(1))] == true)// it went to tab but it should also go to main
{

if(sharedVariables.tabStuff[0].qtellcolor == null)
	channelcolor=sharedVariables.qtellcolor;
else
	channelcolor=sharedVariables.tabStuff[0].qtellcolor;

if(chatTime2.length() > 0)
{
	myStyles.colors[2]=channelcolor;
}
else
{
	myStyles.colors[1]=channelcolor;
}
StyledDocument	doc=sharedVariables.mydocs[0];

if(chatTime2.length() > 0 && myStyles !=null && sharedVariables.tabStuff[0].timestampColor!=null)
myStyles.colors[0]=sharedVariables.tabStuff[0].timestampColor;


	processLink2(doc, thetell, channelcolor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, cindex, myStyles);
if(chatTime2.length() > 0 && myStyles !=null )
myStyles.colors[0]=sharedVariables.chatTimestampColor;

}
//doc.insertString(doc.getEndPosition().getOffset(), thetell, attrs);
//writeToConsole(doc, cindex);

String body = dg.getArg(4);
// auto observe of tomato

 //started. "observe 431"

int i= body.indexOf("started. \"observe");
int go=0;

String botname = dg.getArg(2);

if(botname.equals("Tomato") && sharedVariables.autoTomato == true)
go=1;
if(botname.equals("Flash") && sharedVariables.autoFlash == true)
go=1;
if(botname.equals("Cooly") && sharedVariables.autoCooly == true)
go=1;
if(botname.equals("WildOne") && sharedVariables.autoWildOne == true)
go=1;
if(botname.equals("Olive") && sharedVariables.autoOlive == true)
go=1;
if(botname.equals("Ketchup") && sharedVariables.autoKetchup == true)
go=1;
if(botname.equals("Slomato") && sharedVariables.autoSlomato == true)
go=1;
if(botname.equals("LittlePer") && sharedVariables.autoLittlePer == true)
go=1;


if(i>-1 && go == 1)
{
	int j=body.indexOf("observe", i);
	if(j>-1)
	{
		int k=body.indexOf('\"', j);
		if(k>-1)
		{
			String command=body.substring(j, k);
			command=command + "\n";
			myoutput output = new myoutput();
			output.data=command;
			output.game=1;
			queue.add(output);
		}
	}
}
		}
/************* bughouse partner **********************/

if(dg.getArg(0).equals("44"))
{
	if(dg.getArg(3).equals("0"))
	sharedVariables.myPartner = "";
	else
	{
		if(!sharedVariables.myname.equals(dg.getArg(1)))
			sharedVariables.myPartner=dg.getArg(1);
		else
			sharedVariables.myPartner=dg.getArg(2);
	}
}

/******************* events list ***********************/
 if(dg.getArg(0).equals("103"))
{
	// index , event description, join, watch, info
	eventsList.addToEvents(dg.getArg(3), dg.getArg(1), dg.getArg(4), dg.getArg(5), dg.getArg(6));
}
 if(dg.getArg(0).equals("104"))
{
	// index , event description, join, watch, info
	eventsList.removeFromEvents(dg.getArg(1));
}

/**************** game list events ********************/
 if(dg.getArg(0).equals("72"))
{
try {
if(myGameList != null)
if(myGameList.isVisible()== true)
myGameList.dispose();
}
catch(Exception disposal){}
Thread.sleep(100);
gameList=new tableClass();
//gameList.resetList();
//	gameList.addToList(dg.getArg(1) + " " + dg.getArg(2), dg.getArg(2));
gameList.type1=dg.getArg(1);
gameList.type2=dg.getArg(2);
if(gameList.type1.equals("history") || gameList.type1.equals("stored"))
gameList.createHistoryListColumns();
Thread.sleep(100);

try {
gameListCreator gameT = new gameListCreator();
Thread gamet = new Thread(gameT);
gamet.start();
/*myGameList = new gameFrame(sharedVariables, queue, gameList);
myGameList.setSize(600,425);
myGameList.setVisible(true);
sharedVariables.desktop.add(myGameList);
try {
	myGameList.setSelected(true);}
	catch(Exception couldnt){}
*/
}
catch(Exception gam){}
}// end 72


// game list item
/*(index id event date time white-name white-rating black-name black-rating rated rating-type
	 wild init-time-W inc-W init-time-B inc-B eco status color mode {note} here)
*/
if(dg.getArg(0).equals("73"))
{
String gameString = dg.getArg(1) + " |" + dg.getArg(6) + "(" + dg.getArg(7) + ") " + dg.getArg(8) + "(" + dg.getArg(9) + ")| ";
gameString = gameString + dg.getArg(13) + " " + dg.getArg(14) + " | " + dg.getArg(17);
//gameList.addToList(gameString, dg.getArg(1));
Vector<String> data = new Vector();
data.add(dg.getArg(1));
data.add(dg.getArg(6));
data.add(dg.getArg(7));
data.add(dg.getArg(8));
data.add(dg.getArg(9));



/*void addHistoryRow(String index, String whiteName, String blackName, String whiteRating, String blackRating, String date, String time, String whitetime, String whiteinc,
	String rated, String ratedType, String wild, String color, String mode)
*/
	gameItem myItem = new gameItem();

if(gameList.type1.equals("liblist") || gameList.type1.equals("search"))
myItem.addSearchLiblistRow(dg.getArg(1), dg.getArg(6), dg.getArg(8), dg.getArg(7), dg.getArg(9), dg.getArg(4), dg.getArg(5),dg.getArg(13), dg.getArg(14),
	dg.getArg(10), dg.getArg(11), dg.getArg(12), dg.getArg(17), dg.getArg(18), dg.getArg(19), dg.getArg(20), gameList);

else
{
	myItem.addHistoryRow(dg.getArg(1), dg.getArg(6), dg.getArg(8), dg.getArg(7), dg.getArg(9), dg.getArg(4), dg.getArg(5),dg.getArg(13), dg.getArg(14),
	dg.getArg(10), dg.getArg(11), dg.getArg(12), dg.getArg(17), dg.getArg(18), dg.getArg(19), dg.getArg(20), gameList);
}
}
/**************qsuggest *******************/

if(dg.getArg(0).equals("63"))
{
try {
	if(qsuggestDialog!=null)
	{

		qsuggestDialog.dispose();
		qsuggestDialog=null;
	}
}
catch(Exception qsug){}
try{
if(sharedVariables.showQsuggest == true)
{
qsuggestDialog=new qsuggest(masterFrame, false, queue);
qsuggestDialog.suggestion(dg.getArg(2), dg.getArg(1), dg.getArg(6), dg.getArg(4));// text command id (2,1,6)
qsuggestDialog.setVisible(true);
}// if show qsuggest == true


int tomato=0;
if(dg.getArg(4).equals("Tomato"))
tomato=46;
if(dg.getArg(4).equals("Flash"))
tomato=49;
if(dg.getArg(4).equals("Slomato"))
tomato=222;
if(dg.getArg(4).equals("Wildone"))
tomato=223;
if(dg.getArg(4).equals("Cooly"))
tomato=224;
if(dg.getArg(4).equals("LittlePer"))
tomato=225;
if(dg.getArg(4).equals("Pear"))
tomato=227;
if(dg.getArg(4).equals("Ketchup"))
tomato=228;
if(dg.getArg(4).equals("Olive"))
tomato=230;
if(sharedVariables.showQsuggest == false || tomato != 0)
{



		int [] cindex = new int[sharedVariables.maxConsoleTabs];
		boolean goTab=false;
		for(int b=1; b< sharedVariables.maxConsoleTabs; b++)
		{

			if(sharedVariables.console[b][tomato]==1 && tomato !=0)
			{	cindex[b]=1;

				goTab=true;
			}
			else
				cindex[b]=0;


		}
String theTell = dg.getArg(1);
if(theTell.startsWith("Match") || theTell.startsWith("match"))
theTell = "\"" + theTell + "\"";
SimpleAttributeSet attrs = new SimpleAttributeSet();
	if(sharedVariables.qtellStyle == 1 || sharedVariables.qtellStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.qtellStyle == 2 || sharedVariables.qtellStyle == 3)
		StyleConstants.setBold(attrs, true);



Color channelcolor;

for(int b=1; b<sharedVariables.maxConsoleTabs; b++)
if(cindex[b]==1)
{
StyledDocument doc=sharedVariables.mydocs[b];



if(sharedVariables.tabStuff[b].qtellcolor == null)
	channelcolor=sharedVariables.qtellcolor;
else
	channelcolor=sharedVariables.tabStuff[0].qtellcolor;
processLink(doc, dg.getArg(4) + " suggests: " + theTell + "\n", channelcolor, b, maxLinks, SUBFRAME_CONSOLES, attrs, null);
}
if(goTab==false || sharedVariables.mainAlso[tomato] == true)// it went to tab but it should also go to main
{

if(sharedVariables.tabStuff[0].qtellcolor == null)
	channelcolor=sharedVariables.qtellcolor;
else
	channelcolor=sharedVariables.tabStuff[0].qtellcolor;


StyledDocument	doc=sharedVariables.mydocs[0];



	processLink(doc, dg.getArg(4) + " suggests: " + theTell + "\n", channelcolor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, null);
}
}// show qsuggest false



}
catch(Exception badq){}

}

if(dg.getArg(0).equals("99"))
{
try {
	if(qsuggestDialog!=null)
	{

		if(dg.getArg(1).equals(qsuggestDialog.id))
		{
		qsuggestDialog.dispose();
		qsuggestDialog=null;
		}
	}
}
catch(Exception qsug){}

}// end retract qsuggest

/****************** seek events ***********************/
 if(dg.getArg(0).equals("50"))
{
		String seekstring;
		String ratedness="r";

	// fill for seek graph
	String sIndex = dg.getArg(1);
	String sName = dg.getArg(2);
	String sTitles=dg.getArg(3);
	String sRating = dg.getArg(4);
	String sProvisional = dg.getArg(5);
	String sWild = dg.getArg(6);
	String sRatingType = dg.getArg(7);
	// 8 9 10 time inc rating
	String sTime= dg.getArg(8);
	String sInc=dg.getArg(9);
	if(dg.getArg(10).equals("1"))
			ratedness="r";
		else
			ratedness="u";
	String sRated= ratedness;

	String sRange="";
	if(!(dg.getArg(12).equals("0") && dg.getArg(13).equals("9999")))
			sRange=dg.getArg(12) + "-" + dg.getArg(13);
	String sColor = "";


	if(dg.getArg(11).equals("0"))
			sColor= "black";
	else if(dg.getArg(11).equals("1"))
		    sColor= "white";

	String sFormula="";
	String sManual="";
	if(!dg.getArg(14).equals("1"))
			sManual= "m";

	if(dg.getArg(15).equals("1"))
			sFormula="f";

	sharedVariables.graphData.addSeek(sIndex, sName, sTitles, sRating, sProvisional, sWild, sRatingType, sTime, sInc, sRated, sRange, sColor, sFormula, sManual);
	if(seekGraph.isVisible())
		seekGraph.mypanel.repaint();



		if(dg.getArg(3).equals(""))
			seekstring= dg.getArg(2) + " " + dg.getArg(4)  + " " + dg.getArg(8) + " " + dg.getArg(9)   + " " + ratedness  ;
		else
			seekstring= dg.getArg(2) + "(" + dg.getArg(3) + ") " + dg.getArg(4) + " " + dg.getArg(8) + " " + dg.getArg(9)   + " " + ratedness ;
		if(!dg.getArg(6).equals("0"))
		seekstring = seekstring + " w" + dg.getArg(6);
		if(!(dg.getArg(12).equals("0") && dg.getArg(13).equals("9999")))
			seekstring = seekstring + " " + dg.getArg(12) + "-" + dg.getArg(13);
		if(dg.getArg(11).equals("0"))
			seekstring = seekstring + " " + "black";
		else if(dg.getArg(11).equals("1"))
		    seekstring = seekstring + " " + "white";

		if(!dg.getArg(14).equals("1"))
			seekstring = seekstring + " " + "m";
		if(dg.getArg(15).equals("1"))
			seekstring = seekstring + " " + "f";

	if(dg.getArg(3).contains("C"))
	computerSeeksList.addToList(seekstring, dg.getArg(1));
	else
	seeksList.addToList(seekstring, dg.getArg(1));
}
 if(dg.getArg(0).equals("51"))
{
	seeksList.removeFromList(dg.getArg(1));
	computerSeeksList.removeFromList(dg.getArg(1));
	sharedVariables.graphData.removeSeek(dg.getArg(1));
		if(seekGraph.isVisible())
			seekGraph.mypanel.repaint();

}

 /****************** notify events ***********************/
 if(dg.getArg(0).equals("81"))
{dummyResponse = !dummyResponse;

}
 if(dg.getArg(0).equals("67"))
{
	notifyList.notifyStateChanged(dg.getArg(1) , dg.getArg(2) );
}
 if(dg.getArg(0).equals("64"))
{
		notifyList.notifyStateChanged(dg.getArg(1) , dg.getArg(2) );
		if(dummyResponse == false)
	{
boolean supressLogins=sharedVariables.getNotifyControllerState(dg.getArg(1));

	String theNotifyTell = "Notification: " + dg.getArg(1) + " has arrived.\n";
	StyledDocument doc;
// we use main console now for notifications -- 0

SimpleAttributeSet attrs = new SimpleAttributeSet();
	if(sharedVariables.nonResponseStyle == 1 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.nonResponseStyle == 2 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setBold(attrs, true);


	doc=sharedVariables.mydocs[0];

if(supressLogins == false)
{
	if(sharedVariables.tabStuff[0].ForColor == null)
	processLink(doc, theNotifyTell, sharedVariables.ForColor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, null);
else
	processLink(doc, theNotifyTell, sharedVariables.tabStuff[0].ForColor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, null);

try {
	if(sharedVariables.makeSounds == true && sharedVariables.specificSounds[4]== true)
{
	Sound nsound=new Sound(sharedVariables.songs[4]);
}
}
catch(Exception notifysound){}

}// end of if suppress logins false

}// end dummy response
}



 if(dg.getArg(0).equals("65"))
{
	boolean supressLogins=sharedVariables.getNotifyControllerState(dg.getArg(1));
String theNotifyTell = "Notification: " + dg.getArg(1) + " has departed.\n";
	StyledDocument doc;
// we use main console now for notifications -- 0
	doc=sharedVariables.mydocs[0];
SimpleAttributeSet attrs = new SimpleAttributeSet();
	if(sharedVariables.nonResponseStyle == 1 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.nonResponseStyle == 2 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setBold(attrs, true);

notifyList.removeFromList(dg.getArg(1));

if(supressLogins == false)
{
	if(sharedVariables.tabStuff[0].ForColor == null)
		processLink(doc, theNotifyTell, sharedVariables.ForColor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, null);
	else
		processLink(doc, theNotifyTell, sharedVariables.tabStuff[0].ForColor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, null);


try {
	if(dummyResponse == false)
	if(sharedVariables.makeSounds == true && sharedVariables.specificSounds[4]== true)
{
	Sound nsound=new Sound(sharedVariables.songs[4]);
}
}
catch(Exception notifysound){}
}// end if suppress logins false

}// end if notify left

 if(dg.getArg(0).equals("86"))// logpgn
{

	try
	{
	/*writeToConsole(dg.getArg(1) + "\n");
	writeToConsole(dg.getArg(2) + "\n");
	writeToConsole(dg.getArg(3) + "\n");
	writeToConsole(dg.getArg(4) + "\n");
	writeToConsole(dg.getArg(5) + "\n");
	writeToConsole(dg.getArg(6) + "\n");
	writeToConsole(dg.getArg(7) + "\n");
	writeToConsole(dg.getArg(8) + "\n");
	writeToConsole(dg.getArg(9) + "\n");
	writeToConsole(dg.getArg(10) + "\n");
	writeToConsole(dg.getArg(11) + "\n");
	writeToConsole(dg.getArg(12) + "\n");
	writeToConsole(dg.getArg(13) + "\n");
	writeToConsole(dg.getArg(14) + "\n");
	writeToConsole(dg.getArg(15) + "\n");
	writeToConsole("16" + dg.getArg(16) + "\n");
	writeToConsole("17" + dg.getArg(17) + "\n");
	writeToConsole("18" + dg.getArg(18) + "\n");
	*/
	String pgnlog = "";
	for(int pgnnum=1; pgnnum < dg.argc; pgnnum++)
	{
		if(console.type != 2 && console.type != 3)//logpgn and savepgn
		writeToConsole(dg.getArg(pgnnum));
		else
		{
			String temp= dg.getArg(pgnnum) + "\r\n";
			temp= temp.replace("\0", "");
			temp= temp.replace("\031", "");
			pgnlog=pgnlog + temp;
		}
	}
if(console.type  == 3)
{
				FileWriter fstream = new FileWriter(sharedVariables.defaultpgn, true);

try {
			   BufferedWriter out = new BufferedWriter(fstream);
	    		out.write(pgnlog);
	    		//Close the output stream
	    		out.close();
			}
			catch(Exception e)
			{  };

}
else if(console.type == 2)
{
				FileWriter fstream = new FileWriter("lantern_" + sharedVariables.myname + ".pgn", true);

try {
			   BufferedWriter out = new BufferedWriter(fstream);
	    		out.write(pgnlog);
	    		//Close the output stream
	    		out.close();
			}
			catch(Exception e)
			{  };

}


/*	pgnlog = pgnlog + dg.getArg(1) + "\r\n";
	pgnlog = pgnlog + dg.getArg(2) + "\r\n";
	pgnlog = pgnlog + dg.getArg(3) + "\r\n";
	pgnlog = pgnlog + dg.getArg(4) + "\r\n";
	pgnlog = pgnlog + dg.getArg(5) + "\r\n";
	pgnlog = pgnlog + dg.getArg(6) + "\r\n";
	pgnlog = pgnlog + dg.getArg(7) + "\r\n";
	pgnlog = pgnlog + dg.getArg(8) + "\r\n";
	pgnlog = pgnlog + dg.getArg(9) + "\r\n";
	pgnlog = pgnlog + dg.getArg(10) + "\r\n";
	pgnlog = pgnlog + dg.getArg(11) + "\r\n";
	pgnlog = pgnlog + dg.getArg(12) + "\r\n";
	pgnlog = pgnlog + dg.getArg(13) + "\r\n";
	pgnlog = pgnlog + dg.getArg(14) + "\r\n";
	pgnlog = pgnlog + dg.getArg(15) + "\r\n";
	pgnlog = pgnlog + dg.getArg(16) + "\r\n";
	pgnlog = pgnlog + dg.getArg(17) + "\r\n";
	pgnlog = pgnlog + dg.getArg(18) + "\r\n";

*/


	}catch(Exception loge){writeToConsole("Exception in pgn");}
}
		/******************************* game events ***************************************/
		if(dg.getArg(0).equals("18") || dg.getArg(0).equals("40"))// 12 DG_GAME_STARTED  18/ observing
		{

					newBoardData temp = new newBoardData();
					temp.type=0;
					temp.arg1=dg.getArg(1);
					temp.arg2=dg.getArg(2);
					temp.arg3=dg.getArg(3);
					temp.arg4=dg.getArg(4);
					temp.arg5=dg.getArg(5);
					temp.arg6=dg.getArg(6);
					temp.arg7=dg.getArg(7);
					temp.arg8=dg.getArg(8);
					temp.arg13=dg.getArg(13);
					temp.arg14=dg.getArg(14);
					temp.arg16=dg.getArg(16);
					temp.arg17=dg.getArg(17);



					temp.arg11=dg.getArg(11);
					temp.dg=18;
						gamequeue.add(temp);


		}
		if(dg.getArg(0).equals("12"))// 12 DG_GAME_STARTED  18/ observing
				{

										newBoardData temp = new newBoardData();
										temp.type=0;
										temp.arg1=dg.getArg(1);
									temp.arg2=dg.getArg(2);
										temp.arg3=dg.getArg(3);
										temp.arg4=dg.getArg(4);
										temp.arg11=dg.getArg(11);
					temp.arg5=dg.getArg(5);
					temp.arg6=dg.getArg(6);
					temp.arg7=dg.getArg(7);
					temp.arg8=dg.getArg(8);
					temp.arg13=dg.getArg(13);
					temp.arg14=dg.getArg(14);
					temp.arg16=dg.getArg(16);
					temp.arg17=dg.getArg(17);

						temp.dg=12;
						gamequeue.add(temp);


		}
		if(dg.getArg(0).equals("15"))// 15 DG_GAME_STARTED my game  18/ observing
						{

					newBoardData temp = new newBoardData();
					temp.type=1;
					temp.arg1=dg.getArg(1);
				    temp.arg2=dg.getArg(2);
					temp.arg3=dg.getArg(3);
					temp.arg4=dg.getArg(4);
					temp.arg11=dg.getArg(11);
					temp.arg5=dg.getArg(5);
					temp.arg6=dg.getArg(6);
					temp.arg7=dg.getArg(7);
					temp.arg8=dg.getArg(8);
					temp.arg13=dg.getArg(13);
					temp.arg14=dg.getArg(14);
					temp.arg16=dg.getArg(16);
					temp.arg17=dg.getArg(17);

					temp.dg=15;
					gamequeue.add(temp);


				}

		if(dg.getArg(0).equals("42"))// 42 illegal move
		{

			newBoardData temp = new newBoardData();
			temp.dg=42;
			temp.arg1=dg.getArg(1);
			gamequeue.add(temp);


		}
		if(dg.getArg(0).equals("59"))// 59 circle
		{
			newBoardData temp = new newBoardData();
			temp.dg=59;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);
			temp.arg3=dg.getArg(3);


			gamequeue.add(temp);

		}
		if(dg.getArg(0).equals("43"))// 43 my game relation
		{
			newBoardData temp = new newBoardData();
			temp.dg=43;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);



			gamequeue.add(temp);

		}



		if(dg.getArg(0).equals("60"))// 60 arrow
		{
			newBoardData temp = new newBoardData();
			temp.dg=60;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);
			temp.arg3=dg.getArg(3);
			temp.arg4=dg.getArg(4);

			gamequeue.add(temp);

		}


		if(dg.getArg(0).equals("23"))// 23 backward
		{
			newBoardData temp = new newBoardData();
			temp.dg=23;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);
			gamequeue.add(temp);

		}

		if(dg.getArg(0).equals("39"))// 39 backward
		{
			newBoardData temp = new newBoardData();
			temp.dg=39;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);
			gamequeue.add(temp);

		}
		if(dg.getArg(0).equals("37"))// 37 bughouse holdings
		{
			newBoardData temp = new newBoardData();
			temp.dg=37;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);
			temp.arg3=dg.getArg(3);
			gamequeue.add(temp);

		}



		if(dg.getArg(0).equals("70"))// 70 fen
		{
			newBoardData temp = new newBoardData();
			temp.dg=70;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);
			gamequeue.add(temp);

		}
		if(dg.getArg(0).equals("22"))// 22 taleback
		{
			newBoardData temp = new newBoardData();
			temp.dg=22;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);
			gamequeue.add(temp);

		}



		if(dg.getArg(0).equals("25"))// 25 DG_MOVE_LIST
		{
			/*gamenum=getGameBoard(dg.getArg(1));
				if(gamenum == sharedVariables.NOT_FOUND_NUMBER || myboards[gamenum]== null)
			return;
			for(int a= 3; a < dg.argc; a++)
			myboards[gamenum].moveSent(dg.getArg(1), dg.getArg(a)); // pass game number
			repaintBoards(gamenum);
			//myboards[0].loadMoveList(dg.getArg(1)); // pass game number
			*/
			// we process just the board of datgram move_list.
			// the moves are all sent as instances of send_moves or dg 24 below
		/*JFrame gotit=new JFrame();
			gotit.setVisible(true);
			gotit.setSize(100,100);
		*/
		newBoardData temp = new newBoardData();
			temp.dg=25;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);
			gamequeue.add(temp);
			ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

			Lock readLock = rwl.readLock();

			readLock.lock();
			for(int a= 3; a < dg.argc; a++)
			{
				temp = new newBoardData();
				temp.dg=24;
				temp.arg1=dg.getArg(1);
				String tempmove= dg.getArg(a);
                                int sss=tempmove.indexOf(" ");
                                temp.arg2=tempmove.substring(sss+1, tempmove.length());
                                temp.arg3=tempmove.substring(0, sss);
			//	writeToConsole("25 and " + dg.getArg(a) + "\n");
				gamequeue.add(temp);
			}
			 readLock.unlock();
		}
		if(dg.getArg(0).equals("24"))// 24 DG_SEND_MOVES
		{


			newBoardData temp = new newBoardData();
			temp.dg=24;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(3); // we reverse order cause traditionaly sans is arg2
                        temp.arg3=dg.getArg(2);

			//writeToConsole("in dg send moves and " + "arg1: " + temp.arg1 + " arg2: " + temp.arg2 + " arg3: " + temp.arg3 + "\n");

			gamequeue.add(temp);





		}
		if(dg.getArg(0).equals("13"))// 13 DG_GAME_RESULT // 16 game i'm observingt result
		{
			newBoardData temp = new newBoardData();
			if(dg.getArg(2).equals("1"))
			temp.dg=1600;  // becomes examined
			else
			temp.dg=13;

			temp.arg1=dg.getArg(1);
			gamequeue.add(temp);

		}

		if(dg.getArg(0).equals("14"))// 14 examine game gone
		{
			newBoardData temp = new newBoardData();
			temp.dg=14;
			temp.arg1=dg.getArg(1);
			gamequeue.add(temp);

		}

		if(dg.getArg(0).equals("17"))// 14 examine game gone
		{
			newBoardData temp = new newBoardData();
			temp.dg=17;
			temp.arg1=dg.getArg(1);
			gamequeue.add(temp);

		}
		if(dg.getArg(0).equals("26"))// 26 kib
		{
					newBoardData temp = new newBoardData();
					temp.dg=26;
					temp.arg1=dg.getArg(1);
					temp.arg2=dg.getArg(2);
					temp.arg3=dg.getArg(3);
					temp.arg4=dg.getArg(4);
					temp.arg5=dg.getArg(5);
					gamequeue.add(temp);

		}


		if(dg.getArg(0).equals("77"))// 77 game message
		{
					newBoardData temp = new newBoardData();
					temp.dg=77;
					temp.arg1=dg.getArg(1);
					temp.arg2=dg.getArg(2);
					gamequeue.add(temp);

		}



		if(dg.getArg(0).equals("41"))// 41 refresh
		{
			newBoardData temp = new newBoardData();
			temp.dg=41;
			temp.arg1=dg.getArg(1);
			gamequeue.add(temp);

		}


		if(dg.getArg(0).equals("19"))// 19 stop bobserving // we use relation to my game  now.  19 sent when your mexed
		{
		/*	newBoardData temp = new newBoardData();
			temp.dg=19;
			temp.arg1=dg.getArg(1);
			gamequeue.add(temp);
		*/
		}

		// below seems to make me stop observing when game is examined but has result like follow astrobot
		if(dg.getArg(0).equals("16"))// 13 DG_GAME_RESULT // 16 game i'm observingt result
		{

			newBoardData temp = new newBoardData();
			if(dg.getArg(2).equals("1"))
			temp.dg=1600;  // becomes examined
			else
			temp.dg=16;

			temp.arg1=dg.getArg(1);
			gamequeue.add(temp);

		}
		if(dg.getArg(0).equals("56"))// 13 DG_GAME_RESULT // 16 game i'm observingt result// update clocks
		{

			newBoardData temp = new newBoardData();
			temp.dg=56;
			temp.arg1=dg.getArg(1);
			temp.arg2=dg.getArg(2);
			temp.arg3=dg.getArg(3);
			gamequeue.add(temp);

		}




	}// end try
	catch(Exception e)
	{ }
	}

int getGameNumber(String icsGameNumber)
{
	try {

		int a= Integer.parseInt(icsGameNumber);
	return a;
	}
	catch(Exception e)
	{}
	return -1;
}
	int getGameBoard(String s)
	{
		for(int a=0; a<sharedVariables.maxGameTabs; a++)
				{
					if(myboards[a]!=null)
					{
					int j=getGameNumber(s);
					if(sharedVariables.mygame[a].myGameNumber ==j)
					return a;
					}
				}
	return sharedVariables.NOT_FOUND_NUMBER;

	}

	public void repaintBoards(int num)
	{

		for(int a=0; a<sharedVariables.openBoardCount; a++)
		{
			try {
		if(myboards[a]!=null)
		if(myboards[a].isVisible() == true)
		myboards[a].repaint();
		}
		catch(Exception e){}
		}

	}

void writeToConsole(String s)
{

	StyledDocument doc=consoles[0].getStyledDocument();
							try {
								doc.insertString(doc.getEndPosition().getOffset(), s + "\n", null);


							consoles[0].setStyledDocument(doc);
							}
							catch(Exception e)
							{
							}

}
void writeToSubConsole(String s, int n)
{

	StyledDocument doc=consoles[n].getStyledDocument();
							try {
								doc.insertString(doc.getEndPosition().getOffset(), s, null);


							consoles[n].setStyledDocument(doc);
							}
							catch(Exception e)
							{
							}

}

 boolean sharingChannel(String user)
 {
 	for(int a=0; a < sharedVariables.channelNamesList.size(); a++)
		if(sharedVariables.channelNamesList.get(a).isOnList(user))
				return true;

 return false;
 }
 void globalNotifyAlert(String user, boolean connecting)
{

String theNotifyTell = "Lantern Notification: " + user + " has arrived.\n";
if(connecting == false)
theNotifyTell = "Lantern Notification: " + user + " has left all shared channels.\n";

SimpleAttributeSet attrs = new SimpleAttributeSet();
	if(sharedVariables.nonResponseStyle == 1 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.nonResponseStyle == 2 || sharedVariables.nonResponseStyle == 3)
		StyleConstants.setBold(attrs, true);

for(int z=0; z<sharedVariables.maxConsoleTabs; z++)
{
 try {	StyledDocument doc;

	doc=sharedVariables.mydocs[z];

  if(sharedVariables.tabStuff[z].ForColor == null)
		StyleConstants.setForeground(attrs, sharedVariables.ForColor);
	else
		StyleConstants.setForeground(attrs, sharedVariables.tabStuff[z].ForColor);

		doc.insertString(doc.getEndPosition().getOffset(), theNotifyTell, attrs);

} // end try
catch(Exception dui){}
}

/*try {
	if(dummyResponse == false)
	if(sharedVariables.makeSounds == true && sharedVariables.specificSounds[4]== true)
{
	Sound nsound=new Sound(sharedVariables.songs[4]);
}
}
catch(Exception notifysound){}
*/

}// end method


class newListAdder implements Runnable
{
public void run()
	{
		int aa=1;
	while(aa==1)
	{
		/*	if(sharedVariables.tabChanged != -1)
	    updateTab();*/

	try {
		newBoardData temp=null;
		temp=listqueue.poll();
		if(temp==null)
		Thread.sleep(5);
		else
		{

	try {


	boolean done=false;
	for(int a=0; a < sharedVariables.channelNamesList.size(); a++)
	if(sharedVariables.channelNamesList.get(a).channel.equals(temp.arg1))
	{
		// add or remove
		if(temp.arg3.equals("1"))
		{
                  sharedVariables.channelNamesList.get(a).addToList(temp.arg2);
                 try
                 {
                  if(bellSet == true)
                  for(int e=0; e< sharedVariables.lanternNotifyList.size(); e++)
		  {
                                        if(sharedVariables.lanternNotifyList.get(e).name.toLowerCase().equals(temp.arg2.toLowerCase()))
                                                     notifyJoin(temp.arg1, temp.arg2);

                  }// end for
                 }
                 catch(Exception d){}



                }
                else
		{


		/*for(int e=0; e< sharedVariables.lanternNotifyList.size(); e++)
		if(sharedVariables.lanternNotifyList.get(e).name.toLowerCase().equals(temp.arg2.toLowerCase()))
                if(!sharingChannel(temp.arg2))
                  globalNotifyAlert(temp.arg2, false);
                */

                try
                 {
                  if(bellSet == true)
                  for(int e=0; e< sharedVariables.lanternNotifyList.size(); e++)
		  {

                                                if(sharedVariables.lanternNotifyList.get(e).name.toLowerCase().equals(temp.arg2.toLowerCase()))
                                                     notifyLeave(temp.arg1, temp.arg2);

                  }// end for
                 }
                 catch(Exception d){}
                   sharedVariables.channelNamesList.get(a).removeFromList(temp.arg2);
                }


                if(bellSet == true)
		{

			 for(int w=0; w<sharedVariables.channelNotifyList.size(); w++)
			 if(sharedVariables.channelNotifyList.get(w).channel.equals(temp.arg1))
			 {
				for(int x=0; x<sharedVariables.channelNotifyList.get(w).nameList.size(); x++)
				if(sharedVariables.channelNotifyList.get(w).nameList.get(x).toLowerCase().equals(temp.arg2.toLowerCase()))
				{

				if(temp.arg3.equals("1"))
				   notifyJoin(temp.arg1, temp.arg2);
				else
				   notifyLeave(temp.arg1, temp.arg2);
			    break;

				}// if name channel match
				break;
			}// if channel match




		}// if bell set off
		done=true;
		break;
	}
	if(done==false)
	{

	// new channel
	nameListClass tempNameList = new nameListClass();
	tempNameList.channel=temp.arg1;
	tempNameList.addToList(temp.arg1);
	tempNameList.addToList(temp.arg2);
	sharedVariables.channelNamesList.add(tempNameList);
}// if done = false
}// end try
catch(Exception badchan){}

		}
	}// end try
	catch(Exception done){}

	} // end while
}// end run
void notifyJoin(String channel, String name)
{
String mess = name + " has joined channel " + channel + ".\n";
writeIt(mess, channel);
}// end notify join

void notifyLeave(String channel, String name)
{
String mess = name + " has quit channel " + channel + ".\n";
writeIt(mess, channel);
}// end notify leave

void writeIt(String mess, String channel)
{
	try {
		int tempInt=Integer.parseInt(channel);
		int [] cindex2 = new int[sharedVariables.maxConsoleTabs];

		boolean goTab=false;
		for(int b=1; b< sharedVariables.maxConsoleTabs; b++)
		{

			if(sharedVariables.console[b][tempInt]==1)
			{	cindex2[b]=1;

				goTab=true;
			}
			else
				cindex2[b]=0;


		}

 		
String chatTime2="";
if(sharedVariables.channelTimestamp == true)
chatTime2=getATimestamp();





SimpleAttributeSet attrs = new SimpleAttributeSet();


if(sharedVariables.style[tempInt] > 0)
{


	if(sharedVariables.style[tempInt] == 1 || sharedVariables.style[tempInt] == 3)
		StyleConstants.setItalic(attrs, true);
	if(sharedVariables.style[tempInt] == 2 || sharedVariables.style[tempInt] == 3)
		StyleConstants.setBold(attrs, true);

}

Color channelcolor;
if(chatTime2.length() > 0)
{
 channelcolor= sharedVariables.chatTimestampColor;
 StyleConstants.setForeground(attrs, channelcolor);
for(int z=0; z<sharedVariables.maxConsoleTabs; z++)
{
	//write;
	if(cindex2[z]==1)
	{
	StyledDocument doc = sharedVariables.mydocs[z];
	doc.insertString(doc.getEndPosition().getOffset(), chatTime2, attrs);
	}
}

}



if(sharedVariables.channelOn[tempInt]==1)
	channelcolor=sharedVariables.channelColor[tempInt];

else
	channelcolor=sharedVariables.defaultChannelColor;

StyleConstants.setForeground(attrs, channelcolor.darker());

for(int z=0; z<sharedVariables.maxConsoleTabs; z++)
{
	//write;
	if(cindex2[z]==1)
	{
	StyledDocument doc = sharedVariables.mydocs[z];
	doc.insertString(doc.getEndPosition().getOffset(), mess, attrs);
	}
}
	}
	catch(Exception badwrite){}
}// end write it

}// end class







	int isitatell()
	{
		try {
		int d1=myinput.indexOf(" ");
		if(d1 > -1)
		{
			if(myinput.indexOf("tells you: ") == d1+1)
			{
				n=myinput.substring(0, d1);
				int d4=myinput.indexOf("(");
				if(d4 > 1 && d4 < d1)// they have a title
				n=myinput.substring(0, d4);
				String t1="tells you: ";
				int d2=t1.length();
				int d3=myinput.length();
				// mike tells you: hi  had lenght 3
				p=myinput.substring(d1+d2+1, d3-3);
				return 1;
			}
		}


		}
		catch(Exception e)
		{
			//System.out.println("caught exception in isitatell \n");
		}
		return 0;


	}

	void processtell()
	{
		try {
		hits++;
		String output= "tell " + n + " " + p;
		//System.out.println("output is " + output + " and p.length() is " + p.length()+ "\n");
		/*if(p.equals("help"))
			sendMessage("tell " + n + " send me a tell and i'll repeat it back. tell me stats    and i'll tell you how many tells i've got.");
		else if(p.equals("stats"))
			sendMessage("tell " + n + " i've received " + hits + " hits");
		else
		sendMessage(output);
		*/
		if(p.equals("exit"))
			sendMessage("exit"); // test of reconnect
	}
	catch(Exception e)
	{
		//System.out.println("process tell failed\n");
	}
	}
	void sendMessage(String msg)
	{
			try{

			// this is changed to add + '\n' not + "\n" dont know if it matters but its neater. i add a char not a string that contains a char

			//if(msg.charAt(msg.length() -1) != '\n')
			//msg=msg + '\n';// i dont know maybe we need to end with \n
			int top=msg.length();
			byte[] b = new byte[top];
			// we made a byte array big enough to fit the msg now we are going to put the chars in the string into the byte array
			for(int j=0; j<msg.length(); j++)// send just enough bytes that are in string
			{	b[j]=(byte) msg.charAt(j); // note a char and byte arent quite the same thing so i have to do (byte) which is known as casting.
							   //  i'm saying take the char, convert it to a  byte then assign it

			}
			outStream.write(b);
			outStream.flush();


			   }

		catch(Exception e){
			//System.out.println("error in void login(string msg)");
			}



	}




public class Datagram1
{


Datagram1()
{

}


Datagram1(String s)
{
    makeDatagram(s);
}


void makeDatagram(String s)
{
try {
	if(s.length() == 0)// this is fics dummy datagram
		return;

		type = -1;

        int len = s.length();
        if (len >= 100000)
        {
               // Datagram to long!!
                len = 100000 - 1;
        }


       String p = s.substring(2, len-1);

		// newbox
		//newbox.append("trying to parse 2, p.length =" + p.length() + " p is: " + p + "\n");
/*StyledDocument doc=consoles[0].getStyledDocument();
						try {
							doc.insertString(doc.getEndPosition().getOffset(), "trying to parse 2, p.length =" + p.length() + " p is: " + p + "\n", null);


						consoles[0].setStyledDocument(doc);
						}
						catch(Exception e)
						{
						}

*/
        argc=0;
		int a=1; // allways on
        while (a==1)
        {
                if (p.charAt(0)=='{')
                {
                        int end = p.indexOf("}");
                        String p2;
                        if(end != 1)
                        p2=p.substring(1, end);
                        else
                        p2="";
                        arg[argc++] = p2;
                        try {
							p=p.substring(end+1, p.length());
							if(p.length() < 3)
								return;
													}
						catch(Exception dd){ return;}

                }
                else if(p.charAt(0)=='\031' && p.charAt(1)=='{'){
					int counter=0;
					while (p.charAt(0)=='\031' && p.charAt(1)=='{')
                {
					counter++;

                       int end = p.indexOf("\031}") ;
                         String p2;
                        if(end != 2)
                        p2=p.substring(2, end);
                        else
                        p2="";
                        arg[argc++] = p2;
                        try {
							p=p.substring(end+2, p.length());
							if(p.length() < 3)
							  return;
						}
						catch(Exception dd){ return;}

                }// end while
                }// end if
                else if(p.charAt(0) != ' ' && p.charAt(0) != ')')
                {
				int end = p.indexOf(" ");
				//writedg("p remains start :" + p + ": and lenght is " + p.length());
				if(end == -1)
				{end = p.indexOf("\031");
				if(end == -1)
					return;
				}
					//writedg("final else " + argc);
                        String p2=p.substring(0, end);
                        arg[argc++] = p2;
                        p=p.substring(end, p.length());
                    //    writedg("final else2 " + argc + " and arg is :" + arg[argc-1] + ":");
                    //    writedg("p remains end :" + p + ": and lenght is " + p.length());

				}

				//if(p.charAt(0) == '\031' && p.charAt(1) == ')')
               // return;

                while (a==1)
                {
                        if(p.length() <= 1) // " )'\n''031'" in no particular order
                        return;
                        else
                        {
                   		if(p.charAt(0) == '{')
                   		break;
                   		else if(p.charAt(0) == '\031' && p.charAt(1) == '{')
                   		break;
                   		else
                   		p=p.substring(1, p.length());
				   		}
                       if(p.length() == 1) // " )'\n''031'" in no particular order
                        return;
                        if (p.charAt(0) != ' ')          // Look for a non-space.
                                break;
                }
        }
}// end try
catch(Exception dui){writeToSubConsole(" datagram exception \n", sharedVariables.openConsoleCount-1);}

}

void writedg(String mydg)
{

	StyledDocument doc=consoles[0].getStyledDocument();
							try {
								doc.insertString(doc.getEndPosition().getOffset(), mydg + "\n", null);


							consoles[0].setStyledDocument(doc);
							}
							catch(Exception e)
							{
							}

}

public String getArg(int i)
{
        if (i>=argc || i<0)
                return "";

        return arg[i];
}


public String [] arg = new String[5000];
public int argc;
public int type;


}// end class




class newBoardCreator implements Runnable
{
public void run()
	{
		int a=1;
	while(a==1)
	{
		/*	if(sharedVariables.tabChanged != -1)
	    updateTab();*/

		newBoardData temp = new newBoardData();
		temp=gamequeue.poll();
		proccessGameInfo(temp);

	} // end while
}// end run





void proccessGameInfo(newBoardData temp)
{
		try{
			if(temp != null)
				{


					if(temp.dg == 12 || temp.dg == 15 || temp.dg == 18)
					{
					//writeToConsole("in dg 12 15 18");
						ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
						Lock readLock = rwl.readLock();

						readLock.lock();

					focusOwner whohasit= new focusOwner();
					int gamenum=getNewGameBoard(temp.type);
					try {
					if(sharedVariables.myServer.equals("ICC"))
					myboards[gamenum].gameStarted(temp.arg1, temp.arg2, temp.arg3, temp.arg4, temp.arg5, temp.arg6, temp.arg7, temp.arg8, temp.arg11, temp.arg13, temp.arg14, temp.arg16, temp.arg17, temp.type); // pass game number
					else // fics
					myboards[gamenum].gameStartedFics(temp.arg1);

				}// end try
				catch(Exception ddd){}


					try {
						newGameTab(gamenum);
					repaintBoards(gamenum);


					selectBoard();


					myboards[gamenum].myconsolepanel.makehappen(gamenum); // i need this to happen on new board
				}// end try
				catch(Exception foc){}

					giveFocus(whohasit);


					//try { Thread.sleep(100); } catch(Exception e){}
					readLock.unlock();
					}

					if(temp.dg == 42)// illegal move
					{
						//writeToConsole("in dg 42");
						int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
							return;
							if(myboards[gamenum]== null)
							return;
							myboards[gamenum].illegalMove(temp.arg1);
							repaintBoards(gamenum);

					}
					if(temp.dg == 22 || temp.dg==23)// takeback backward
					{
							//writeToConsole("in dg 22 23");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
								return;
							if(myboards[gamenum]== null)
								return;
							myboards[gamenum].Backward(temp.arg1, temp.arg2);
							repaintBoards(gamenum);
					}


					if(temp.dg == 152)// send move
					{
							//writeToConsole("in dg 24 send move");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
							return;
							if(myboards[gamenum]== null)
							return;
							myboards[gamenum].writeCountry(temp.arg1, temp.arg2, temp.arg3);
							repaintBoards(gamenum);

					}

					if(temp.dg == 37)// send move
					{
							//writeToConsole("in dg 24 send move");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
							return;
							if(myboards[gamenum]== null)
							return;
							myboards[gamenum].parseCrazyHoldings(temp.arg1, temp.arg2, temp.arg3);
							repaintBoards(gamenum);

					}


					if(temp.dg == 24)// send move
					{
							//writeToConsole("in dg 24 send move");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
							return;
							if(myboards[gamenum]== null)
							return;

							lastMoveGame = gamenum;
							//writeToConsole("in dg 24 send move and lastMoveGame is " + lastMoveGame);
							myboards[gamenum].moveSent(temp.arg1, temp.arg2, temp.arg3);

							if(notmyownmove(gamenum) || sharedVariables.mygame[gamenum].state != sharedVariables.STATE_OVER)
							updateGameTabs(gamenum);

							newmove(gamenum);//  used to check if i'm playing, move  that came in its my turn on game and i'm playing  more than one game. then switch to board ( simul  thing)

							repaintBoards(gamenum);
							//pass game number
					}

					if(temp.dg == 41)// refresh
					{
							//writeToConsole("in dg 25 initial position");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
							return;
							if(myboards[gamenum]== null)
							return;

							//writeToConsole(temp.arg2);
							//writeToConsole("in dg 24 send move and lastMoveGame is " + lastMoveGame);
							myboards[gamenum].refreshSent(temp.arg1);
							repaintBoards(gamenum);
							//pass game number
					}
					if(temp.dg == 25)// initial board from dg-Move_list
					{
							//writeToConsole("in dg 25 initial position");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
							return;
							if(myboards[gamenum]== null)
							return;

							//writeToConsole(temp.arg2);
							//writeToConsole("in dg 24 send move and lastMoveGame is " + lastMoveGame);
							myboards[gamenum].initialPositionSent(temp.arg1, temp.arg2);
							repaintBoards(gamenum);
							//pass game number
					}

					if(temp.dg == 250)// fics game info
					{

							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
							return;
							if(myboards[gamenum]== null)
							return;


							//writeToConsole("in dg 24 send move and lastMoveGame is " + lastMoveGame);
							myboards[gamenum].initialFicsInfo(temp.arg1, temp.arg2, temp.arg3, temp.arg4, temp.arg5, temp.arg6, temp.arg7);
							repaintBoards(gamenum);
							//pass game number
					}

					if(temp.dg == 70)// fen
					{
							//writeToConsole("in dg 70 fen");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
							return;
							if(myboards[gamenum]== null)
							return;

							//writeToConsole(temp.arg2);
							//writeToConsole("in dg 24 send move and lastMoveGame is " + lastMoveGame);
							myboards[gamenum].fenSent(temp.arg1, temp.arg2);
							repaintBoards(gamenum);
							//pass game number
					}

					if(temp.dg == 39)// fen
					{

							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
							return;
							if(myboards[gamenum]== null)
							return;


							myboards[gamenum].flipSent(temp.arg1, temp.arg2);
							repaintBoards(gamenum);

					}














if(temp.dg == 77)// game message
{

int gamenum=getGameBoard(temp.arg1);
// gamenum can be -1 if no board has this game
 StyledDocument doc;
 if(gamenum != sharedVariables.NOT_FOUND_NUMBER)
	doc = sharedVariables.mygamedocs[gamenum];
 else
   	doc = sharedVariables.mydocs[0];

String thetell ="";


thetell = temp.arg2;
/***************** parsing for examiner editing names and ratings ***********************/
if(gamenum != sharedVariables.NOT_FOUND_NUMBER)
{
if(thetell.contains("sets Black's name to"))
myboards[gamenum].updateBlackName(temp.arg1, parseValueSet(thetell));
if(thetell.contains("sets White's name to"))
myboards[gamenum].updateWhiteName(temp.arg1, parseValueSet(thetell));
if(thetell.contains("BlackElo tag set to"))
myboards[gamenum].updateBlackElo(temp.arg1, parseValueSet(thetell));
if(thetell.contains("WhiteElo tag set to"))
myboards[gamenum].updateWhiteElo(temp.arg1, parseValueSet(thetell));
}
/******************* end parsing of changes in names and ratings ************************/
// special parsing for autoExamine i.e. we may stop auto exam or we may not send a forward message
if(gamenum != sharedVariables.NOT_FOUND_NUMBER)
if(sharedVariables.mygame[gamenum].state == sharedVariables.STATE_EXAMINING)
{
	try {
						if(thetell.contains("You're at the end of the game.") && thetell.length()<35)
						sharedVariables.autoexam=0;

						if(thetell.contains("goes forward 1") && thetell.contains("Game"))
						{

							if(sharedVariables.autoexam==1 && sharedVariables.autoexamnoshow == 1)
							  return;
						}
					}
					catch(Exception e){}
}
if(gamenum != sharedVariables.NOT_FOUND_NUMBER)                                                                           // mike investigate if this is double null
	processLink(doc, thetell, sharedVariables.ForColor, gamenum, maxLinks, GAME_CONSOLES, null, null);// 1 at end means go to game console
else
{
	if(sharedVariables.tabStuff[0].ForColor== null)
		processLink(doc, thetell, sharedVariables.ForColor, 0, maxLinks, SUBFRAME_CONSOLES, null, null);// console 0 and last 0 is not a game console

	else
		processLink(doc, thetell, sharedVariables.tabStuff[0].ForColor, 0, maxLinks, SUBFRAME_CONSOLES, null, null);// console 0 and last 0 is not a game console
}
}



















					if(temp.dg == 26)// kib
					{

						int gamenum=getGameBoard(temp.arg1);
						// gamenum can be -1 if no board has this game
                         StyledDocument doc;
						if(gamenum != sharedVariables.NOT_FOUND_NUMBER)
						doc = sharedVariables.mygamedocs[gamenum];
						else
						doc = sharedVariables.mydocs[0];

						String thetell ="";

						if(temp.arg4.equals("1"))//kib
						{
							thetell = temp.arg2 + "(" + temp.arg1 + ")" + " kibitzes: " + temp.arg5 + "\n";
							if(!temp.arg3.equals(""))
								thetell = temp.arg2 + "(" + temp.arg3 + ")" + "(" + temp.arg1 + ")" + " kibitzes: " + temp.arg5 + "\n";
						}
						else // whisper
						{
							thetell = temp.arg2 + "(" + temp.arg1 + ")" + " whispers: " + temp.arg5 + "\n";
							if(!temp.arg3.equals(""))
								thetell = temp.arg2 + "(" + temp.arg3 + ")" + "(" + temp.arg1 + ")" + " whispers: " + temp.arg5 + "\n";
						}
				SimpleAttributeSet attrs = new SimpleAttributeSet();
					if(sharedVariables.kibStyle == 1 || sharedVariables.kibStyle == 3)
						StyleConstants.setItalic(attrs, true);
					if(sharedVariables.kibStyle == 2 || sharedVariables.kibStyle == 3)
						StyleConstants.setBold(attrs, true);
					if(gamenum != sharedVariables.NOT_FOUND_NUMBER)
					processLink(doc, thetell, sharedVariables.kibcolor, gamenum, maxLinks, GAME_CONSOLES, attrs, null);// 1 at end means go to game console
					else
					processLink(doc, thetell, sharedVariables.kibcolor, 0, maxLinks, SUBFRAME_CONSOLES, attrs, null);// console 0 and last 0 is not a game console

                    }

					if(temp.dg == 1600)// becomes examined. i made up the number 1600 to let the datagram parser  handle it
					{
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
								return;
							if(myboards[gamenum]== null)
								return;
							myboards[gamenum].gameEndedExamined(temp.arg1); // pass game number
							updateGameTabs("WE", gamenum);
							repaintBoards(gamenum);
				    }




					if(temp.dg == 900)// forward. i made up the number 900 to let the datagram parser  handle it
					{

						int gamenum=getGameBoard(temp.arg1);
						// gamenum can be -1 if no board has this game
                         StyledDocument doc;
						if(gamenum != sharedVariables.NOT_FOUND_NUMBER)
						doc = sharedVariables.mygamedocs[gamenum];
						else
						doc = sharedVariables.mydocs[0];

						String thetell ="";

						thetell = thetell + temp.arg2;
                                                                                                                                      // mike investigate if this is double null
					if(gamenum != sharedVariables.NOT_FOUND_NUMBER)
					processLink(doc, thetell, sharedVariables.ForColor, gamenum, maxLinks, GAME_CONSOLES, null, null);// 1 at end means go to game console
					else
					processLink(doc, thetell, sharedVariables.ForColor, 0, maxLinks, SUBFRAME_CONSOLES, null, null);// console 0 and last 0 is not a game console

                    }



					if(temp.dg == 13 || temp.dg == 16 || temp.dg == 19 || temp.dg == 14  || temp.dg == 17)// 13 16 19 result
					{

							//writeToConsole("in dg 13 16 19 result");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
								return;
							if(myboards[gamenum]== null)
								return;
							myboards[gamenum].gameEnded(temp.arg1); // pass game number
							if(sharedVariables.mygame[gamenum].newBoard== true)// they closed the board so this is set to keep the result from entering tab
								updateGameTabs(sharedVariables.tabTitle[gamenum] = "G" + (gamenum+1), gamenum);
							else
								updateGameTabs(sharedVariables.tabTitle[gamenum], gamenum);

							repaintBoards(gamenum);
					}


					if(temp.dg == 43)// 43 my relation to game
					{

							//writeToConsole("in dg 60 arrow");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
								return;
							if(myboards[gamenum]== null)
								return;
							myboards[gamenum].newGameRelation(temp.arg1, temp.arg2); // pass game number

							if(sharedVariables.mygame[gamenum].newBoard== true)// they closed the board so this is set to keep the result from entering tab
								updateGameTabs(sharedVariables.tabTitle[gamenum] = "G" + (gamenum+1), gamenum);
							else
								updateGameTabs(sharedVariables.tabTitle[gamenum], gamenum);

							repaintBoards(gamenum);
					}

					if(temp.dg == 59)// 59 cirlce
					{

							//writeToConsole("in dg 60 arrow");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
								return;
							if(myboards[gamenum]== null)
								return;
							myboards[gamenum].newCircle(temp.arg1, temp.arg2, temp.arg3); // pass game number

							repaintBoards(gamenum);
					}

					if(temp.dg == 60)// 60 arrow
					{

							//writeToConsole("in dg 60 arrow");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
								return;
							if(myboards[gamenum]== null)
								return;
							myboards[gamenum].newArrow(temp.arg1, temp.arg2, temp.arg3, temp.arg4); // pass game number

							repaintBoards(gamenum);
					}

					if(temp.dg == 56)// send move
					{

							//writeToConsole("in dg 56 update clocks");
							int gamenum=getGameBoard(temp.arg1);
							if(gamenum == sharedVariables.NOT_FOUND_NUMBER)
								return;
						    if(myboards[gamenum]!=null)
							{
								if(sharedVariables.myServer.equals("ICC"))
								myboards[gamenum].updateClock(temp.arg1, temp.arg2, temp.arg3); // pass game number
								else
								myboards[gamenum].updateFicsClock(temp.arg1, temp.arg2, temp.arg3); // pass game number
								repaintBoards(gamenum);
							}

					}
				}
				else
				{
					try {

					Thread.sleep(3);
					}
					catch(Exception e)
					{}
				}// end no data

	}
	catch(Exception e)
	{}
}// end method


	void updateGameTabs(int i)
{

		try {
			for(int a=0; a< sharedVariables.openBoardCount; a++)
			     if(myboards[a]!=null)
			     if(myboards[a].isVisible())
				if(myboards[a].myconsolepanel!=null)// we do these extra checks in case of a racing condition. like 20 games and 20 boards open at once and they dont fully create when this hits
				if(myboards[a].myconsolepanel.channelTabs[i]!=null)
			     {
					 if(sharedVariables.gamelooking[a]==i)
				 	;
				 else
				myboards[a].myconsolepanel.channelTabs[i].setBackground(sharedVariables.newInfoTabBackground);
				}

		}
		catch(Exception racer){}
}
void updateGameTabs(String title, int num)
{

	for(int a=0; a < sharedVariables.maxBoardTabs; a++)
	{
		try {
			if(myboards[a]!=null)
		if(myboards[a].isVisible() == true )
		if(myboards[a].myconsolepanel!=null)// we do these extra checks in case of a racing condition. like 20 games and 20 boards open at once and they dont fully create when this hits
		if(myboards[a].myconsolepanel.channelTabs[num]!=null)
		{
			myboards[a].myconsolepanel.channelTabs[num].setText(title, num);
			myboards[a].myconsolepanel.channelTabs[num].setVisible(true);

		}// end if
		}// end try
		catch(Exception racer){}
	}
	sharedVariables.mygame[num].tabtitle= title;

}

boolean notPlaying()
{
	for(int a = 0; a<sharedVariables.openBoardCount; a++)
	if(myboards[a]!=null)
		if(sharedVariables.mygame[a].state == sharedVariables.STATE_PLAYING)
			return true;

	return false;
}
void selectBoard()
{


boolean maximum = false;
int bb=0;
try {

for(bb=0; bb<sharedVariables.openConsoleCount; bb++)
	if(consoleSubframes[bb]!=null)
		if(consoleSubframes[bb].isVisible() == true)
			if(consoleSubframes[bb].isSelected())
				if(consoleSubframes[bb].isMaximum())
				{	maximum=true;
					consoleSubframes[bb].setMaximum(false);
				}
}// end try
catch(Exception e) {}

if(sharedVariables.tabsOnly == true)
	for(bb=0; bb<sharedVariables.openBoardCount; bb++)
		if(myboards[bb] != null)
			if(myboards[bb].isVisible() == true)
			{
				try {
					myboards[bb].setSelected(true);
					if(maximum == true)
						myboards[bb].setMaximum(true);
					bb=sharedVariables.openBoardCount;
				}
				catch(Exception e){}
			}

}
	int getNewGameBoard(int type)
	{

		int last=-1;
		boolean visible = false;
		boolean visibleBoardExists=false;

		for(int a=0; a<sharedVariables.maxGameTabs; a++)
			if(myboards[a] != null)
				if(myboards[a].isVisible() == true)
		 			visibleBoardExists = true;

		for(int a=0; a<sharedVariables.maxGameTabs; a++)
		{

			if(myboards[a] != null)
			{
				if((myboards[a].isVisible() == true || (sharedVariables.tabsOnly == true && visibleBoardExists == true))  && (sharedVariables.mygame[a].myGameNumber == sharedVariables.NOT_FOUND_NUMBER))// || ((sharedVariables.mygame[a].state == 1 || sharedVariables.mygame[a].state==2) && type==1)))
				{
				writeToConsole("Reusing board.");
				// make board go to front with tabs only.

					if(!notPlaying()) // we havent started this new board whatever we are doing but if we are playing on a board we dont select the new board.  the simul functions will take care of it if simulizing
					for(int bb=0; bb <= sharedVariables.openBoardCount; bb++)
						if(myboards[bb]!=null)
							if(myboards[bb].isVisible() == true)
								myboards[bb].myconsolepanel.makehappen(a);
				return a;
				}
				else
				if(myboards[a].isVisible() == true)
				visible = true;
			/*
				if(last == -1 && (sharedVariables.mygame[a].myGameNumber == -1 && sharedVariables.mygame[myboards[a].gameData.LookingAt].myGameNumber == -1)) //|| ((sharedVariables.mygame[a].state == 1 || sharedVariables.mygame[a].state==2) && type==1)))
				 last=a; // we will use an available board even if its not visible
			*/
			}
		}

		writeToConsole("Creating game board.");
		for(int a=0; a<sharedVariables.openBoardCount; a++)
		{

			if(myboards[a] != null)
			{
				if(myboards[a].isVisible() == false)
				{
					if(sharedVariables.tabTitle[a].startsWith("G"))
					{
						if(sharedVariables.mygame[a] == null)
						last=a;
						else if(sharedVariables.mygame[a].state == sharedVariables.STATE_OVER)
						 last=a;




					}
					break;
				}

			}
		}


		if(last == -1)
			createGameFrame(last, visible);
		else
		{
                 if(sharedVariables.useTopGames == true)
                 myboards[last].topGame.setVisible(true);
                 else
                  myboards[last].setVisible(true);
		}
                if(last > -1)
		return last;

		return sharedVariables.openBoardCount-1;

	}


		protected void createGameFrame(int last, boolean visible) {

	    int reuse = last;
	    if(last == -1)
	    last = sharedVariables.openBoardCount;

	    if(reuse != -1) // this is now coded so if we resuse we just make visible, it wont hit this method MA 12-11-10
	    {
		//	writeToConsole("reuse is not -1 and i'm trying to destroy a board");
			if(myboards[last].generalTimer != null)
			myboards[last].generalTimer.cancel();
			if(myboards[last].timer != null)
			myboards[last].timer.cancel();
			myboards[last].dispose();
			myboards[last]=null;

		}
	    //writeToConsole("about to execute new game board command");
	    myboards[last] = new gameboard(consoles, consoleSubframes, gameconsoles, gamequeue, last, sharedVariables.img, queue, sharedVariables, graphics, myDocWriter);
		//writeToConsole("success in making new game board");
		if(sharedVariables.useTopGames == false)
                myboards[last].setSize(sharedVariables.defaultBoardWide,sharedVariables.defaultBoardHigh);
		else
                {
                 if(myboards[last].topGame != null)
                myboards[last].topGame.setSize(sharedVariables.defaultBoardWide,sharedVariables.defaultBoardHigh);
                }
                //writeToConsole("made new game board at spot myboards[" + last + "]");
		if(visible == false || sharedVariables.tabsOnly == false)
		{
			if(sharedVariables.useTopGames == true)
                       myboards[last] .topGame.setVisible(true);
			else
                        myboards[last] .setVisible(true);
		}
		else
		{
			if(sharedVariables.useTopGames == true)
                       myboards[last] .topGame.setVisible(false);
			else
                        myboards[last] .setVisible(false);
		//writeToConsole("i made visible false");
		}
	   sharedVariables.desktop.add(myboards[last] );  
	     myboards[last].myconsolepanel.myself=(JDesktopPaneCustom) sharedVariables.desktop;

		//writeToConsole("added to desktop");
		try {
		//     writeToConsole("going to set selected true for board " + last );
		        if(myboards[last].isVisible() == true)
		        {

			myboards[last] .setSelected(true);
			//writeToConsole("going to set selected true for board " + last );
			}
		    }
		    catch (Exception e) {}
		if(reuse == -1) // last is passed in as -1 if we dont have a board to use
		{
			for(int a=0; a<sharedVariables.openBoardCount; a++)
			{try {
				if(myboards[a] != null && (a != last))
				if(myboards[a].isVisible() == true)
			myboards[a].myconsolepanel.channelTabs[sharedVariables.openBoardCount].setVisible(true);
			}
			catch(Exception e)
			{}}

		//	writeToConsole("done setting any channel tabs that need to be set to visible");
			try {
				Thread.sleep(15);
			}
			catch(Exception e){}



		}
			if(reuse == -1)
			 sharedVariables.openBoardCount++;
			else
			{
				// make this tab visible on all boards
			for(int bb=0; bb <= sharedVariables.openBoardCount; bb++)
			   if(myboards[bb]!=null)
					if(myboards[bb].isVisible() == true)
						myboards[bb].myconsolepanel.channelTabs[last].setVisible(true);

			}
		//	writeToConsole("open board count is now " + sharedVariables.openBoardCount + " and last is " + last + " and reuse is " + reuse);
			try {
				Thread.sleep(15);
			}
			catch(Exception e){}

		myboards[last].initializeGeneralTimer();
			if(sharedVariables.tabsOnly == true)// we want board tab to go to front
			{
				for(int bb=0; bb <= sharedVariables.openBoardCount; bb++)
			   		if(myboards[bb]!=null)
						if(myboards[bb].isVisible() == true)
							myboards[bb].myconsolepanel.makehappen(last);
			}
			else// tabs only false
				myboards[last].myconsolepanel.makehappen(last);
		}


void updateTab()
{

	if(sharedVariables.tabChanged > -1 && sharedVariables.tabChanged< sharedVariables.maxBoardTabs)
	{
		for(int a=0; a< sharedVariables.maxBoardTabs; a++)
		if(myboards[a]!=null)
		if(myboards[a].isVisible() == true)
		{
			try {
				myboards[a].myconsolepanel.channelTabs[sharedVariables.tabChanged].setText(sharedVariables.tabTitle[sharedVariables.tabChanged], sharedVariables.tabChanged);
					myboards[a].setTitle(sharedVariables.mygame[myboards[a].gameData.LookingAt].title);

			}
			catch(Exception e)
			{

			}
	}

		sharedVariables.tabChanged=-1;
	}
}


void newGameTab(int num)
{

	/*			myoutput output = new myoutput();
				output.tab=num;
*/
	String title="";
	try {

	if(sharedVariables.mygame[num].state == sharedVariables.STATE_OBSERVING)
				sharedVariables.tabTitle[num]="O" + sharedVariables.mygame[num].myGameNumber;
	if(sharedVariables.mygame[num].state == sharedVariables.STATE_PLAYING)
				sharedVariables.tabTitle[num]="P" + sharedVariables.mygame[num].myGameNumber;
	if(sharedVariables.mygame[num].state == sharedVariables.STATE_EXAMINING)
				sharedVariables.tabTitle[num]="E" + sharedVariables.mygame[num].myGameNumber;


		if(sharedVariables.tabTitle[num].length()>2)
		{
		if(sharedVariables.openBoardCount > 5)
		{
			String p = sharedVariables.tabTitle[num].substring(0, 2);
			sharedVariables.tabTitle[num]=p;
		}
		if(sharedVariables.openBoardCount > 10)
		{
			String p = sharedVariables.tabTitle[num].substring(0, 1);
			sharedVariables.tabTitle[num]=p;
	    }
		}// if lenght >2

updateGameTabs(sharedVariables.tabTitle[num], num);
}
catch(Exception e)
{}
}






}
String parseValueSet(String thetell)
{
	try{
	int a=thetell.indexOf("to");
	a=thetell.indexOf(" ", a);
	int b=thetell.indexOf(".", a);
	String mystring = thetell.substring(a+1, b);
	return mystring;
	}catch(Exception d){}


	return "";
}

class sendToIcs implements Runnable  // this method checks the queue which other classes in the program use to send data
{
public void run()
	{
		int a=1;
	while(a==1)
	{
	try{
					myoutput tosend = new myoutput();
					tosend=queue.poll();
					if(tosend != null)
					{
					while(tosend != null)
					{


					if(tosend.closetab > -1)
					{
						closeGameTab(tosend.closetab);
						tosend=queue.poll();
					}

					if(tosend.clearconsole > -1)
					{
						clearConsole(tosend.clearconsole, 0);
						tosend=queue.poll();
					}
					if(tosend.trimconsole > -1)
					{
						trimConsole(tosend.trimconsole, 0);
						tosend=queue.poll();
					}
					if(tosend.trimboard > -1)
					{
							trimConsole(tosend.trimboard, 1); // 0/1 console or board
							tosend=queue.poll();
					}


					if(tosend.clearboard > -1)
					{
							clearConsole(tosend.clearboard, 1); // 0/1 console or board
							tosend=queue.poll();
					}
					if(tosend.startengine > -1)
					{
							initializeEngine(); // 0/1 console or board
							tosend=queue.poll();
					}

					else if(tosend.tab > -1)
					{
						//updateGameTabs(tosend);
					}
					else
					{
					if(tosend.game == 0)
					lastConsoleNumber=tosend.consoleNumber;


						// these are set whenever a move is made ( not a premove) so we check if playing more than one game ( a simul)
						// and try to switch them to low time board
						if(tosend.gameboard > -1 &&  tosend.gamelooking > -1)
						switchboard(tosend.gameboard, tosend.gamelooking);


						sendMessage(tosend.data);
						tosend=queue.poll();
					}
					}
				}// end if not null
				else
				{
					try { Thread.sleep(5); } catch(Exception e){}
				}


	}
	catch(Exception e)
	{
	}

}// end while
}// end run

void initializeEngine()
{
		for(int a=0; a< sharedVariables.openBoardCount; a++)
		     if(myboards[a]!=null)
		     if(myboards[a].isVisible())
		     {
				 if(sharedVariables.mygame[sharedVariables.gamelooking[a]].state == sharedVariables.STATE_EXAMINING && sharedVariables.pointedToMain[a] == false)

			{

                          boolean go=true;
                           if(sharedVariables.engineOn == true)
                           if(sharedVariables.mygame[sharedVariables.gamelooking[a]].clickCount %2 == 0)
                           go=true;

                           if(go==true)
                          {gameconsoles[a].setStyledDocument(sharedVariables.engineDoc);
                          sharedVariables.mygame[a].clickCount++;

                          }
                          }
                          }


}

	void clearConsole(int conNumber, int board)
	{
		try {
			if(board == 0)
			{
				StyledDocument doc=sharedVariables.mydocs[conNumber];// 0 for main console
				doc.remove(0, doc.getLength());
				myDocWriter.writeToConsole(doc, conNumber);
			}
			else
			{
				StyledDocument doc=sharedVariables.mygamedocs[conNumber];// 0 for main console
				doc.remove(0, doc.getLength());
				myDocWriter.writeToGameConsole(doc, conNumber);
			}

		}
		catch(Exception d){}

	}


	void trimConsole(int conNumber, int board)
	{
		try {
			if(board == 0)
			{
				StyledDocument doc=sharedVariables.mydocs[conNumber];// 0 for main console
				doc.remove(0, (int)doc.getLength()/2);
				myDocWriter.writeToConsole(doc, conNumber);
			}
			else
			{
				StyledDocument doc=sharedVariables.mygamedocs[conNumber];// 0 for main console
				doc.remove(0,(int) doc.getLength()/2);
				myDocWriter.writeToGameConsole(doc, conNumber);
			}

		}
		catch(Exception d){}

	}



	void switchboard(int boardnumber, int boardlooking)
	{
		// sharedVariables.myname
		// below in gamestate
		// name1 white
		// name2 black
		// whiteClock and blackClock

		// the goal here is to see if we are playing more than one game
		// we loop through boards, skipping boardlooking (board we moved on)
		// and we look for state 1 and my turn
		// if we find it the first one becomes lowtimeboard and lowtime becomes the time on our clock there
		// as we continue if we find another board that we are playing on and its our turn
		// we make it lowtimeboard and that the time.
		// if we find any lowtimeboard we are in a simul, and we will switch the player to the board with lowest time.

		double lowtime=999999999;// set it high so the first check we always find less time
		int lowtimeboard=-1;

		for(int a=0; a<sharedVariables.openBoardCount; a++)
		{
			if(a == boardlooking)
				continue;
			if(sharedVariables.mygame[a].state == sharedVariables.STATE_PLAYING) // we found another board we are playing on
			{
				// now we need to know if its my turn on that board
				if(sharedVariables.myname.equals(sharedVariables.mygame[a].realname1)) // we are white
				{
					if(sharedVariables.mygame[a].movetop % 2 == 0)// after whites first move movetop is 1 and its blacks move
					{// success we found a board where its my turn
						if(sharedVariables.mygame[a].whiteClock < lowtime)
						{
							lowtimeboard=a;
							lowtime=sharedVariables.mygame[a].whiteClock;
						}
					}
				}
				else if(sharedVariables.myname.equals(sharedVariables.mygame[a].realname2)) // we are black
				{
					if(sharedVariables.mygame[a].movetop % 2 == 1)// after whites first move movetop is 1 and its blacks move
					{// success we found a board where its my turn
						if(sharedVariables.mygame[a].blackClock < lowtime)
						{
							lowtimeboard=a;
							lowtime=sharedVariables.mygame[a].blackClock;
						}

					}

				}// end checking black

			}// end if state 1

		}// end for

		if(lowtimeboard > -1)
		{
			myboards[boardnumber].myconsolepanel.makehappen(lowtimeboard);
			//myboards[boardnumber].repaint();

		}


	}//end method switch board



} // end class

void enabletimestamp()
{Runtime rt;



if(sharedVariables.myServer.equals("ICC"))
{

try {


Process timestamp;
String myurl="timestamp 207.99.83.228 5000 -p 5500"; // was -p 5000 &

	rt = Runtime.getRuntime();
sharedVariables.timestamp = rt.exec(myurl);


}
catch(Exception e)
{}
/*if(sharedVariables.timestamp == null)
{
try {


//Process timestamp;
String myurl="\\multiframe\\timestamp 207.99.83.228 5000 -p 5500"; // was -p 5000 &

	rt = Runtime.getRuntime();
sharedVariables.timestamp = rt.exec(myurl);


}
catch(Exception e)
{}
}
if(sharedVariables.timestamp == null)
{
	try {

	//Process timestamp;
	String myurl="/multiframe/timestamp 207.99.83.228 5000 -p 5500"; // was -p 5000 &

		rt = Runtime.getRuntime();
	sharedVariables.timestamp = rt.exec(myurl);


	}
	catch(Exception e)
{}
}

if(sharedVariables.timestamp == null)
{
	try {

	//Process timestamp;
	String myurl="\"\\Program Files\\WinBoard-4.2.7\\timestamp\" 207.99.83.228 5000 -p 5500"; // was -p 5000 &



	}
catch(Exception e){}
}
if(sharedVariables.timestamp == null)
{
	try {

	//Process timestamp;
	String myurl="\"\\Program Files\\WinBoard\\timestamp\" 207.99.83.228 5000 -p 5500"; // was -p 5000 &

		rt = Runtime.getRuntime();
	sharedVariables.timestamp = rt.exec(myurl);


	}
catch(Exception e){}
}


if(sharedVariables.timestamp == null)
{
	try {

	//Process timestamp;
	String myurl="\"\\Program Files\\WinBoard-4.4.1\\WinBoard\\timestamp\" 207.99.83.228 5000 -p 5500"; // was -p 5000 &

		rt = Runtime.getRuntime();
	sharedVariables.timestamp = rt.exec(myurl);


	}

catch(Exception e){}
}
*/

}// end if icc
else // we are on fics
{

try {


//Process timestamp;
String myurl="\\multiframe\\timeseal 69.36.243.188 5000 -p 5499"; // was -p 5000 &

	rt = Runtime.getRuntime();
sharedVariables.timestamp = rt.exec(myurl);


}
catch(Exception e)
{}

}// end if fics


}// end method


boolean playingSimul()
{

		int g=0;
		for(int a=0; a<sharedVariables.openBoardCount; a++)
		{

			if(sharedVariables.mygame[a].state == sharedVariables.STATE_PLAYING) // we found another board we are playing on
			{
				g++;
			}
		}

		if(g > 1)
		return true;

		return false;

}

void newmove(int num)
{
  // if move from game num is game you are playing
	  // and you are p	laying more than one game i.e. playingSimul == true
	  //  and you have no moves  in other  games you are playing
  // it will switch you  to the game that just had a move come in

	if(playingSimul() == false)
		return;


		int g=0;
		int go=0;
		for(int a=0; a<sharedVariables.openBoardCount; a++)
		{

			if(sharedVariables.mygame[a].state == sharedVariables.STATE_PLAYING) // we found another board we are playing on
			{

				if(notmyownmove(a) == true) //  not my own move means my turn not my move bouncing back
				{
					g++;// how many boards is it my turn
					if(a==num)//  its my turn in game that just had  move
					go=1;
				}
			}


		}
	if(g== 1 && go == 1)//  its my turn in new move game and  no other  boards
	{
		for(int a = 0; a< sharedVariables.openBoardCount; a++)
		if(myboards[a] != null)
		if(myboards[a].isVisible())
		myboards[a].myconsolepanel.makehappen(num);
	}


}


boolean notmyownmove(int a)// will return true if its not my move being sent back in this game i.e. they moved and its really my turn
	{
		// this is called to see if we need to change the tab color on a send move datagram
		// practically speaking it only matters in a simul since your own move comes back after you switched to the low time board
				if(sharedVariables.myname.equals(sharedVariables.mygame[a].realname1)) // we are white
				{
					if(sharedVariables.mygame[a].movetop % 2 == 0)// after whites first move movetop is 1 and its blacks move
						return true;
				}
				else if(sharedVariables.myname.equals(sharedVariables.mygame[a].realname2)) // we are black
				{
					if(sharedVariables.mygame[a].movetop % 2 == 1)// after whites first move movetop is 1 and its blacks move
						return true;
				}
				return false;
	}



class gameListCreator  implements Runnable
{
public void run()
{
myGameList = new gameFrame(sharedVariables, queue, gameList);
sharedVariables.myGameList=myGameList;
myGameList.setSize(600,425);
myGameList.setVisible(true);
//sharedVariables.desktop.add(myGameList);

try {
	myGameList.setSelected(true);
	if(!(gameList.type1.equals("") && gameList.type2.equals("")))
		myGameList.setTitle(gameList.type1 + " " + gameList.type2);
	}
	catch(Exception couldnt){}

}// end run
}// end class gamelistcreator

















	void closeGameTab(int tabNumber)
	{
		try {

			myoutput data = new myoutput();
			String prefixcommand="`g" + tabNumber + "`";
			data.data=data.data+prefixcommand;
			int myGameNumber = -100;
		if(sharedVariables.mygame[tabNumber].state == sharedVariables.STATE_PLAYING)
		{	data.data = "Resign\n";
			data.consoleNumber = 0;
			data.game=1;
			queue.add(data);
			myGameNumber = sharedVariables.mygame[tabNumber].myGameNumber;
		}
		else if(sharedVariables.mygame[tabNumber].state == sharedVariables.STATE_EXAMINING)
		{	data.data = "Unexamine\n";
			data.consoleNumber = 0;
			data.game=1;
			// now call game over this ensures any engines shut down
			if(myboards[tabNumber]!=null)
				myboards[tabNumber].gameEnded("" + sharedVariables.mygame[tabNumber].myGameNumber);
			queue.add(data);
			myGameNumber = sharedVariables.mygame[tabNumber].myGameNumber;
		}
		else if(sharedVariables.mygame[tabNumber].state == sharedVariables.STATE_OBSERVING)
		{	data.data = "Unobserve " + sharedVariables.mygame[tabNumber].myGameNumber + "\n";
			data.consoleNumber = 0;
			data.game=1;
			queue.add(data);
			myGameNumber = sharedVariables.mygame[tabNumber].myGameNumber;
		}

		try {
			myboards[tabNumber].resetMoveList();
		}
		catch(Exception reseting){}

sharedVariables.tabTitle[tabNumber] = "G" + (tabNumber+1);

		updateGameTabs(sharedVariables.tabTitle[tabNumber], tabNumber);

try {
	ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		Lock readLock = rwl.readLock();

readLock.lock();
		sharedVariables.mygame[tabNumber] = new gamestate(sharedVariables.excludedPieces);
readLock.unlock();

// now reduce openBoardCount and make tab not visible if its far left tab and greater than 0


		for(int a = 0; a< sharedVariables.openBoardCount; a++)
		{
			if(myboards[a]!=null)
			{
				if(tabNumber > 0)
				{
					myboards[a].myconsolepanel.channelTabs[tabNumber].setVisible(false);

					if(myboards[a].isVisible() == true)
						if(myboards[a].gameData.LookingAt == tabNumber)
							myboards[a].myconsolepanel.makehappen(tabNumber-1);
				}
				else
					if(myboards[a].isVisible() == true)
						if(myboards[a].gameData.LookingAt == tabNumber)
							myboards[a].myconsolepanel.makehappen(tabNumber);// we call make happen on tab 0 so it adjusts the title


			}


		}








}catch(Exception z){}
	}catch(Exception d){}

	}


class focusOwner {

boolean console;
boolean board;
int number;

focusOwner()
{

	console=false;/// if it doesnt set one of these to true nobody gets it
	number=0;
	board=false;

	try {


	for(int a=0; a<sharedVariables.openConsoleCount; a++)
	{
		if(consoleSubframes[a].overall.Input.hasFocus())
		{
			console=true;
			number=a;
			//writeToConsole("console is true and number is " + a + "\n");
			return;
		}
	}// end for

	for(int a=0; a<sharedVariables.openBoardCount; a++)
	{
		if(myboards[a].myconsolepanel.Input.hasFocus())
		{
			board=true;
			number=a;
			return;

		}
	}

}// end try
catch(Exception e){}

}// end constructor


}// end class focus owner.

void returnFocus(focusOwner mine)
{

	try {
	if(mine.console == true)
	{
		consoleSubframes[mine.number].setSelected(true);
	}// end if console == true
	if(mine.board == true)
	{
		myboards[mine.number].setSelected(true);
	}// end if board == true

	}
	catch(Exception e){}
}

void giveFocus(focusOwner mine)
{

	try {
	if(mine.console == true)
	{
		consoleSubframes[mine.number].giveFocus();
	}// end if console == true
	if(mine.board == true)
	{
		myboards[mine.number].myconsolepanel.giveFocus();
	}// end if board == true

	}
	catch(Exception e){}

}

String getATimestamp()
{
String theTime="";
try {

Calendar Now=Calendar.getInstance();
String hour= "" + Now.get(Now.HOUR);// was HOUR_OF_DAY for 24 hour time
if(hour.equals("0"))
hour = "12";

String minute="" + Now.get(Now.MINUTE);
if(minute.length()==1)
minute="0"+ minute;

String second="" + Now.get( Now.SECOND);
if(second.length()==1)
second="0"+ second;

theTime="(" + hour + ":" + minute + ":" + second + ")";
if(sharedVariables.leftTimestamp == true)
theTime=hour + ":" + minute + ":" + second + " ";

}
catch(Exception dumtime){}

return theTime;
}


String getADatestamp()
{
String theTime="";
try {

Calendar Now=Calendar.getInstance();
String hour= "" + Now.get(Now.HOUR);// was HOUR_OF_DAY for 24 hour time
if(hour.equals("0"))
hour = "12";

String minute="" + Now.get(Now.MINUTE);
if(minute.length()==1)
minute="0"+ minute;

String second="" + Now.get( Now.SECOND);
if(second.length()==1)
second="0"+ second;
// day of week, am pm
int dayNum = Now.get(Now.DAY_OF_WEEK);
String weekday = "";
if(dayNum == 1)
weekday="Sunday";
if(dayNum == 2)
weekday="Monday";
if(dayNum == 3)
weekday="Tuesday";
if(dayNum == 4)
weekday="Wednesday";
if(dayNum == 5)
weekday="Thursday";
if(dayNum == 6)
weekday="Friday";
if(dayNum == 7)
weekday="Saturday";




int ampmNum = Now.get(Now.AM_PM);
String ampm = "" ;
if(ampmNum == 0)
ampm = "AM";
else
ampm = "PM";


theTime=weekday + " " + hour + ":" + minute + " " + ampm;

}
catch(Exception dumtime){}

return theTime;
}

void writeDateStamps()
{
try {
String dateStamp=getADatestamp() + "\n";
for(int mydocs=0; mydocs < sharedVariables.maxConsoleTabs; mydocs++)
{
StyledDocument doc=sharedVariables.mydocs[mydocs];
SimpleAttributeSet attrs = new SimpleAttributeSet();

StyleConstants.setForeground(attrs, sharedVariables.chatTimestampColor);
doc.insertString(doc.getEndPosition().getOffset(), dateStamp, attrs);
}// end for
}// end try
catch(Exception badWrite){}

}// end method


Color getNameColor(Color col)
{

 int red= col.getRed();
 int green = col.getGreen();
 int blue = col.getBlue();

 red=255-red;
 blue=255-blue;
 green=255-green;
 Color col2=new Color(red,green, blue);
 col2=col2.darker();

  red= col2.getRed();
  green = col2.getGreen();
  blue = col2.getBlue();

red=255-red;
 blue=255-blue;
 green=255-green;

return new Color(red,green,blue);

 /*int value=20;
int oldgreen = green;
int oldred = red;
int oldblue = blue;

 if(green < 75)
 green=green+50;
 else
 green=green+25;
 if(green > 255)
 	green=255;

 red=red-20;
 if(red<0)
 red=0;

 blue=blue-20;
 if(blue<0)
 blue=0;

if(oldgreen > oldred + 50 && oldgreen > oldblue + 50)
{
	green=oldgreen-20;
	if(green<0)
	green=0;

	if(red > blue && oldred < 225)
	{
		red=oldred+40;
		if(red>255)
		red=255;

	}
	else
	{
		blue=oldblue+40;
		if(blue>255)
		blue=255;

	}

}

return new Color(red,green,blue);
*/
}// end method getTransparentColor


}// end chessbot