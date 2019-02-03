-- MySQL dump 10.14  Distrib 5.5.60-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: gifter
-- ------------------------------------------------------
-- Server version	5.5.60-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `algorithm`
--

DROP TABLE IF EXISTS `algorithm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `algorithm` (
  `pk` int(11) NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`pk`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cached_game`
--

DROP TABLE IF EXISTS `cached_game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cached_game` (
  `pk` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` int(11) NOT NULL,
  `is_dlc` tinyint(1) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `error_log`
--

DROP TABLE IF EXISTS `error_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `error_log` (
  `pk` int(11) NOT NULL AUTO_INCREMENT,
  `message` text COLLATE utf8_unicode_ci NOT NULL,
  `value` mediumtext COLLATE utf8_unicode_ci,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_pk` int(11) DEFAULT NULL,
  PRIMARY KEY (`pk`),
  KEY `error_log_user` (`user_pk`),
  CONSTRAINT `error_log_user` FOREIGN KEY (`user_pk`) REFERENCES `user` (`pk`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `info_log`
--

DROP TABLE IF EXISTS `info_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `info_log` (
  `pk` int(11) NOT NULL AUTO_INCREMENT,
  `title` text COLLATE utf8_unicode_ci NOT NULL,
  `points` int(11) NOT NULL,
  `win_chance` double NOT NULL,
  `steam_link` text COLLATE utf8_unicode_ci,
  `giveaway_link` text COLLATE utf8_unicode_ci NOT NULL,
  `image_link` text COLLATE utf8_unicode_ci,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_asset_pk` int(11) NOT NULL,
  PRIMARY KEY (`pk`),
  KEY `info_log_user_asset` (`user_asset_pk`),
  CONSTRAINT `info_log_user_asset` FOREIGN KEY (`user_asset_pk`) REFERENCES `user_asset` (`pk`) ON DELETE CASCADE
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `pk` int(11) NOT NULL AUTO_INCREMENT,
  `login_name` text COLLATE utf8_unicode_ci NOT NULL,
  `profile_name` text COLLATE utf8_unicode_ci,
  `steam_id` bigint(10) DEFAULT NULL,
  `password` text COLLATE utf8_unicode_ci NOT NULL,
  `notification_email` text COLLATE utf8_unicode_ci,
  `phpsessionid` text COLLATE utf8_unicode_ci NOT NULL,
  `image_link` text COLLATE utf8_unicode_ci,
  `skip_dlc` tinyint(1) NOT NULL,
  `skip_sub` tinyint(1) NOT NULL,
  `skip_wishlist` tinyint(1) NOT NULL DEFAULT '0',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `is_admin` tinyint(1) NOT NULL,
  `algorithm_pk` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`pk`),
  KEY `user_algorithm` (`algorithm_pk`),
  CONSTRAINT `user_algorithm` FOREIGN KEY (`algorithm_pk`) REFERENCES `algorithm` (`pk`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_asset`
--

DROP TABLE IF EXISTS `user_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_asset` (
  `pk` int(11) NOT NULL AUTO_INCREMENT,
  `points` int(11) NOT NULL,
  `level` double NOT NULL,
  `synced` tinyint(1) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_pk` int(11) NOT NULL,
  PRIMARY KEY (`pk`),
  KEY `user_asset_user` (`user_pk`),
  CONSTRAINT `user_asset_user` FOREIGN KEY (`user_pk`) REFERENCES `user` (`pk`) ON DELETE CASCADE
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `won_giveaway`
--

DROP TABLE IF EXISTS `won_giveaway`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `won_giveaway` (
  `pk` int(11) NOT NULL AUTO_INCREMENT,
  `title` text COLLATE utf8_unicode_ci NOT NULL,
  `points` int(11) NOT NULL,
  `copies` int(11) NOT NULL,
  `entries` bigint(20) NOT NULL,
  `win_chance` double NOT NULL,
  `author` text COLLATE utf8_unicode_ci NOT NULL,
  `steam_store_price` double DEFAULT NULL,
  `giveaway_link` text COLLATE utf8_unicode_ci NOT NULL,
  `steam_link` text COLLATE utf8_unicode_ci,
  `image_link` text COLLATE utf8_unicode_ci,
  `level_requirement` int(11) NOT NULL,
  `steam_key` text COLLATE utf8_unicode_ci,
  `has_received` tinyint(1) DEFAULT NULL,
  `received_date` timestamp NULL DEFAULT NULL,
  `steam_activation_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `user_pk` int(11) NOT NULL,
  PRIMARY KEY (`pk`),
  KEY `won_game_user` (`user_pk`),
  CONSTRAINT `won_game_user` FOREIGN KEY (`user_pk`) REFERENCES `user` (`pk`) ON DELETE CASCADE
);
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

INSERT INTO algorithm (name, description) VALUES('Default', 'Enters giveaways, sorted by remaining time');
INSERT INTO algorithm (name, description) VALUES('Snitch', 'Enters giveaways with winchance > 0.5%, sorted by remaining time [checks & removes already entered giveaways < 0.5%]');
INSERT INTO algorithm (name, description) VALUES('Grabber', 'Enters giveaways with remaining time < 1h, sorted by winchance');
INSERT INTO algorithm (name, description) VALUES('Snatcher', 'Enters giveaways with remaining time < 1h and winchance > 0.5%, sorted by remaining time');
INSERT INTO algorithm (name, description) VALUES('Chancer', 'Enters giveaways with a calculated minimum winchance depending on available points');
INSERT INTO algorithm (name, description) VALUES('Faker', 'Enters no giveaways at all (ideal for holydays)');

INSERT INTO gifter.`user`
(pk, login_name, profile_name, steam_id, password, notification_email, phpsessionid, image_link, skip_dlc, skip_sub, skip_wishlist, created_date, last_login, is_active, is_admin, algorithm_pk)
VALUES(1, 'helloingob', NULL, 0, '11bb3787792d6e25611b450337ddb72969430aea', 'mynotification@address.com', 'MyPHPSessionID', 0, 1, 1, 1, '2015-02-10 18:26:09.000', 'null', 1, 1, 1);