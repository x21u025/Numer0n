package dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class BaseDao {

	protected JdbcTemplate jdbcTemplate = this.getJdbcTemplate();

	private JdbcTemplate getJdbcTemplate() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName(org.h2.Driver.class.getName());
		driverManagerDataSource.setUrl("jdbc:h2:file:./numer0n;MODE=MySQL;NON_KEYWORDS=USER");
		driverManagerDataSource.setUsername("sa");
		driverManagerDataSource.setPassword("");
		return new JdbcTemplate(driverManagerDataSource);
	}
}
