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
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.idsource.extended;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_STORE;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.DELETE_AFTER;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.READLIST_BEFORE_ENHANCE_SINGLE;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.READ_BEFORE_ENHANCE;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.SEARCH_BEFORE_ENHANCE_SINGLE;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.UPDATE_BEFORE_STORE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.idsource.extended.spy.SpiderDependencyProviderSpy;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactoryImp;

public class IdSourceExtendedFunctionalityFactoryTest {

	private static final String RECORD_TYPE = "sequence";
	private ExtendedFunctionalityFactory factory;
	private SpiderDependencyProvider dependencyProvider;

	@BeforeMethod
	public void beforeMethod() {
		setUpLoggerProvider();
		setUpSettingsProvider();

		factory = new IdSourceExtendedFunctionalityFactory();
		dependencyProvider = new SpiderDependencyProviderSpy();
		factory.initializeUsingDependencyProvider(dependencyProvider);

	}

	private void setUpSettingsProvider() {
		Map<String, String> settings = new HashMap<>();
		settings.put("coraDatabaseLookupName", "coraDatabaseLookupNameValue");
		SettingsProvider.setSettings(settings);
	}

	private void setUpLoggerProvider() {
		LoggerFactorySpy loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
	}

	@Test
	public void testInit() {
		assertTrue(factory instanceof IdSourceExtendedFunctionalityFactory);
	}

	@Test
	public void testGetExtendedFunctionalityContextsForHandleSequence() {
		assertContext(CREATE_BEFORE_STORE);
		assertContext(UPDATE_BEFORE_STORE);
		assertContext(DELETE_AFTER);
	}

	@Test
	public void testGetExtendedFunctionalityContextsForReadSequence() {
		assertContext(READ_BEFORE_ENHANCE);
		assertContext(READLIST_BEFORE_ENHANCE_SINGLE);
		assertContext(SEARCH_BEFORE_ENHANCE_SINGLE);
	}

	@Test
	public void testFactorCreateSequence() {
		List<ExtendedFunctionality> functionalities = factory.factor(CREATE_BEFORE_STORE,
				RECORD_TYPE);

		assertCreateSequenceExtFuntIsUsed(functionalities);
	}

	private void assertCreateSequenceExtFuntIsUsed(List<ExtendedFunctionality> functionalities) {
		assertEquals(functionalities.size(), 1);
		CreateSequenceExtendedFunctionality extendedFunctionality = (CreateSequenceExtendedFunctionality) functionalities
				.get(0);

		SqlDatabaseFactoryImp sqlDatabasFactory = (SqlDatabaseFactoryImp) extendedFunctionality
				.onlyForTestGetDatabaseFactory();
		assertEquals(sqlDatabasFactory.onlyForTestGetLookupName(), "coraDatabaseLookupNameValue");
	}

	@Test
	public void testFactorUpdateSequence() {
		List<ExtendedFunctionality> functionalities = factory.factor(UPDATE_BEFORE_STORE,
				RECORD_TYPE);

		assertUpdateSequenceExtFuntIsUsed(functionalities);
	}

	private void assertUpdateSequenceExtFuntIsUsed(List<ExtendedFunctionality> functionalities) {
		assertEquals(functionalities.size(), 1);

		UpdateSequenceExtendedFunctionality extendedFunctionality = (UpdateSequenceExtendedFunctionality) functionalities
				.get(0);
		SqlDatabaseFactoryImp sqlDatabasFactory = (SqlDatabaseFactoryImp) extendedFunctionality
				.onlyForTestGetDatabaseFactory();
		assertEquals(sqlDatabasFactory.onlyForTestGetLookupName(), "coraDatabaseLookupNameValue");
	}

	@Test
	public void testFactorDeleteSequence() {
		List<ExtendedFunctionality> functionalities = factory.factor(DELETE_AFTER, RECORD_TYPE);

		assertDeleteSequenceExtFuntIsUsed(functionalities);
	}

	private void assertDeleteSequenceExtFuntIsUsed(List<ExtendedFunctionality> functionalities) {
		assertEquals(functionalities.size(), 1);

		DeleteSequenceExtendedFunctionality extendedFunctionality = (DeleteSequenceExtendedFunctionality) functionalities
				.get(0);
		SqlDatabaseFactoryImp sqlDatabasFactory = (SqlDatabaseFactoryImp) extendedFunctionality
				.onlyForTestGetDatabaseFactory();
		assertEquals(sqlDatabasFactory.onlyForTestGetLookupName(), "coraDatabaseLookupNameValue");
	}

	@Test
	public void testFactorReadSequence() {
		List<ExtendedFunctionality> functionalities = factory.factor(READ_BEFORE_ENHANCE,
				RECORD_TYPE);

		assertReadSequenceExtFunctIsUsed(functionalities);
	}

	@Test
	public void testFactorReadListSequence() {
		List<ExtendedFunctionality> functionalities = factory.factor(READLIST_BEFORE_ENHANCE_SINGLE,
				RECORD_TYPE);

		assertReadSequenceExtFunctIsUsed(functionalities);
	}

	@Test
	public void testFactorReadSearchSequence() {
		List<ExtendedFunctionality> functionalities = factory.factor(SEARCH_BEFORE_ENHANCE_SINGLE,
				RECORD_TYPE);

		assertReadSequenceExtFunctIsUsed(functionalities);
	}

	private void assertReadSequenceExtFunctIsUsed(List<ExtendedFunctionality> functionalities) {
		assertEquals(functionalities.size(), 1);

		ReadSequenceExtendedFunctionality extendedFunctionality = (ReadSequenceExtendedFunctionality) functionalities
				.get(0);
		SqlDatabaseFactoryImp sqlDatabasFactory = (SqlDatabaseFactoryImp) extendedFunctionality
				.onlyForTestGetDatabaseFactory();
		assertEquals(sqlDatabasFactory.onlyForTestGetLookupName(), "coraDatabaseLookupNameValue");
	}

	private void assertContext(ExtendedFunctionalityPosition position) {
		ExtendedFunctionalityContext extFunc = assertContextExistsAndReturnIt(position);
		assertEquals(extFunc.recordType, RECORD_TYPE);
		assertEquals(extFunc.position, position);
		assertEquals(extFunc.runAsNumber, 0);
	}

	private ExtendedFunctionalityContext assertContextExistsAndReturnIt(
			ExtendedFunctionalityPosition position) {
		var optionalExtFuncContext = tryToFindMatchingContext(position);

		if (!optionalExtFuncContext.isPresent()) {
			Assert.fail("Failed find a matching context");
		}

		return optionalExtFuncContext.get();
	}

	private Optional<ExtendedFunctionalityContext> tryToFindMatchingContext(
			ExtendedFunctionalityPosition position) {
		return factory.getExtendedFunctionalityContexts().stream()
				.filter(contexts -> contexts.position.equals(position)).findFirst();
	}
}
