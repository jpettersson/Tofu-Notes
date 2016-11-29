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

import com.spacetofu.umlaut.model.Model;

public class PhotoNote extends Note {
	
	PhotoNote() {
		super();
		type = Model.TYPE_PHOTO_NOTE;
	}
	
	public PhotoNote(long id, long parentId, String name, String content, long createdAt,long updatedAt, long type, long priority, long reminderAt, long locationId) {
		super(id, parentId, name, content, createdAt, updatedAt, type, priority, reminderAt, locationId);
	}
}
