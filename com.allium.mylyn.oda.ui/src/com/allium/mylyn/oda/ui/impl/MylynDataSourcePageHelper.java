/**
 * 
 */
package com.allium.mylyn.oda.ui.impl;

import java.util.Properties;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.ui.nls.Messages;
import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePageHelper;
import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePropertyPage;
import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourceWizardPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.EmptyCategoriesFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesViewSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * @author guille
 *
 */
public class MylynDataSourcePageHelper extends DefaultDataSourcePageHelper {

	private TaskRepositoriesContentProvider contentProvider;
	private TreeViewer viewer;

	/**
	 * @param page
	 */
	public MylynDataSourcePageHelper(DefaultDataSourceWizardPage page) {
		super(page);
	}

	/**
	 * @param page
	 */
	public MylynDataSourcePageHelper(DefaultDataSourcePropertyPage page) {
		super(page);
	}

	@Override
	protected void createCustomControl(Composite parent) throws OdaException {
		super.createCustomControl(parent);
//		Composite composite = new Composite(parent, SWT.NONE);
//		composite.setLayout(new RowLayout(SWT.VERTICAL));
		
		
		// create mylyn repo viewer
		contentProvider = new TaskRepositoriesContentProvider();
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
		
		viewer.setContentProvider(contentProvider);
		viewer.setUseHashlookup(true);
		ViewerFilter[] filters = { new EmptyCategoriesFilter(contentProvider) };
		viewer.setFilters(filters);
		viewer.setLabelProvider(new DecoratingLabelProvider(new TaskRepositoryLabelProvider(),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

		viewer.setSorter(new TaskRepositoriesViewSorter());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				validatePropertyFields();
			}
		});

		viewer.setInput(this);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				//WorkbenchUtil.openProperties(getSite());
			}
		});

//		final IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
//		new GradientDrawer(themeManager, getViewer()) {
//			@Override
//			protected boolean shouldApplyGradient(org.eclipse.swt.widgets.Event event) {
//				return event.item.getData() instanceof Category;
//			}
//		};

		viewer.expandAll();
	}
	
	@Override
	protected Properties collectCustomProperties(Properties props) {
		if( props == null )
            props = new Properties();
		
		if (selectedRepository() != null) {
			props.put("repositoryUrl", selectedRepository().getRepositoryUrl());
			props.put("repositoryKind", selectedRepository().getConnectorKind());
		}
		return props;
	}
	
	private TaskRepository selectedRepository() {
		IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
		TaskRepository repository = (TaskRepository) selection.getFirstElement();
		return repository;
	}

	@Override
	protected void validatePropertyFields() {
		TaskRepository propVal = selectedRepository();
		if ( propVal == null ) {
            setMessage( Messages.ui_requiredFieldsMissingValue, 
            		IMessageProvider.ERROR );
            return;
        }
		setMessage( DEFAULT_MESSAGE, IMessageProvider.NONE );
	}
}
