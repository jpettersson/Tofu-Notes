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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.spacetofu.umlaut.model.Model;
import com.spacetofu.umlaut.model.note.Note;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "TofuNote.AlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
        //Get data from db
        long noteId = intent.getExtras().getLong("noteId", 0);
        if(noteId > 0) {
        	
        	Model.getInstance().setContext(context);
        	Note note = Model.getInstance().getNoteById(noteId);
        	if(note != null) {
	        	
        		Notification n = new Notification(R.drawable.icon_systembar, "Tofu Note Reminder", System.currentTimeMillis());
        		
        		n.defaults = Notification.DEFAULT_ALL;
        		n.flags = Notification.FLAG_AUTO_CANCEL;
        		
        		note.setReminderDate(0);
        		
        		Class activity = Umlaut.getHomeActivityFor(note.getType());
        		
        		if(activity != null) {
	        		
	        		Intent ni = new Intent(context, activity);
	        		ni.putExtra("noteId", noteId);
	        		ni.setAction("tofunote" + System.currentTimeMillis());
	        		ni.setData(Uri.parse("tofunote://" + System.currentTimeMillis()));
	                PendingIntent sender = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), ni, 0);
	        		
		        	String title = "Tofu Note Reminder";
		        	
		        	String body = note.getName().length() > 0 ? "Tap to read \"" + note.getName() + "\"." : "Tap to read note." ;
		        	
		        	n.setLatestEventInfo(context, title, body, sender);
		    		nm.notify(Umlaut.UML_NOTIFICATION_ID, n);
		    
		    		return;
		    		
        		}
	    		
        	}
        }
        
        Log.e(TAG, "Note not found.");
        
	}

}