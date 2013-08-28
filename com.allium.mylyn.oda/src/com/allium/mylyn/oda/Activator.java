package com.allium.mylyn.oda;

import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static ITaskList taskList;

	private static TaskDataManager taskDataManager;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	public static ITaskList getTaskList() {
		return taskList;
	}

	public static void setTaskList(ITaskList tasks) {
		taskList = tasks;
	}

	public static TaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

	public static void setTaskDataManager(TaskDataManager taskManager) {
		taskDataManager = taskManager;
	}

}
