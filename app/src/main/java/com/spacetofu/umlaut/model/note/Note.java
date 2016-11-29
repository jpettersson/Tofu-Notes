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

import java.util.ArrayList;

import android.content.Context;

import com.spacetofu.umlaut.model.Model;

public abstract class Note {
	
	public static long PRIORITY_NORMAL = 0;
	public static long PRIORITY_HIGH = 1;	
	
	private long id = 0;
	private long parentId = 0;
	
	private String name = "";
	private String content = "";
	
	private String stagedName = "";
	private String stagedContent = "";
	
	private long createdAt = 0;
	private long updatedAt = 0;
	
	protected long type;

	private ArrayList<Note> children;
	private long priority = 0;
	private long reminderAt = 0;
	private long locationId = 0;
	
	Note() {
		children = new ArrayList<Note>();
	}
	
	Note(long id, long parentId, String name, String content, long createdAt, long updatedAt, long type, long priority, long reminderAt, long locationId) {
		super();
		setId(id);
		setParentId(parentId);
		setName(name);
		setContent(content);
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.type = type;
		this.priority = priority;
		this.reminderAt = reminderAt;
		this.locationId = locationId;
	}


	public void stageChanges(String defaultName, String defaultContent, String name, String content) {
		if(!name.equals(defaultName)) this.stagedName = name;
		if(!content.equals(defaultContent)) this.stagedContent = content;
	}
	
	public void postChanges() {
		name = stagedName;
		content = stagedContent;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public long getType() {
		return type;
	}

	public void setName(String name) {
		this.name = this.stagedName = name;
	}

	public String getName() {
		return name;
	}

	public void setContent(String content) {
		this.content = this.stagedContent = content;
	}

	public String getContent() {
		return content;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}
	
	public boolean isSaved() {
		return this.id != 0;
	}

	public void setChildren(ArrayList<Note> children) {
		this.children = children;
	}
	
	public ArrayList<Note> getChildren() {
		return this.children;
	}
	
	public boolean hasChanges() {
		
		if(!name.equals(stagedName)) return true;
		if(!content.equals(stagedContent)) return true;
		
		return false;
	}

	public String toEmail(Context ctx) {
		return "no content";
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public long getParentId() {
		return parentId;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}
	
	public void setPriority(long priority) {
		this.priority = priority;
	
		Model.getInstance().writePriority(this);
	}
	
	public long getPriority() {
		return priority;
	}
	
	public boolean hasReminder() {
		return reminderAt > 0;
	}
	
	public long getReminderDate() { 
		return reminderAt;
	}
	
	public void setReminderDate(long date) {
		this.reminderAt = date;
		
		Model.getInstance().writeReminder(this);
	}
	
	public boolean hasLocation() {
		return locationId > 0;
	}
	
	
}