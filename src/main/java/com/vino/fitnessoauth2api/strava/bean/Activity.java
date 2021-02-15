package com.vino.fitnessoauth2api.strava.bean;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

	@Id
	private String id;
	private int userId;
	private String name;
	private String date;
	private String type;
	private double distance;
	private String activityName;
	private String platform;
	private String month;

}
