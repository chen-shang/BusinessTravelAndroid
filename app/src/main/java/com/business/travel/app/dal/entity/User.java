package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

/**
 * @author chenshang
 */
@Entity
@Data
public class User {
	@PrimaryKey(autoGenerate = true)
	public long id;
	public String firstName;
	public String lastName;
}
