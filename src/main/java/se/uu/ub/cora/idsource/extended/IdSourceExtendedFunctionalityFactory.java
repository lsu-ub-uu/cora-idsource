/*
 *	 Copyright 2025, 2026 Uppsala University Library
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
 *     You should have received a opy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.idsource.extended;

import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_STORE;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.DELETE_AFTER;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.READLIST_BEFORE_ENHANCE_SINGLE;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.READ_BEFORE_ENHANCE;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.SEARCH_BEFORE_ENHANCE_SINGLE;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.UPDATE_BEFORE_STORE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactoryImp;

public class IdSourceExtendedFunctionalityFactory implements ExtendedFunctionalityFactory {

	private static final String RECORD_TYPE = "sequence";
	private static final int RUN_AS_NUMBER = 0;
	private List<ExtendedFunctionalityContext> contexts = new ArrayList<>();
	private SqlDatabaseFactoryImp dbFactory;

	@Override
	public void initializeUsingDependencyProvider(SpiderDependencyProvider dependencyProvider) {
		createListOfContexts();
		startDatabaseFactory();
	}

	private void createListOfContexts() {
		createContext(CREATE_BEFORE_STORE);
		createContext(UPDATE_BEFORE_STORE);
		createContext(DELETE_AFTER);

		createContext(READ_BEFORE_ENHANCE);
		createContext(READLIST_BEFORE_ENHANCE_SINGLE);
		createContext(SEARCH_BEFORE_ENHANCE_SINGLE);
	}

	private void createContext(ExtendedFunctionalityPosition position) {
		contexts.add(new ExtendedFunctionalityContext(position, RECORD_TYPE, RUN_AS_NUMBER));
	}

	private void startDatabaseFactory() {
		String databaseLookupValue = SettingsProvider.getSetting("coraDatabaseLookupName");
		dbFactory = SqlDatabaseFactoryImp.usingLookupNameFromContext(databaseLookupValue);
	}

	@Override
	public List<ExtendedFunctionalityContext> getExtendedFunctionalityContexts() {
		return contexts;
	}

	@Override
	public List<ExtendedFunctionality> factor(ExtendedFunctionalityPosition position,
			String recordType) {
		if (CREATE_BEFORE_STORE.equals(position)) {
			return toList(CreateSequenceExtendedFunctionality.usingDatabaseFactory(dbFactory));
		}
		if (UPDATE_BEFORE_STORE.equals(position)) {
			return toList(UpdateSequenceExtendedFunctionality.usingDatabaseFactory(dbFactory));
		}
		if (DELETE_AFTER.equals(position)) {
			return toList(DeleteSequenceExtendedFunctionality.usingDatabaseFactory(dbFactory));
		}
		return toList(ReadSequenceExtendedFunctionality.usingDatabaseFactory(dbFactory));
	}

	private List<ExtendedFunctionality> toList(ExtendedFunctionality usingDatabaseFactory) {
		return Collections.singletonList(usingDatabaseFactory);
	}

}
