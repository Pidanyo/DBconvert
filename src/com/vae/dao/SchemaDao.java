package com.vae.dao;

import java.sql.SQLException;
import java.util.List;

import com.vae.bean.Schema;

public interface SchemaDao {

	// ��������
	public List<Schema> findAll() throws SQLException;

	
}
