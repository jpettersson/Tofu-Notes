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

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.android.listview.SectionedAdapter;
import com.spacetofu.bitmap.Effect;
import com.spacetofu.umlaut.model.Model;
import com.spacetofu.umlaut.model.note.ListNote;
import com.spacetofu.umlaut.model.note.Note;
import com.spacetofu.umlaut.model.note.NoteRow;
import com.spacetofu.umlaut.ui.ConfirmDialog;
import com.spacetofu.umlaut.ui.EditDialog;
import com.spacetofu.umlaut.utils.Say;

public class ListNoteActivity extends BaseNoteActivity {
    protected static final String TAG = "Umlaut.ListNoteActivity";
	
	ListNoteAdapter listAdapter;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Model.getInstance().setContext(this);
    	processIntent();
    	
    	setTheme(Umlaut.getCurrentTheme(this));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.listnote);
        
        if(!Model.getInstance().hasCurrentNote()) {
        	goToOverview();
        	return;
        }
        
        final EditText noteName = (EditText) findViewById(R.id.noteName);
        final EditText newRow = (EditText) findViewById(R.id.newRowField);
        

        String name = Model.getInstance().getCurrentNote().getName();
        if(name.length() > 0) {
        	noteName.setText(name);
        	noteName.setTextColor(0xFF000000);
        } else
        	noteName.setText(getString(R.string.defaultNoteName));
        
        newRow.setText(getString(R.string.defaultRowName));
        
        class DefaultString {
        	
        	public void evaluate(View v, boolean hasFocus) {
        		TextView tv;
				String str;
				String def;
				
				if(v == noteName) {
					tv = noteName;
					def = getString(R.string.defaultNoteName);
				}else{
					tv = newRow;
					def = getString(R.string.defaultRowName);
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
		
        noteName.setOnFocusChangeListener(focusListener);
        newRow.setOnFocusChangeListener(focusListener);
        
		ImageButton btnNewRow = (ImageButton)findViewById(R.id.btnSaveRow);
		btnNewRow.setOnClickListener(new OnClickListener() {
			
		    @Override
		    public void onClick(View v) {
		    	
		    	EditText et = (EditText) findViewById(R.id.newRowField);
		    	String text = et.getText().toString();
		    	if(text.equals(getString(R.string.defaultRowName))) {
		    		text = "";
		    	}
		    	
		    	et.setText("");
		    	et.clearFocus();
		    	ds.evaluate(v, false);
		    	
		    	//Make sure we save the ListNote before adding children to it!
		    	if(!Model.getInstance().getCurrentNote().isSaved()) {
		    		
		    		final EditText noteName = (EditText) findViewById(R.id.noteName);
		        	String name = noteName.getText().toString();

		        	Model.getInstance().getCurrentNote().stageChanges(getString(R.string.defaultNoteName), getString(R.string.defaultNoteContent), name, "");
		        	Model.getInstance().getCurrentNote().postChanges();
		    		Model.getInstance().writeCurrentNote();
		    	}
		    	
				((ListNote) Model.getInstance().getCurrentNote()).createNewRow(text);
				
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		    	imm.hideSoftInputFromWindow(noteName.getWindowToken(), 0);
				
		    	
		    	ListView list = (ListView) findViewById(R.id.todoList);
		    	
		    	int idx = list.getFirstVisiblePosition();
		    	View vfirst = list.getChildAt(0);
		    	int pos = 0;
		    	if (vfirst != null) pos = vfirst.getTop();

		    	build();
		    	
		    	list.setSelectionFromTop(idx, pos);
		    }
		    
		});
		
		super.onCreate(savedInstanceState);
		
		build();
		
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    
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
    
    
    private void build() {
 
    	listAdapter = new ListNoteAdapter();
    	
    	ArrayList<Note> pendingNotes = ((ListNote) Model.getInstance().getCurrentNote()).getPendingRows();
    	ArrayList<Note> completedNotes = ((ListNote) Model.getInstance().getCurrentNote()).getCompletedRows();
    	
    	if(pendingNotes.size() > 0) listAdapter.addSection(getString(R.string.pendingItems), new ListNoteRowAdapter(this, R.layout.list_note_row_pending, pendingNotes));
    	if(completedNotes.size() > 0) listAdapter.addSection(getString(R.string.completedItems), new ListNoteRowAdapter(this, R.layout.list_note_row_completed, completedNotes));
    	
		ListView listView = (ListView) findViewById(R.id.todoList);
		listView.setAdapter(listAdapter);
		
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
    	
    	//save
    	final EditText noteName = (EditText) findViewById(R.id.noteName);
    	String name = noteName.getText().toString();
    	
    	InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(noteName.getWindowToken(), 0);
    	
    	Model.getInstance().getCurrentNote().stageChanges(getString(R.string.defaultNoteName), getString(R.string.defaultNoteContent), name, "");
    	Model.getInstance().getCurrentNote().postChanges();
    	
    	Model.getInstance().writeCurrentNote();
    	
    	Say.now(ListNoteActivity.this, getString(R.string.saved));
    	
    }
    
    @Override
    protected void delete() {
    	
        ConfirmDialog deleteDialog = Umlaut.getConfirmDialoge(ListNoteActivity.this, getString(R.string.confirmDelete) + " " + Model.getInstance().getCurrentNote().getName() + "?", 
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
        
    	String name = noteName.getText().toString();
    	
    	Model.getInstance().getCurrentNote().stageChanges(getString(R.string.defaultNoteName), getString(R.string.defaultNoteContent), name, "");	
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
  
    public void editListItem(int position, View view, final Note object) {
   	
    	final ListView list = (ListView) findViewById(R.id.todoList);
    	
    	EditDialog editDialog = Umlaut.getEditDialog(ListNoteActivity.this, getString(R.string.edit_item), 
	        	new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	dialog.dismiss();
	                	
	                	String str = ((EditDialog) dialog).getEditFieldText();
	                	object.stageChanges(getString(R.string.defaultNoteName), getString(R.string.defaultNoteContent), str, object.getContent());
	                	object.postChanges();
	                	Model.getInstance().write(object);
	                	
	                	int idx = list.getFirstVisiblePosition();
	                	View vfirst = list.getChildAt(0);
	                	int pos = 0;
	                	if (vfirst != null) pos = vfirst.getTop();

	                	build();
	                	
	                	
	                	list.setSelectionFromTop(idx, pos);
	                }
	            },
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    dialog.dismiss();
	                    ((ListNote) Model.getInstance().getCurrentNote()).deleteRow((NoteRow) object);
	                    
	                    int idx = list.getFirstVisiblePosition();
	                	View vfirst = list.getChildAt(0);
	                	int pos = 0;
	                	if (vfirst != null) pos = vfirst.getTop();

	                	build();
	                	
	                	list.setSelectionFromTop(idx, pos);
	                }
	            }
	        );
	        
    	editDialog.setEditFieldText(object.getName());
    	editDialog.show();
    	
    }
    
    public void toggleListItem(int position, View view, Note object) {
    	TextView name = (TextView) view.findViewById(R.id.listNoteRowName);
    	
    	NoteRow row = (NoteRow) object;
    	row.toggleCompleted();
    	
    	ListView list = (ListView) findViewById(R.id.todoList);
    	
    	int idx = list.getFirstVisiblePosition();
    	View vfirst = list.getChildAt(0);
    	int pos = 0;
    	if (vfirst != null) pos = vfirst.getTop();

    	build();
    	
    	list.setSelectionFromTop(idx, pos);
    }
    
    
    private class ListNoteAdapter extends SectionedAdapter {

		@Override
		protected View getHeaderView(Section section, int index, View convertView, ViewGroup parent) {
			
			LinearLayout result=(LinearLayout)convertView;
			
			if (convertView==null) {
				result = (LinearLayout)getLayoutInflater().inflate(R.layout.list_header, null);
			}
			
			TextView label = (TextView) result.findViewById(R.id.label);
			
			label.setText(section.getCaption());
			
			return result;
		}
    	
    }
    
    private class ListNoteRowAdapter extends ArrayAdapter<Note> {

        private ArrayList<Note> items;

        public ListNoteRowAdapter(Context context, int textViewResourceId, ArrayList<Note> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }

		@Override
        public View getView(final int position, View convertView, ViewGroup parent) {
                View view = convertView;
                final Note o = items.get(position);
                
                if (view == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    if(((NoteRow) o).isCompleted()) {
                    	view = vi.inflate(R.layout.list_note_row_completed, null);
                    	
                    	ImageButton icon = (ImageButton) view.findViewById(R.id.btnToggleListRow);
                    	Effect.substituteBlack(icon.getDrawable(), Umlaut.getInteractiveColor(ListNoteActivity.this));
                    	
                    } else
                    	view = vi.inflate(R.layout.list_note_row_pending, null);
                
                }
                
                final View fView = view;
                
                TextView name = (TextView) view.findViewById(R.id.listNoteRowName);
                
                String text = o.getName();
                text = text.length() > 0 ? text : getString(R.string.untitled);;
                
                name.setText(text);
          
                name.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						editListItem(position, fView, o);
					}
                });
                
                ImageButton editBtn = (ImageButton) view.findViewById(R.id.btnToggleListRow);
                editBtn.setOnClickListener(new OnClickListener() {
        		    @Override
        		    public void onClick(View v) {
        		    	toggleListItem(position, fView, o);
        		    }
        		  });
                
                return view;
        }
		
		
    }
}