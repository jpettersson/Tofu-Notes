package com.spacetofu.ui.picker;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class AbstractPicker<T> extends LinearLayout {

	public AbstractPicker(Context context) {
		super(context);
		listeners = new ArrayList<PickerListener>();
	}

	public AbstractPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		listeners = new ArrayList<PickerListener>();
	}

	private ArrayList<T> items;
	private int position = 0;
	private ArrayList<PickerListener> listeners;
	
	public void setItems(ArrayList<T> items) {
		this.items = items;
	}
	
	public ArrayList<T> getItems() {
		return this.items;
	}
	
	public T getSelectedItem() {
		return items.get(position);
	}
	
	public int getCurrentPosition() {
		return position;
	}
	
	public void selectItemAt(int index) {
		moveTo(index);
	}
	
	public void step(int step) {
		moveTo(position + step);
	}
	
	public void stepForward() {
		moveTo(position+1);
	}
	
	public void stepBackward() {
		moveTo(position-1);
	}

	protected void moveTo(int position) {
		//TODO: Check bounds and stuff
		Iterator<PickerListener> i;
		
		if(position >= items.size()) {
			this.position = 0;
			
			i = listeners.iterator();
			while(i.hasNext()) {
				PickerListener pl = i.next();
				pl.onPickerHitHigh(this);
			}
		}else if(position < 0) {
			this.position = items.size()-1;
			
			i = listeners.iterator();
			while(i.hasNext()) {
				PickerListener pl = i.next();
				pl.onPickerHitLow(this);
			}
		}else{
			this.position = position;
		}
			
		i = listeners.iterator();
		while(i.hasNext()) {
			PickerListener pl = i.next();
			pl.onPickerUpdated(this);
		}
		
		onPositionUpdated(); 
	}
	
	protected void onPositionUpdated() {
		
	}
	
	public void addPickerListener(PickerListener listener) {
		listeners.add(listener);
	}
	
	public void removePickerListener(PickerListener listener) {
		listeners.remove(listener);
	}
	
}
