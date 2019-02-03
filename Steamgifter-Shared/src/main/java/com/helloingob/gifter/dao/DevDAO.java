package com.helloingob.gifter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.HikariDatabasePool;
import com.helloingob.gifter.utilities.SharedSettings;

public class DevDAO {

    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private HikariDatabasePool hikariPool;
    private final static String TABLE_NAME = "info_log";

    public DevDAO() {
        hikariPool = HikariDatabasePool.getInstance();
    }

    public Integer getLatestTwoPollingsCount() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = hikariPool.getConnection();
            String query = "SELECT COUNT(*) AS count FROM " + TABLE_NAME + " WHERE date > DATE_SUB(NOW(), INTERVAL 2 HOUR)";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                return resultSet.getInt("count");
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return null;
    }

}
