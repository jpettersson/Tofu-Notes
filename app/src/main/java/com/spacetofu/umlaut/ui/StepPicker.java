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
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.spacetofu.ui.picker.AbstractPicker;
import com.spacetofu.umlaut.R;

public class StepPicker<T> extends AbstractPicker<T> {

	private View posBtn;
	private View negBtn;
	private TextView display;
	
	private Handler animationHandler = new Handler();
	
	private static int DIRECTION_FORWARD = 1;
	private static int DIRECTION_BACKWARD = -1;
	private static int INITIAL_TIMEOUT = 500;
	private static int UPDATE_FREQ = 100;
	private static int STEP_SIZE = 1;
	
	private Runnable animationRunnable = new Runnable(){

		@Override
		public void run() {
			
			step(scrollDirection * STEP_SIZE);
			animationHandler.postDelayed(this, UPDATE_FREQ);
		}
		
	};
	
	private int scrollDirection;
	
	public StepPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StepPicker(Context context) {
		super(context);
	}
	
	public void prepare() {
		
		posBtn = findViewById(R.id.posBtn);
		negBtn = findViewById(R.id.negBtn);
		display = (TextView) findViewById(R.id.valueText);
		
		final View.OnClickListener clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		};
		
		if(posBtn != null) {
			
			posBtn.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							stepForward();
							scroll(DIRECTION_FORWARD);
							posBtn.setSelected(true);
						break;					
						case MotionEvent.ACTION_UP:
							cancelScroll();
							posBtn.setSelected(false);
						break;
					}
					
					return false;
				}
			});
			
			posBtn.setOnClickListener(clickListener);
			
		}
		
		if(negBtn != null) {
			negBtn.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							stepBackward();
							scroll(DIRECTION_BACKWARD);
							negBtn.setSelected(true);
						break;					
						case MotionEvent.ACTION_UP:
							cancelScroll();
							negBtn.setSelected(false);
						break;
					}
					
					return false;
				}
			});
			
			negBtn.setOnClickListener(clickListener);
		}
	}
	
	private void scroll(int direction) {
		scrollDirection = direction;
		animationHandler.postDelayed(animationRunnable, INITIAL_TIMEOUT);
	}
	
	private void cancelScroll() {
		if(animationHandler != null) {
			animationHandler.removeCallbacks(animationRunnable);
		}
	}

	@Override
	protected void onPositionUpdated() {
		if(display!=null)
			display.setText((String)getSelectedItem());
	}
	
	@Override
	public void setItems(ArrayList<T> items) {
		super.setItems(items);
		
		moveTo(0);
		
	}

}