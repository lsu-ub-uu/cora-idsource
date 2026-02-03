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

import java.util.Collections;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.idsource.IdSourceInstanceProvider;
import se.uu.ub.cora.bookkeeper.recordtype.RecordType;

public class TimestampInstanceProviderTest {

	IdSourceInstanceProvider provider;
	private RecordType recordType;

	@BeforeMethod
	void beforeMethod() {
		provider = new TimestampInstanceProviderImp();
		createRecordTypeIdSource();
	}

	@Test
	public void testGetType() {
		assertEquals(provider.getTypeToSelectImplementionsBy(), "timestamp");
	}

	@Test
	public void testGetIdSource() {
		TimestampIdSource idSource = (TimestampIdSource) provider.getIdSource(recordType);
		assertTrue(idSource instanceof TimestampIdSource);
		assertEquals(idSource.onlyForTestGetRecordTypeId(), recordType.id());
	}

	private void createRecordTypeIdSource() {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		recordType = new RecordType("someRecordTypeId", "someDefinitionId", Optional.empty(),
				"someIdSource", Optional.of("sequenceId"), Collections.emptyList(), isPublic,
				usePermissionUnit, useVisibility, useTrashBin, storeInArchive);
	}
}
