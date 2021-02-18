package com.vino.fitnessoauth2api.strava.controller;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vino.fitnessoauth2api.strava.bean.Activity;
import com.vino.fitnessoauth2api.strava.bean.Athlete;
import com.vino.fitnessoauth2api.strava.bean.AthleteActivityDetails;
import com.vino.fitnessoauth2api.strava.bean.StravaActivity;
import com.vino.fitnessoauth2api.strava.dao.StravaActivityRepository;

// https://stackoverflow.com/questions/19238715/how-to-set-an-accept-header-on-spring-resttemplate-request

@RestController
public class StravaController {

	private final String PLATFORM = "Strava";
	private final String MONTH_JAN = "JAN";
	private final String MONTH_FEB = "FEB";
	private final String MONTH_MAR = "MAR";
	private final String MONTH_APR = "APR";
	private final String MONTH_MAY = "MAY";
	private final String MONTH_JUN = "JUN";
	private final String MONTH_JUL = "JUL";
	private final String MONTH_AUG = "AUG";
	private final String MONTH_SEP = "SEP";
	private final String MONTH_OCT = "OCT";
	private final String MONTH_NOV = "NOV";
	private final String MONTH_DEC = "DEC";
	private final String TYPE_RUN = "Run";
	private final String TYPE_WALK = "Walk";
	private final String TYPE_RIDE = "Ride";

	@Autowired
	private StravaActivityRepository stravaActivityRepository;

	@RequestMapping("/athlete/clubs")
	public ResponseEntity<String> athleteClubs(final @AuthenticationPrincipal Principal principal) {

		final String url = "https://www.strava.com/api/v3/athlete/clubs";

		return sendGetRequest(principal, url);
	}

