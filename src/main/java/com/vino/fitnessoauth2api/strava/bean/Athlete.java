package com.vino.fitnessoauth2api.strava.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Athlete {
	 public int id;
	 public String firstname;
	 public String lastname;

}
