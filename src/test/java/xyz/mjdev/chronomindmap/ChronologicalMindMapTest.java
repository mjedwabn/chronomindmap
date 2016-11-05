package xyz.mjdev.chronomindmap;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class ChronologicalMindMapTest {
	@Test
	public void addDurationFact() {
		Timeline timeline = new Timeline();
		timeline.addFact("4E174", "4E176", new Fact("Fact"));
		assertThat(timeline.facts("4E174", "4E176"), contains("Fact"));
	}

	private class Timeline {
		private List<TimelineFact> facts = new ArrayList<>();

		public List<String> facts(String from, String to) {
			return facts.stream()
					.filter(f -> f.isBetween(from, to))
					.map(TimelineFact::getDescription)
					.collect(Collectors.toList());
		}

		public void addFact(String from, String to, Fact fact) {
			facts.add(new DurationFact(from, to, fact));
		}

		private class DurationFact extends TimelineFact {
			private final String from;
			private final String to;


			public DurationFact(String from, String to, Fact fact) {
				super(fact);
				this.from = from;
				this.to = to;
			}
		}

		private class TimelineFact {
			private final Fact fact;

			public TimelineFact(Fact fact) {
				this.fact = fact;
			}

			public boolean isBetween(String from, String to) {
				return true;
			}

			public String getDescription() {
				return fact.getDescription();
			}
		}
	}

	private class Fact {
		private String description;

		public Fact(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}
}
