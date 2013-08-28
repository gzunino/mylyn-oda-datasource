/*
 *************************************************************************
 * Copyright (c) 2013 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package com.allium.mylyn.oda.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Implementation class of IResultSetMetaData for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class ResultSetMetaData implements IResultSetMetaData
{
	public static final String ATTR_SEP = ":::";
	
	private List<TaskData> tasksData;
	private ArrayList<String> names;
	private ArrayList<String> types;
	private ArrayList<String> labels;
//	private ArrayList<String> typesMap;
	public static Map<String, Integer> typesMap = new HashMap<>();
	static {
		typesMap.put(TaskAttribute.TYPE_SHORT_TEXT, 1);
		typesMap.put(TaskAttribute.TYPE_SHORT_RICH_TEXT, 2);
		typesMap.put(TaskAttribute.TYPE_LONG_TEXT, 3);
		typesMap.put(TaskAttribute.TYPE_LONG_RICH_TEXT, 4);
		typesMap.put(TaskAttribute.TYPE_URL, 5);
		typesMap.put(TaskAttribute.TYPE_TASK_DEPENDENCY, 6);
		typesMap.put(TaskAttribute.TYPE_SINGLE_SELECT, 7);
		typesMap.put(TaskAttribute.TYPE_PERSON, 8);
		typesMap.put(TaskAttribute.TYPE_OPERATION, 9);
		typesMap.put(TaskAttribute.TYPE_MULTI_SELECT, 10);
		typesMap.put(TaskAttribute.TYPE_LONG, 11);
		typesMap.put(TaskAttribute.TYPE_INTEGER, 12);
		typesMap.put(TaskAttribute.TYPE_DOUBLE, 13);
		typesMap.put(TaskAttribute.TYPE_DATETIME, 14);
		typesMap.put(TaskAttribute.TYPE_DATE, 15);
		typesMap.put(TaskAttribute.TYPE_CONTAINER, 16);
		typesMap.put(TaskAttribute.TYPE_COMMENT, 17);
		typesMap.put(TaskAttribute.TYPE_BOOLEAN, 18);
		typesMap.put(TaskAttribute.TYPE_ATTACHMENT, 19);
	}

	public ResultSetMetaData(List<TaskData> tasksData) {
		this.tasksData = tasksData;
		buildMetadata();
	}

	private void buildMetadata() {
		names = new ArrayList<String>(50);
		labels = new ArrayList<String>(50);
		types = new ArrayList<String>(50);
		
		for (TaskData taskData : tasksData) {
			if (taskData.getRoot() != null) {
				Map<String, TaskAttribute> attrs = taskData.getRoot().getAttributes();
				
				int pos = 1;
				processAttributes(attrs.values(), "", pos);
			}
		}
	}
	
	private void processAttributes(Collection<TaskAttribute> attrs, String prefix, int pos) {
		for (TaskAttribute attr : attrs) {
//			String kind = attr.getMetaData().getKind();
			String id = (prefix.isEmpty()) ? attr.getId() : prefix + ATTR_SEP + attr.getId();
			int index = names.indexOf(id);
			if (index == -1) {
				if (names.size() == 0) {
					pos = 0;
				} else {
					pos = pos + 1;
				}
				names.ensureCapacity(pos + 1);
				labels.ensureCapacity(pos + 1);
				types.ensureCapacity(pos + 1);
				names.add(pos, id);
				labels.add(pos, attr.getMetaData().getLabel());
				types.add(pos, attr.getMetaData().getType());
			}
			processAttributes(attr.getAttributes().values(), id, pos);
//			if (!typesMap.contains(attr.getMetaData().getType())) {
//				typesMap.add(attr.getMetaData().getType());
//			}
		}
	}
	
	public int getColumnIndex(String columnName) {
		return this.names.indexOf(columnName)+1;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnCount()
	 */
	public int getColumnCount() throws OdaException
	{
        return names.size();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnName(int)
	 */
	public String getColumnName( int index ) throws OdaException
	{
        return names.get(index-1);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnLabel(int)
	 */
	public String getColumnLabel( int index ) throws OdaException
	{
		return labels.get(index-1);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnType(int)
	 */
	public int getColumnType( int index ) throws OdaException
	{
		// as defined in data set extension manifest
		Integer type = typesMap.get(types.get(index-1));
		if (type == null) {
			type = typesMap.get(TaskAttribute.TYPE_SHORT_TEXT);
		}
		return type;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnTypeName(int)
	 */
	public String getColumnTypeName( int index ) throws OdaException
	{
//        int nativeTypeCode = getColumnType( index );
//        return Driver.getNativeDataTypeName( nativeTypeCode );
		return types.get(index-1);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnDisplayLength(int)
	 */
	public int getColumnDisplayLength( int index ) throws OdaException
	{
		return 20;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getPrecision(int)
	 */
	public int getPrecision( int index ) throws OdaException
	{
        // TODO Auto-generated method stub
		return -1;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getScale(int)
	 */
	public int getScale( int index ) throws OdaException
	{
        // TODO Auto-generated method stub
		return -1;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#isNullable(int)
	 */
	public int isNullable( int index ) throws OdaException
	{
        // TODO Auto-generated method stub
		return IResultSetMetaData.columnNullableUnknown;
	}
    
}
