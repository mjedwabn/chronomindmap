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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.expectThrows;

public class SkyrimCalendarTest {
	@Test
	public void givenErasAreWellDefined_compareDates() {
		assertThat(new SkyrimCalendar("2E2"), is(lessThan(new SkyrimCalendar("2E4"))));
		assertThat(new SkyrimCalendar("3E3"), is(equalTo(new SkyrimCalendar("3E3"))));
		assertThat(new SkyrimCalendar("3E3"), is(greaterThan(new SkyrimCalendar("3E2"))));
	}

	@Test
	public void whenCreateNewCalendarInUndefinedEra_defineEraEndingOnThisYear() {
		assertThat(new SkyrimCalendar("6E30"), is(lessThan(new SkyrimCalendar("7E10"))));
	}

	@Test
	public void givenEraIsFloating_whenCreateNewCalendarGreaterThanErasEnd_adjustEraToThisYear() {
		SkyrimCalendar calendarOne = new SkyrimCalendar("6E10");
		SkyrimCalendar calendarTwo = new SkyrimCalendar("6E30");
		SkyrimCalendar calendarThree = new SkyrimCalendar("8E15");

		assertThat(calendarOne, is(lessThan(calendarThree)));
		assertThat(calendarTwo, is(lessThan(calendarThree)));
	}

	@Test
	public void givenEraIsWellDefined_whenCreateCalenderExceedingEra_throwException() {
		expectThrows(IndexOutOfBoundsException.class, () -> {
			new SkyrimCalendar("1E666");
		});
	}
}
