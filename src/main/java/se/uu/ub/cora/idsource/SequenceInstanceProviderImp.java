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

import se.uu.ub.cora.bookkeeper.idsource.IdSource;
import se.uu.ub.cora.bookkeeper.idsource.IdSourceInstanceProvider;
import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactory;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactoryImp;

public class SequenceInstanceProviderImp implements IdSourceInstanceProvider {
	private SqlDatabaseFactory databaseFactory;

	public SequenceInstanceProviderImp() {
		startStorage();
	}

	private void startStorage() {
		String databaseLookupValue = SettingsProvider.getSetting("coraDatabaseLookupName");
		databaseFactory = SqlDatabaseFactoryImp.usingLookupNameFromContext(databaseLookupValue);
	}

	@Override
	public IdSource getIdSource(RecordType recordType) {
		try {
			return new SequenceIdSource(databaseFactory, getSequenceIdWhichMustExists(recordType));
		} catch (Exception e) {
			throw IdSourceException.withMessageAndException("Getting idSource failed", e);
		}
	}

	private String getSequenceIdWhichMustExists(RecordType recordType) {
		return recordType.sequenceId().get();
	}

	@Override
	public String getTypeToSelectImplementionsBy() {
		return "sequence";
	}

	public SqlDatabaseFactory onlyForTestGetSqlDatabaseFactory() {
		return databaseFactory;
	}
}