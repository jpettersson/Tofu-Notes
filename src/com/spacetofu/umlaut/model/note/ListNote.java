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

package com.spacetofu.umlaut.model.note;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;


import com.spacetofu.umlaut.model.Model;
import com.spacetofu.umlaut.R;

public class ListNote extends Note {
	
	public ListNote() {
		super();
		type = Model.TYPE_LIST_NOTE;
		
	}
	
	public ListNote(long id, long parentId, String name, String content, long createdAt, long updatedAt, long type, long priority, long reminderAt, long locationId) {
		super(id, parentId, name, content, createdAt, updatedAt, type, priority, reminderAt, locationId);
	}
	
	public void createNewRow(String name) {
		
		NoteRow nr = new NoteRow(name);
		nr.setParentId(getId());
		nr.write();
		
		getChildren().add(nr);
	}
	
	public void deleteRow(NoteRow row) {
		Iterator<Note> iterator = getChildren().iterator();
		while(iterator.hasNext()) {
			NoteRow next = (NoteRow) iterator.next();
			if(next == row) {
				getChildren().remove(row);
				Model.getInstance().delete(row);
				return;
			}
		}
	}
	
	public ArrayList<Note> getPendingRows() {
		ArrayList<Note> list = new ArrayList<Note>();
		Iterator<Note> iterator = getChildren().iterator();
		while(iterator.hasNext()) {
			Note next = iterator.next();
			if(next instanceof NoteRow) {
				if(!((NoteRow) next).isCompleted()) {
					list.add(next);
				}
			}
		}
		
		return list;
	}
	
	public ArrayList<Note> getCompletedRows() {
		ArrayList<Note> list = new ArrayList<Note>();
		Iterator<Note> iterator = getChildren().iterator();
		while(iterator.hasNext()) {
			Note next = iterator.next();
			if(next instanceof NoteRow) {
				if(((NoteRow) next).isCompleted()) {
					list.add(next);
				}
			}
		}
		
		return list;
	}
	
	@Override
	public String toEmail(Context ctx) {
		String str = getName() + "\n";
		
	   	ArrayList<Note> pendingNotes = getPendingRows();
    	ArrayList<Note> completedNotes = getCompletedRows();
    	
    	if(pendingNotes.size() > 0) {
    		str += "\n";
    		
    		str += ctx.getString(R.string.pendingItems) + " \n";
    		
    		Iterator<Note> pi = pendingNotes.iterator();
    		while(pi.hasNext()) {
    			Note pn = pi.next();
    			str += ctx.getString(R.string.pendingAscii) + pn.getName() + "\n";
    		}

    	}
    	
    	if(completedNotes.size() > 0) {
    		str += "\n";
    		
    		str += ctx.getString(R.string.completedItems) + " \n";
    		
    		Iterator<Note> ci = completedNotes.iterator();
    		while(ci.hasNext()) {
    			Note cn = ci.next();
    			str += ctx.getString(R.string.completedAscii) + cn.getName() + "\n";
    		}
    	}
 
		return str;
	}

}