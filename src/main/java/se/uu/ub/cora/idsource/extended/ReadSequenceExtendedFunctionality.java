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

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.idsource.IdSourceException;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactory;
import se.uu.ub.cora.sqldatabase.sequence.Sequence;

public class ReadSequenceExtendedFunctionality implements ExtendedFunctionality {

	private static final String CURRENT_NUMBER = "currentNumber";
	private SqlDatabaseFactory sqlDatabaseFactory;

	public static ReadSequenceExtendedFunctionality usingDatabaseFactory(
			SqlDatabaseFactory sqlDatabaseFactory) {
		return new ReadSequenceExtendedFunctionality(sqlDatabaseFactory);
	}

	private ReadSequenceExtendedFunctionality(SqlDatabaseFactory sqlDatabaseFactory) {
		this.sqlDatabaseFactory = sqlDatabaseFactory;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		DataRecordGroup dataRecordGroup = data.dataRecordGroup;
		long sequenceCurrentNumbre = readCurrentNumberFromSequence(dataRecordGroup.getId());
		replaceCurrentNumber(dataRecordGroup, sequenceCurrentNumbre);
	}

	private void replaceCurrentNumber(DataRecordGroup dataRecordGroup, long sequenceCurrentNumber) {
		dataRecordGroup.removeAllChildrenWithNameInData(CURRENT_NUMBER);
		String valueOf = String.valueOf(sequenceCurrentNumber);
		DataAtomic currentNumber = DataProvider.createAtomicUsingNameInDataAndValue(CURRENT_NUMBER,
				valueOf);
		dataRecordGroup.addChild(currentNumber);
	}

	private long readCurrentNumberFromSequence(String sequenceId) {
		try (Sequence sequence = sqlDatabaseFactory.factorSequence()) {
			return sequence.getCurrentValueForSequence(sequenceId);
		} catch (Exception e) {
			throw IdSourceException.withMessageAndException(
					"Error reading current value for sequence with id: " + sequenceId, e);
		}
	}
}
