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
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.filechooser.FileFilter;


class toolboxDialog extends JDialog
{
JList toolboxList;
JScrollPane toolboxListScroller;
Timer timer;
DataInputStream in;
//ArrayList<toolscripts> myscripts;  // need to define this as a class. holds the actual scripts, indexed by list number.
int count = 0;
int conNumber=0;
String myprefix;
double delay=0;
BufferedReader br;
int onLine=0;
/* controls */
JLabel headerLabel;
JTextField myprefixField;
JTextField myoutputTabField;
JTextField mydelayField;
JLabel myprefixLabel;
JLabel myoutputTabLabel;
JLabel mydelayLabel;
JButton loaderButton;
JButton cancelButton;
ConcurrentLinkedQueue<myoutput> queue;
channels sharedVariables;

toolboxDialog(final JFrame frame, boolean mybool, ConcurrentLinkedQueue<myoutput> queue1, channels sharedVariables1)
{
super(frame, mybool);
setTitle("ToolBox File Interface");
queue=queue1;
sharedVariables=sharedVariables1;

/* now define controls */
headerLabel = new JLabel("<html>Load a script then right click on it to run.<br>Add an optional prefix or timer command before running it.</html>");


/* the list */
toolboxList = new JList(sharedVariables.toolboxListData.model);
toolboxList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
toolboxList.setLayoutOrientation(JList.VERTICAL);
toolboxList.setVisibleRowCount(-1);
//toolboxList.setBackground(listColor);
toolboxListScroller = new JScrollPane(toolboxList);
/* end list initializtion */

myprefixLabel = new JLabel("Enter an optional prefix to script commands");
myprefixField = new JTextField(20);
mydelayLabel = new JLabel("Enter a number for delay between script commands in seconds. defaults to 0.");
mydelayField = new JTextField(2);
myoutputTabLabel = new JLabel("Indicate the tab you want messages generated from script commands to go. 0-11. defaults to 11.");
myoutputTabField = new JTextField(2);
loaderButton = new JButton("Load Script File");
cancelButton = new JButton("Cancel");
/************ now listeners *******************/
	cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event)
				{dispose();
				}
			});

	loaderButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event)
				{


		try {
			JFileChooser fc = new JFileChooser();








fc.setCurrentDirectory(new File("."));;
fc.setFileFilter(new FileFilter() {
        public boolean accept(File f) {
          if(f.getName().toLowerCase().endsWith(".b2s"))
          return true;

           if(f.getName().toLowerCase().endsWith(".b2a"))
          return true;

            if( f.isDirectory())
            return true;
            return false;
        }

        public String getDescription() {
         /* if(f.getName().toLowerCase().endsWith(".b2s"))
          return "*.b2s";
                    if(f.getName().toLowerCase().endsWith(".b2a"))
		            return "*.b2a";
*/

return "Scripter Files";

        }
      });






			int returnVal = fc.showOpenDialog(frame);

			 if (returnVal == JFileChooser.APPROVE_OPTION)
			 {
			  File scriptFile = fc.getSelectedFile();
			  if(!scriptFile.getName().toLowerCase().endsWith(".b2s") && !scriptFile.getName().toLowerCase().endsWith(".b2a"))
			  return;

			  String myFilename=scriptFile.getAbsolutePath();
			  Integer mycount = new Integer(count);
				sharedVariables.toolboxListData.addToList(myFilename, mycount.toString());
				count++;
		  	}// end if
		}// end try
		catch(Exception d){}
	}// end method
}// end class
);


MouseListener mouseListenerScripts = new MouseAdapter() {
     public void mouseClicked(MouseEvent e) {
         if (e.getClickCount() == 2) {
             int index = toolboxList.locationToIndex(e.getPoint());

             String play = sharedVariables.toolboxListData.getOfferNumber(index);
             if(!play.equals("-1"))
             {
				/* JFrame myframe = new JFrame();
				 JDialog mydi = new JDialog(myframe, true);
				 mydi.setTitle((String)toolboxListData.model.elementAt(index));

				 mydi.setSize(500,50);
				 mydi.setVisible(true);
					*/
					String outputTab = myoutputTabField.getText();
					try {
						int num1 = Integer.parseInt(outputTab);
						if(num1 >= 0 && num1 < sharedVariables.maxConsoleTabs)
						conNumber=num1;
						else
						conNumber=sharedVariables.maxConsoleTabs - 1;
					}catch(Exception d){}
					try {
						delay=Double.parseDouble(mydelayField.getText());
						if(!(delay>0 && delay <30))
						delay=0;

						}
						catch(Exception de){}


					myprefix = myprefixField.getText();
					runFile((String)sharedVariables.toolboxListData.model.elementAt(index));

          	}
     }}
 };
 toolboxList.addMouseListener(mouseListenerScripts);

/********* now layout *************************/

JPanel pane = new JPanel();
pane.setLayout(new GridLayout(6,1));
JPanel pane1 = new JPanel();
JPanel pane2 = new JPanel();
JPanel pane3 = new JPanel();
JPanel pane4 = new JPanel();

// 1
pane.add(headerLabel);
// 2
pane.add(toolboxListScroller);
// 3
pane1.add(myprefixLabel);
pane1.add(myprefixField);
pane.add(pane1);
// 4
pane2.add(mydelayLabel);
pane2.add(mydelayField);
pane.add(pane2);
// 5
pane3.add(myoutputTabLabel);
pane3.add(myoutputTabField);
pane.add(pane3);
// 6
pane4.add(cancelButton);
pane4.add(loaderButton);
pane.add(pane4);
add(pane);

}// end constructor



void runFile(String myfile)
{
      try{
	boolean isb2a=false;

	String extension="";
	String prefix="";
	try{
		extension=myfile.substring(myfile.length()-3, myfile.length());
		if(extension.equals("b2a"))
		{
			prefix=myprefix + " ";
			isb2a=true;
		}
	}catch(Exception d9){}
	myprefix=prefix;
    FileInputStream fstream = new FileInputStream(myfile);
    // Get the object of DataInputStream
    in = new DataInputStream(fstream);
       br = new BufferedReader(new InputStreamReader(in));
    String strLine;
    //Read File Line By Line
if(delay == 0)
{   onLine =0;
	while ((strLine = br.readLine()) != null)   {
    {

		runCommand(strLine, prefix);
		onLine++;

	}

      in.close();
    }

}
else
{
timer = new Timer (  ) ;
		timer.schedule( new ToDoTask (  ) , (int)(delay * 1000)) ;
}
    //Close the input stream

    }catch (Exception e){//Catch exception if any
     // do nothing
    }
}


void runCommand(String command, String prefix)
{
	myoutput output = new myoutput();
	output.data="`c" + conNumber + "`" + prefix + command + "\n";
	output.consoleNumber=conNumber;
    queue.add(output);

}














class ToDoTask extends TimerTask  {


    public void run (  )   {
	String strLine;
	try {
		if ((strLine = br.readLine()) != null)
      {
		  runCommand(strLine, myprefix);
		  timer.schedule( new ToDoTask (  ) , (int)(delay * 1000)) ;

	  }
	  else
	  in.close();
  }catch(Exception d){}

}// end run

}// end class




}// end class