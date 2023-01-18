/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.sample.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;FOO_Foo&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see Foo
 * @generated
 */
public class FooTable extends BaseTable<FooTable> {

	public static final FooTable INSTANCE = new FooTable();

	public final Column<FooTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FooTable, Long> fooId = createColumn(
		"fooId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<FooTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FooTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FooTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FooTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FooTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<FooTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<FooTable, String> field1 = createColumn(
		"field1", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FooTable, Boolean> field2 = createColumn(
		"field2", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<FooTable, Integer> field3 = createColumn(
		"field3", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<FooTable, Date> field4 = createColumn(
		"field4", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<FooTable, String> field5 = createColumn(
		"field5", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private FooTable() {
		super("FOO_Foo", FooTable::new);
	}

}