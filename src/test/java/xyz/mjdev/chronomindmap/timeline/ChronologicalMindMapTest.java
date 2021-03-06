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

import org.junit.jupiter.api.Test;
import xyz.mjdev.chronomindmap.timeline.control.Timeline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ChronologicalMindMapTest {
	@Test
	public void newTimelineShallNotHaveFacts() {
		Timeline timeline = new Timeline();
		assertThat(timeline.getFacts(), is(empty()));
	}

	@Test
	public void addDurationFact() {
		Timeline timeline = new Timeline();
		timeline.addFact("4E174", "4E176", "FactDTO");

		assertThat(timeline.getFacts(), contains("FactDTO"));
	}

	@Test
	public void addMultipleFacts() {
		Timeline timeline = new Timeline();
		timeline.addFact("4E20", "4E22", "Fact2");
		timeline.addFact("4E1", "4E2", "Fact1");

		assertThat(timeline.getFacts(), containsInAnyOrder("Fact1", "Fact2"));
	}

	@Test
	public void queryFactsBetweenDates() {
		Timeline timeline = new Timeline();
		timeline.addFact("4E1", "4E2", "Fact1");
		timeline.addFact("4E2", "4E3", "Fact2");
		timeline.addFact("4E3", "4E4", "Fact3");

		assertThat(timeline.getFacts("1E1", "4E3"), hasSize(2));
	}
}
