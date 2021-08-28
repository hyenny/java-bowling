package bowling.domain;

import static bowling.domain.PitchResult.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import bowling.domain.exception.InvalidPinCountException;

public class LastFrame extends Frame {

	private static final int MAX = 10;

	private Pitch bonus;

	private LastFrame() {

	}

	private LastFrame(final Pitch first) {
		super(first);
	}

	private LastFrame(final Pitch first, final Pitch second) {
		super(first, second);
	}

	private LastFrame(final Pitch first, final Pitch second, final Pitch bonus) {
		super(first, second);
		this.bonus = bonus;
	}

	public static LastFrame of() {
		return new LastFrame();
	}

	public static LastFrame of(final Pitch first) {
		return new LastFrame(first);
	}

	public static LastFrame of(final Pitch first, final Pitch second) {
		return new LastFrame(first, second);
	}

	public static LastFrame of(final Pitch first, final Pitch second, final Pitch bonus) {
		return new LastFrame(first, second, bonus);
	}

	@Override
	public boolean isEnd() {
		if (first == null || second == null) {
			return false;
		}

		if (bonus != null) {
			return true;
		}

		if (first.getPitchResult().equals(STRIKE)
			|| PitchResult.findByPinCount(first.getPinCount(), second.getPinCount()).equals(SPARE)) {
			return false;
		}

		return true;
	}

	@Override
	public void pitch(final int pinCount) {
		if (first == null) {
			first = new Pitch(pinCount);
			return;
		}

		if (second == null) {
			addSecond(pinCount);
			return;
		}

		addBonus(pinCount);
	}

	@Override
	public String getResult() {
		final List<String> result = new ArrayList<>();
		Optional.ofNullable(first)
			.ifPresent(e -> result.add(resultHelper(first)));
		Optional.ofNullable(second)
			.ifPresent(e -> result.add(resultHelper(first, second)));
		Optional.ofNullable(bonus)
			.ifPresent(e -> result.add(resultHelper(second, bonus)));

		return String.join(DELIMITER, result);
	}

	private String resultHelper(final Pitch first) {
		if (first.getPitchResult().equals(STRIKE)) {
			return STRIKE.getFlag(first.getPinCount());
		}

		return String.valueOf(first.getPinCount());
	}

	private String resultHelper(final Pitch first, final Pitch second) {
		if (first.getPitchResult().equals(STRIKE)) {
			return second.getPitchResult().getFlag(second.getPinCount());
		}

		return PitchResult.findByPinCount(first.getPinCount(), second.getPinCount()).getFlag(second.getPinCount());
	}

	private void addSecond(final int pinCount) {
		if (first.getPitchResult().equals(STRIKE)) {
			second = first.next(pinCount);
			return;
		}

		if (first.getPinCount() + pinCount > MAX) {
			throw new InvalidPinCountException();
		}

		second = first.next(pinCount);
	}

	private void addBonus(final int pinCount) {
		if (second.getPitchResult().equals(STRIKE)
			|| second.getPitchResult().equals(SPARE)) {
			bonus = second.next(pinCount);
			return;
		}

		if (second.getPinCount() + pinCount > MAX) {
			throw new InvalidPinCountException();
		}

		bonus = second.next(pinCount);
	}

	public Pitch getBonus() {
		return bonus;
	}
}