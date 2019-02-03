package com.helloingob.gifter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.utilities.SharedSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariDatabasePool {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private static HikariDatabasePool instance = null;
    private HikariDataSource dataSource = null;

    static {
        try {
            instance = new HikariDatabasePool();
        } catch (Exception e) {
            new ErrorLogHandler().writeFile(e, logger);
        }
    }

    public HikariDatabasePool() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(5);
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("prepStmtCacheSize", 300);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("characterEncoding", "utf-8");
        config.addDataSourceProperty("serverName", SharedSettings.Database.SERVER);
        config.addDataSourceProperty("port", SharedSettings.Database.PORT);
        config.addDataSourceProperty("databaseName", SharedSettings.Database.DATABASE);
        config.addDataSourceProperty("user", SharedSettings.Database.USERNAME);
        config.addDataSourceProperty("password", SharedSettings.Database.PASSWORD);
        dataSource = new HikariDataSource(config);
    }

    public static HikariDatabasePool getInstance() {
        return instance;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            new ErrorLogHandler().writeFile(e, logger);
        }
        return null;
    }

    public void close(PreparedStatement preparedStatement, ResultSet resultSet, Connection connection) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (resultSet != null) {
                resultSet.close();
            }

            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            new ErrorLogHandler().writeFile(e, logger);
        }
    }
}
