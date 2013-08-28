/*
 *************************************************************************
 * Copyright (c) 2013 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package com.allium.mylyn.oda.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.impl.Blob;
import org.eclipse.datatools.connectivity.oda.impl.Clob;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Implementation class of IResultSet for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
@SuppressWarnings("restriction")
public class ResultSet implements IResultSet
{
	private int m_maxRows;
    private int m_currentRowId = 0;
	private ResultSetMetaData resultMetaData;
	private TaskDataManager taskDataManager;
	private RepositoryQuery repositoryQuery;
	private ArrayList<ITask> tasks;
	private ITask task;
	private TaskData taskData;
	private boolean wasNull;
	
	public ResultSet(ResultSetMetaData metaData, RepositoryQuery repositoryQuery, TaskDataManager taskDataManager) {
		this.resultMetaData = metaData;
		this.repositoryQuery = repositoryQuery;
		this.taskDataManager = taskDataManager;
		this.tasks = new ArrayList<>(repositoryQuery.getChildren());
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getMetaData()
	 */
	public IResultSetMetaData getMetaData() throws OdaException
	{
		return resultMetaData;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#setMaxRows(int)
	 */
	public void setMaxRows( int max ) throws OdaException
	{
		m_maxRows = max;
	}
	
	/**
	 * Returns the maximum number of rows that can be fetched from this result set.
	 * @return the maximum number of rows to fetch.
	 */
	protected int getMaxRows()
	{
		return m_maxRows;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#next()
	 */
	public boolean next() throws OdaException
	{
        int maxRows = getMaxRows();
        if( maxRows <= 0 || maxRows > tasks.size() )  // no limit is specified
            maxRows = tasks.size();    // hard-coded for demo purpose
        
        if( m_currentRowId < maxRows )
        {
        	task = tasks.get(getRow());
            try {
				taskData = taskDataManager.getTaskData(task);
			} catch (CoreException e) {
				throw new OdaException(e);
			}
            m_currentRowId++;
            return true;
        }
        
        return false;        
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#close()
	 */
	public void close() throws OdaException
	{
        // TODO Auto-generated method stub       
        m_currentRowId = 0;     // reset row counter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getRow()
	 */
	public int getRow() throws OdaException
	{
		return m_currentRowId;
	}

	/**
	 * @param index
	 * @return
	 * @throws OdaException
	 */
	private String columnName(int index) throws OdaException {
		return getMetaData().getColumnName(index);
	}

	/**
	 * @param columnName
	 * @return
	 * @throws OdaException 
	 */
	private TaskAttribute getAttribute(String columnName) throws OdaException {
		TaskAttribute attr = taskData.getRoot();
		String[] paths = columnName.split(ResultSetMetaData.ATTR_SEP);
		for (String path : paths) {
			if (attr == null) {
				return null;
			}
			attr = attr.getAttribute(path);
		}
		return attr;
	}

	/**
	 * @return
	 */
	private TaskAttributeMapper attributeMapper() {
		return taskData.getAttributeMapper();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(int)
	 */
	public String getString( int index ) throws OdaException
	{
		return getString(columnName(index));
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(java.lang.String)
	 */
	public String getString( String columnName ) throws OdaException
	{
		TaskAttribute attr = getAttribute(columnName);
		if (attr == null) {
			return "";
		}
		if (TaskAttribute.TYPE_PERSON.equals(attr.getMetaData().getType())) {
			IRepositoryPerson person = attributeMapper().getRepositoryPerson(attr);
			return (safe(person) != null) ? (safe(person.getPersonId()) != null ? person.getName() : person.getPersonId()) : "" ;
		}
		return safe(attributeMapper().getValueLabel(attr));
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(int)
	 */
	public int getInt( int index ) throws OdaException
	{
		return getInt(columnName(index));
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(java.lang.String)
	 */
	public int getInt( String columnName ) throws OdaException
	{
		TaskAttribute attr = getAttribute(columnName);
		if (attr == null) return -1;
		Integer val = attributeMapper().getIntegerValue(attr);
		return (safe(val) != null) ? val : -1;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDouble(int)
	 */
	public double getDouble( int index ) throws OdaException
	{
		return getDouble(columnName(index));
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDouble(java.lang.String)
	 */
	public double getDouble( String columnName ) throws OdaException
	{
		TaskAttribute attr = getAttribute(columnName);
		if (attr == null) return -1;
		Double val = attributeMapper().getDoubleValue(attr);
		return (safe(val) != null) ? val : -1;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal( int index ) throws OdaException
	{
		return getBigDecimal(columnName(index));
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal( String columnName ) throws OdaException
	{
		TaskAttribute attr = getAttribute(columnName);
		if (attr == null) return null;
		Double val = attributeMapper().getDoubleValue(attr);
		return (safe(val) != null) ? BigDecimal.valueOf(val) : null;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(int)
	 */
	public Date getDate( int index ) throws OdaException
	{
		return getDate(columnName(index));
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(java.lang.String)
	 */
	public Date getDate( String columnName ) throws OdaException
	{
		TaskAttribute attr = getAttribute(columnName);
		if (attr == null) return null;
		java.util.Date value = attributeMapper().getDateValue(attr);
		return (safe(value) != null) ? new Date(value.getTime()) : null;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTime(int)
	 */
	public Time getTime( int index ) throws OdaException
	{
		return getTime(columnName(index));
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTime(java.lang.String)
	 */
	public Time getTime( String columnName ) throws OdaException
	{
		TaskAttribute attr = getAttribute(columnName);
		if (attr == null) return null;
		java.util.Date val = attributeMapper().getDateValue(attr);
		return (safe(val) != null) ? new Time(val.getTime()) : null;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp( int index ) throws OdaException
	{
		return getTimestamp(columnName(index));
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp( String columnName ) throws OdaException
	{
		TaskAttribute attr = getAttribute(columnName);
		if (attr == null) return null;
		java.util.Date value = attributeMapper().getDateValue(attr);
		return safe(value) != null ? new Timestamp(value.getTime()) : null;
	}

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBlob(int)
     */
    public IBlob getBlob( int index ) throws OdaException
    {
    	return getBlob(columnName(index));
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBlob(java.lang.String)
     */
    public IBlob getBlob( String columnName ) throws OdaException
    {
    	byte[] byteArray = new byte[0];
		return new Blob(byteArray);
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getClob(int)
     */
    public IClob getClob( int index ) throws OdaException
    {
    	return getClob(columnName(index));
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getClob(java.lang.String)
     */
    public IClob getClob( String columnName ) throws OdaException
    {
    	return new Clob(getString(columnName));
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBoolean(int)
     */
    public boolean getBoolean( int index ) throws OdaException
    {
    	return getBoolean(columnName(index));
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBoolean(java.lang.String)
     */
    public boolean getBoolean( String columnName ) throws OdaException
    {
    	TaskAttribute attr = getAttribute(columnName);
		if (attr == null) return false;
    	return safe(attributeMapper().getBooleanValue(attr));
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getObject(int)
     */
    public Object getObject( int index ) throws OdaException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getObject(java.lang.String)
     */
    public Object getObject( String columnName ) throws OdaException
    {
        return safe(getObject( findColumn( columnName ) ));
    }

    private <T> T safe(T value) {
		wasNull = (value == null);
		return value;
	}

	/*
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#wasNull()
     */
    public boolean wasNull() throws OdaException
    {
        return wasNull;
    }

    /*
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#findColumn(java.lang.String)
     */
    public int findColumn( String columnName ) throws OdaException
    {
    	return resultMetaData.getColumnIndex(columnName);
    }
    
}
