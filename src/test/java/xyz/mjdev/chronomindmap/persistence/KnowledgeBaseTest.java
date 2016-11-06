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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import xyz.mjdev.chronomindmap.knowledge.Gateway;
import xyz.mjdev.chronomindmap.knowledge.KnowledgeBase;
import xyz.mjdev.chronomindmap.knowledge.Person;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class KnowledgeBaseTest {
	@Before
	public void setUp() {
		KnowledgeBase.personsGateway = new Gateway<>();
	}

	@Test
	public void addPerson() {
		KnowledgeBase.personsGateway.add(new Person("Person"));
		assertThat(KnowledgeBase.personsGateway.findAll().stream()
				.map(Person::getName).collect(Collectors.toList()), contains("Person"));
	}

	@Test
	public void loadPersonsFromJson() {
		String data = "{\"persons\":[{\"name\":\"Person\"}]}";
		KnowledgeBasePersistence.load(asInputStream(data));

		assertThat(KnowledgeBase.personsGateway.findAll(),
				contains(new Person("Person")));
	}

	@Ignore
	@Test
	public void loadFactsFromJson() {

//		assertThat(KnowledgeBase.factsGateway.findAll(), contains(new TimelineFact(new Fact("Fact"))));
	}

	private ByteArrayInputStream asInputStream(String data) {
		return new ByteArrayInputStream(data.getBytes());
	}

	@Test
	public void whenDataFileDoesNotExist_loadEmptyKnowledgeBase() {
		KnowledgeBasePersistence.load(Paths.get("notExistingFile"));
		assertThat(KnowledgeBase.personsGateway.findAll(), is(empty()));
	}

	@Test
	public void whenDataFileIsCorrupted_loadEmptyKnowledgeBase() {
		KnowledgeBasePersistence.load(asInputStream("corrupted content"));
		assertThat(KnowledgeBase.personsGateway.findAll(), is(empty()));
	}

	@Test
	public void dumpEmptyKnowledgeBase() throws URISyntaxException, IOException {
		Path dataFilePath = getDataFilePath("test_db.json");
		KnowledgeBasePersistence.writeData(dataFilePath);

		assertThat(read(dataFilePath), is("{\"persons\":[]}"));

		Files.deleteIfExists(dataFilePath);
	}

	private Path getDataFilePath(String dataFile) throws URISyntaxException {
		Path configDir = Paths.get(getClass().getClassLoader().getResource(".").toURI());
		return configDir.resolve(dataFile);
	}

	private String read(Path dataFilePath) throws IOException {
		return new String(Files.readAllBytes(dataFilePath));
	}
}