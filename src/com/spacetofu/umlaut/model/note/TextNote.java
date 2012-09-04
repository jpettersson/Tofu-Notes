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

package com.spacetofu.umlaut.model.note;

import android.content.Context;

import com.spacetofu.umlaut.model.Model;

public class TextNote extends Note {
	
	public TextNote() {
		super();
		type = Model.TYPE_TEXT_NOTE;
	}
	
	public TextNote(long id, long parent_id, String name, String content, long createdAt,long updatedAt, long type, long priority, long reminderAt, long locationId) {
		super(id, parent_id, name, content, createdAt, updatedAt, type, priority, reminderAt, locationId);
	}
	
	@Override
	public String toEmail(Context ctx) {
		return getContent();
	}
}
