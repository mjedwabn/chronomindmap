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

import org.junit.Test;
import xyz.mjdev.chronomindmap.knowledge.Fact;
import xyz.mjdev.chronomindmap.knowledge.TimelineFact;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ChronologicalMindMapTest {
	@Test
	public void newTimelineShallNotHaveFacts() {
		Timeline timeline = new Timeline();
		assertThat(timeline.getFacts(), is(empty()));
	}

	@Test
	public void addDurationFact() {
		Timeline timeline = new Timeline();
		timeline.addFact("4E174", "4E176", new Fact("Fact"));

		assertThat(timeline.getFacts(), contains("Fact"));
	}

	@Test
	public void addMultipleFacts() {
		Timeline timeline = new Timeline();
		timeline.addFact("4E20", "4E22", new Fact("Fact2"));
		timeline.addFact("4E1", "4E2", new Fact("Fact1"));

		assertThat(timeline.getFacts(), containsInAnyOrder("Fact1", "Fact2"));
	}

	@Test
	public void queryFactsBetweenDates() {
		Timeline timeline = new Timeline();
		timeline.addFact("4E1", "4E2", new Fact("Fact1"));
		timeline.addFact("4E2", "4E3", new Fact("Fact2"));
		timeline.addFact("4E3", "4E4", new Fact("Fact3"));

		assertThat(timeline.getFacts("1E1", "4E3"), hasSize(2));
	}

	private class Timeline {
		private List<TimelineFact> facts = new ArrayList<>();

		public List<String> getFacts(String from, String to) {
			return facts.stream()
					.filter(f -> f.isBetween(new SkyrimCalendar(from), new SkyrimCalendar(to)))
					.map(TimelineFact::getDescription)
					.collect(Collectors.toList());
		}

		public void addFact(String from, String to, Fact fact) {
			facts.add(new DurationFact(from, to, fact));
		}

		public List<String> getFacts() {
			return facts.stream()
					.map(TimelineFact::getDescription)
					.collect(Collectors.toList());
		}

		private class DurationFact extends TimelineFact {
			private final Calendar from;
			private final Calendar to;

			public DurationFact(String from, String to, Fact fact) {
				super(fact);
				this.from = new SkyrimCalendar(from);
				this.to = new SkyrimCalendar(to);
			}

			@Override
			public boolean isBetween(Calendar from, Calendar to) {
				return this.from.compareTo(from) >= 0 && this.to.compareTo(to) <= 0;
			}
		}
	}
}
