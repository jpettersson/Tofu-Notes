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

package com.spacetofu.umlaut.ui;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spacetofu.umlaut.R;

public class MultiChoiceDialog extends AlertDialog {

	public MultiChoiceDialog(Context context, int theme) {
        super(context, theme);
    }

    public MultiChoiceDialog(Context context) {
        super(context);
    }
    
    public void setOnItemClickListener(ListView.OnItemClickListener listener) {
    	ListView list = (ListView) findViewById(R.id.m_choice_list);
        list.setOnItemClickListener(listener);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String title;
        private ArrayList<String> items;
        private View contentView;
        private ListView.OnItemClickListener listener;
		private ArrayList<Integer> icons = null;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }
        
        public Builder setItems(ArrayList<String> items) {
        	this.items = items;
        	return this;
        }
        
        public Builder setIcons(ArrayList<Integer> icons) {
        	this.icons = icons;
        	return this;
        }
        
        public MultiChoiceDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final MultiChoiceDialog dialog = new MultiChoiceDialog(context, R.style.ConfirmDialog);
            View layout = inflater.inflate(R.layout.m_choice_dialog, null);
            
            View.OnClickListener closeListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			};
            
            layout.findViewById(R.id.dialogFrame).setOnClickListener(closeListener);
            layout.findViewById(R.id.btnClose).setOnClickListener(closeListener);
     
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            ListView list = (ListView) layout.findViewById(R.id.m_choice_list);
            
            ListAdapter adapter = new ListAdapter(context, R.layout.m_choice_row, items);
            if(icons != null) adapter.setIcons(icons);
            
            list.setAdapter(adapter);
           
            
            dialog.setContentView(layout);
            return dialog;
        }
        
        private class ListAdapter extends ArrayAdapter<String> {

        	ArrayList<Integer> icons = null;
        	ArrayList<String> items;
        	
			public ListAdapter(Context context, int textViewResourceId, ArrayList<String> items) {
				super(context, textViewResourceId, items);
				this.items = items;
			}

			public void setIcons(ArrayList<Integer> icons) {
				this.icons = icons;
			}

			@Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	                View v = convertView;
	                if (v == null) {
	                    LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                    v = vi.inflate(R.layout.m_choice_row, null);
	                }
	                
	                String str = items.get(position);
	           
	                TextView label = (TextView) v.findViewById(R.id.mChoiceLabel);
	                
	                if(str == null) {
	                	label.setText("undefined");
	                }else{
	                    if(label != null){
	                    	  label.setText(str);
	                    }
	                }
					
	                if(icons != null) { 
	                	
	                	RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.listRowIconHolder);
	                	if(layout != null) {
	                		((ViewGroup) v).removeView(layout);
	                	}
	                	
	                	ImageView iv = new ImageView(getContext());
	                	iv.setImageResource(icons.get(position));
	                	iv.setScaleType(ImageView.ScaleType.FIT_XY);
	                	
	                	layout = new RelativeLayout(getContext());
	                	layout.setId(R.id.listRowIconHolder);

	                	RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	                	relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	                	
	                	float scale = getContext().getResources().getDisplayMetrics().density; 
	                	float padding = 19 * scale; 
	                	
	                	iv.setPadding(0, 0, Math.round(padding), 0);
	                	layout.addView(iv, relativeParams);
	                	
	                	RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	                	layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
	                	
	                	((ViewGroup) v).addView(layout, layoutParams);
	                }
	                
	                return v;
	        }
        	
        }

    }

}