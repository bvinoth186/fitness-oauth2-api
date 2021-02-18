package com.vino.fitnessoauth2api.strava.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vino.fitnessoauth2api.strava.bean.Activity;

public interface StravaActivityRepository extends MongoRepository<Activity, String> {
	
	public List<Activity> findByUserIdAndMonth(int userId, String month);
	
	public List<Activity> findByMonth(String month);
	
	

}
