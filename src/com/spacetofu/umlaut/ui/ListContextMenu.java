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



import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.spacetofu.umlaut.R;

public class ListContextMenu extends Dialog {

    public ListContextMenu(Context context, int theme) {
        super(context, theme);
    }

    public ListContextMenu(Context context) {
        super(context);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String title;
        private String shareButtonText;
        private String deleteButtonText;
        private View contentView;

        private DialogInterface.OnClickListener 
                        shareButtonClickListener,
                        deleteButtonClickListener;
		private int titleMaxLen = 17;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog title from resource
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
        	
        	if(title.length() > titleMaxLen  ) {
        		title = title.substring(0, titleMaxLen) + "...";
        	}
        	
            this.title = title;
            return this;
        }
        
        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * @param shareButtonText
         * @param listener
         * @return
         */
      
        public Builder setShareButton(String text, DialogInterface.OnClickListener listener) {
            this.shareButtonText = text;
            this.shareButtonClickListener = listener;
            return this;
        }

        public Builder setDeleteButton(String text, DialogInterface.OnClickListener listener) {
            this.deleteButtonText = text;
            this.deleteButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public ListContextMenu create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final ListContextMenu dialog = new ListContextMenu(context, R.style.ConfirmDialog);
            View layout = inflater.inflate(R.layout.listcontext, null);
            
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            
            View.OnClickListener closeListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			};
            
            layout.findViewById(R.id.dialogFrame).setOnClickListener(closeListener);
            layout.findViewById(R.id.btnClose).setOnClickListener(closeListener);
            
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            
                ((Button) layout.findViewById(R.id.shareButton))
                        .setText(shareButtonText);
                if (shareButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.shareButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    shareButtonClickListener.onClick(
                                                dialog, 
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            
            
                ((Button) layout.findViewById(R.id.deleteButton))
                        .setText(deleteButtonText);
                if (deleteButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.deleteButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                	deleteButtonClickListener.onClick(
                                                dialog, 
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            
            
            dialog.setContentView(layout);
            
            return dialog;
        }

    }

}