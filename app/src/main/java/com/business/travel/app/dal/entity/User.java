package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author chenshang
 */
@Entity
public class User {
	@PrimaryKey(autoGenerate = true)
	public int id;

	public String firstName;
	public String lastName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
