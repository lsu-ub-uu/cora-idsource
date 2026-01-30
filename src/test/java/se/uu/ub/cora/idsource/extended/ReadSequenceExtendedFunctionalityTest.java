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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.idsource.IdSourceException;
import se.uu.ub.cora.idsource.SequenceSpy;
import se.uu.ub.cora.idsource.spy.SqlDatabaseFactorySpy;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class ReadSequenceExtendedFunctionalityTest {

	private ExtendedFunctionality extFunc;
	private DataRecordGroupSpy dataRecordGroup;
	private ExtendedFunctionalityData data;
	private SqlDatabaseFactorySpy sqlDatabaseFactory;
	private DataFactorySpy dataFactorySpy;

	@BeforeMethod
	private void beforeMethod() {
		sqlDatabaseFactory = new SqlDatabaseFactorySpy();
		extFunc = ReadSequenceExtendedFunctionality.usingDatabaseFactory(sqlDatabaseFactory);
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
		setData();
	}

	private void setData() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
		data = new ExtendedFunctionalityData();
		data.dataRecordGroup = dataRecordGroup;
	}

	@AfterMethod
	private void afterMethod() {
		DataProvider.onlyForTestSetDataFactory(null);
	}

	@Test
	public void testReadSequence() {
		SequenceSpy sequenceSpy = new SequenceSpy();
		sqlDatabaseFactory.MRV.setDefaultReturnValuesSupplier("factorSequence", () -> sequenceSpy);
		sequenceSpy.MRV.setDefaultReturnValuesSupplier("getCurrentValueForSequence", () -> 222L);

		extFunc.useExtendedFunctionality(data);

		dataRecordGroup.MCR.assertCalledParametersReturn("removeAllChildrenWithNameInData",
				"currentNumber");
		var newCurrentNumber = dataFactorySpy.MCR.assertCalledParametersReturn(
				"factorAtomicUsingNameInDataAndValue", "currentNumber", "222");
		dataRecordGroup.MCR.assertCalledParameters("addChild", newCurrentNumber);
	}

	@Test
	public void testReadSequence_isClosed() {
		extFunc.useExtendedFunctionality(data);

		SequenceSpy sequence = (SequenceSpy) sqlDatabaseFactory.MCR
				.assertCalledParametersReturn("factorSequence");
		sequence.MCR.assertMethodWasCalled("close");
	}

	@Test
	public void testReadSequence_error() {
		SequenceSpy sequenceSpy = new SequenceSpy();
		sqlDatabaseFactory.MRV.setDefaultReturnValuesSupplier("factorSequence", () -> sequenceSpy);
		RuntimeException error = new RuntimeException("some error");
		sequenceSpy.MRV.setAlwaysThrowException("getCurrentValueForSequence", error);
		try {
			extFunc.useExtendedFunctionality(data);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof IdSourceException);
			assertEquals(e.getMessage(),
					"Error reading current value for sequence with id: someId");
			assertEquals(e.getCause(), error);
		}

	}

}
