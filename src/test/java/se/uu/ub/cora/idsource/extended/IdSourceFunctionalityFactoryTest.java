/*
 *	 Copyright 2025 Uppsala University Library
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

import java.util.List;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.idsource.extended.spy.SpiderDependencyProviderSpy;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class IdSourceFunctionalityFactoryTest {

	private static final String RECORDTYPE = "sequence";
	private ExtendedFunctionalityFactory factory;
	private SpiderDependencyProvider dependencyProvider;

	@BeforeMethod
	public void beforeMethod() {
		factory = new IdSourceExtendedFunctionalityFactory();
		dependencyProvider = new SpiderDependencyProviderSpy();
		factory.initializeUsingDependencyProvider(dependencyProvider);
	}

	@Test
	public void testInit() {
		assertTrue(factory instanceof IdSourceExtendedFunctionalityFactory);
	}

	@Test
	public void testGetExtendedFunctionalityContextsForUpdate() {
		assertEquals(factory.getExtendedFunctionalityContexts().size(), 2);
		assertContext(ExtendedFunctionalityPosition.UPDATE_AFTER_METADATA_VALIDATION);
	}

	@Test
	public void testGetExtendedFunctionalityContextsForCreate() {
		assertContext(ExtendedFunctionalityPosition.CREATE_AFTER_METADATA_VALIDATION);
	}

	@Test
	public void testFactorUpdate() {
		List<ExtendedFunctionality> functionalities = factory.factor(
				ExtendedFunctionalityPosition.UPDATE_AFTER_METADATA_VALIDATION, "someRecordType");
		assertTrue(functionalities.get(0) instanceof CreateSequenceExtendedFunctionality);
	}

	@Test
	public void testFactorCreate() {
		List<ExtendedFunctionality> functionalities = factory.factor(
				ExtendedFunctionalityPosition.CREATE_AFTER_METADATA_VALIDATION, "someRecordType");
		assertTrue(functionalities.get(0) instanceof CreateSequenceExtendedFunctionality);
	}

	private void assertContext(ExtendedFunctionalityPosition position) {
		ExtendedFunctionalityContext extFunc = assertContextExistsAndReturnIt(position);
		assertEquals(extFunc.recordType, RECORDTYPE);
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
