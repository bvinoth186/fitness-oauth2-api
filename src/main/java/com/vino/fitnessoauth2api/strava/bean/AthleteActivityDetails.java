package com.vino.fitnessoauth2api.strava.bean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AthleteActivityDetails {
	
	private Athlete athlete;
	private List<Activity> activityList;

}
