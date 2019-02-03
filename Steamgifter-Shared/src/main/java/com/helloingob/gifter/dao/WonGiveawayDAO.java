package com.helloingob.gifter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.HikariDatabasePool;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.to.WonGiveawayDisplayTO;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.StringConverter;
import com.mysql.jdbc.Statement;

public class WonGiveawayDAO {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private HikariDatabasePool hikariPool;

    public WonGiveawayDAO() {
        hikariPool = HikariDatabasePool.getInstance();
    }

    public boolean save(WonGiveawayTO wonGiveaway) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("INSERT INTO ");
            query.append(" won_giveaway ");
            query.append("(title, points, copies, entries, win_chance, author, steam_store_price, giveaway_link, steam_link, image_link, level_requirement, steam_key, has_received, received_date, steam_activation_date, end_date, user_pk) ");
            query.append("VALUES ");
            query.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, StringConverter.removeBadChars(wonGiveaway.getTitle()));
            preparedStatement.setInt(2, wonGiveaway.getPoints());
            preparedStatement.setInt(3, wonGiveaway.getCopies());
            preparedStatement.setInt(4, wonGiveaway.getEntries());
            preparedStatement.setDouble(5, wonGiveaway.getWinChance());
            preparedStatement.setString(6, wonGiveaway.getAuthor());
            preparedStatement.setObject(7, wonGiveaway.getSteamStorePrice().orElse(null));
            preparedStatement.setString(8, wonGiveaway.getGiveawayLink());
            preparedStatement.setObject(9, wonGiveaway.getSteamLink().orElse(null));
            preparedStatement.setObject(10, wonGiveaway.getImageLink().orElse(null));
            preparedStatement.setInt(11, wonGiveaway.getLevelRequirement());
            preparedStatement.setObject(12, wonGiveaway.getSteamKey().orElse(null));
            preparedStatement.setObject(13, wonGiveaway.getHasReceived().orElse(null));
            preparedStatement.setObject(14, wonGiveaway.getReceivedDate().orElse(null));
            preparedStatement.setObject(15, wonGiveaway.getSteamActivationDate().orElse(null));
            preparedStatement.setTimestamp(16, wonGiveaway.getEndDate());
            preparedStatement.setInt(17, wonGiveaway.getUserPk());
            int manipulatedRows = preparedStatement.executeUpdate();
            if (manipulatedRows > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                wonGiveaway.setPk(rs.getInt(1));
                return true;
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, wonGiveaway.getGiveawayLink(), logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public boolean update(WonGiveawayTO wonGiveaway) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("UPDATE ");
            query.append(" won_giveaway ");
            query.append("SET ");
            query.append("title = ?, ");
            query.append("points = ?, ");
            query.append("copies = ?, ");
            query.append("entries = ?, ");
            query.append("win_chance = ?, ");
            query.append("author = ?, ");
            query.append("steam_store_price = ?, ");
            query.append("giveaway_link = ?, ");
            query.append("steam_link = ?, ");
            query.append("image_link = ?, ");
            query.append("level_requirement = ?, ");
            query.append("steam_key = ?, ");
            query.append("has_received = ?, ");
            query.append("received_date = ?, ");
            query.append("steam_activation_date = ?, ");
            query.append("end_date = ?, ");
            query.append("user_pk = ? ");
            query.append("WHERE ");
            query.append("pk = ?;");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, wonGiveaway.getTitle());
            preparedStatement.setInt(2, wonGiveaway.getPoints());
            preparedStatement.setInt(3, wonGiveaway.getCopies());
            preparedStatement.setInt(4, wonGiveaway.getEntries());
            preparedStatement.setDouble(5, wonGiveaway.getWinChance());
            preparedStatement.setString(6, wonGiveaway.getAuthor());
            preparedStatement.setObject(7, wonGiveaway.getSteamStorePrice().orElse(null));
            preparedStatement.setObject(8, wonGiveaway.getGiveawayLink());
            preparedStatement.setObject(9, wonGiveaway.getSteamLink().orElse(null));
            preparedStatement.setObject(10, wonGiveaway.getImageLink().orElse(null));
            preparedStatement.setInt(11, wonGiveaway.getLevelRequirement());
            preparedStatement.setObject(12, wonGiveaway.getSteamKey().orElse(null));
            preparedStatement.setObject(13, wonGiveaway.getHasReceived().orElse(null));
            preparedStatement.setObject(14, wonGiveaway.getReceivedDate().orElse(null));
            preparedStatement.setObject(15, wonGiveaway.getSteamActivationDate().orElse(null));
            preparedStatement.setTimestamp(16, wonGiveaway.getEndDate());
            preparedStatement.setInt(17, wonGiveaway.getUserPk());
            preparedStatement.setInt(18, wonGiveaway.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public List<WonGiveawayDisplayTO> getDisplayTOList() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<WonGiveawayDisplayTO> wonGiveaways = new LinkedList<WonGiveawayDisplayTO>();
        try {
            connection = hikariPool.getConnection();

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT ");
            stringBuilder.append(" u.login_name AS name, ");
            stringBuilder.append(" (SELECT COUNT(1) FROM won_giveaway WHERE user_pk = u.pk) AS wins, ");
            stringBuilder.append(" (SELECT pk FROM won_giveaway WHERE user_pk = u.pk ORDER BY end_date DESC LIMIT 1) AS last_game_pk, ");
            stringBuilder.append(" (SELECT title FROM won_giveaway WHERE user_pk = u.pk ORDER BY end_date DESC LIMIT 1) AS last_game, ");
            stringBuilder.append(" (SELECT end_date FROM won_giveaway WHERE user_pk = u.pk ORDER BY end_date DESC LIMIT 1) AS last_game_date, ");
            stringBuilder.append(" (SELECT SUM(steam_store_price) FROM won_giveaway WHERE user_pk = u.pk) AS sum_price ");
            stringBuilder.append(" FROM user u ");
            stringBuilder.append(" WHERE u.is_active ");
            stringBuilder.append(" ORDER BY wins DESC ");

            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                WonGiveawayDisplayTO displayTO = new WonGiveawayDisplayTO();

                displayTO.setName(resultSet.getString("name"));
                displayTO.setWins(resultSet.getInt("wins"));
                displayTO.setLastWonGamePk(resultSet.getInt("last_game_pk"));
                displayTO.setLastWonGame(resultSet.getString("last_game"));
                displayTO.setLastWonGameDate(resultSet.getTimestamp("last_game_date"));
                displayTO.setSumPrice(resultSet.getDouble("sum_price"));

                wonGiveaways.add(displayTO);
            }
            return wonGiveaways;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<WonGiveawayDisplayTO>();
    }

    public List<WonGiveawayTO> get() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<WonGiveawayTO> wonGiveaways = new LinkedList<WonGiveawayTO>();
        try {
            connection = hikariPool.getConnection();
            String query = getGetStatement(null);
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                wonGiveaways.add(createTransferObject(resultSet));
            }
            return wonGiveaways;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<WonGiveawayTO>();
    }

    public WonGiveawayTO get(Integer lastWonGamePk) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        WonGiveawayTO wonGiveaway = null;
        try {
            connection = hikariPool.getConnection();
            String query = getGetStatement("wg.pk = ?");
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, lastWonGamePk);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                wonGiveaway = createTransferObject(resultSet);
            }
            return wonGiveaway;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return null;
    }

    public List<WonGiveawayTO> get(UserTO user) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<WonGiveawayTO> wonGiveaways = new LinkedList<WonGiveawayTO>();
        try {
            connection = hikariPool.getConnection();
            String query = getGetStatement("wg.user_pk = ?");
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getPk());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                wonGiveaways.add(createTransferObject(resultSet));
            }
            return wonGiveaways;
        } catch (Exception e) {
            new ErrorLogHandler(user).error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<WonGiveawayTO>();
    }

    public List<WonGiveawayTO> getNotAcknowledged(UserTO user) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<WonGiveawayTO> wonGiveaways = new LinkedList<WonGiveawayTO>();
        try {
            connection = hikariPool.getConnection();
            String query = getGetStatement("wg.has_acknowledged = false AND wg.user_pk = ?");
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getPk());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                wonGiveaways.add(createTransferObject(resultSet));
            }
            return wonGiveaways;
        } catch (Exception e) {
            new ErrorLogHandler(user).error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<WonGiveawayTO>();
    }

    private String getGetStatement(String whereClause) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(" SELECT wg.*, u.login_name AS user_name ");
        stringBuilder.append(" FROM won_giveaway wg ");
        stringBuilder.append(" LEFT JOIN user u ON u.pk = wg.user_pk ");
        if (whereClause != null) {
            stringBuilder.append(" WHERE ");
            stringBuilder.append(whereClause);
        }
        stringBuilder.append(" ORDER BY wg.end_date DESC ");

        return stringBuilder.toString();
    }

    public List<WonGiveawayTO> getUnnoticed(UserTO user) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<WonGiveawayTO> wonGiveaways = new LinkedList<WonGiveawayTO>();
        try {
            connection = hikariPool.getConnection();
            String query = getGetStatement("wg.has_received IS NULL AND wg.user_pk = ?");
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getPk());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                wonGiveaways.add(createTransferObject(resultSet));
            }
            return wonGiveaways;
        } catch (Exception e) {
            new ErrorLogHandler(user).error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<WonGiveawayTO>();
    }

    public List<WonGiveawayTO> getUnnoticed() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<WonGiveawayTO> wonGiveaways = new LinkedList<WonGiveawayTO>();
        try {
            connection = hikariPool.getConnection();

            StringBuilder whereClause = new StringBuilder();
            whereClause.append(" (wg.has_received IS NULL OR (wg.has_received = 1 && wg.steam_activation_date IS NULL))");
            whereClause.append(" AND ");
            whereClause.append(" u.is_active = true AND wg.steam_link IS NOT NULL ");

            String query = getGetStatement(whereClause.toString());
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                wonGiveaways.add(createTransferObject(resultSet));
            }
            return wonGiveaways;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<WonGiveawayTO>();
    }

    public boolean delete(WonGiveawayTO wonGiveaway) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("DELETE FROM ");
            query.append(" won_giveaway ");
            query.append("WHERE ");
            query.append("pk = ?;");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setInt(1, wonGiveaway.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public Optional<Map<String, Object>> getSumWonGiveaways() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, Object> map = null;
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT SUM(steam_store_price) as sum ");
            stringBuilder.append(" FROM won_giveaway ");

            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                map = new HashMap<String, Object>();
                map.put("sum", resultSet.getDouble("sum"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.ofNullable(map);
    }

    public Optional<Map<String, Object>> getPoorBastard() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, Object> map = null;
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT u.login_name AS name, MAX(wg.end_date) AS DATE ");
            stringBuilder.append(" FROM won_giveaway AS wg ");
            stringBuilder.append(" LEFT JOIN user u ");
            stringBuilder.append(" ON wg.user_pk = u.pk ");
            stringBuilder.append(" WHERE u.is_active = true ");
            stringBuilder.append(" GROUP BY wg.user_pk ");
            stringBuilder.append(" ORDER BY date ASC ");
            stringBuilder.append(" LIMIT 1 ");

            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                map = new HashMap<String, Object>();
                map.put("name", resultSet.getString("name"));
                map.put("date", resultSet.getTimestamp("date"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.ofNullable(map);
    }

    public Optional<Map<String, Object>> getLuckyDevil() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, Object> map = null;
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT wg.title AS giveaway_title, wg.win_chance, u.login_name AS name, wg.steam_link, wg.giveaway_link ");
            stringBuilder.append(" FROM won_giveaway wg ");
            stringBuilder.append(" LEFT JOIN user u ");
            stringBuilder.append(" ON wg.user_pk = u.pk ");
            stringBuilder.append(" WHERE wg.end_date > DATE_SUB(now(), INTERVAL 7 DAY) AND wg.win_chance > 0 ");
            stringBuilder.append(" ORDER BY wg.win_chance ASC ");
            stringBuilder.append(" LIMIT 1 ");

            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                map = new HashMap<String, Object>();
                map.put("giveaway_title", resultSet.getString("giveaway_title"));
                map.put("win_chance", resultSet.getDouble("win_chance"));
                map.put("name", resultSet.getString("name"));
                map.put("steam_link", resultSet.getString("steam_link"));
                map.put("giveaway_link", resultSet.getString("giveaway_link"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.ofNullable(map);
    }

    public Optional<Map<String, Object>> getMostExpensive() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, Object> map = null;
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT wg.title AS giveaway_title, wg.steam_store_price AS max_price, u.login_name AS name, wg.steam_link, wg.giveaway_link ");
            stringBuilder.append(" FROM won_giveaway wg ");
            stringBuilder.append(" LEFT JOIN user u ");
            stringBuilder.append(" ON wg.user_pk = u.pk ");
            stringBuilder.append(" WHERE wg.end_date > DATE_SUB(now(), INTERVAL 7 DAY) ");
            stringBuilder.append(" ORDER BY wg.steam_store_price DESC ");
            stringBuilder.append(" LIMIT 1 ");

            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                map = new HashMap<String, Object>();
                map.put("giveaway_title", resultSet.getString("giveaway_title"));
                map.put("max_price", resultSet.getDouble("max_price"));
                map.put("name", resultSet.getString("name"));
                map.put("steam_link", resultSet.getString("steam_link"));
                map.put("giveaway_link", resultSet.getString("giveaway_link"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.ofNullable(map);
    }

    public Optional<Map<String, Object>> getNightCrawler() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, Object> map = null;
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT COUNT(1) AS wins, u.login_name AS name ");
            stringBuilder.append(" FROM won_giveaway wg, user u ");
            stringBuilder.append(" WHERE wg.user_pk = u.pk AND HOUR(wg.end_date) < 5 AND u.is_active = true ");
            stringBuilder.append(" GROUP BY user_pk ");
            stringBuilder.append(" ORDER BY wins DESC ");
            stringBuilder.append(" LIMIT 1 ");

            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                map = new HashMap<String, Object>();
                map.put("wins", resultSet.getInt("wins"));
                map.put("name", resultSet.getString("name"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.ofNullable(map);
    }

    public Optional<Map<String, Object>> getAverage() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, Object> map = null;
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT AVG(wg.steam_store_price) AS average ");
            stringBuilder.append(" FROM won_giveaway wg ");

            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                map = new HashMap<String, Object>();
                map.put("average", resultSet.getDouble("average"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.ofNullable(map);
    }

    public Optional<Map<String, Object>> getGreedy() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, Object> map = null;
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT count(1) AS wins, u.login_name AS name ");
            stringBuilder.append(" FROM won_giveaway wg, user u ");
            stringBuilder.append(" WHERE u.pk = wg.user_pk AND wg.end_date > DATE_SUB(now(), INTERVAL 7 DAY) AND u.is_active = true ");
            stringBuilder.append(" GROUP BY wg.user_pk ");
            stringBuilder.append(" ORDER BY wins DESC ");
            stringBuilder.append(" LIMIT 1 ");

            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                map = new HashMap<String, Object>();
                map.put("wins", resultSet.getInt("wins"));
                map.put("name", resultSet.getString("name"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.ofNullable(map);
    }

    public Optional<Map<String, Integer>> getUserGiveawayCount() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, Integer> map = null;
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT COUNT(wg.user_pk) AS count, u.login_name ");
            stringBuilder.append(" FROM won_giveaway AS wg ");
            stringBuilder.append(" INNER JOIN user AS u ");
            stringBuilder.append(" ON wg.user_pk = u.pk ");
            stringBuilder.append(" GROUP BY wg.user_pk ");
            stringBuilder.append(" ORDER BY count DESC ");
            stringBuilder.append(" LIMIT 10 ");

            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            map = new LinkedHashMap<String, Integer>();
            while (resultSet.next()) {
                map.put(resultSet.getString("login_name"), resultSet.getInt("count"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.ofNullable(map);
    }

    public Optional<Map<Integer, Integer>> getLevelGroupedGiveaways(int userPk) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<Integer, Integer> map = null;
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT level_requirement AS level, count(1) as count ");
            stringBuilder.append(" FROM won_giveaway WHERE user_pk = ? ");
            stringBuilder.append(" GROUP BY level_requirement ");
            stringBuilder.append(" ORDER BY level_requirement ");

            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            preparedStatement.setInt(1, userPk);
            resultSet = preparedStatement.executeQuery();
            map = new LinkedHashMap<Integer, Integer>();
            while (resultSet.next()) {
                map.put(resultSet.getInt("level"), resultSet.getInt("count"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.ofNullable(map);
    }

    public Optional<WonGiveawayTO> getLastWonGiveaway() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = hikariPool.getConnection();
            String query = getGetStatement(null);
            preparedStatement = connection.prepareStatement(query);
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

    private WonGiveawayTO createTransferObject(ResultSet resultSet) throws Exception {
        WonGiveawayTO wonGiveaway = new WonGiveawayTO();

        wonGiveaway.setPk(resultSet.getInt("pk"));
        wonGiveaway.setTitle(resultSet.getString("title"));
        wonGiveaway.setPoints(resultSet.getInt("points"));
        wonGiveaway.setCopies(resultSet.getInt("copies"));
        wonGiveaway.setEntries(resultSet.getInt("entries"));
        wonGiveaway.setWinChance(resultSet.getDouble("win_chance"));
        wonGiveaway.setAuthor(resultSet.getString("author"));
        wonGiveaway.setSteamStorePrice((Double) resultSet.getObject("steam_store_price"));
        wonGiveaway.setGiveawayLink(resultSet.getString("giveaway_link"));
        wonGiveaway.setSteamLink((String) resultSet.getObject("steam_link"));
        wonGiveaway.setImageLink((String) resultSet.getObject("image_link"));
        wonGiveaway.setLevelRequirement(resultSet.getInt("level_requirement"));
        wonGiveaway.setSteamKey(resultSet.getString("steam_key"));
        wonGiveaway.setHasReceived((Boolean) resultSet.getObject("has_received"));
        wonGiveaway.setReceivedDate(resultSet.getTimestamp("received_date"));
        wonGiveaway.setSteamActivationDate(resultSet.getTimestamp("steam_activation_date"));
        wonGiveaway.setEndDate(resultSet.getTimestamp("end_date"));
        wonGiveaway.setUserPk(resultSet.getInt("user_pk"));
        wonGiveaway.setUserName(resultSet.getString("user_name"));

        return wonGiveaway;
    }
}