	@RequestMapping("/athlete")
	public ResponseEntity<String> getAthlete(final @AuthenticationPrincipal Principal principal) {
		ResponseEntity<String> response = null;
		try {
			final String url = "https://www.strava.com/api/v3/athlete";

			response = sendGetRequest(principal, url);
			ObjectMapper objectMapper = getObjectMapper();
			Athlete athlete = getAthlet(response.getBody());
			response = new ResponseEntity<String>(objectMapper.writeValueAsString(athlete), HttpStatus.OK);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	private Athlete getAthlet(String atheletJson) throws Exception {
		ObjectMapper objectMapper = getObjectMapper();
		return objectMapper.readValue(atheletJson, Athlete.class);
	}

	@RequestMapping("/athlete/activities")
	public ResponseEntity<String> athleteActivities(final @AuthenticationPrincipal Principal principal) {

		ResponseEntity<String> response = null;
		try {

			String url = "https://www.strava.com/api/v3/athlete";
			String month = MONTH_FEB;

			response = sendGetRequest(principal, url);
			ObjectMapper objectMapper = getObjectMapper();
			Athlete athlete = getAthlet(response.getBody());

			Instant beforeInstant = getInstant(true, month);
			Instant afterInstant = getInstant(false, month);

			System.out.println("Before " + beforeInstant);
			System.out.println("After " + afterInstant);

			long after = afterInstant.toEpochMilli() / 1000;
			long before = beforeInstant.toEpochMilli() / 1000;

			url = "https://www.strava.com/api/v3/athlete/activities" + "?after=" + after + "&before=" + before
					+ "?per_page=" + 200;

			response = sendGetRequest(principal, url);

			objectMapper = getObjectMapper();
			List<StravaActivity> activityList = objectMapper.readValue(response.getBody(),
					new TypeReference<List<StravaActivity>>() {
					});

			AthleteActivityDetails athleteActivityDetails = new AthleteActivityDetails(athlete, activityList);
			response = new ResponseEntity<String>(objectMapper.writeValueAsString(athleteActivityDetails),
					HttpStatus.OK);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	private Instant getInstant(boolean isBefore, String month) {

		String monthNo = getMonthNo(month);
		String endDate = getEndDate(month);
		String date = "01";
		String time = "T00:00:01.00Z";
		if (isBefore) {
			date = endDate;
			time = "T23:59:59.00Z";
		}

		String dateString = "2021-" + monthNo + "-" + date + time;
		Instant instant = Instant.parse(dateString);
		return instant;
	}

	private String getEndDate(String month) {
		String endDate = "00";
		switch (month) {
		case MONTH_JAN:
		case MONTH_MAR:
		case MONTH_MAY:
		case MONTH_JUL:
		case MONTH_AUG:
		case MONTH_OCT:
		case MONTH_DEC:
			endDate = "31";
			break;
		case MONTH_FEB:
			endDate = "28";
			break;
		case MONTH_APR:
		case MONTH_JUN:
		case MONTH_SEP:
		case MONTH_NOV:
			endDate = "30";
			break;
		}
		return endDate;
	}

	private String getMonthNo(String month) {
		String monthNo = "00";
		switch (month) {
		case MONTH_JAN:
			monthNo = "01";
			break;
		case MONTH_FEB:
			monthNo = "02";
			break;
		case MONTH_MAR:
			monthNo = "03";
			break;
		case MONTH_APR:
			monthNo = "04";
			break;
		case MONTH_MAY:
			monthNo = "05";
			break;
		case MONTH_JUN:
			monthNo = "06";
			break;
		case MONTH_JUL:
			monthNo = "07";
			break;
		case MONTH_AUG:
			monthNo = "08";
			break;
		case MONTH_SEP:
			monthNo = "09";
			break;
		case MONTH_OCT:
			monthNo = "10";
			break;
		case MONTH_NOV:
			monthNo = "11";
			break;
		case MONTH_DEC:
			monthNo = "12";
			break;

		}
		return monthNo;
	}

	@RequestMapping("/strava/activities")
	public ResponseEntity<String> getStravaActivities(final @AuthenticationPrincipal Principal principal) {

		ResponseEntity<String> response = null;
		try {

			String url = "https://www.strava.com/api/v3/athlete";
			String month = MONTH_FEB;

			response = sendGetRequest(principal, url);
			ObjectMapper objectMapper = getObjectMapper();
			Athlete athlete = getAthlet(response.getBody());

			Instant beforeInstant = getInstant(true, month);
			Instant afterInstant = getInstant(false, month);

			System.out.println("Before " + beforeInstant);
			System.out.println("After " + afterInstant);

			long after = afterInstant.toEpochMilli() / 1000;
			long before = beforeInstant.toEpochMilli() / 1000;

			url = "https://www.strava.com/api/v3/athlete/activities" + "?after=" + after + "&before=" + before
					+ "?per_page=" + 200;

			response = sendGetRequest(principal, url);

			objectMapper = getObjectMapper();
			List<StravaActivity> activityList = objectMapper.readValue(response.getBody(),
					new TypeReference<List<StravaActivity>>() {
					});

			List<Activity> newActivities = new ArrayList<Activity>();
			for (Iterator<StravaActivity> iterator = activityList.iterator(); iterator.hasNext();) {
				StravaActivity activity = (StravaActivity) iterator.next();

				String activityType = activity.getType();
				if (activityType.equals(TYPE_RUN) || activityType.equals(TYPE_RIDE) || activityType.equals(TYPE_WALK)) {

					String startdate = activity.getStart_date_local();
					String date = startdate.substring(0, 10);
					Activity stravaActivity = new Activity();
					stravaActivity.setUserId(athlete.getId());
					stravaActivity.setName(athlete.getFirstname() + " " + athlete.getLastname());
					stravaActivity.setActivityName(activity.getName());
					stravaActivity.setDate(date);
					stravaActivity.setDistance(Math.round(activity.getDistance() / 10.0) / 100.0);
					stravaActivity.setType(activityType);
					stravaActivity.setPlatform(PLATFORM);
					stravaActivity.setMonth(month);
					newActivities.add(stravaActivity);
				}
			}

			List<Activity> oldActivities = stravaActivityRepository.findByUserIdAndMonth(athlete.getId(), MONTH_FEB);
			if (!oldActivities.isEmpty()) {
				stravaActivityRepository.deleteAll(oldActivities);
				System.out.println(oldActivities.size() + " old activities of " + athlete.getFirstname() + " "
						+ athlete.getLastname() + " of the month " + MONTH_FEB + " is deleted");
			}

			stravaActivityRepository.saveAll(newActivities);
			System.out.println(newActivities.size() + " activities of " + athlete.getFirstname() + " "
					+ athlete.getLastname() + " of the month " + MONTH_FEB + " is created ");

			response = new ResponseEntity<String>(objectMapper.writeValueAsString(newActivities), HttpStatus.OK);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@RequestMapping("/strava/report")
	public ResponseEntity<String> getStravaActivitiesReport(final @AuthenticationPrincipal Principal principal) {

		ResponseEntity<String> response = null;
		try {
			List<Activity> activitiesList = stravaActivityRepository.findByMonth(MONTH_FEB);
			response = new ResponseEntity<String>(getObjectMapper().writeValueAsString(activitiesList), HttpStatus.OK);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	private ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
		return objectMapper;
	}

	private ResponseEntity<String> sendGetRequest(final Principal principal, final String url) {

		final RestTemplate restTemplate = new RestTemplate();

		final HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(getAccessToken(principal));
		final HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	}

	private String getAccessToken(final Principal principal) {

		final OAuth2Authentication oauth2Auth = (OAuth2Authentication) principal;
		final OAuth2AuthenticationDetails oauth2AuthDetails = (OAuth2AuthenticationDetails) oauth2Auth.getDetails();

		return oauth2AuthDetails.getTokenValue();
	}

}
