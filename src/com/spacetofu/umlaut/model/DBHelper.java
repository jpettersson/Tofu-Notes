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
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.spacetofu.umlaut.model.note.Note;
import com.spacetofu.umlaut.model.note.NoteFactory;

public class DBHelper {

   private static final String 	DATABASE_NAME 	= "umlaut.db";
   private static final int 	DATABASE_VERSION 	= 13;
   
   private static final String T_NOTES 			= "unotes_extended";
   private static final String T_LOCATIONS		= "ulocations";
   
   private Context context;
   private SQLiteDatabase db;

   private static final String TAG = "DBHelper";

   public DBHelper(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
      
   }
   
   public void close() {
	   if(this.db != null) {
		   this.db.close();
	   }
   }
   
   public void write(Note n) {
	   if(n.isSaved()) {
		   updateNote(n);
	   }else{
		   insertNote(n);
	   }
   }
   
   public void writePriority(Note n) {
	   if(n.isSaved()) {
		   updateNotePriority(n);
	   }else{
		   insertNote(n);
	   }
   }
   
   public void writeReminder(Note n) {
	   if(n.isSaved()) {
		   updateNoteReminder(n);
	   }else{
		   insertNote(n);
	   }
   }
   
   private long insertNote(Note n) {
	   Calendar c = new GregorianCalendar();
	   
	   /*int step = (int) Math.round(Math.random() * 4);
	   Log.i(TAG, "roll back days: " + step);
	   c.add(Calendar.DATE, -step);*/
	   
	   long now = c.getTimeInMillis();
	   
       ContentValues values = new ContentValues();
       values.put("parent_id", n.getParentId());
       values.put("name", n.getName());
       values.put("content", n.getContent());
       values.put("createdAt", now);
       values.put("updatedAt", now);
       values.put("type", n.getType());
       values.put("priority", n.getPriority());
       values.put("reminderAt", n.getReminderDate());
       
       long id = db.insert(T_NOTES, null, values);
       
       Log.i("MODEL", "Inserted: " + id + " " + n.getType());
       
       n.setId(id);
       
       return id;
   }
   
   private void updateNote(Note n) {
	   Calendar c = new GregorianCalendar();
	   long now = c.getTimeInMillis();
	   
	   ContentValues values = new ContentValues();
	   values.put("name", n.getName());
	   values.put("content", n.getContent());
	   values.put("updatedAt", now);
	   
	   this.db.update(T_NOTES, values, "_id = " + n.getId(), null);
   }
   
   private void updateNotePriority(Note n) {
	   Calendar c = new GregorianCalendar();
	   long now = c.getTimeInMillis();
	   
	   ContentValues values = new ContentValues();
	   values.put("priority", n.getPriority());
	   values.put("updatedAt", now);
	   
	   Log.i("MODEL", "Update note priority. id:" + n.getId() + " priority:" + n.getPriority());
	   
	   this.db.update(T_NOTES, values, "_id = " + n.getId(), null);
   }
   
   private void updateNoteReminder(Note n) {
	   Calendar c = new GregorianCalendar();
	   long now = c.getTimeInMillis();
	   
	   ContentValues values = new ContentValues();
	   values.put("reminderAt", n.getReminderDate());
	   values.put("updatedAt", now);
	   
	   Log.i("MODEL", "Update note reminder date. id:" + n.getId() + " reminderAt:" + n.getReminderDate());
	   
	   this.db.update(T_NOTES, values, "_id = " + n.getId(), null);
   }
   

   public void deleteNote(Note n) {
	   if(n.getId() > 0) {
		   Log.i(TAG, "Delete note: " + n.getId() + " " + n.getType());
		   this.db.delete(T_NOTES, "_id = " + n.getId(), null);
	   }
   }
   
   public void deleteAllNotes() {
      this.db.delete(T_NOTES, null, null);
   }
   
