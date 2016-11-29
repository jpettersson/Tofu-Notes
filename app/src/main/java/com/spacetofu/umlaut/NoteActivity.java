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


import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.spacetofu.umlaut.model.Model;
import com.spacetofu.umlaut.ui.ConfirmDialog;
import com.spacetofu.umlaut.utils.Say;

public class NoteActivity extends BaseNoteActivity {
    protected static final String TAG = "Umlaut.NoteActivity";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Model.getInstance().setContext(this);
    	processIntent();
    	
    	setTheme(Umlaut.getCurrentTheme(this));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.note);
        
        final EditText noteName = (EditText) findViewById(R.id.noteName);
        final EditText noteBody = (EditText) findViewById(R.id.noteBody);
        
        String name = Model.getInstance().getCurrentNote().getName();
        if(name.length() > 0) {
        	noteName.setText(name);
        	noteName.setTextColor(0xFF000000);
        } else
        	noteName.setText(getString(R.string.defaultNoteName));
        
        String body = Model.getInstance().getCurrentNote().getContent();
        
        if(body.length() > 0) {
        	noteBody.setText(body);
        	noteBody.setTextColor(0xFF000000);
        } else
        	noteBody.setText(getString(R.string.defaultNoteContent));
        
        class DefaultString {
        	
        	public void evaluate(View v, boolean hasFocus) {
        		TextView tv;
				String str;
				String def;
				
				if(v == noteName) {
					tv = noteName;
					def = getString(R.string.defaultNoteName);
				}else{
					tv = noteBody;
					def = getString(R.string.defaultNoteContent);
				}
				
				str = tv.getText().toString();
				
				if(str.length() == 0) {
		    		tv.setText(def);
		    		tv.setTextColor(0xFF9f9f9f);
		    	}else if(str.equals(def) && hasFocus){
		    		tv.setText("");
		    		tv.setTextColor(0xFF000000);
		    	}
        	}
    
        }
        
        final DefaultString ds = new DefaultString();
        
        OnFocusChangeListener focusListener = new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				ds.evaluate(v, hasFocus);
			}
			
		};
		
		OnClickListener clickListener = new OnClickListener() {
			
		    @Override
		    public void onClick(View v) {
		    	noteBody.requestFocus();
		    	InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		    	imm.showSoftInput(noteBody, 0);
		    	
		    	Editable etext = noteBody.getText();
		    	int position = etext.length();
		    	Selection.setSelection(etext, position);
		    }
		    
        };
        
        noteName.setOnFocusChangeListener(focusListener);
        noteBody.setOnFocusChangeListener(focusListener);
        
        View v = findViewById(R.id.noteFrame);
        v.setOnClickListener(clickListener);
        
        noteName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			    	imm.hideSoftInputFromWindow(noteName.getWindowToken(), 0);
                    return true;
                }
				return false;
			}
        });
        
        noteBody.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			    	imm.hideSoftInputFromWindow(noteBody.getWindowToken(), 0);
                    return true;
                }
				return false;
			}
        });
        
    	super.onCreate(savedInstanceState);
		
		
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
    
        switch (item.getItemId()) {   
        case R.id.save:
        		save();
            return true;  
        case R.id.goBack:
        		close();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	close();
        	return false;
        }else return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void save() {
    	
    	final EditText noteName = (EditText) findViewById(R.id.noteName);
        final EditText noteBody = (EditText) findViewById(R.id.noteBody);
    	
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(noteName.getWindowToken(), 0);
        
        String name = noteName.getText().toString();
    	String content = noteBody.getText().toString();
    	Model.getInstance().getCurrentNote().stageChanges(getString(R.string.defaultNoteName), getString(R.string.defaultNoteContent), name, content);
    	Model.getInstance().getCurrentNote().postChanges();
    	
    	Model.getInstance().writeCurrentNote();
    	
        Say.now(this, getString(R.string.saved));
   
    }
    
    @Override
    protected void delete() {
        ConfirmDialog deleteDialog = Umlaut.getConfirmDialoge(NoteActivity.this, getString(R.string.confirmDelete) + " " + Model.getInstance().getCurrentNote().getName() + "?", 
	        	new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	dialog.dismiss();
	                	Model.getInstance().deleteCurrentNote();
	                	goToOverview();
	                }
	            },
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    dialog.dismiss();
	                }
	            }
	        );
	        
        deleteDialog.show();
    }
    
    @Override
    protected void close() {
    	
    	EditText noteName = (EditText) findViewById(R.id.noteName);
        EditText noteBody = (EditText) findViewById(R.id.noteBody);
    	
    	String name = noteName.getText().toString();
    	String content = noteBody.getText().toString();

    	Model.getInstance().getCurrentNote().stageChanges(getString(R.string.defaultNoteName), getString(R.string.defaultNoteContent), name, content);	
		if(Model.getInstance().getCurrentNote().hasChanges()) {
    
    		ConfirmDialog dialog = Umlaut.getConfirmDialoge(this, getString(R.string.confirmChanges), 
    	        	new DialogInterface.OnClickListener() {
    	                public void onClick(DialogInterface dialog, int which) {
    	                	dialog.dismiss();
    	                	Model.getInstance().getCurrentNote().postChanges();
    	                	Model.getInstance().writeCurrentNote();
     		        	    goToOverview();
    	                }
    	            },
    	            new DialogInterface.OnClickListener() {
    	                public void onClick(DialogInterface dialog, int which) {
    	                    dialog.dismiss();
    	                    goToOverview();
    	                }
    	            }
    	        );
    	   
           dialog.show();
    		
		}else{
			goToOverview();
		}    	
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
}
