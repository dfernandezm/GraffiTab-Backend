package com.graffitab.server.persistence.model.activity;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.joda.time.DateTime;

import com.graffitab.server.persistence.model.activity.Activity.ActivityType;
import com.graffitab.server.persistence.model.user.User;

@Getter
@Setter
@EqualsAndHashCode
public class ActivityContainer {

	private ActivityType activityType;
	private DateTime createdOn;
	private User user;
	private List<Activity> activities = new ArrayList<>();
}
