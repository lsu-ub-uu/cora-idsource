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
package se.uu.ub.cora.idsource.extended;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.idsource.IdSourceException;
import se.uu.ub.cora.idsource.SequenceSpy;
import se.uu.ub.cora.idsource.spy.SqlDatabaseFactorySpy;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class UpdateSequenceExtendedFunctionalityTest {

	private ExtendedFunctionality extFunc;
	private DataRecordGroupSpy dataRecordGroup;
	private ExtendedFunctionalityData data;
	private SqlDatabaseFactorySpy sqlDatabaseFactory;

	@BeforeMethod
	private void beforeMethod() {
		sqlDatabaseFactory = new SqlDatabaseFactorySpy();
		extFunc = UpdateSequenceExtendedFunctionality.usingDatabaseFactory(sqlDatabaseFactory);

		setData();
	}

	private void setData() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "100");
		data = new ExtendedFunctionalityData();
		data.dataRecordGroup = dataRecordGroup;
	}

	@Test
	public void testUpdateSequence() {
		extFunc.useExtendedFunctionality(data);

		SequenceSpy sequence = (SequenceSpy) sqlDatabaseFactory.MCR
				.assertCalledParametersReturn("factorSequence");
		sequence.MCR.assertCalledParameters("updateSequenceValue", "someId", 100L);
	}

	@Test
	public void testUpdateSequence_isClosed() {
		extFunc.useExtendedFunctionality(data);

		SequenceSpy sequence = (SequenceSpy) sqlDatabaseFactory.MCR
				.assertCalledParametersReturn("factorSequence");
		sequence.MCR.assertMethodWasCalled("close");
	}

	@Test
	public void testUpdateSequence_error() {
		SequenceSpy sequenceSpy = new SequenceSpy();
		sqlDatabaseFactory.MRV.setDefaultReturnValuesSupplier("factorSequence", () -> sequenceSpy);
		RuntimeException error = new RuntimeException("some error");
		sequenceSpy.MRV.setAlwaysThrowException("updateSequenceValue", error);
		try {
			extFunc.useExtendedFunctionality(data);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof IdSourceException);
			assertEquals(e.getMessage(), "Error updating sequence with id: someId");
			assertEquals(e.getCause(), error);
		}

	}

	@Test
	public void testOnlyForTest() {
		assertEquals(
				((UpdateSequenceExtendedFunctionality) extFunc).onlyForTestGetDatabaseFactory(),
				sqlDatabaseFactory);
	}

}
