/*
 * Copyright (c) 2016, Maciej Jedwabny
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package xyz.mjdev.chronomindmap.timeline;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.IntStream;

public class SkyrimCalendar extends Calendar {
	private static final Map<Integer, Integer> eras;
	private static final Set<Integer> wellDefinedEras;

	static {
		eras = new HashMap<>();
		wellDefinedEras = new HashSet<>();
		// FIXME: test values. Need logic allowing evolving eras definition
		// basing on gathered knowledge from game play.
		setWellDefinedEra(1, 200);
		setWellDefinedEra(2, 200);
		setWellDefinedEra(3, 200);
		setWellDefinedEra(4, 200);
	}

	private final int era;
	private final int year;

	public SkyrimCalendar(String date) {
		this(extractEra(date), extractYear(date));
	}

	public SkyrimCalendar(int era, int year) {
		this.era = era;
		this.year = year;

		adjustEra();
	}

	private static void setWellDefinedEra(int eraNumber, int eraEnd) {
		eras.put(eraNumber, eraEnd);
		wellDefinedEras.add(eraNumber);
	}

	private static int extractEra(String date) {
		String[] parts = parseDate(date);
		return Integer.parseInt(parts[0]);
	}

	private static int extractYear(String date) {
		String[] parts = parseDate(date);
		return Integer.parseInt(parts[1]);
	}

	private static String[] parseDate(String date) {
		return date.split("E");
	}

	private void adjustEra() {
		if (eraIsUndefined())
			setFloatingEra();
		else if (yearExceedsEra())
			if (eraIsFloating())
				setFloatingEra();
			else
				throw new IndexOutOfBoundsException(String.format(
						"Year %d exceeds end era %s: %d.",
						year, era, eras.get(era)));
	}

	private boolean eraIsUndefined() {
		return !eras.containsKey(era);
	}

	private void setFloatingEra() {
		eras.put(era, year);
	}

	private boolean yearExceedsEra() {
		return year > eras.get(era);
	}

	private boolean eraIsFloating() {
		return !wellDefinedEras.contains(era);
	}

	@Override
	protected void computeTime() {
		time = pastErasAsMillis() + yearAsMillis();
	}

	private long pastErasAsMillis() {
		return IntStream.range(1, era)
				.mapToLong(this::eraToMillis)
				.sum();
	}

	private long eraToMillis(int e) {
		return yearToMillis(eraDuration(e));
	}

	private long yearAsMillis() {
		return yearToMillis(this.year);
	}

	private long yearToMillis(int year) {
		return Duration.of(year, SkyrimUnit.YEARS).toMillis();
	}

	private int eraDuration(int era) {
		return eras.getOrDefault(era, 1);
	}

	@Override
	protected void computeFields() {
		set(SkyrimCalendar.YEAR, year);
		set(SkyrimCalendar.ERA, era);
	}

	@Override
	public void add(int field, int amount) {

	}

	@Override
	public void roll(int field, boolean up) {

	}

	@Override
	public int getMinimum(int field) {
		return 0;
	}

	@Override
	public int getMaximum(int field) {
		return 0;
	}

	@Override
	public int getGreatestMinimum(int field) {
		return 0;
	}

	@Override
	public int getLeastMaximum(int field) {
		return 0;
	}

	private enum SkyrimUnit implements TemporalUnit {
		// To make calculations easy, there is assumption that year is not an
		// estimated value - taking Earth's non-leap year duration.
		YEARS("Years", Duration.ofSeconds(365 * 24 * 60 * 60));

		private final String name;
		private final Duration duration;

		SkyrimUnit(String name, Duration duration) {
			this.name = name;
			this.duration = duration;
		}

		@Override
		public Duration getDuration() {
			return duration;
		}

		@Override
		public boolean isDurationEstimated() {
			return false;
		}

		@Override
		public boolean isDateBased() {
			return false;
		}

		@Override
		public boolean isTimeBased() {
			return false;
		}

		@Override
		public <R extends Temporal> R addTo(R temporal, long amount) {
			return null;
		}

		@Override
		public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
			return 0;
		}
	}
}
