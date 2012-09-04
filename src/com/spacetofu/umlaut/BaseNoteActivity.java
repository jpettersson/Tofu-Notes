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

import java.lang.reflect.Method;
import java.util.Calendar;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.spacetofu.bitmap.Effect;
import com.spacetofu.umlaut.model.Model;
import com.spacetofu.umlaut.model.note.Note;
import com.spacetofu.umlaut.ui.ReminderDialog;
import com.spacetofu.umlaut.utils.Say;

public abstract class BaseNoteActivity extends Activity {
	
	protected void processIntent() {
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			long attachedNoteId = extras.getLong("noteId", 0);
			if(attachedNoteId > 0) {
				Model.getInstance().selectNoteById(attachedNoteId);
			}
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ImageButton save = (ImageButton)findViewById(R.id.btnSave);
		save.setOnClickListener(new OnClickListener() {
			
		    @Override
		    public void onClick(View v) {
				save();
		    }
		    
		});
		
		ImageButton delete = (ImageButton)findViewById(R.id.btnDelete);
		delete.setOnClickListener(new OnClickListener() {
			
		    @Override
		    public void onClick(View v) {
		    	delete();
		    }
		    
		});
		
		ImageButton close = (ImageButton)findViewById(R.id.btnClose);
		close.setOnClickListener(new OnClickListener() {
			
		    @Override
		    public void onClick(View v) {
		    	close();
		    }
		    
		});
		
		//This used to be the news button.
		//ImageButton btnTofu = (ImageButton)findViewById(R.id.btnTofu);
		
		ImageButton btnImportant = (ImageButton)findViewById(R.id.btnImportant);
		btnImportant.setOnClickListener(new OnClickListener() {
			
		    @Override
		    public void onClick(View v) {
				
		    	if(Model.getInstance().getCurrentNote().getPriority() == Note.PRIORITY_NORMAL) {
		    		Model.getInstance().getCurrentNote().setPriority(Note.PRIORITY_HIGH);
		    		
		    		Say.now(BaseNoteActivity.this, getString(R.string.markedImportant));
		    		
		    	}else{
		    		Model.getInstance().getCurrentNote().setPriority(Note.PRIORITY_NORMAL);
		    	}
		    	
		    	updateFooterMenu();
		    }
		    
		});
		
		ImageButton btnReminder = (ImageButton)findViewById(R.id.btnReminder);
		btnReminder.setOnClickListener(new OnClickListener() {
			
		    @Override
		    public void onClick(View v) {
				
		    	ReminderDialog.Builder builder = new ReminderDialog.Builder(BaseNoteActivity.this);
		    	
		    	builder.setTitle(getString(R.string.setReminder));
		    	
		    	String posButtonLabel = getString(R.string.set);
		    	
		    	if(Model.getInstance().getCurrentNote().hasReminder()) {
		    		Calendar c = Calendar.getInstance();
		    		c.setTimeInMillis(Model.getInstance().getCurrentNote().getReminderDate());
		    		builder.setCalendar(c);
		    		
		    		posButtonLabel = getString(R.string.clear);
		    	}
		    	
		    	builder.setPositiveButton(posButtonLabel, new DialogInterface.OnClickListener() {
		    		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
			            long alarmDate = ((ReminderDialog) dialog).getCalendar().getTimeInMillis();
			            
			            String message;
						long noteId = Model.getInstance().getCurrentNote().getId();
						Intent intent = new Intent(BaseNoteActivity.this, AlarmReceiver.class);
						intent.putExtra("noteId", noteId);
			            PendingIntent sender = PendingIntent.getBroadcast(BaseNoteActivity.this, (int)noteId, intent, PendingIntent.FLAG_ONE_SHOT);
			            
						if(Model.getInstance().getCurrentNote().hasReminder() && Model.getInstance().getCurrentNote().getReminderDate() == alarmDate) {
							
							Model.getInstance().getCurrentNote().setReminderDate(0);
				            Umlaut.cancelAlarm(BaseNoteActivity.this, sender);
				            
				            //Log.i("BaseNoteActivity", "removed reminder");
				            ((ReminderDialog) dialog).changePositiveButtonText(getString(R.string.set));
						
						}else{

							if(alarmDate > System.currentTimeMillis()) {	
								Model.getInstance().getCurrentNote().setReminderDate(alarmDate);
								
					            Umlaut.setAlarm(BaseNoteActivity.this, ((ReminderDialog) dialog).getCalendar().getTimeInMillis(), sender);
					            
								message = getString(R.string.reminderSet);
							}else{
								message = getString(R.string.reminderDateError);
							}
							
							dialog.dismiss();
							Say.now(BaseNoteActivity.this, message);	
							
						}
						
						updateFooterMenu();
					}
		    		
		    	});
		    	
		    	builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
		    		
		    	});
		    	
		    	ReminderDialog dialog = builder.create();
		    	
		    	dialog.show();
		    }
		    
		});
		
		updateFooterMenu();
	}
	
	//handle menu highlights
	protected void updateFooterMenu() {
    	ImageButton btnImportant = (ImageButton)findViewById(R.id.btnImportant);
    	if(Model.getInstance().getCurrentNote().getPriority() == Note.PRIORITY_HIGH) {
    		Effect.substituteWhite(btnImportant.getDrawable(), Umlaut.getActiveColor(BaseNoteActivity.this));
    	}else{
    		Effect.clear(btnImportant.getDrawable());
    	}
    	btnImportant.invalidate();
    	
    	ImageButton btnReminder = (ImageButton)findViewById(R.id.btnReminder);
    	if(Model.getInstance().getCurrentNote().hasReminder()) {
    		Effect.substituteWhite(btnReminder.getDrawable(), Umlaut.getActiveColor(BaseNoteActivity.this));
    	}else{
    		Effect.clear(btnReminder.getDrawable());
    	}
    	btnReminder.invalidate();
    }
	
	@Override
    public void onResume() {
		if(!Model.getInstance().hasCurrentNote()) goToOverview();
		
		Umlaut.readSettings(this);
    	super.onResume();
    }
	
	
	protected void goToOverview() {
    	
    	Class myTarget;
    	Class[] paramTypes = {
    	       Integer.TYPE, Integer.TYPE
    	};
    	Method myMethod = null;
    	
    	try {
    	    myTarget = Class.forName("android.app.Activity");
    	    myMethod = myTarget.getDeclaredMethod("overridePendingTransition", paramTypes);
    	} catch (Exception e) {
    	    //e.printStackTrace();
    	}
    	
    	finish();
    	try {
    	    myMethod.invoke(this, 0, 0); // this - your Activity instance
    	} catch (Exception e) {
    	    //e.printStackTrace();
    	}

    }
	
	@Override
	public void onPause() {
		Say.silence();
		super.onPause();
	}
	
	abstract void save();
	abstract void delete();
	abstract void close();
	
}
