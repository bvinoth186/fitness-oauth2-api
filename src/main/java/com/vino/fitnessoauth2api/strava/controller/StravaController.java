package com.vino.fitnessoauth2api.strava.controller;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

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

// https://stackoverflow.com/questions/19238715/how-to-set-an-accept-header-on-spring-resttemplate-request

@RestController
public class StravaController {

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

			response = sendGetRequest(principal, url);
			ObjectMapper objectMapper = getObjectMapper();
			Athlete athlete = getAthlet(response.getBody());

			Instant afterInstant = Instant.parse("2021-02-01T00:00:01.00Z");
			Instant beforeInstant = Instant.parse("2021-02-27T23:59:59.00Z");

			long after = afterInstant.toEpochMilli() / 1000;
			long before = beforeInstant.toEpochMilli() / 1000;

			url = "https://www.strava.com/api/v3/athlete/activities" + "?after=" + after + "&before=" + before
					+ "?per_page=" + 200;

			response = sendGetRequest(principal, url);

			objectMapper = getObjectMapper();
			List<Activity> activityList = objectMapper.readValue(response.getBody(),
					new TypeReference<List<Activity>>() {
					});
			
			AthleteActivityDetails athleteActivityDetails = new AthleteActivityDetails(athlete, activityList);
			response = new ResponseEntity<String>(objectMapper.writeValueAsString(athleteActivityDetails), HttpStatus.OK);
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
