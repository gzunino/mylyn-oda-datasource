package com.allium.mylyn.oda;

import static org.fest.assertions.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Before;
import org.junit.Test;

import com.allium.mylyn.oda.impl.ResultSetMetaData;

public class ResultSetMetaDataTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMetadataOneTask() throws OdaException {
		TaskData task1 = getTaskData("task1");
		addTaskAttribute(task1.getRoot(), "attr1", "shortText", "val1");
		addTaskAttribute(task1.getRoot(), "attr2", "longText", "val2");
		
		ResultSetMetaData md = new ResultSetMetaData(asTaskList(task1));
		
		assertThat(md.getColumnCount()).as("Should have 2 columns").isEqualTo(2);
		assertMetaData(md, 1, "attr1", "shortText");
		assertMetaData(md, 2, "attr2", "longText");
	}

	@Test
	public void testMetadataNormalized() throws OdaException {
		TaskData task1 = getTaskData("task1");
		addTaskAttribute(task1.getRoot(), "attr1", "shortText", "val1");
		TaskAttribute attr = addTaskAttribute(task1.getRoot(), "attr2", "longText", "val2");
		addTaskAttribute(attr, "nested", "shortText", "val22");

		TaskData task2 = getTaskData("task2");
		addTaskAttribute(task2.getRoot(), "attr1", "shortText", "val3");
		attr = addTaskAttribute(task2.getRoot(), "attr2", "longText", "val4");
		addTaskAttribute(attr, "nested", "shortText", "val22");
		
		ResultSetMetaData md = new ResultSetMetaData(asTaskList(task1, task2));
		assertThat(md.getColumnCount()).as("Should have 3 columns").isEqualTo(3);
		
		assertMetaData(md, 1, "attr1", "shortText");
		assertMetaData(md, 2, "attr2", "longText");
		assertMetaData(md, 3, "attr2:::nested", "shortText");
	}

	@Test
	public void testMetadataMixed() throws OdaException {
		TaskData task1 = getTaskData("task1");
		addTaskAttribute(task1.getRoot(), "attr1", "shortText", "val1");
		addTaskAttribute(task1.getRoot(), "attr2", "longText", "val2");
		addTaskAttribute(task1.getRoot(), "attr4", "longText", "val2");
		
		TaskData task2 = getTaskData("task2");
		addTaskAttribute(task2.getRoot(), "attr3", "shortText", "val3");
		addTaskAttribute(task2.getRoot(), "attr2", "longText", "val4");
		addTaskAttribute(task2.getRoot(), "attr5", "longText", "val5");
		
		ResultSetMetaData md = new ResultSetMetaData(asTaskList(task1, task2));
		assertThat(md.getColumnCount()).as("Should have 4 columns").isEqualTo(5);
		
		assertMetaData(md, 1, "attr1", "shortText");
		assertMetaData(md, 2, "attr2", "longText");
		assertMetaData(md, 3, "attr3", "shortText");
		assertMetaData(md, 4, "attr5", "longText");
		assertMetaData(md, 5, "attr4", "longText");
	}
	
	private void assertMetaData(ResultSetMetaData md, int index, String name, String type) {
		try {
			assertThat(md.getColumnIndex(name)).isEqualTo(index);
			String[] names = name.split(":::");
			assertThat(md.getColumnLabel(index)).isEqualTo(firstCase(names[names.length-1]));
			assertThat(md.getColumnName(index)).isEqualTo(name.toLowerCase());
			assertThat(md.getColumnType(index)).isEqualTo(ResultSetMetaData.typesMap.get(type));
			assertThat(md.getColumnTypeName(index)).isEqualTo(type);
		} catch (OdaException e) {
			fail("Exception on ResultSetMetaData", e);
		}
	}

	private List<TaskData> asTaskList(TaskData... task) {
		return Arrays.asList(task);
	}

	public TaskRepository getRepository() {
		return new TaskRepository("podio", "podio.com");
	}

	public TaskAttributeMapper getAttributeMapper() {
		TaskRepository repository = getRepository();
		return new TaskAttributeMapper(repository);
	}
	
	public TaskData getTaskData(String id) {
		TaskRepository repository = getRepository();
		return new TaskData(getAttributeMapper(), "TEST_CONNECTOR",
				repository.getRepositoryUrl(), id); //$NON-NLS-1$
	}

	public TaskAttribute addTaskAttribute(TaskAttribute parent, String name, String type, String value) {
		TaskAttribute attribute = new TaskAttribute(parent, name);
		attribute.getMetaData().setType(type);
		attribute.getMetaData().setLabel(firstCase(name));
		attribute.setValue(value);
		return attribute;
	}

	private String firstCase(String name) {
		return name.toUpperCase().charAt(0) + name.substring(1);
	}

}
