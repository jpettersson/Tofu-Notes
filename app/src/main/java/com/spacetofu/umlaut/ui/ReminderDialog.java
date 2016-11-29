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
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spacetofu.ui.picker.AbstractPicker;
import com.spacetofu.ui.picker.PickerListener;
import com.spacetofu.umlaut.R;
import com.spacetofu.umlaut.Umlaut;
import com.spacetofu.util.DateTools;

public class ReminderDialog extends Dialog {
	
	private Calendar calendar;
	
    public ReminderDialog(Context context, int theme) {
        super(context, theme);
    }

    public ReminderDialog(Context context) {
        super(context);
    }

	public void prepare() {
		if(calendar == null) {
			calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, 5);
		}
       
        final StepPicker<String> yearPicker = (StepPicker<String>) findViewById(R.id.yearPicker);
        final StepPicker<String> monthPicker = (StepPicker<String>) findViewById(R.id.monthPicker);
        final StepPicker<String> datePicker = (StepPicker<String>) findViewById(R.id.datePicker);
        final StepPicker<String> hourPicker = (StepPicker<String>) findViewById(R.id.hourPicker);
        final StepPicker<String> minutePicker = (StepPicker<String>) findViewById(R.id.minutePicker);
        StepPicker<String> halfPicker = (StepPicker<String>) findViewById(R.id.halfPicker);
        
        
        if(Umlaut.units == Umlaut.UNITS_IMPERIAL) {
        	LinearLayout ll = (LinearLayout) yearPicker.getParent();
        	ll.removeView(yearPicker);
        	ll.removeView(monthPicker);
        	ll.removeView(datePicker);
        	
        	ll.addView(monthPicker);
        	ll.addView(datePicker);
        	ll.addView(yearPicker);
        }
        
        
        if(Umlaut.units == Umlaut.UNITS_SI) {
        	halfPicker.setVisibility(View.GONE);
        }else{
        	halfPicker.prepare();
        	ArrayList<String> half = new ArrayList<String>();
            half.add("AM");
            half.add("PM");
            halfPicker.setItems(half);
            halfPicker.selectItemAt(calendar.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1);
        }
        
        yearPicker.prepare();
        monthPicker.prepare();
        datePicker.prepare();
        hourPicker.prepare();
        minutePicker.prepare();
        
        yearPicker.setItems(DateTools.getYearList(calendar, 3));
        
        monthPicker.setItems(DateTools.getMonthList());
        monthPicker.selectItemAt(calendar.get(Calendar.MONTH));
        
        datePicker.setItems(DateTools.getDateListFor(calendar));
        datePicker.selectItemAt(calendar.get(Calendar.DATE)-1);
        
        PickerListener onAnyChangeListener = new PickerListener() {

			@Override
			public void onPickerHitHigh(AbstractPicker picker) {}

			@Override
			public void onPickerHitLow(AbstractPicker picker) {}

			@Override
			public void onPickerUpdated(AbstractPicker picker) {
				changePositiveButtonText("Set");
			}
        	
        };
        
        
        PickerListener dateResetListener = new PickerListener() {

			@Override
			public void onPickerUpdated(AbstractPicker abstractPicker) {
				if(abstractPicker == yearPicker) {
					calendar.set(Calendar.YEAR, Integer.parseInt(yearPicker.getSelectedItem()));
				}else if(abstractPicker == monthPicker) {
					calendar.set(Calendar.MONTH, (monthPicker.getCurrentPosition() + 1));
				}
				calendar.set(Calendar.DATE, 1);
				datePicker.setItems(DateTools.getDateListFor(calendar));
		        datePicker.selectItemAt(calendar.get(Calendar.DATE)-1);
			}

			@Override
			public void onPickerHitHigh(AbstractPicker picker) {}

			@Override
			public void onPickerHitLow(AbstractPicker picker) {}
        	
        };
        
