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
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Vector;

class tableClass {



public myDefaultTableModel gamedata;
Vector<String> data;
Vector<String> collumns;
String type1;
String type2;

tableClass()
{
data = new Vector();
collumns = new Vector();

collumns.add("index");
collumns.add("white name");
collumns.add("white rating");
collumns.add("black name");
collumns.add("black rating");
collumns.add("Result");
collumns.add("Time Controls");
collumns.add("ECO");
collumns.add("Date Game Started");

gamedata = new myDefaultTableModel(data, collumns);
type1="none";
type2="none";

}// end constructor

void createHistoryListColumns()
{
Vector<String> data = new Vector();
Vector<String> collumns = new Vector();

collumns.add("Index");
collumns.add("Result");
collumns.add("Rating");
collumns.add("Color");
collumns.add("Opponents Name");
collumns.add("Opponents Rating");
collumns.add("Time Controls");
collumns.add("ECO");
collumns.add("End");
collumns.add("Date Game Started");


gamedata = new myDefaultTableModel(data, collumns);
}

void createPgnListColumns()
{
Vector<String> data = new Vector();
Vector<String> collumns = new Vector();

collumns.add("Index");
collumns.add("White Name");
collumns.add("White Rating");

collumns.add("Black Name");
collumns.add("Black Rating");

collumns.add("Result");
collumns.add("Eco");
collumns.add("Event");
collumns.add("Site");
collumns.add("Date");




gamedata = new myDefaultTableModel(data, collumns);
}




void createMoveListColumns()
{
Vector<String> data = new Vector();
Vector<String> collumns = new Vector();

collumns.add("Move No.");
collumns.add("White");
collumns.add("Black");





gamedata = new myDefaultTableModel(data, collumns);
}

void addMove(int moveNum, String move)
{
	// moveNum 0 no moves.  move 1 white move 2 black
	Vector<String> newRow = new Vector();

	if(moveNum %2 == 1)
	{
		newRow.add("" +(int) (moveNum/2 + 1));
		newRow.add(move);
		newRow.add("");
		gamedata.addRow(newRow);

	}
	else // black insert
	{	gamedata.setValueAt(move, (int) (moveNum/2 ) -1, 2);
		//String whitemove = (String) gamedata.getValueAt((int) (moveNum/2 ) -1 , 1);
		//newRow.add("" + (int) (moveNum/2 + 1));
		//newRow.add(whitemove);
		//newRow.add(move);
		//gamedata.insertRow((int) (moveNum/2) -1, newRow);
	}
}

void removeMoves(int movetop, int num)
{
	//movetop represnts how many moves are in table before removal

	for(int a=0; a<num; a++)
	{
		removeOneMove(movetop);
		movetop--;
	}
}
void removeOneMove(int movetop)
{
	try {
	if(movetop%2 == 0) // remove a black move
	{

		gamedata.setValueAt("", (int) (movetop/2 ) -1, 2);

	}
	else // whites move removed
	{
		int row = (int) ((movetop + 1)/2 - 1);
		gamedata.removeRow(row);
	}
	}catch(Exception d){}
}
class myDefaultTableModel extends DefaultTableModel
{
	boolean done;
	int count;
	public boolean isCellEditable(int row, int collumn)
	{

		return false;

	}
	myDefaultTableModel(Vector<String> a1, Vector<String> a2)
	{
		super(a1,a2);
		count=0;
		done=false;

	}
	void addTableRow(Vector<String> a1)
	{
		super.addRow(a1);
	}

}
}// end class