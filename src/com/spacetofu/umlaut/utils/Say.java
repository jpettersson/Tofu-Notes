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

package com.spacetofu.umlaut.utils;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.spacetofu.umlaut.Umlaut;

public class Say {
	
	private static final String TAG = "Say";
	private static ArrayList<Toast> toasts = new ArrayList<Toast>();
	
	public static void silence() {
		clean();
	}
	
	public static void now(Context ctx, String text) { 
		toast(ctx, text, Toast.LENGTH_SHORT);
	}
	
	public static void now(Context ctx, String text, int duration) { 
		toast(ctx, text, duration);
	}
	
	private static void toast(Context ctx, String text, int duration) {
		Toast toastView = new Toast(ctx);
        toastView.setView(Umlaut.getTofuToastView(ctx, text));
        toastView.setDuration(duration);
        toastView.setGravity(Gravity.BOTTOM | Gravity.LEFT, 0,0);
        toasts.add(toastView);
        toastView.show();
	}
	
	private static void clean() {
		Iterator<Toast> i = toasts.iterator();
		while(i.hasNext()) {
			Toast t = i.next();
			t.cancel();
		}
		
		toasts = new ArrayList<Toast>();
	}
	
}
