package com.spacetofu.ui.picker;

public interface PickerListener {
	
	public void onPickerUpdated(AbstractPicker picker);
	public void onPickerHitHigh(AbstractPicker picker);
	public void onPickerHitLow(AbstractPicker picker);
	
}
