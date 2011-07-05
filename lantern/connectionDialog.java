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
import java.awt.Window.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JDialog;
import java.util.ArrayList;;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.GroupLayout.*;


class connectionDialog extends JDialog
{
JTextField userNameField;
JPasswordField passwordField;
JLabel userNameLabel;
JLabel passwordLabel;
JButton okButton;
JButton cancelButton;
channels sharedVariables;
ConcurrentLinkedQueue<myoutput> queue;


connectionDialog(JFrame frame, channels sharedVariables1, ConcurrentLinkedQueue<myoutput> queue1, boolean mybool)
{
super(frame, mybool);
try {
queue=queue1;
sharedVariables=sharedVariables1;

userNameField=new JTextField(20);
if(!sharedVariables.myname.equals(""))
	userNameField.setText(sharedVariables.myname);

passwordField = new JPasswordField(20);

userNameLabel=new JLabel("User Name");
passwordLabel = new JLabel("Password");

okButton = new JButton("Ok");
cancelButton = new JButton("Cancel");


okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event)
				{
					login();
				}});

cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event)
				{


					dispose();
				}});

okButton.addKeyListener(new KeyListener() {

	public void keyPressed(KeyEvent e)
	{
        int a=e.getKeyCode();

		if(a==10)
		login();
	}


  public void keyTyped(KeyEvent e) {;

    }



    /** Handle the key-released event from the text field. */
    public void keyReleased(KeyEvent e) {;

    }

});

passwordField.addKeyListener(new KeyListener() {

	public void keyPressed(KeyEvent e)
	{
        int a=e.getKeyCode();

		if(a==10)
		login();

		if(a==27)
		passwordField.setText("");
	}


  public void keyTyped(KeyEvent e) {;

    }



    /** Handle the key-released event from the text field. */
    public void keyReleased(KeyEvent e) {;

    }

});

userNameField.addKeyListener(new KeyListener() {

	public void keyPressed(KeyEvent e)
	{
        int a=e.getKeyCode();

		if(a==10)
		giveFocus();
		if(a==27)
		userNameField.setText("");
	}


  public void keyTyped(KeyEvent e) {;

    }



    /** Handle the key-released event from the text field. */
    public void keyReleased(KeyEvent e) {;

    }

});


connectionPanel mypanel = new connectionPanel();
add(mypanel);
setSize(300,200);
}
catch(Exception dumb){}

}// end constructor

class connectionPanel extends JPanel {

	connectionPanel(){
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	JPanel panel3 = new JPanel();

	panel1.add(userNameLabel);
	panel1.add(userNameField);

	panel2.add(passwordLabel);
	panel2.add(passwordField);

	panel3.add(cancelButton);
	panel3.add(okButton);

 GroupLayout layout = new GroupLayout(getContentPane());
   //GroupLayout layout = new GroupLayout(this);
   getContentPane().setLayout(layout);

	ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
	ParallelGroup h2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

	h2.addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE);
	h2.addComponent(panel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE);
	h2.addComponent(panel3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE);





	//Add the group h1 to the hGroup
	hGroup.addGroup(GroupLayout.Alignment.TRAILING, h2);// was trailing
	//Create the horizontal group
	layout.setHorizontalGroup(hGroup);



	ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);// was leading


SequentialGroup v4 = layout.createSequentialGroup();



	v4.addComponent(panel1);
	v4.addComponent(panel2);
	v4.addComponent(panel3);




	vGroup.addGroup(v4);
layout.setVerticalGroup(vGroup);
	}// end constructor

}// end sub class


void login()
{

	String user = userNameField.getText();  
	if(user.startsWith("~"))
	{
         user=user.substring(1, user.length());
          sharedVariables.myServer="FICS";
           sharedVariables.doreconnect=true;

        }
	String password = passwordField.getText();
	myoutput data1= new myoutput();
    data1.data=user + "\n";
	myoutput data2 = new myoutput();
	data2.data=password + "\n";
    queue.add(data1);
    queue.add(data2);
	dispose();

}// end login method


void giveFocus()
{
 SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                              //  JComponent comp = DataViewer.getSubcomponentByName(e.getInternalFrame(),
                                //SearchModel.SEARCHTEXT);

                             passwordField.setFocusable(true);
                               passwordField.setRequestFocusEnabled(true);
                                //Input.requestFocus();
                           passwordField.requestFocusInWindow();
                            } catch (Exception e1) {
                                //ignore
                            }
                        }
                    });

}


void startFocus()
{
 SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                              //  JComponent comp = DataViewer.getSubcomponentByName(e.getInternalFrame(),
                                //SearchModel.SEARCHTEXT);

                             userNameField.setFocusable(true);
                               userNameField.setRequestFocusEnabled(true);
                                //Input.requestFocus();
                           userNameField.requestFocusInWindow();
                            } catch (Exception e1) {
                                //ignore
                            }
                        }
                    });

}

}// end class