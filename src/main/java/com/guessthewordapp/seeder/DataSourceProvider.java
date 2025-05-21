package com.guessthewordapp.seeder;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;  // потрібно імпортувати!

public class DataSourceProvider {

    private static final HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:data/guesstheword.db");
        config.setMaximumPoolSize(5);
        config.setConnectionTestQuery("SELECT 1");
        ds = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return ds;  // повертаємо HikariDataSource як DataSource
    }
}
