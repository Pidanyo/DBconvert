package com.vae.dao;

import java.sql.SQLException;
import java.util.List;

import com.vae.bean.Column;

public interface ColumnDao {

	// ��������
	public List<Column> findAll(String table) throws SQLException;
}
