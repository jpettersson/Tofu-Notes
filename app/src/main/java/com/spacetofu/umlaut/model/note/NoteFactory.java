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

public class NoteFactory {
	
	public static Note create(long id, long parentId, String name, String content, long createdAt, long updatedAt, long type, long priority, long reminderAt, long locationId) throws Exception {
		if(type == Model.TYPE_TEXT_NOTE) {
			return new TextNote(id, parentId, name, content, createdAt, updatedAt, type, priority, reminderAt, locationId);
		}else if(type == Model.TYPE_LIST_NOTE) {
			return new ListNote(id, parentId, name, content, createdAt, updatedAt, type, priority, reminderAt, locationId);
		}else if(type == Model.TYPE_PHOTO_NOTE) {
			return new PhotoNote(id, parentId, name, content, createdAt, updatedAt, type, priority, reminderAt, locationId);
		}else if(type == Model.TYPE_VOICE_NOTE) {
			return new VoiceNote(id, parentId, name, content, createdAt, updatedAt, type, priority, reminderAt, locationId);
		}if(type == Model.TYPE_NOTE_ROW) {
			return new NoteRow(id, parentId, name, content, createdAt, updatedAt, type, priority, reminderAt, locationId);
		}
		
		throw(new Exception("Unsupported note type"));
	}
	
	public static Note create(long type) throws Exception {
		if(type == Model.TYPE_TEXT_NOTE) {
			return new TextNote();
		}else if(type == Model.TYPE_LIST_NOTE) {
			return new ListNote();
		}else if(type == Model.TYPE_PHOTO_NOTE) {
			return new PhotoNote();
		}else if(type == Model.TYPE_VOICE_NOTE) {
			return new VoiceNote();
		}if(type == Model.TYPE_NOTE_ROW) {
			return new NoteRow();
		}
		
		throw(new Exception("Unsupported note type"));
	}
	
}
