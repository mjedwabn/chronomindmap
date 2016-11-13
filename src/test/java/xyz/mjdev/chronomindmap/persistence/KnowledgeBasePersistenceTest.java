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
package xyz.mjdev.chronomindmap.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import xyz.mjdev.chronomindmap.knowledge.Gateway;
import xyz.mjdev.chronomindmap.knowledge.KnowledgeBase;
import xyz.mjdev.chronomindmap.knowledge.entity.Fact;
import xyz.mjdev.chronomindmap.knowledge.entity.Person;
import xyz.mjdev.chronomindmap.knowledge.entity.Place;
import xyz.mjdev.chronomindmap.knowledge.entity.SimpleTimeFact;
import xyz.mjdev.chronomindmap.timeline.control.Timeline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class KnowledgeBasePersistenceTest {
	@BeforeEach
	public void initKnowledgeBase() {
		KnowledgeBase.factsGateway = new Gateway<>();
	}

	private ByteArrayInputStream asInputStream(String data) {
		return new ByteArrayInputStream(data.getBytes());
	}

	@Test
	public void whenDataFileDoesNotExist_loadEmptyKnowledgeBase() {
		KnowledgeBasePersistence.load(Paths.get("notExistingFile"));
		assertThat(KnowledgeBase.factsGateway.findAll(), is(empty()));
	}

	@Test
	public void whenDataFileIsCorrupted_loadEmptyKnowledgeBase() {
		KnowledgeBasePersistence.load(asInputStream("corrupted content"));
		assertThat(KnowledgeBase.factsGateway.findAll(), is(empty()));
	}

	@Test
	public void dumpEmptyKnowledgeBase() throws URISyntaxException, IOException {
		Path dataFilePath = getDataFilePath("test_db.json");
		KnowledgeBasePersistence.writeData(dataFilePath);

		assertThat(read(dataFilePath), is("{}"));

		Files.deleteIfExists(dataFilePath);
	}

	private Path getDataFilePath(String dataFile) throws URISyntaxException {
		Path configDir = Paths.get(getClass().getClassLoader().getResource(".").toURI());
		return configDir.resolve(dataFile);
	}

	private String read(Path dataFilePath) throws IOException {
		return new String(Files.readAllBytes(dataFilePath));
	}

	@Nested
	class Persons {
		@Test
		public void addPerson() {
			KnowledgeBase.factsGateway.add(new Person("Person"));
			assertThat(KnowledgeBase.factsGateway.findAll().stream()
					.filter(f -> f instanceof Person)
					.map(Fact::getName).collect(Collectors.toList()), contains("Person"));
		}

		@Test
		public void loadPersonsFromJson() {
			String data = "{\"facts\":[{\"@type\":\"Person\", \"name\":\"Person\"}]}";
			KnowledgeBasePersistence.load(asInputStream(data));

			assertThat(KnowledgeBase.factsGateway.findAll(),
					contains(new Person("Person")));
		}
	}

	@Nested
	class Facts {
		@Test
		public void loadFactsFromJson() {
			String data = "{\"facts\":[" +
					"{\"@type\":\"Person\", \"name\":\"Fact1\"}," +
					"{\"@type\":\"Place\", \"name\":\"Fact2\"}]}";
			KnowledgeBasePersistence.load(asInputStream(data));

			assertThat(KnowledgeBase.factsGateway.findAll(),
					contains(new Person("Fact1"), new Place("Fact2")));
		}
	}

	@Nested
	class TimelineFacts {
		@Test
		public void buildTimelineFromGatheredFacts() {
			KnowledgeBase.factsGateway.add(new SimpleTimeFact("1E1", "Fact1"));
			KnowledgeBase.factsGateway.add(new SimpleTimeFact("1E1", "Fact2"));

			Timeline timeline = Timeline.fromFacts(KnowledgeBase.factsGateway.findAll());

			assertThat(timeline.getFacts(), contains("Fact1", "Fact2"));
		}
	}
}