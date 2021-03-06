package com.graffitab.server.persistence.model.activity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.graffitab.server.persistence.model.user.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("FOLLOW")
public class ActivityFollow extends Activity {

	private static final long serialVersionUID = 1L;

	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "followed_user_id")
	private User followed;

	public ActivityFollow() {
		super(ActivityType.FOLLOW);
	}

	public ActivityFollow(User followed) {
		super(ActivityType.FOLLOW);

		this.followed = followed;
	}

	@Override
	public boolean isSameActivity(Activity other) {
		if (!isSameTypeOfActivity(other)) {
			return false;
		}

		ActivityFollow activityFollow = (ActivityFollow) other;
		return activityFollow.getFollowed().equals(this.followed);
	}
}
