package com.helloingob.gifter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.HikariDatabasePool;
import com.helloingob.gifter.to.CachedGameTO;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.TimeHelper;
import com.mysql.jdbc.Statement;

public class CachedGameDAO {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private HikariDatabasePool hikariPool;
    private final static String TABLE_NAME = " cached_game ";

    public CachedGameDAO() {
        hikariPool = HikariDatabasePool.getInstance();
    }

    public boolean save(Integer appId, Boolean isDlc) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("INSERT INTO ");
            query.append(TABLE_NAME);
            query.append("(app_id, is_dlc, date) ");
            query.append("VALUES ");
            query.append("(?, ?, ?);");
            preparedStatement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, appId);
            preparedStatement.setBoolean(2, isDlc);
            preparedStatement.setTimestamp(3, TimeHelper.getCurrentTimestamp());
            int manipulatedRows = preparedStatement.executeUpdate();
            if (manipulatedRows > 0) {
                return true;
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public Boolean get(Integer appId) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = hikariPool.getConnection();
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE app_id = ? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, appId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                return resultSet.getBoolean("is_dlc");
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return null;
    }

    public Integer deleteDuplicates() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = hikariPool.getConnection();
            String query = "DELETE n1 FROM " + TABLE_NAME + " n1, " + TABLE_NAME + " n2 WHERE n1.pk < n2.pk AND n1.app_id = n2.app_id";
            preparedStatement = connection.prepareStatement(query);
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return null;
    }

    public List<CachedGameTO> getList() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<CachedGameTO> cachedGames = new LinkedList<CachedGameTO>();
        try {
            connection = hikariPool.getConnection();
            String query = " SELECT * FROM " + TABLE_NAME;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CachedGameTO cachedGame = new CachedGameTO();
                cachedGame.setPk(resultSet.getInt("pk"));
                cachedGame.setAppId(resultSet.getInt("app_id"));
                cachedGame.setIsDlc(resultSet.getBoolean("is_dlc"));
                cachedGame.setDate(resultSet.getTimestamp("date"));
                cachedGames.add(cachedGame);
            }
            return cachedGames;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<CachedGameTO>();
    }
}
