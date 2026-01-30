/*
 * Copyright 2025, 2026 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.idsource.IdSource;
import se.uu.ub.cora.idsource.spy.SqlDatabaseFactorySpy;

public class SequenceIdSourceTest {

	private static final String SEQUENCE_ID = "someSequenceId";
	private SequenceSpy sequence;
	private IdSource idSource;
	private SqlDatabaseFactorySpy sqlDatabaseFactorySpy;

	@BeforeMethod
	private void beforeMethod() {
		sqlDatabaseFactorySpy = new SqlDatabaseFactorySpy();
		sequence = new SequenceSpy();
		sqlDatabaseFactorySpy.MRV.setDefaultReturnValuesSupplier("factorSequence", () -> sequence);
		idSource = new SequenceIdSource(sqlDatabaseFactorySpy, SEQUENCE_ID);
	}

	@Test
	public void testGetId() {
		sequence.MRV.setDefaultReturnValuesSupplier("getNextValueForSequence", () -> 111L);

		String id = idSource.getId();

		sequence = (SequenceSpy) sqlDatabaseFactorySpy.MCR.getReturnValue("factorSequence", 0);
		sequence.MCR.assertParameters("getNextValueForSequence", 0, SEQUENCE_ID);
		assertEquals(id, "111");
	}

	@Test
	public void testSequenceIsClosed() {
		sequence.MRV.setDefaultReturnValuesSupplier("getNextValueForSequence", () -> 111L);

		idSource.getId();

		sequence.MCR.assertMethodWasCalled("close");
	}

	@Test
	public void testGetId_throwsIdSourceException() {
		RuntimeException runtimeException = new RuntimeException("some error");
		sequence.MRV.setAlwaysThrowException("getNextValueForSequence", runtimeException);

		try {
			idSource.getId();
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof IdSourceException);
			assertEquals(e.getMessage(), "Getting id failed");
			assertEquals(e.getCause(), runtimeException);
		}
	}

	@Test
	public void testOnlyForTest() {
		SequenceIdSource sequenceIdSource = (SequenceIdSource) idSource;
		assertEquals(sequenceIdSource.onlyForTestGetDatabaseFactory(), sqlDatabaseFactorySpy);
		assertEquals(sequenceIdSource.onlyForTestGetSequenceId(), SEQUENCE_ID);
	}
}
