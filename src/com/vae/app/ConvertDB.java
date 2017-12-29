package com.vae.app;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.vae.bean.Column;
import com.vae.bean.Schema;
import com.vae.convert.KeyDict;
import com.vae.dao.ColumnDaoImpl;
import com.vae.dao.SchemaDaoImpl;

/**
 * ����MYSQL�⣬������Ӧ��ORACLE����ű�
 * 
 * */
public class ConvertDB {

	public static void main(String[] args) throws SQLException {

		/** CREATE TABLE t1(
			id varchar2(32) primary key,
			name VARCHAR2(32) ,
			age VARCHAR2(32) 
			);
			COMMENT ON table t1 IS '������Ϣ';
			comment on column t1.id is 'id';
			comment on column t1.name is '����';
			comment on column t1.age is '����';*/
		SchemaDaoImpl schemadao = new SchemaDaoImpl();
		ColumnDaoImpl columndao = new ColumnDaoImpl();
		List<Schema> schemas = schemadao.findAll();

		File file = new File("d:/jl.sql");
		for(int i = 0; i < schemas.size(); i++){
			StringBuffer csql = new StringBuffer();//create table�ű�
			StringBuffer osql = new StringBuffer();//ע�ͽű�
			Schema schema = schemas.get(i);
			csql.append("----------No"+(i+1)+"����ʼ������"+schema.getTable_name()+"\n");
			csql.append("create table "+schema.getTable_name()+"(");
			try {
				List<Column> columns = columndao.findAll(schema.getTable_name());
			for(int j = 0; j < columns.size(); j++){
				Column column = columns.get(j);
				String mysqlKey = column.getData_type().toUpperCase();
				
				int mysqlKeyLen = Integer.valueOf(column.getColumn_length()==null?"0":column.getColumn_length());
				String oraKey = KeyDict.getMap().get(mysqlKey);
				if(mysqlKey.equals("VARCHAR") && mysqlKeyLen<4000){
					oraKey = oraKey.split(",")[0];
				}else if(mysqlKey.equals("VARCHAR") && mysqlKeyLen>=4000){
					oraKey = oraKey.split(",")[1];
				}
				
				if(mysqlKeyLen == 0){
					csql.append(column.getColumn_name()+" "+oraKey);
				}else{
					if(oraKey.equals("CLOB")){
						csql.append(column.getColumn_name()+" "+oraKey);
					}else{
						csql.append(column.getColumn_name()+" "+oraKey+"("+2*mysqlKeyLen+") ");
					}
				}
				csql = column.getColumn_name().equals("id")?csql.append("primary key,"):csql.append(",");//�Ƿ�Ϊ����
				
				//�ֶ�ע��
				osql.append("comment on column "+schema.getTable_name()+"."+column.getColumn_name()+" is '"+column.getColumn_comment()+"';\n");
			}
			csql = csql.deleteCharAt(csql.lastIndexOf(",")).append(");\n");
			
			//��ӱ�ע��
			csql.append("comment on table "+schema.getTable_name()+" is '"+schema.getTable_comment()+"';\n");
			//����ֶ�ע��
			csql.append(osql.toString());
			
//			String[] o = csql.toString().split(";");
//			for(int j = 0; j < o.length; j++){
//				System.out.println("execute: "+o[j]);
////				DDLDaoImpl.execute(o[j]);
//			}
			FileUtils.writeByteArrayToFile(file, csql.toString().getBytes(), true);
			System.out.println(schema.getTable_name()+"�������");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
