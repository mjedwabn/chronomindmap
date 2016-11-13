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
package xyz.mjdev.chronomindmap.timeline.control;

import xyz.mjdev.chronomindmap.knowledge.entity.Fact;
import xyz.mjdev.chronomindmap.timeline.SkyrimCalendar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Timeline {
	private List<TimelineFact> facts = new ArrayList<>();

	public static Timeline fromFacts(List<Fact> facts) {
		Timeline timeline = new Timeline();
		timeline.facts = facts.stream()
				.filter(f -> f instanceof TimelineFact)
				.map(f -> (TimelineFact) f)
				.collect(Collectors.toList());
		return timeline;
	}

	public List<String> getFacts(String from, String to) {
		return facts.stream()
				.filter(f -> f.isBetween(new SkyrimCalendar(from), new SkyrimCalendar(to)))
				.map(Fact::getName)
				.collect(Collectors.toList());
	}

	public void addFact(TimelineFact fact) {
		facts.add(fact);
	}

	public List<String> getFacts() {
		return facts.stream()
				.map(Fact::getName)
				.collect(Collectors.toList());
	}

	public void addFact(String from, String to, String fact) {
		addFact(new DurationFact(from, to, fact));
	}

}