   public Note getNoteById(long id) {
	   Cursor cursor = this.db.query(T_NOTES, new String[] { "_id", "parent_id", "name", "content", "createdAt", "updatedAt", "type", "priority", "reminderAt", "locationId"}, null, null, null, null, "createdAt desc");
	      if (cursor.moveToFirst()) {
	         do {
	        	
	        	if(cursor.getLong(cursor.getColumnIndex("_id")) == id) {
	        	
					try {
						Note note = NoteFactory.create(	
												cursor.getLong(cursor.getColumnIndex("_id")),
												cursor.getLong(cursor.getColumnIndex("parent_id")),
												cursor.getString(cursor.getColumnIndex("name")),
												cursor.getString(cursor.getColumnIndex("content")),
												cursor.getLong(cursor.getColumnIndex("createdAt")),
												cursor.getLong(cursor.getColumnIndex("updatedAt")),
												cursor.getLong(cursor.getColumnIndex("type")),
												cursor.getLong(cursor.getColumnIndex("priority")),
												cursor.getLong(cursor.getColumnIndex("reminderAt")),
												cursor.getLong(cursor.getColumnIndex("locationId"))
											);
						
						note.setChildren(getChildrenFor(note.getId()));
						
						if (cursor != null && !cursor.isClosed()) {
			        		cursor.close();
			   	      	}
						return note;
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		
	        	}
	        	
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      
	      return null;
   }
   
   public ArrayList<Note> getNotes(int filter) {
	  ArrayList<Note> list = new ArrayList<Note>();
	  Cursor cursor;
	  
	  if(filter == Model.FILTER_HIGH_PRIORITY) {
		  cursor = this.db.query(T_NOTES, new String[] { "_id", "parent_id", "name", "content", "createdAt", "updatedAt", "type", "priority", "reminderAt", "locationId"}, "type != '" + Model.TYPE_NOTE_ROW + "' AND priority = '" + Note.PRIORITY_HIGH + "'", null, null, null, "createdAt desc");
	  }else if (filter == Model.FILTER_NORMAL_PRIORITY){
		  cursor = this.db.query(T_NOTES, new String[] { "_id", "parent_id", "name", "content", "createdAt", "updatedAt", "type", "priority", "reminderAt", "locationId" }, "type != '" + Model.TYPE_NOTE_ROW + "' AND priority != '" + Note.PRIORITY_HIGH + "'", null, null, null, "createdAt desc");
	  }else if(filter == Model.FILTER_HAS_REMINDER) {
		  cursor = this.db.query(T_NOTES, new String[] { "_id", "parent_id", "name", "content", "createdAt", "updatedAt", "type", "priority", "reminderAt", "locationId" }, "reminderAt > 0", null, null, null, "createdAt desc");
	  }else{
		  cursor = this.db.query(T_NOTES, new String[] { "_id", "parent_id", "name", "content", "createdAt", "updatedAt", "type", "priority", "reminderAt", "locationId"}, "type != '" + Model.TYPE_NOTE_ROW + "'", null, null, null, "createdAt desc");
	  }
	  
	  if (cursor.moveToFirst()) {
         do {
        	 Note note;
			try {
				note = NoteFactory.create(
						 				cursor.getLong(cursor.getColumnIndex("_id")),
						 				cursor.getLong(cursor.getColumnIndex("parent_id")),
										cursor.getString(cursor.getColumnIndex("name")),
										cursor.getString(cursor.getColumnIndex("content")),
										cursor.getLong(cursor.getColumnIndex("createdAt")),
										cursor.getLong(cursor.getColumnIndex("updatedAt")),
										cursor.getLong(cursor.getColumnIndex("type")),
										cursor.getLong(cursor.getColumnIndex("priority")),
										cursor.getLong(cursor.getColumnIndex("reminderAt")),
										cursor.getLong(cursor.getColumnIndex("locationId")) 
									);
				
				note.setChildren(getChildrenFor(note.getId()));
				
				list.add(note);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
             
         } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      
      return list;
   }
   
   
   
   public ArrayList<Note> getChildrenFor(long parentId) {
	   if(parentId != 0) {
		   ArrayList<Note> list = new ArrayList<Note>();
		      Cursor cursor = this.db.query(T_NOTES, new String[] { "_id", "parent_id", "name", "content", "createdAt", "updatedAt", "type", "priority", "reminderAt", "locationId" }, "parent_id = '" + parentId + "' AND type = '" + Model.TYPE_NOTE_ROW + "'", null, null, null, "createdAt desc");
		      if (cursor.moveToFirst()) {
		         do {
		        	 
					try {
						Note child = NoteFactory.create(
								 				cursor.getLong(cursor.getColumnIndex("_id")),
								 				cursor.getLong(cursor.getColumnIndex("parent_id")),
												cursor.getString(cursor.getColumnIndex("name")),
												cursor.getString(cursor.getColumnIndex("content")),
												cursor.getLong(cursor.getColumnIndex("createdAt")),
												cursor.getLong(cursor.getColumnIndex("updatedAt")),
												cursor.getLong(cursor.getColumnIndex("type")),
												cursor.getLong(cursor.getColumnIndex("priority")) ,
												cursor.getLong(cursor.getColumnIndex("reminderAt")),
												cursor.getLong(cursor.getColumnIndex("locationId")) 
											);
						list.add(child);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	
		             
		         } while (cursor.moveToNext());
		      }
		      if (cursor != null && !cursor.isClosed()) {
		         cursor.close();
		      }
		      return list;
	   	  }
	   return null;
   }
   
   
   
 
   public int getCount() {
	   Cursor cursor = this.db.query(T_NOTES, new String[] { "_id", "name", "content", "createdAt", "updatedAt", "type" }, null, null, null, null, "createdAt");
	   int count = cursor.getCount();
	   
	   cursor.close();
	   return count;
	}
   
   private static class OpenHelper extends SQLiteOpenHelper {

      OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE " + T_NOTES + " (_id INTEGER PRIMARY KEY, parent_id INTEGER, name TEXT, content TEXT, createdAt INTEGER, updatedAt INTEGER, type INTEGER, priority INTEGER, reminderAt INTEGER, locationId INTEGER)");
         db.execSQL("CREATE TABLE " + T_LOCATIONS + " (_id INTEGER PRIMARY KEY, name TEXT, long TEXT, lat TEXT, createdAt INTEGER, updatedAt INTEGER)");
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	  
    	  
    	  if(oldVersion == 12) {
    		  Log.i("Umlaut.Model", "Migrating DB from 12 to 13.");
    		  onCreate(db);
    		  
    		  //Migrate all the old notes
    		  Cursor cursor = db.query("unotes", new String[] { "_id", "parent_id", "name", "content", "createdAt", "updatedAt", "type" }, null, null, null, null, "createdAt desc");
    	      if (cursor.moveToFirst()) {
    	         do {
					try {
						
	    	        	ContentValues values = new ContentValues();
	    	        	values.put("_id", cursor.getLong(cursor.getColumnIndex("_id")));
	     		        values.put("parent_id", cursor.getLong(cursor.getColumnIndex("parent_id")));
	     		        values.put("name", cursor.getString(cursor.getColumnIndex("name")));
	     		        values.put("content", cursor.getString(cursor.getColumnIndex("content")));
	     		        values.put("createdAt", cursor.getLong(cursor.getColumnIndex("createdAt")));
	     		        values.put("updatedAt", cursor.getLong(cursor.getColumnIndex("updatedAt")));
	     		        values.put("type", cursor.getLong(cursor.getColumnIndex("type")));
	     		        values.put("priority", 0);
	     		        values.put("reminderAt", 0);
	     		    
	     		        long id = db.insert(T_NOTES, null, values);
	     		       
	     		        //Log.i("Umlaut.Model", "Migrated 12>13 note: " + id + " parent_id: " + cursor.getLong(cursor.getColumnIndex("parent_id")) + " type: " + cursor.getLong(cursor.getColumnIndex("type")));
	    	        	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	         } while (cursor.moveToNext());
    	      }
    	      if (cursor != null && !cursor.isClosed()) {
    	         cursor.close();
    	      }
    		  
    	      //db.execSQL("DROP TABLE IF EXISTS unotes");
    	      Log.i("Umlaut.Model", "Migration complete!");
    		  
    	  }else if(oldVersion < 12) {
    		  Log.i("Umlaut.Model", "Migrating DB from 11 to 12.");
    		  //Old versions only had textnotes, so just migrate those.
    		  
    		  String OLD_TABLE = "notes";
    		  
    		  //Create new tables
    		  onCreate(db);
    		
    		  Calendar c = new GregorianCalendar();
			   long now = c.getTimeInMillis();
    		  
    		  //Fetch old data and insert into new tables
    		  Cursor cursor = db.query(OLD_TABLE, new String[] { "_id", "name", "body", "date" }, null, null, null, null, "_id desc");
    	      if (cursor.moveToFirst()) {
    	         do {
					try {
						
						Note n = NoteFactory.create(Model.TYPE_TEXT_NOTE);
						n.setName(cursor.getString(cursor.getColumnIndex("name")));
	    	        	n.setContent(cursor.getString(cursor.getColumnIndex("body")));
	    	        	n.setCreatedAt(cursor.getLong(cursor.getColumnIndex("date")));
	    	        	
	    	        	long createdAtDate;
	     			   	if(n.getCreatedAt() > 0) { 
	     				   createdAtDate = n.getCreatedAt();
	     			   	}else{
	     				   createdAtDate = now;
	     			   	}
	    	        	
	    	        	ContentValues values = new ContentValues();
	     		        values.put("parent_id", n.getParentId());
	     		        values.put("name", n.getName());
	     		        values.put("content", n.getContent());
	     		        values.put("createdAt", createdAtDate);
	     		        values.put("updatedAt", now);
	     		        values.put("type", n.getType());
	     		    
	     		        long id = db.insert(T_NOTES, null, values);
	     		       
	     		        Log.i("Umlaut.Model", "Migrated 11>12 note: " + id + " " + n.getType());
	    	        	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	         } while (cursor.moveToNext());
    	      }
    	      if (cursor != null && !cursor.isClosed()) {
    	         cursor.close();
    	      }
    	      
    	      //Remove old table
    	      //disabled for now.. do it in next version instead! 
    	      //db.execSQL("DROP TABLE IF EXISTS " + OLD_TABLE);
    		  
    	      Log.i("Umlaut.Model", "Migration complete!");
    	  }
    	 
    	  
         //For the future...
    	  
    	  //Log.w("DBHelper", "Upgrading database, this will drop tables and recreate.");
         //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
         //onCreate(db);
    	  
      }
      
   }



}