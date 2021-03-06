/**
 * Copyright (c) 2008, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *      IBM Corporation - initial API and implementation
 */
package org.eclipse.e4.ui.model.application.ui.basic;


/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @generated
 */
public interface MBasicFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MBasicFactory INSTANCE = org.eclipse.e4.ui.model.application.ui.basic.impl.BasicFactoryImpl.eINSTANCE;

	/**
	 * Returns a new object of class '<em>Part</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Part</em>'.
	 * @since 1.0
	 * @generated
	 */
	MPart createPart();

	/**
	 * Returns a new object of class '<em>Composite Part</em>'.
	 * <!-- begin-user-doc -->
	 * @since 1.1
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Composite Part</em>'.
	 * @since 1.1
	 * @generated
	 */
	MCompositePart createCompositePart();

	/**
	 * Returns a new object of class '<em>Input Part</em>'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Input Part</em>'.
	 * @since 1.0
	 * @deprecated See {@link MInputPart model documentation} for details
	 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=509868">Bug
	 *      509868</a>
	 * @generated NOT
	 * @noreference This method is not intended to be referenced by clients.
	 */
	@Deprecated
	MInputPart createInputPart();

	/**
	 * Returns a new object of class '<em>Part Stack</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Part Stack</em>'.
	 * @since 1.0
	 * @generated
	 */
	MPartStack createPartStack();

	/**
	 * Returns a new object of class '<em>Part Sash Container</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Part Sash Container</em>'.
	 * @since 1.0
	 * @generated
	 */
	MPartSashContainer createPartSashContainer();

	/**
	 * Returns a new object of class '<em>Window</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Window</em>'.
	 * @since 1.0
	 * @generated
	 */
	MWindow createWindow();

	/**
	 * Returns a new object of class '<em>Trimmed Window</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Trimmed Window</em>'.
	 * @since 1.0
	 * @generated
	 */
	MTrimmedWindow createTrimmedWindow();

	/**
	 * Returns a new object of class '<em>Trim Bar</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Trim Bar</em>'.
	 * @since 1.0
	 * @generated
	 */
	MTrimBar createTrimBar();

	/**
	 * Returns a new object of class '<em>Dialog</em>'. <!-- begin-user-doc -->
	 * 
	 * @since 1.1 <!-- end-user-doc -->
	 * @return a new object of class '<em>Dialog</em>'.
	 * @since 1.1
	 * @generated NOT
	 * @deprecated
	 * @noreference This method is not intended to be referenced by clients.
	 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=531054">Bug
	 *      531054</a>
	 */
	@Deprecated
	MDialog createDialog();

	/**
	 * Returns a new object of class '<em>Wizard Dialog</em>'. <!-- begin-user-doc
	 * -->
	 * 
	 * @since 1.1 <!-- end-user-doc -->
	 * @return a new object of class '<em>Wizard Dialog</em>'.
	 * @since 1.1
	 * @generated NOT
	 * @deprecated
	 * @noreference This method is not intended to be referenced by clients.
	 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=531054">Bug
	 *      531054</a>
	 */
	@Deprecated
	MWizardDialog createWizardDialog();

} //MBasicFactory
