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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;

import com.spacetofu.umlaut.R;
import com.spacetofu.umlaut.Umlaut;
import com.spacetofu.umlaut.model.note.Note;
import com.spacetofu.umlaut.model.note.NoteFactory;

public class Model {
	
	private static final String TAG = "Umlaut.Model";

	private static Model instance = null;
	
	private Note currentNote = null;
	private Context context;

	public static long TYPE_TEXT_NOTE = 0;
	public static long TYPE_LIST_NOTE = 1;
	public static long TYPE_PHOTO_NOTE = 2;
	public static long TYPE_VOICE_NOTE = 3;
	public static long TYPE_ROOT_NOTES = 4;
	public static long TYPE_NOTE_ROW = 5;

	public static int FILTER_ALL = 0;
	public static int FILTER_HIGH_PRIORITY = 1;
	public static final int FILTER_NORMAL_PRIORITY = 2;
	public static final int FILTER_HAS_REMINDER = 1002;
	
	public static Model getInstance() {
		if(instance == null) instance = new Model();
		return instance;
	}

	public boolean selectNewNote(long type) {
		
		DBHelper helper = getDBHelper();
		int numNotes = helper.getCount();
		helper.close();
		
		if(numNotes <= Umlaut.NUM_NOTES_MAX) {
			try {
				selectNote(NoteFactory.create(type));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;	
		}
		
		return false;
	}
	
	public Boolean selectNoteByReference(Object ref) {
		
		if(ref instanceof Note) {
			ArrayList<Note> notes = getNotes();
			Iterator<Note> i = notes.iterator();
			while(i.hasNext()) {
				Note n = i.next();
				if(n.getId() == ((Note) ref).getId()){
					selectNote(n);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void selectNoteById(long noteId) {
		Note n = getNoteById(noteId);
		if(n != null) {
			selectNote(n);
		}
	}
	
	public void selectNote(Note note) {
		currentNote = note;
	}
	
	public void write(Note n) { 
		DBHelper helper = getDBHelper();
		helper.write(n);
		helper.close();
	}
	
	public void writePriority(Note n) {
		DBHelper helper = getDBHelper();
		helper.writePriority(n);
		helper.close();
	}
	
	public void writeReminder(Note n) {
		DBHelper helper = getDBHelper();
		helper.writeReminder(n);
		helper.close();
	}

	public void delete(Note n) {
		DBHelper helper = getDBHelper();
		helper.deleteNote(n);
		helper.close();
	}
		
	public void writeCurrentNote() {
		if(currentNote != null) {
			DBHelper helper = getDBHelper();
			helper.write(currentNote);
			helper.close();
		}
	}

	public void deleteCurrentNote() {
		if(currentNote != null) {
			DBHelper helper = getDBHelper();
			
			//delete children
			ArrayList<Note> children = currentNote.getChildren();
			Iterator<Note> i = children.iterator();
			while(i.hasNext()) {
				Note n = i.next();
				helper.deleteNote(n);
			}
			
			helper.deleteNote(currentNote);
			
			helper.close();
		}
	}
	
	public void deleteAllNotes() {
		DBHelper helper = getDBHelper();
		helper.deleteAllNotes();
		helper.close();
	}
	

	public Note getNoteById(long noteId) {
		DBHelper helper = getDBHelper();
		Note note = helper.getNoteById(noteId);
		helper.close();
		
		return note;
	}	
	
	public ArrayList<Note> getNotes() {
		
		DBHelper helper = getDBHelper();
		ArrayList<Note> notes = helper.getNotes(FILTER_ALL);
		helper.close();
		
		return notes;
	}
	
	public ArrayList<Note> getNotes(int condition) {
		
		DBHelper helper = getDBHelper();
		ArrayList<Note> notes = helper.getNotes(condition);
		helper.close();
		
		return notes;
	}
	
	public ArrayList<NoteGroup> getNotesGrouped(Context ctx, boolean useDateGroups) {
		DBHelper helper = getDBHelper();
		
		NoteGroup nullgGroup;
		
		ArrayList<NoteGroup> groups = new ArrayList<NoteGroup>();
		//First, get important notes.
		ArrayList<Note> impNotes = helper.getNotes(FILTER_HIGH_PRIORITY);
		if(impNotes.size() > 0) {
			
			nullgGroup = new NoteGroup("null", new ArrayList<Note>());
			groups.add(nullgGroup);
			
			NoteGroup ng = new NoteGroup("Important!", impNotes);
			ng.setIconResourceId(R.drawable.icon_important_dark);
			ng.setTextColor(0xffcd4040);
			groups.add(ng);
		}else{
			if(useDateGroups) {
				nullgGroup = new NoteGroup("null", new ArrayList<Note>());
				groups.add(nullgGroup);
			}
		}
		
		//Then get all other notes and group them per day.
		ArrayList<Note> norNotes = helper.getNotes(FILTER_NORMAL_PRIORITY);
		if(norNotes.size() > 0) {
			
			if(useDateGroups) {
			
				LinkedHashMap<String, ArrayList<Note>> m = new LinkedHashMap<String, ArrayList<Note>>();
				
				Iterator<Note> i = norNotes.iterator();
				while(i.hasNext()) {
					Note n = i.next();
					String date = Umlaut.formatDate(ctx, n.getCreatedAt());
					
					if(!m.containsKey(date)) {
						m.put(date, new ArrayList<Note>());
					}
					
					m.get(date).add(n);
				}
				
				for (Map.Entry<String, ArrayList<Note>> entry : m.entrySet()) {
				    String key = entry.getKey();
				    ArrayList<Note> value = entry.getValue();
				    
				    NoteGroup ng = new NoteGroup(key, value);
				    ng.setTextColor(0xff505050);
				    groups.add(ng);
				    
				}
				
			}else{
				
				groups.add(new NoteGroup("Regular", norNotes));
				
			}
			
			//groups.add(new NoteGroup("Regular", norNotes));
		} 
	
		helper.close();
		return groups;
	}
	
	public Note getCurrentNote() {
		return currentNote;
	}
	
	/*public ArrayList<NoteRow> getCurrentNoteRows() {
		DBHelper helper = getDBHelper();
		ArrayList<NoteRow> rows = helper.getRowsFor(currentNote.getId());
		
		if(rows.size() == 0) {
			createNewNoteRow(currentNote.getId());
			rows = helper.getRowsFor(currentNote.getId());
		}
		
		helper.close();
		
		return rows;
	}*/
	
	
	
	public void reset() {
		currentNote = null;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	private DBHelper getDBHelper() {
		return new DBHelper(context);
	}

	public boolean hasCurrentNote() {
		return currentNote != null;
	}

	


}