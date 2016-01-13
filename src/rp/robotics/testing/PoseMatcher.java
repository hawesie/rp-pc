package rp.robotics.testing;

import lejos.robotics.navigation.Pose;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class PoseMatcher {

	private static final float COORD_ERROR_RANGE_M = 0.1f;
	private static final float HEADING_ERROR_RANGE_DEG = 10;

	public static Matcher<Pose> is(final Pose _this) {
		return new BaseMatcher<Pose>() {

			@Override
			public boolean matches(final Object _item) {
				final Pose that = (Pose) _item;
				return Math.abs(_this.getX() - that.getX()) < COORD_ERROR_RANGE_M
						&& Math.abs(_this.getY() - that.getY()) < COORD_ERROR_RANGE_M
						&& Math.abs(_this.getHeading() - that.getHeading()) < HEADING_ERROR_RANGE_DEG;

			}

			@Override
			public void describeTo(final Description _description) {
				_description.appendText("pose should be ").appendText(
						_this.toString());
			}

			@Override
			public void describeMismatch(final Object _item,
					final Description _description) {
				_description.appendText("was").appendText(_item.toString());
			}
		};
	}

}
