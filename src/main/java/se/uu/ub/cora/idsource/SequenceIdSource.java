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

import se.uu.ub.cora.bookkeeper.idsource.IdSource;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactory;
import se.uu.ub.cora.sqldatabase.sequence.Sequence;

public class SequenceIdSource implements IdSource {

	private String sequenceId;
	private SqlDatabaseFactory sqlDatabaseFactory;

	public SequenceIdSource(SqlDatabaseFactory sqlDatabaseFactory, String sequenceId) {
		this.sqlDatabaseFactory = sqlDatabaseFactory;
		this.sequenceId = sequenceId;
	}

	@Override
	public String getId() {
		try (Sequence sequence = sqlDatabaseFactory.factorSequence()) {
			return tryToGetId(sequence);
		} catch (Exception e) {
			throw IdSourceException.withMessageAndException("Getting id failed", e);
		}
	}

	private String tryToGetId(Sequence sequence) {
		long nextValueForSequence = sequence.getNextValueForSequence(sequenceId);
		return String.valueOf(nextValueForSequence);
	}

	public SqlDatabaseFactory onlyForTestGetDatabaseFactory() {
		return sqlDatabaseFactory;
	}

	public String onlyForTestGetSequenceId() {
		return sequenceId;
	}

}
