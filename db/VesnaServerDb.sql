CREATE DATABASE  IF NOT EXISTS `sensor_data` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `sensor_data`;
-- MySQL dump 10.13  Distrib 5.5.16, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: sensor_data
-- ------------------------------------------------------
-- Server version	5.5.24-log

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
-- Table structure for table `sensor_measurement_table`
--

DROP TABLE IF EXISTS `sensor_measurement_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sensor_measurement_table` (
  `measurement_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sensor_uid` bigint(20) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `value` double DEFAULT NULL,
  PRIMARY KEY (`measurement_id`),
  UNIQUE KEY `measurement_id_UNIQUE` (`measurement_id`),
  KEY `sensor_index` (`sensor_uid`),
  KEY `time_index` (`timestamp`),
  KEY `value_index` (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=3136147 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sensor_node_table`
--

DROP TABLE IF EXISTS `sensor_node_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sensor_node_table` (
  `sn_auto_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sn_uid` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `gps_latitude` double DEFAULT NULL,
  `gps_longitude` double DEFAULT NULL,
  `geonames_id` int(11) DEFAULT NULL,
  `sn_description` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `gateway` int(11) DEFAULT NULL,
  `setup` int(11) DEFAULT NULL,
  PRIMARY KEY (`sn_auto_id`),
  UNIQUE KEY `sn_auto_id_UNIQUE` (`sn_auto_id`,`sn_uid`),
  UNIQUE KEY `node_index` (`sn_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sensor_table`
--

DROP TABLE IF EXISTS `sensor_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sensor_table` (
  `sensor_uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `sn_uid` bigint(20) DEFAULT NULL,
  `st_uid` bigint(20) DEFAULT NULL,
  `data_code` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`sensor_uid`),
  UNIQUE KEY `sensor_auto_id_UNIQUE` (`sensor_uid`),
  KEY `index` (`sn_uid`,`st_uid`,`data_code`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sensor_type_table`
--

DROP TABLE IF EXISTS `sensor_type_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sensor_type_table` (
  `st_uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `sensor_type` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `measured_phenomenon` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `unit_of_measurement` varchar(15) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`st_uid`),
  UNIQUE KEY `st_uid_UNIQUE` (`st_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-04-14 12:10:01
