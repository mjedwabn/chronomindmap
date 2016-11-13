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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import xyz.mjdev.chronomindmap.knowledge.KnowledgeBase;
import xyz.mjdev.chronomindmap.knowledge.entity.Person;
import xyz.mjdev.chronomindmap.knowledge.entity.Place;
import xyz.mjdev.chronomindmap.knowledge.entity.SimpleTimeFact;
import xyz.mjdev.chronomindmap.persistence.entity.DataFile;
import xyz.mjdev.chronomindmap.timeline.control.DurationFact;

import java.io.*;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KnowledgeBasePersistence {
	private static final Logger logger = Logger.getLogger(
			KnowledgeBasePersistence.class.getName());
	private static final String EMPTY_DATAFILE = "{}";

	public static DataFile readData(Path dataFile) {
		return readData(asInputStream(dataFile));
	}

	private static InputStream asInputStream(Path dataFile) {
		try {
			return new FileInputStream(dataFile.toFile());
		} catch (FileNotFoundException e) {
			return new ByteArrayInputStream(EMPTY_DATAFILE.getBytes());
		}
	}

	private static DataFile readData(InputStream inputStream) {
		try {
			return getObjectMapper().readValue(inputStream, DataFile.class);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not read data file. {0}", e.getMessage());
			return new DataFile();
		}
	}

	private static ObjectMapper getObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		om.registerSubtypes(
				new NamedType(Person.class, "Person"),
				new NamedType(Place.class, "Place"),
				new NamedType(DurationFact.class, "DurationFact"),
				new NamedType(SimpleTimeFact.class, "SimpleTimeFact")
		);
		return om;
	}

	private static DataFile dumpKnowledgeBase() {
		DataFile dataFile = new DataFile();
		dataFile.setFacts(KnowledgeBase.factsGateway.findAll());
		return dataFile;
	}

	public static void fillKnowledgeBase(DataFile dataFile) {
		// boundary between data layer and business application
		dataFile.getFacts().forEach(f -> KnowledgeBase.factsGateway.add(f));
	}

	public static void load(InputStream inputStream) {
		fillKnowledgeBase(readData(inputStream));
	}

	public static void load(Path path) {
		fillKnowledgeBase(readData(path));
	}

	public static void writeData(Path dataFilePath) throws IOException {
		writeData(dumpKnowledgeBase(), dataFilePath);
	}

	private static void writeData(DataFile dataFile, Path dataFilePath) throws IOException {
		getObjectMapper().writeValue(dataFilePath.toFile(), dataFile);
	}
}
