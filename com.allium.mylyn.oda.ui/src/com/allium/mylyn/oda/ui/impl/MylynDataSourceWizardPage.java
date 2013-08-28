package com.allium.mylyn.oda.ui.impl;

import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePageHelper;
import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourceWizardPage;

/**
 * @author Guillermo Zunino
 *
 */
public class MylynDataSourceWizardPage extends DefaultDataSourceWizardPage {

	/**
	 * @param pageName
	 */
	public MylynDataSourceWizardPage(String pageName) {
		super(pageName);
	}
	
	@Override
	protected DefaultDataSourcePageHelper createDataSourcePageHelper() {
		return new MylynDataSourcePageHelper(this);
	}

}