        yearPicker.addPickerListener(dateResetListener);
        yearPicker.addPickerListener(onAnyChangeListener);
        monthPicker.addPickerListener(dateResetListener);
        monthPicker.addPickerListener(onAnyChangeListener);
        datePicker.addPickerListener(onAnyChangeListener);
        
        PickerListener minuteListener = new PickerListener() {

			@Override
			public void onPickerHitHigh(AbstractPicker picker) {
				hourPicker.stepForward();
			}

			@Override
			public void onPickerHitLow(AbstractPicker picker) {
				hourPicker.stepBackward();
			}

			@Override
			public void onPickerUpdated(AbstractPicker picker) {}
        	
        };
        
        if(Umlaut.units == Umlaut.UNITS_SI) {
        	hourPicker.setItems(DateTools.getHoursOfDayList());
        	hourPicker.selectItemAt(calendar.get(Calendar.HOUR_OF_DAY));
        }else{
        	hourPicker.setItems(DateTools.getHoursList());
        	hourPicker.selectItemAt(calendar.get(Calendar.HOUR)-1);
        }
        
        hourPicker.addPickerListener(onAnyChangeListener);
        minutePicker.setItems(DateTools.getMinutesList());
        minutePicker.selectItemAt(calendar.get(Calendar.MINUTE));
        minutePicker.addPickerListener(minuteListener);
        minutePicker.addPickerListener(onAnyChangeListener);
        
	}   
	
	public void changePositiveButtonText(String text) {
		//replace the button
		Button button = (Button) findViewById(R.id.positiveButton);
		button.setText(text);
		
	}

	private void updateCalendar() {
		StepPicker<String> yearPicker = (StepPicker<String>) findViewById(R.id.yearPicker);
        StepPicker<String> monthPicker = (StepPicker<String>) findViewById(R.id.monthPicker);
        StepPicker<String> datePicker = (StepPicker<String>) findViewById(R.id.datePicker);
        StepPicker<String> hourPicker = (StepPicker<String>) findViewById(R.id.hourPicker);
        StepPicker<String> minutePicker = (StepPicker<String>) findViewById(R.id.minutePicker);
        StepPicker<String> halfPicker = (StepPicker<String>) findViewById(R.id.halfPicker);
        
        calendar.set(Calendar.YEAR, Integer.parseInt(yearPicker.getSelectedItem()));
        calendar.set(Calendar.MONTH, monthPicker.getCurrentPosition());
        calendar.set(Calendar.DATE, Integer.parseInt(datePicker.getSelectedItem()));
        
        if(Umlaut.units == Umlaut.UNITS_SI)
        	calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourPicker.getSelectedItem()));
        else{
        	calendar.set(Calendar.HOUR, Integer.parseInt(hourPicker.getSelectedItem()));
            calendar.set(Calendar.AM_PM, halfPicker.getCurrentPosition() == 0 ? Calendar.AM : Calendar.PM);        
        }
        
        calendar.set(Calendar.MINUTE, Integer.parseInt(minutePicker.getSelectedItem()));
        calendar.set(Calendar.SECOND, 0);
	}
	
	public Calendar getCalendar() {
		
		updateCalendar();
		
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
    
    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String title;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;

        private DialogInterface.OnClickListener positiveButtonClickListener, negativeButtonClickListener;
        
		private Calendar calendar;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Create the custom dialog
         */
        public ReminderDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final ReminderDialog dialog = new ReminderDialog(context, R.style.ConfirmDialog);
            View layout = inflater.inflate(R.layout.reminderdialog, null);
            
            dialog.setCalendar(calendar);
            
            View.OnClickListener closeListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			};
            
            //layout.findViewById(R.id.dialogFrame).setOnClickListener(closeListener);
            layout.findViewById(R.id.btnClose).setOnClickListener(closeListener);
            
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(
                                                dialog, 
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                	negativeButtonClickListener.onClick(
                                                dialog, 
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            
            dialog.setContentView(layout);
            dialog.prepare();
            
            return dialog;
        }

		public void setCalendar(Calendar calendar) {
			this.calendar = calendar;
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
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }
		

    }


}
