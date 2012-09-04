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

package com.spacetofu.umlaut;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spacetofu.umlaut.model.Model;
import com.spacetofu.umlaut.model.note.Note;

public class AlarmSetter extends BroadcastReceiver {

	private static final String TAG = "TofuNotes.AlarmSetter";

	@Override
	public void onReceive(Context ctx, Intent intent) {
		
		Model.getInstance().setContext(ctx);
		ArrayList<Note> notes = Model.getInstance().getNotes(Model.FILTER_HAS_REMINDER);
		Iterator<Note> i = notes.iterator();
		while(i.hasNext()) {
			Note note = i.next();
			
			if(note.hasReminder()) {
				Log.i(TAG, "Set alarm: " + note.getId() + " at " + Umlaut.formatDate(ctx, note.getReminderDate()));
				
				long noteId = note.getId();
				Intent newIntent = new Intent(ctx, AlarmReceiver.class);
				intent.putExtra("noteId", noteId);
	            PendingIntent sender = PendingIntent.getBroadcast(ctx, (int)noteId, newIntent, PendingIntent.FLAG_ONE_SHOT);
				
	            Umlaut.setAlarm(ctx, note.getReminderDate(), sender);
			}
			
		}
	}

}
