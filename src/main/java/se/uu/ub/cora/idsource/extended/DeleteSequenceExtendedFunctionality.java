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

import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.idsource.IdSourceException;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactory;
import se.uu.ub.cora.sqldatabase.sequence.Sequence;

public class DeleteSequenceExtendedFunctionality implements ExtendedFunctionality {

	private SqlDatabaseFactory sqlDatabaseFactory;

	public static DeleteSequenceExtendedFunctionality usingDatabaseFactory(
			SqlDatabaseFactory sqlDatabaseFactory) {
		return new DeleteSequenceExtendedFunctionality(sqlDatabaseFactory);
	}

	private DeleteSequenceExtendedFunctionality(SqlDatabaseFactory sqlDatabaseFactory) {
		this.sqlDatabaseFactory = sqlDatabaseFactory;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		DataRecordGroup dataRecordGroup = data.dataRecordGroup;
		deleteSequence(dataRecordGroup.getId());
	}

	private void deleteSequence(String sequenceId) {
		try (Sequence sequence = sqlDatabaseFactory.factorSequence()) {
			sequence.deleteSequence(sequenceId);
		} catch (Exception e) {
			throw IdSourceException
					.withMessageAndException("Error deleting sequence with id: " + sequenceId, e);
		}
	}
}
