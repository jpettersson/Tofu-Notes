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



import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.Window;

public class MainPreferenceActivity extends PreferenceActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		//setTheme(Umlaut.getCurrentPreferencesTheme(this));
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.layout.preferences);
        
        OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
				Intent intent = new Intent(MainPreferenceActivity.this, OverviewActivity.class);
		    	startActivity(intent);
				
				return true;
			}
    	
    	};
        
    	ListPreference lp2 = (ListPreference) findPreference("dateFormat");
        if(lp2 != null)
        	lp2.setOnPreferenceChangeListener(listener);
    	
        ListPreference lp = (ListPreference) findPreference("theme");
        if(lp != null)
        	lp.setOnPreferenceChangeListener(listener);
        
        ListPreference lp1 = (ListPreference) findPreference("listStyle");
        if(lp1 != null)
        	lp1.setOnPreferenceChangeListener(listener);
    }
}
