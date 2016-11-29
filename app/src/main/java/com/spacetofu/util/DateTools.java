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

package com.spacetofu.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateUtils;

public class DateTools {
	
	public static ArrayList<String> getYearList(Calendar c, int numFutureYears) {
		ArrayList<String> y = new ArrayList<String>();
		
		Date d = c.getTime();
		int year = d.getYear() + 1900;

		for(int i=0;i<numFutureYears;i++) {
			y.add(Integer.toString(year + i));
		}
		
		return y;
	}
	
	public static ArrayList<String> getDateListFor(Calendar c) {
		ArrayList<String> list = new ArrayList<String>();
		
		int days = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		for(int i=0;i<days;i++) {
			list.add(Integer.toString(i+1));
		}
		
		return list;
	}

	public static ArrayList<String> getHoursList() {
		ArrayList<String> list = new ArrayList<String>();
		
		for(int i=0;i<12;i++) {
			list.add(Integer.toString(i+1));
		}
		
		return list;
	}

	public static ArrayList<String> getHoursOfDayList() {
		ArrayList<String> list = new ArrayList<String>();
		
		for(int i=0;i<24;i++) {
			list.add(Integer.toString(i));
		}
		
		return list;
	}	
	
	public static ArrayList<String> getMinutesList() {
		ArrayList<String> list = new ArrayList<String>();
		
		for(int i=0;i<60;i++) {
			list.add(Integer.toString(i));
		}
		
		return list;		
	}

	public static ArrayList<String> getMonthList() {
		ArrayList<String> monthList = new ArrayList<String>();
        for(int i=0;i<12;i++) {
        	monthList.add(DateUtils.getMonthString(i, DateUtils.LENGTH_SHORT));
        }
        
        return monthList;
	}
}