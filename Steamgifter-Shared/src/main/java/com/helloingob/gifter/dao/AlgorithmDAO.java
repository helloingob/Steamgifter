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
import com.helloingob.gifter.to.AlgorithmTO;
import com.helloingob.gifter.utilities.SharedSettings;

public class AlgorithmDAO {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private HikariDatabasePool hikariPool;
    private final static String TABLE_NAME = "algorithm ";

    public AlgorithmDAO() {
        hikariPool = HikariDatabasePool.getInstance();
    }

    public List<AlgorithmTO> get() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<AlgorithmTO> algorithms = new LinkedList<>();
        try {
            connection = hikariPool.getConnection();
            String query = "SELECT * FROM " + TABLE_NAME;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                AlgorithmTO algorithm = new AlgorithmTO();

                algorithm.setPk(resultSet.getInt("pk"));
                algorithm.setName(resultSet.getString("name"));
                algorithm.setDescription(resultSet.getString("description"));

                algorithms.add(algorithm);
            }
            return algorithms;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<>();
    }

    public AlgorithmTO get(int algorithmPk) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = hikariPool.getConnection();
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE pk = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, algorithmPk);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.first()) {
                AlgorithmTO algorithm = new AlgorithmTO();
                algorithm.setPk(resultSet.getInt("pk"));
                algorithm.setName(resultSet.getString("name"));
                algorithm.setDescription(resultSet.getString("description"));
                return algorithm;
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }

        return null;
    }
}
