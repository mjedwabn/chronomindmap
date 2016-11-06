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

import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.mjdev.chronomindmap.knowledge.KnowledgeBase;

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

	static DataFile readData(InputStream inputStream) {
		try {
			return new ObjectMapper().readValue(inputStream, DataFile.class);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not read data file. {0}", e.getMessage());
			return new DataFile();
		}
	}

	static DataFile dumpKnowledgeBase() {
		DataFile dataFile = new DataFile();
		dataFile.setPersons(KnowledgeBase.personsGateway.findAll());
		return dataFile;
	}

	public static void fillKnowledgeBase(DataFile dataFile) {
		dataFile.getPersons().forEach(p -> KnowledgeBase.personsGateway.add(p));
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
		ObjectMapper om = new ObjectMapper();
		om.writeValue(dataFilePath.toFile(), dataFile);
	}
}
