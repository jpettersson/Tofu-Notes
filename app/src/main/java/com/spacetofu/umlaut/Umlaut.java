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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spacetofu.bitmap.Effect;
import com.spacetofu.umlaut.model.Model;
import com.spacetofu.umlaut.ui.AlertDialog;
import com.spacetofu.umlaut.ui.ConfirmDialog;
import com.spacetofu.umlaut.ui.EditDialog;
import com.spacetofu.umlaut.ui.ListContextMenu;

public class Umlaut extends Application {
	
	public static final int NUM_NOTES_MAX = 500; //Increased this from 150.
	
	public static final String PREFERENCES_UMLAUT = "umlaut";
	public static final String PREFERENCES_NUM_START = "numStart";
	public static final String PREFERENCES_LIST_STYLE = "listStyle";
	public static final String PREFERENCES_UNITS = "dateFormat";

	public static final int NUM_START_LIMIT = 1;

	private static final long DAY = 1000 * 60 * 60 * 24;
	
	public static final long STATUS_TIME_VALID_MILLIS = 60 * 1000;  
	
	private static final String TAG = "Umlaut";

	public static final int UML_NOTIFICATION_ID = 9876467;

	public static final int LIST_STYLE_GROUP_BY_DATE = 1;
	public static final int LIST_STYLE_CONTINUOUS = 2;
	
	public static int UNITS_SI = 1;
	public static int UNITS_IMPERIAL = 2;
	
	public static String DATE_FORMAT_US = "E MM/dd/yy";
	public static String DATE_FORMAT_EU = "E yy/MM/dd";
	public static String DATE_FORMAT_US_SHORT = "MM/dd/yy";
	public static String DATE_FORMAT_EU_SHORT = "yy/MM/dd";
	
	private static int titleMaxLen = 17;
	public static int listStyle = LIST_STYLE_GROUP_BY_DATE;
	public static int units = UNITS_SI;

	private static int versionCode = 0;
	
	public void onCreate() {
		
		
		super.onCreate();
	}
	
	public void onLowMemory() {
		
	}
	
	public static int getCurrentTheme(Context ctx) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		String themeId = sp.getString("theme", "1");
		
