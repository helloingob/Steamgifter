package com.helloingob.gifter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.HikariDatabasePool;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.TimeHelper;
import com.mysql.jdbc.Statement;

public class UserDAO {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private HikariDatabasePool hikariPool;
    private final static String TABLE_NAME = "user ";

    public UserDAO() {
        hikariPool = HikariDatabasePool.getInstance();
    }

    public boolean save(UserTO user) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("INSERT INTO ");
            query.append(TABLE_NAME);
            query.append("(login_name, profile_name, steam_id, password, notification_email, image_link, phpsessionid, skip_dlc, skip_sub, skip_wishlist, created_date, last_login, is_active, is_admin, algorithm_pk) ");
            query.append("VALUES ");
            query.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getLoginName());
            preparedStatement.setObject(2, user.getProfileName());
            preparedStatement.setObject(3, user.getSteamId());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setObject(5, user.getNotificationEmail());
            preparedStatement.setObject(6, user.getImageLink());
            preparedStatement.setString(7, user.getPhpsessionid());
            preparedStatement.setBoolean(8, user.getSkipDlc());
            preparedStatement.setBoolean(9, user.getSkipSub());
            preparedStatement.setBoolean(10, user.getSkipWishlist());
            preparedStatement.setTimestamp(11, TimeHelper.getCurrentTimestamp());
            preparedStatement.setObject(12, user.getLastLogin().orElse(null));
            preparedStatement.setBoolean(13, user.getIsActive());
            preparedStatement.setBoolean(14, user.getIsAdmin());
            preparedStatement.setInt(15, user.getAlgorithmPk());
            int manipulatedRows = preparedStatement.executeUpdate();
            if (manipulatedRows > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                user.setPk(rs.getInt(1));
                return true;
            }
        } catch (Exception e) {
            new ErrorLogHandler(user).error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public boolean update(UserTO user) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("UPDATE ");
            query.append(TABLE_NAME);
            query.append("SET ");
            query.append("login_name = ?, ");
            query.append("profile_name = ?, ");
            query.append("steam_id = ?, ");
            query.append("password = ?, ");
            query.append("notification_email = ?, ");
            query.append("image_link = ?, ");
            query.append("phpsessionid = ?, ");
            query.append("skip_dlc = ?, ");
            query.append("skip_sub = ?, ");
            query.append("skip_wishlist = ?, ");
            query.append("created_date = ?, ");
            query.append("last_login = ?, ");
            query.append("is_active = ?, ");
            query.append("is_admin = ?, ");
            query.append("algorithm_pk = ? ");
            query.append("WHERE ");
            query.append("pk = ?;");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, user.getLoginName());
            preparedStatement.setObject(2, user.getProfileName());
            preparedStatement.setObject(3, user.getSteamId());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setObject(5, user.getNotificationEmail());
            preparedStatement.setObject(6, user.getImageLink());
            preparedStatement.setString(7, user.getPhpsessionid());
            preparedStatement.setBoolean(8, user.getSkipDlc());
            preparedStatement.setBoolean(9, user.getSkipSub());
            preparedStatement.setBoolean(10, user.getSkipWishlist());
            preparedStatement.setTimestamp(11, user.getCreatedDate());
            preparedStatement.setObject(12, user.getLastLogin().orElse(null));
            preparedStatement.setBoolean(13, user.getIsActive());
            preparedStatement.setBoolean(14, user.getIsAdmin());
            preparedStatement.setInt(15, user.getAlgorithmPk());
            preparedStatement.setInt(16, user.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler(user).error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public boolean updateLastLogin(UserTO user) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("UPDATE ");
            query.append(TABLE_NAME);
            query.append("SET ");
            query.append("last_login = ? ");
            query.append("WHERE ");
            query.append("pk = ?;");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setObject(1, user.getLastLogin().orElse(null));
            preparedStatement.setInt(2, user.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler(user).error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public List<UserTO> get() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<UserTO> users = new LinkedList<>();
        try {
            connection = hikariPool.getConnection();
            String query = getGetStatement(null);
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(createTransferObject(resultSet));
            }
            return users;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<>();
    }

    public List<UserTO> getInactiveOrdered() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<UserTO> users = new LinkedList<>();
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" SELECT u.*, COUNT(wg.pk) AS won_giveaways, (SELECT level FROM user_asset WHERE user_pk = u.pk ORDER BY date DESC LIMIT 1) AS current_level ");
            stringBuilder.append(" FROM user u ");
            stringBuilder.append(" LEFT JOIN won_giveaway wg ON u.pk = wg.user_pk ");
            stringBuilder.append(" GROUP BY u.pk ");
            stringBuilder.append(" ORDER BY is_active DESC ");
            String query = stringBuilder.toString();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(createTransferObject(resultSet));
            }
            return users;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<>();
    }

    public UserTO get(int pk) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = hikariPool.getConnection();
            String query = getGetStatement("u.pk = ?");
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pk);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                return createTransferObject(resultSet);
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return null;
    }

    public Optional<UserTO> login(String loginName, String password) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = hikariPool.getConnection();
            String query = getGetStatement("lower(u.login_name) = ? AND u.password = ?");
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, loginName);
            preparedStatement.setString(2, password);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                return Optional.of(createTransferObject(resultSet));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.empty();
    }

    private String getGetStatement(String whereClause) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(" SELECT u.*, COUNT(wg.pk) AS won_giveaways, (SELECT level FROM user_asset WHERE user_pk = u.pk ORDER BY date DESC LIMIT 1) AS current_level ");
        stringBuilder.append(" FROM user u ");
        stringBuilder.append(" LEFT JOIN won_giveaway wg ON u.pk = wg.user_pk ");
        if (whereClause != null) {
            stringBuilder.append(" WHERE ");
            stringBuilder.append(whereClause);
        }
        stringBuilder.append(" GROUP BY u.pk ");

        return stringBuilder.toString();
    }

    public boolean delete(UserTO user) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("DELETE FROM ");
            query.append(TABLE_NAME);
            query.append("WHERE ");
            query.append("pk = ?;");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setInt(1, user.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler(user).error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    private UserTO createTransferObject(ResultSet resultSet) throws Exception {
        UserTO user = new UserTO();

        user.setPk(resultSet.getInt("pk"));
        user.setLoginName(resultSet.getString("login_name"));
        user.setProfileName((String) resultSet.getObject("profile_name"));
        user.setSteamId((Long) resultSet.getObject("steam_id"));
        user.setPassword(resultSet.getString("password"));
        user.setNotificationEmail((String) resultSet.getObject("notification_email"));
        user.setImageLink((String) resultSet.getObject("image_link"));
        user.setPhpsessionid(resultSet.getString("phpsessionid"));
        user.setSkipDlc(resultSet.getBoolean("skip_dlc"));
        user.setSkipSub(resultSet.getBoolean("skip_sub"));
        user.setSkipWishlist(resultSet.getBoolean("skip_wishlist"));
        user.setCreatedDate(resultSet.getTimestamp("created_date"));
        user.setLastLogin((Timestamp) resultSet.getObject("last_login"));
        user.setIsActive(resultSet.getBoolean("is_active"));
        user.setIsAdmin(resultSet.getBoolean("is_admin"));
        user.setAlgorithmPk(resultSet.getInt("algorithm_pk"));
        user.setWonGiveaways(resultSet.getInt("won_giveaways"));
        user.setCurrentLevel(resultSet.getDouble("current_level"));

        return user;
    }
}
