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

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonsware.android.listview.SectionedAdapter;
import com.spacetofu.bitmap.Effect;
import com.spacetofu.umlaut.model.Model;
import com.spacetofu.umlaut.model.NoteGroup;
import com.spacetofu.umlaut.model.note.ListNote;
import com.spacetofu.umlaut.model.note.Note;
import com.spacetofu.umlaut.model.note.TextNote;
import com.spacetofu.umlaut.ui.AlertDialog;
import com.spacetofu.umlaut.ui.ConfirmDialog;
import com.spacetofu.umlaut.ui.ListContextMenu;
import com.spacetofu.umlaut.ui.MultiChoiceDialog;
import com.spacetofu.umlaut.utils.Say;

public class OverviewActivity extends ListActivity {
    private static final String TAG = "Umlaut.OverviewActivity";
	/** Called when the activity is first created. */
	
	ArrayAdapter<String> itemStrings;
	NoteRowAdapter rowAdapter;
	private BaseAdapter listAdapter;
	
	public static final int NAME_LEN_LANDSCAPE = 30;
	public static final int NAME_LEN_PORTRAIT = 20;
	public static final int GROUPED_LIST_EXTRA_CHARS = 8;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {	
    	
    	setTheme(Umlaut.getCurrentTheme(this));
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        setContentView(R.layout.main);
    
        Model.getInstance().reset();
        
        registerForContextMenu(getListView());
        
        ImageButton btnNew = (ImageButton)findViewById(R.id.btnNew);
        btnNew.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	
		    	goToNewNote();
    			
		    }
		  });
        
        //This used to be the news button.
        //ImageButton btnTofu = (ImageButton)findViewById(R.id.btnTofu);
        
        ImageGetter imgGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
            		Resources res = getResources();
            	
            		Drawable drawable = res.getDrawable( R.drawable.default_new_small );
            		Effect.substituteBlack(drawable, Umlaut.getInteractiveColor(OverviewActivity.this), PorterDuff.Mode.LIGHTEN);
            		
            		// Important
            		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                  
                  return drawable;
            }
        };
        
        TextView instructions = (TextView)findViewById(R.id.instructions);
        instructions.setText(Html.fromHtml(getString(R.string.instructions), imgGetter, null));
        instructions.setVisibility(View.GONE);
        instructions.setOnClickListener(new OnClickListener() {
			
		    @Override
		    public void onClick(View v) {
		    	goToNewNote();
		    }
		    
        });
    }
    
    private void goToNewNote() {
    	
    	
    	MultiChoiceDialog.Builder builder = new MultiChoiceDialog.Builder(this);
    	builder.setTitle(getString(R.string.selectType));
    	ArrayList<String> list = new ArrayList<String>();
    	list.add(getString(R.string.textNote));
    	list.add(getString(R.string.listNote));
    	builder.setItems(list);
    	
    	ArrayList<Integer> icons = new ArrayList<Integer>();
    	icons.add(R.drawable.pen_ico_w);
    	icons.add(R.drawable.todo_ico_w);
    	builder.setIcons(icons);
    	
    	final MultiChoiceDialog dialog = builder.create();
    	
    	dialog.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> list, View view, int position, long i) {
				dialog.dismiss();
				
				switch(position) {
				case 0:
					
					if(Model.getInstance().selectNewNote(Model.TYPE_TEXT_NOTE)) {
			    		Intent intent = new Intent(OverviewActivity.this, NoteActivity.class);
						intent.addFlags(65536); //FLAG_ACTIVITY_NO_ANIMATION (api 5)
						startActivity(intent);
			    	}else{
			    		AlertDialog dialog = Umlaut.getAlertDialoge(OverviewActivity.this, getString(R.string.noteLimitReached), 
			    	        	new DialogInterface.OnClickListener() {
			    	                public void onClick(DialogInterface dialog, int which) {
			    	                	dialog.dismiss();
			    	                }
			    	            }
			    	        );

			           dialog.show();
			    	}
					
					break;
				case 1:
					
					if(Model.getInstance().selectNewNote(Model.TYPE_LIST_NOTE)) {
			    		Intent intent = new Intent(OverviewActivity.this, ListNoteActivity.class);
						intent.addFlags(65536); //FLAG_ACTIVITY_NO_ANIMATION (api 5)
						startActivity(intent);
			    	}else{
			    		AlertDialog dialog = Umlaut.getAlertDialoge(OverviewActivity.this, getString(R.string.noteLimitReached), 
			    	        	new DialogInterface.OnClickListener() {
			    	                public void onClick(DialogInterface dialog, int which) {
			    	                	dialog.dismiss();
			    	                }
			    	            }
			    	        );
			    	        
			           dialog.show();
			    	}
					
					break;
				}
			}
    		
    	});
    	
    	dialog.show();
    
    }
    
   
    @Override
    public void onStart() {
    	super.onStart();
    }
    
    @Override
    public void onResume() { 
    	
    	Umlaut.readSettings(this);
    	
    	Model.getInstance().setContext(this);
    	
    	if(build()) {
	    	
	    	final SharedPreferences preferences = getSharedPreferences(Umlaut.PREFERENCES_UMLAUT, Activity.MODE_PRIVATE);
	    	int numStart = preferences.getInt(Umlaut.PREFERENCES_NUM_START, 0);
	    	if(numStart < Umlaut.NUM_START_LIMIT) {
	    		preferences.edit().putInt(Umlaut.PREFERENCES_NUM_START, (numStart + 1)).commit();
	    	        Say.now(this, getString(R.string.contextInstruction));
	    	}
    	}
    	
    	super.onResume();
    }
    
    @Override
    public void onPause() {
    	Say.silence();
    	super.onPause();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    	Intent intent;
    	
        switch (item.getItemId()) {
      
        case R.id.settings:

        	intent = new Intent(OverviewActivity.this, MainPreferenceActivity.class);
        	startActivity(intent);	
        	
            return true;  
        case R.id.deleteAll:
        	
	    		ConfirmDialog dialog = Umlaut.getConfirmDialoge(this, getString(R.string.confirmDeleteAll), 
	    	        	new DialogInterface.OnClickListener() {
	    	                public void onClick(DialogInterface dialog, int which) {
	    	                	dialog.dismiss();
	    	                    Model.getInstance().deleteAllNotes();
		    		        	build();
	    	                }
	    	            },
	    	            new DialogInterface.OnClickListener() {
	    	                public void onClick(DialogInterface dialog, int which) {
	    	                    dialog.dismiss();
	    	                }
	    	            }
	    	        );
	    	        
	           dialog.show();
	        	
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean build() {
    	
    	int maxLen;
    	Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();  
		if(display.getOrientation() == Surface.ROTATION_0) {
			maxLen = NAME_LEN_PORTRAIT;
		}else{
			maxLen = NAME_LEN_LANDSCAPE;
		}
    	
		int numNotes = 0;
		
		listAdapter = new ListNoteAdapter(this);
		
		ArrayList<NoteGroup> groups;
		
		if(Umlaut.listStyle == Umlaut.LIST_STYLE_GROUP_BY_DATE) {
			maxLen += GROUPED_LIST_EXTRA_CHARS;
			groups = Model.getInstance().getNotesGrouped(this, true);
		} else
			groups = Model.getInstance().getNotesGrouped(this, false);
		
    	Iterator<NoteGroup> i = groups.iterator();
    	while(i.hasNext()) {
    		NoteGroup group = i.next();
    		NoteRowAdapter noteRowAdapter = new NoteRowAdapter(this, R.layout.note_row, group.getNotes());
    		numNotes += group.getNotes().size();
    		noteRowAdapter.setNameMaxCharacters(maxLen);
    		((SectionedAdapter) listAdapter).addSection(group.getTitle(), group.getTextColor(), group.getIconResourceId(), noteRowAdapter);
    	}
    	
		setListAdapter(listAdapter);

	    TextView instructions = (TextView)findViewById(R.id.instructions);
    	if(numNotes == 0) {
    		instructions.setVisibility(View.VISIBLE);
    	}else{
    		instructions.setVisibility(View.GONE);
    	}
		 
		return numNotes > 1;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        Object obj = listAdapter.getItem(position);
        
        if(Model.getInstance().selectNoteByReference(obj)) {
        	
        	if(Model.getInstance().getCurrentNote() instanceof TextNote) {
        		Intent intent = new Intent(OverviewActivity.this, NoteActivity.class);
            	intent.addFlags(65536); //FLAG_ACTIVITY_NO_ANIMATION (api 5)
            	startActivity(intent);
        	}else if(Model.getInstance().getCurrentNote() instanceof ListNote) {
        		Intent intent = new Intent(OverviewActivity.this, ListNoteActivity.class);
            	intent.addFlags(65536); //FLAG_ACTIVITY_NO_ANIMATION (api 5)
            	startActivity(intent);
        	}
        	
        }
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    	Object obj = listAdapter.getItem(info.position);
    	
    	if(obj != null) {
    	
	    	Model.getInstance().selectNoteByReference(obj);
	    	
	    	final Activity activity = this;
	    	
	    	ListContextMenu dialog = Umlaut.getListContextMenu(this, Model.getInstance().getCurrentNote().getName(),
	    			new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			            	dialog.dismiss();
			            	
			            	Intent it = new Intent(Intent.ACTION_SEND);    
			    			it.putExtra(Intent.EXTRA_SUBJECT, Model.getInstance().getCurrentNote().getName());
			    			it.putExtra(Intent.EXTRA_TEXT, Model.getInstance().getCurrentNote().toEmail(OverviewActivity.this));   
			    			it.setType("text/plain");   
			    			startActivity(Intent.createChooser(it, getString(R.string.chooseApplication)));  
			    			
			            }
			        },
			        new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			                dialog.dismiss();
			                
			                ConfirmDialog deleteDialog = Umlaut.getConfirmDialoge(activity, getString(R.string.confirmDelete) + " " + Model.getInstance().getCurrentNote().getName() + "?", 
			        	        	new DialogInterface.OnClickListener() {
			        	                public void onClick(DialogInterface dialog, int which) {
			        	                	dialog.dismiss();
			        	                	Model.getInstance().deleteCurrentNote();
			         		   			    build();
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
			        }
		    			
	    	);
	    	
	    	dialog.show();
	    	
    	}
    	
    	return;
    	
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }
      
    private class ListNoteAdapter extends SectionedAdapter {
    	
    	private Context context;
    	
    	public ListNoteAdapter(Context ctx) {
    		super();	
    		this.context = ctx;
        }
    	
		@Override
		protected View getHeaderView(Section section, int index, View convertView, ViewGroup parent) {
			
			LinearLayout result=(LinearLayout)convertView;
			
			if (convertView==null) {
				result = (LinearLayout)getLayoutInflater().inflate(index == 0 ? R.layout.null_list_header : R.layout.overview_list_header, null);
			}else{
				if(index == 0 && convertView.getId() == R.id.listHeader || index != 0 && convertView.getId() == R.id.nullListHeader)
					result = (LinearLayout)getLayoutInflater().inflate(index == 0 ? R.layout.null_list_header : R.layout.overview_list_header, null);
			}
			
			TextView label = (TextView) result.findViewById(R.id.label);
			label.setText(section.getCaption());
			
			ImageView iv = (ImageView) result.findViewById(R.id.icon);
			if(iv != null) {
				if(section.hasIcon()) {
					iv.setImageResource(section.getIconResourceId());
					iv.setVisibility(View.VISIBLE);
				}else{
					iv.setVisibility(View.GONE);
				}
        	}
			
			return result;
		}
			
    }
    
    private class NoteRowAdapter extends ArrayAdapter<Note> {

        private ArrayList<Note> items;
		private int nameMaxLen = 15;

        public NoteRowAdapter(Context context, int textViewResourceId, ArrayList<Note> items) {
                super(context, textViewResourceId, items);
                this.items = items;
                //items.add(0, null);
        }

        public void setNameMaxCharacters(int nameMaxLen) {
			this.nameMaxLen  = nameMaxLen;
		}

		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
				
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    
                    v = vi.inflate(R.layout.date_note_row, null);
                }
                
                Note o = items.get(position);
           
                TextView date = (TextView) v.findViewById(R.id.noteRowDate);
                TextView name = (TextView) v.findViewById(R.id.noteRowName);
                
                if(date != null) {
                	if(Umlaut.listStyle == Umlaut.LIST_STYLE_GROUP_BY_DATE)
                		date.setVisibility(View.GONE);
                	else
                		date.setVisibility(View.VISIBLE);
                }
                
                LinearLayout layout = (LinearLayout) v.findViewById(R.id.listRowIconHolder);
                
                int numIcons = 0;
                
                if(o != null) {
	                if(o.hasReminder()) {
	                	addIcon(layout, R.id.reminderIcon, R.drawable.icon_reminder_dark); //reminder_ico
	                	numIcons++;
	                }else{
	                	removeIcon(layout, R.id.reminderIcon);
	                }
	                
	                if(o instanceof ListNote) {
	                	addIcon(layout, R.id.todoIcon, R.drawable.todo_ico);
	                	numIcons++;
	                }else{
	                	removeIcon(layout, R.id.todoIcon);
	                }

                }else{
                	removeIcon(layout, R.id.reminderIcon);
                	removeIcon(layout, R.id.todoIcon);
                }
                
                if(o == null) {
                	date.setText(" ");
                	name.setVisibility(View.GONE);
                }else{
                	name.setVisibility(View.VISIBLE);
                	
                    if (date != null) {
                        date.setText(Umlaut.listStyle == Umlaut.LIST_STYLE_GROUP_BY_DATE ? Umlaut.formatDate(OverviewActivity.this, o.getCreatedAt()) : Umlaut.formatDateShort(OverviewActivity.this, o.getCreatedAt()));                            
                    }
                    
                    if(name != null){
                    	  if(o.getName().length() > 0)
                    		  name.setText(o.getName());
                    		  //name.setText(getTruncatedString(o.getName(), (int)Math.round(numIcons * 3)));
                    	  else
                    		  name.setText(getString(R.string.untitled));
                    }
                }
                                
                
                return v;
        }
        
        private void addIcon(ViewGroup vg, int id, int iconId) {
        	ImageView iv = (ImageView) vg.findViewById(id);
        	if(iv == null) {
        		iv = new ImageView(getContext());
	        	iv.setId(id);
	        	iv.setImageResource(iconId);
	        	iv.setScaleType(ImageView.ScaleType.FIT_XY);
	        	
	        	Effect.substituteBlack(iv.getDrawable(), Umlaut.getInteractiveColor(OverviewActivity.this));
	        	
	        	float scale = getContext().getResources().getDisplayMetrics().density; 
	        	float padding = 10 * scale; 
	        	
	        	RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        	
	        	iv.setPadding(0, 0, (int)padding, 0);
	        	vg.addView(iv, relativeParams);
        	}
        }
        
        private void removeIcon(ViewGroup vg, int id) {
        	ImageView iv = (ImageView) vg.findViewById(id);
        	if(iv != null) { 
        		vg.removeView(iv);
        	}
        }
      
    }
    
}