		if(themeId.equals("1")) {
			return R.style.UmlautDefault;
		}else if(themeId.equals("2")) {
			return R.style.UmlautCinema;
		}else if(themeId.equals("3")) {
			return R.style.UmlautWorld;
		}else if(themeId.equals("4")) {
			return R.style.UmlautFabulous;
		}else{
			return R.style.UmlautPirate;
		}
	}
	
	public static void readSettings(Context ctx) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
    	listStyle = Integer.parseInt(sp.getString(Umlaut.PREFERENCES_LIST_STYLE, "2"));
    	units = Integer.parseInt(sp.getString(Umlaut.PREFERENCES_UNITS, "1"));
    	
    	ComponentName comp = new ComponentName(ctx, Umlaut.class);
	    PackageInfo pinfo;
		try {
			pinfo = ctx.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
			versionCode  = pinfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static ConfirmDialog getConfirmDialoge(Context ctx, String title, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
		ConfirmDialog.Builder customBuilder = new ConfirmDialog.Builder(ctx);
    	
		if(title.length() > titleMaxLen ) {
    		title = title.substring(0, titleMaxLen) + ctx.getString(R.string.truncateSymbols);
    	}
		
    	customBuilder.setTitle(title)
        .setNegativeButton(ctx.getString(R.string.cancel), negativeListener)
        .setPositiveButton(ctx.getString(R.string.confirm), positiveListener);
    	
    	return customBuilder.create();
	}
	

	public static EditDialog getEditDialog(Context ctx, String title, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
		EditDialog.Builder customBuilder = new EditDialog.Builder(ctx);
    	
		if(title.length() > titleMaxLen ) {
    		title = title.substring(0, titleMaxLen) + ctx.getString(R.string.truncateSymbols);
    	}
		
    	customBuilder.setTitle(title)
        .setNegativeButton(ctx.getString(R.string.delete), negativeListener)
        .setPositiveButton(ctx.getString(R.string.update), positiveListener);
    	
    	return customBuilder.create();
	}	
	
	public static AlertDialog getAlertDialoge(Context ctx, String title, DialogInterface.OnClickListener positiveListener) {
		AlertDialog.Builder customBuilder = new AlertDialog.Builder(ctx);
    	
    	customBuilder.setTitle(title)
        .setPositiveButton(ctx.getString(R.string.ok), positiveListener);
    	
    	return customBuilder.create();
	}
	
	public static TextView getToastView(Context ctx, String label) {
		
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView textView = (TextView)inflater.inflate(R.drawable.utoast, null);
        textView.setText(label);
        
        return textView;
	}
	
	public static View getTofuToastView(Context ctx, String label) {
		
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.tofutoast, null);
        TextView textView = (TextView) layout.findViewById(R.id.title);
		textView.setText(label);
        
		ImageView peak = (ImageView) layout.findViewById(R.id.peak);
		Effect.substituteBlack(peak.getDrawable(), Umlaut.getInteractiveColor(ctx));
		
        return layout;
	}
	
	public static ListContextMenu getListContextMenu(Context ctx, String itemName, DialogInterface.OnClickListener shareListener, DialogInterface.OnClickListener deleteListener) {
		
		ListContextMenu.Builder customBuilder = new ListContextMenu.Builder(ctx);
		customBuilder.setTitle(ctx.getString(R.string.optionsFor) + " " + itemName);
		customBuilder.setShareButton(ctx.getString(R.string.share), shareListener);
		customBuilder.setDeleteButton(ctx.getString(R.string.delete), deleteListener);
		
		return customBuilder.create();
	}
	
	public static String formatDate(Context ctx, long time) {
		
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 1);
		c.set(Calendar.MILLISECOND, 0);
		
		long createdAt = c.getTimeInMillis();
		
		Calendar n = new GregorianCalendar();
		n.set(Calendar.HOUR_OF_DAY, 0);
		n.set(Calendar.MINUTE, 1);
		n.set(Calendar.MILLISECOND, 0);
		
		long now = n.getTimeInMillis();
		
		long days = ((Math.abs(now-createdAt)) / DAY);

		String s;
		
		//Log.i(TAG, "Days: " + days + " now: " + now + " createdAt: " + createdAt);
		
		if(days == 0) {
			s = "Today";
		}else if(days == 1) {
			s = "Yesterday";
		}else{
			
	    	SimpleDateFormat formatter = new SimpleDateFormat(units == UNITS_SI ? Umlaut.DATE_FORMAT_EU : Umlaut.DATE_FORMAT_US);
	    	s = formatter.format(c.getTime());
		}
		
    	return s;
	}
	
	public static String formatDateShort(Context ctx, long time) {
		
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 1);
		c.set(Calendar.MILLISECOND, 0);
		
		long createdAt = c.getTimeInMillis();
		
		Calendar n = new GregorianCalendar();
		n.set(Calendar.HOUR_OF_DAY, 0);
		n.set(Calendar.MINUTE, 1);
		n.set(Calendar.MILLISECOND, 0);
		
		long now = n.getTimeInMillis();
		
		long days = ((Math.abs(now-createdAt)) / DAY);

		String s;
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		String dateFormat = sp.getString("dateFormat", "1");
		
    	SimpleDateFormat formatter = new SimpleDateFormat(dateFormat.equals("1") ? Umlaut.DATE_FORMAT_EU_SHORT : Umlaut.DATE_FORMAT_US_SHORT);
    	s = formatter.format(c.getTime());
	
    	return s;
	}

	public static int getCurrentHtmlIcon(Context ctx) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		String themeId = sp.getString("theme", "1");
		
		if(themeId.equals("1")) {
			return R.drawable.default_new_small;
		}else if(themeId.equals("2")) {
			return R.drawable.cinema_new_small;
		}else if(themeId.equals("3")) {
			return R.drawable.world_new_small;
		}else if(themeId.equals("4")) {
			return R.drawable.fabulous_new_small;
		}else{
			return R.drawable.pirate_new_small;
		}
	}


	public static int getActiveColor(Context ctx) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		String themeId = sp.getString("theme", "1");
		
		int c;
		
		if(themeId.equals("1")) {
			c = ctx.getResources().getColor(R.color.defaultActive);
		}else if(themeId.equals("2")) {
			c = ctx.getResources().getColor(R.color.cinemaActive);
		}else if(themeId.equals("3")) {
			c = ctx.getResources().getColor(R.color.worldActive);
		}else if(themeId.equals("4")) {
			c = ctx.getResources().getColor(R.color.fabulousActive);
		}else{
			c = ctx.getResources().getColor(R.color.pirateActive);
		}
		
		return c;
	}

	public static int getInteractiveColor(Context ctx) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		String themeId = sp.getString("theme", "1");
		
		int c;
		
		if(themeId.equals("1")) {
			c = ctx.getResources().getColor(R.color.defaultInteractive);
		}else if(themeId.equals("2")) {
			c = ctx.getResources().getColor(R.color.cinemaInteractive);
		}else if(themeId.equals("3")) {
			c = ctx.getResources().getColor(R.color.worldInteractive);
		}else if(themeId.equals("4")) {
			c = ctx.getResources().getColor(R.color.fabulousInteractive);
		}else{
			c = ctx.getResources().getColor(R.color.pirateInteractive);
		}
		
		return c;
	}	

	public static int getPassiveColor(Context ctx) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		String themeId = sp.getString("theme", "1");
		
		int c;
		
		if(themeId.equals("1")) {
			c = ctx.getResources().getColor(R.color.defaultPassiveStrong);
		}else if(themeId.equals("2")) {
			c = ctx.getResources().getColor(R.color.cinemaPassiveStrong);
		}else if(themeId.equals("3")) {
			c = ctx.getResources().getColor(R.color.worldPassiveStrong);
		}else if(themeId.equals("4")) {
			c = ctx.getResources().getColor(R.color.fabulousPassiveStrong);
		}else{
			c = ctx.getResources().getColor(R.color.piratePassiveStrong);
		}
		
		return c;
	}

	public static Class getHomeActivityFor(long type) {
		if(type == Model.TYPE_TEXT_NOTE) {
			return NoteActivity.class;
		}else if(type == Model.TYPE_LIST_NOTE) {
			return ListNoteActivity.class;
		}
		
		return null;
	}	
	
	public static void setAlarm(Context ctx, long time, PendingIntent sender) {
		AlarmManager am = (AlarmManager)ctx.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, time, sender);
	}
	
	public static void cancelAlarm(Context ctx, PendingIntent sender) {
		AlarmManager am = (AlarmManager)ctx.getSystemService(ALARM_SERVICE);
		am.cancel(sender);
	}
	
}
