package com.vino.fitnessoauth2api.strava.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StravaActivity {

	/*
	 * public String getStart_date_str() { return formatter.format(start_date_str);
	 * }
	 */

	private String name;
	private double distance;
	private int moving_time;
	private int elapsed_time;
	private String type;
	private int workout_type;
	private Object id;
	private String start_date;
	private String start_date_local;
//	private String start_date_str;
	private String timezone;
	private int utc_offset;

//	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

}
