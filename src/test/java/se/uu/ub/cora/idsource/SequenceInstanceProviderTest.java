/*
 * Copyright 2026 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.idsource;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactoryImp;

public class SequenceInstanceProviderTest {

	SequenceInstanceProviderImp provider;
	private RecordType recordType;

	@BeforeMethod
	void beforeMethod() {
		recordType = createRecordTypeIdSource();
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		setUpSettingsProvider();

		provider = new SequenceInstanceProviderImp();
	}

	private void setUpSettingsProvider() {
		Map<String, String> settings = new HashMap<>();
		settings.put("coraDatabaseLookupName", "someDatabase");
		SettingsProvider.setSettings(settings);
	}

	@Test
	public void testGetType() {
		assertEquals(provider.getTypeToSelectImplementionsBy(), "sequence");
	}

	@Test
	public void testGetIdSource_DatabaseFactory() {
		SequenceIdSource idSource = (SequenceIdSource) provider.getIdSource(recordType);

		SqlDatabaseFactoryImp sqlDatabaseFactory = (SqlDatabaseFactoryImp) idSource
				.onlyForTestGetDatabaseFactory();
		assertEquals(sqlDatabaseFactory.onlyForTestGetLookupName(), "someDatabase");
	}

	@Test
	public void testGetIdSource_usesSequenceIdFromRecordType() {
		SequenceIdSource idSource = (SequenceIdSource) provider.getIdSource(recordType);

		assertTrue(idSource instanceof SequenceIdSource);
		assertEquals(idSource.onlyForTestGetSequenceId(), "sequenceId");
	}

	@Test
	public void testGetIdSource_throwsIdSourceException() {
		recordType = createRecordTypeIdSource(Optional.empty());
		try {
			provider.getIdSource(recordType);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof IdSourceException);
			assertEquals(e.getMessage(), "Getting idSource failed");
			assertEquals(e.getCause().getMessage(), "No value present");
		}
	}

	private RecordType createRecordTypeIdSource() {
		return createRecordTypeIdSource(Optional.of("sequenceId"));
	}

	private RecordType createRecordTypeIdSource(Optional<String> sequenceId) {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		return new RecordType("someRecordTypeId", "someDefinitionId", Optional.empty(),
				"someIdSource", sequenceId, Collections.emptyList(), isPublic, usePermissionUnit,
				useVisibility, useTrashBin, storeInArchive);
	}
}
