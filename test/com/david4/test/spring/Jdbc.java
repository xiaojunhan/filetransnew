package com.david4.test.spring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;

public class Jdbc {
	static JdbcTemplate jdbcTemplate = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();
//		String str = jdbcTemplate.queryForObject("select remark from ac_sam where sam_id='70500273'",String.class);
//		System.out.println(str);
//		jdbcTemplate.g
		int i = jdbcTemplate.queryForInt("select count(*) from ac_sam where sam_id='70500273'");
		System.out.println(i);
		jdbcTemplate.setFetchSize(0);
//		List<String> list = jdbcTemplate.queryForList("select sam_id,sam_type,remark from ac_sam", String.class);
//		
//		for(String a:list){
//			System.out.println(a);
//		}
//		String sql = "select sam_id,sam_type,remark from ac_sam";
//		RowMapper<AC_SAM> rowMapper= new RowMapper<AC_SAM>() {
//	            public AC_SAM mapRow(ResultSet rs, int rowNum) throws SQLException {
//	            	AC_SAM actor = new AC_SAM();
//	            	actor.setSam_id(rs.getString("SAM_ID"));
//	            	actor.setSam_type(rs.getString("SAM_TYPE"));
//	            	actor.setRemark(rs.getString("REMARK"));
//	                return actor;
//	            }
//	        };
//	        List<AC_SAM> list = jdbcTemplate.query(sql, rowMapper);
//	         
//		for(AC_SAM a:list){
//			System.out.println(a);
//		}
	}

	public static void test(){
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@10.1.28.3:1521:oltp");
		dataSource.setUsername("card");
		dataSource.setPassword("card");
		//dataSource.getConnection(arg0)
		jdbcTemplate = new JdbcTemplate(dataSource);
		//SimpleJdbcTemplate sjt = new SimpleJdbcTemplate(dataSource);
	}
	
	
}
