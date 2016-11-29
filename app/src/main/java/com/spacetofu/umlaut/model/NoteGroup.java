/***
 	Copyright (c) 2010-2012 Jonathan Pettersson & Hampus Olsson
	
	Contact us at: 
	jonathan@spacetofu.com
	hampus@spacetofu.com
	
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.	If not, see <http://www.gnu.org/licenses/>.
*/		

package com.spacetofu.umlaut.model;

import java.util.ArrayList;

import com.spacetofu.umlaut.model.note.Note;

public class NoteGroup {
	
	private String title;
	private ArrayList<Note> notes;
	private int textColor;
	private int iconResourceId = 0;
	
	public NoteGroup(String title, ArrayList<Note> notes) {
		this.title = title;
		this.notes = notes;
	}
	
	public String getTitle() {
		return title;
	}
	
	public ArrayList<Note> getNotes() {
		return notes;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
	
	public int getTextColor() {
		return textColor;
	}

	public void setIconResourceId(int iconResourceId) {
		this.iconResourceId = iconResourceId;
	}
	
	public int getIconResourceId() {
		return iconResourceId;
	}
	
}
