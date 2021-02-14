package com.vino.fitnessoauth2api.strava.controller;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

public class Main {

	public static void main(String[] args) {

		Instant afterInstant = Instant.parse("2021-02-01T00:00:01.00Z"); 
		Instant beforeInstant = Instant.parse("2021-02-27T23:59:59.00Z"); 
		
		long after = afterInstant.toEpochMilli()/1000;
		long before = beforeInstant.toEpochMilli()/1000;
		
		System.out.println(after);
		System.out.println(before);
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
		formatter.format(date);
		
		System.out.println(date);



	}

}
