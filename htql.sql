-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: htql_rap_phim
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bapnuoc`
--

DROP TABLE IF EXISTS `bapnuoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bapnuoc` (
  `macombo` varchar(45) NOT NULL,
  `tencombo` varchar(45) NOT NULL,
  `giacombo` double unsigned NOT NULL,
  `mota` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`macombo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bapnuoc`
--

LOCK TABLES `bapnuoc` WRITE;
/*!40000 ALTER TABLE `bapnuoc` DISABLE KEYS */;
/*!40000 ALTER TABLE `bapnuoc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ghe`
--

DROP TABLE IF EXISTS `ghe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ghe` (
  `maghe` varchar(45) NOT NULL,
  `sohang` varchar(2) NOT NULL,
  `soghe` int unsigned NOT NULL,
  `loaighe` varchar(45) NOT NULL,
  `phongchieu_maphong` varchar(45) NOT NULL,
  PRIMARY KEY (`maghe`),
  KEY `fk_ghe_phongchieu1_idx` (`phongchieu_maphong`),
  CONSTRAINT `fk_ghe_phongchieu1` FOREIGN KEY (`phongchieu_maphong`) REFERENCES `phongchieu` (`maphong`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ghe`
--

LOCK TABLES `ghe` WRITE;
/*!40000 ALTER TABLE `ghe` DISABLE KEYS */;
/*!40000 ALTER TABLE `ghe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hoadon`
--

DROP TABLE IF EXISTS `hoadon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hoadon` (
  `mahoadon` varchar(45) NOT NULL,
  `soluongcombo` int unsigned NOT NULL,
  `ngaymua` date NOT NULL,
  `tongtien` double unsigned NOT NULL,
  `khachhang_makhachhang` varchar(45) NOT NULL,
  `bapnuoc_macombo` varchar(45) NOT NULL,
  PRIMARY KEY (`mahoadon`),
  KEY `fk_hoadon_khachhang1_idx` (`khachhang_makhachhang`),
  KEY `fk_hoadon_bapnuoc1_idx` (`bapnuoc_macombo`),
  CONSTRAINT `fk_hoadon_bapnuoc1` FOREIGN KEY (`bapnuoc_macombo`) REFERENCES `bapnuoc` (`macombo`),
  CONSTRAINT `fk_hoadon_khachhang1` FOREIGN KEY (`khachhang_makhachhang`) REFERENCES `khachhang` (`makhachhang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hoadon`
--

LOCK TABLES `hoadon` WRITE;
/*!40000 ALTER TABLE `hoadon` DISABLE KEYS */;
/*!40000 ALTER TABLE `hoadon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khachhang`
--

DROP TABLE IF EXISTS `khachhang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khachhang` (
  `makhachhang` varchar(45) NOT NULL,
  `tenkhachhang` varchar(45) NOT NULL,
  `sdt` varchar(15) NOT NULL,
  `email` varchar(45) NOT NULL,
  PRIMARY KEY (`makhachhang`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khachhang`
--

LOCK TABLES `khachhang` WRITE;
/*!40000 ALTER TABLE `khachhang` DISABLE KEYS */;
/*!40000 ALTER TABLE `khachhang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phim`
--

DROP TABLE IF EXISTS `phim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phim` (
  `maphim` varchar(45) NOT NULL,
  `tenphim` varchar(255) NOT NULL,
  `theloai` varchar(45) DEFAULT NULL,
  `daodien` varchar(45) DEFAULT NULL,
  `thoiluong` int unsigned NOT NULL,
  `ngaykhoichieu` date DEFAULT NULL,
  `dotuoichophep` int DEFAULT NULL,
  PRIMARY KEY (`maphim`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phim`
--

LOCK TABLES `phim` WRITE;
/*!40000 ALTER TABLE `phim` DISABLE KEYS */;
/*!40000 ALTER TABLE `phim` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phongchieu`
--

DROP TABLE IF EXISTS `phongchieu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phongchieu` (
  `maphong` varchar(45) NOT NULL,
  `tenphong` varchar(45) NOT NULL,
  `soghe` int unsigned NOT NULL,
  `loaiphong` varchar(45) NOT NULL,
  PRIMARY KEY (`maphong`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phongchieu`
--

LOCK TABLES `phongchieu` WRITE;
/*!40000 ALTER TABLE `phongchieu` DISABLE KEYS */;
/*!40000 ALTER TABLE `phongchieu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suatchieu`
--

DROP TABLE IF EXISTS `suatchieu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suatchieu` (
  `masuatchieu` varchar(45) NOT NULL,
  `Ngaychieu` date NOT NULL,
  `giochieu` time NOT NULL,
  `giave` float unsigned NOT NULL,
  `Phim_maphim` varchar(45) NOT NULL,
  `phongchieu_maphong` varchar(45) NOT NULL,
  PRIMARY KEY (`masuatchieu`),
  KEY `fk_suatchieu_Phim_idx` (`Phim_maphim`),
  KEY `fk_suatchieu_phongchieu1_idx` (`phongchieu_maphong`),
  CONSTRAINT `fk_suatchieu_Phim` FOREIGN KEY (`Phim_maphim`) REFERENCES `phim` (`maphim`),
  CONSTRAINT `fk_suatchieu_phongchieu1` FOREIGN KEY (`phongchieu_maphong`) REFERENCES `phongchieu` (`maphong`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suatchieu`
--

LOCK TABLES `suatchieu` WRITE;
/*!40000 ALTER TABLE `suatchieu` DISABLE KEYS */;
/*!40000 ALTER TABLE `suatchieu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ve`
--

DROP TABLE IF EXISTS `ve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ve` (
  `mave` varchar(45) NOT NULL,
  `ngaydat` date NOT NULL,
  `giave` double unsigned NOT NULL,
  `trangthai` varchar(45) NOT NULL,
  `suatchieu_masuatchieu` varchar(45) NOT NULL,
  `khachhang_makhachhang` varchar(45) NOT NULL,
  `ghe_maghe` varchar(45) NOT NULL,
  PRIMARY KEY (`mave`),
  KEY `fk_ve_suatchieu1_idx` (`suatchieu_masuatchieu`),
  KEY `fk_ve_khachhang1_idx` (`khachhang_makhachhang`),
  KEY `fk_ve_ghe1_idx` (`ghe_maghe`),
  CONSTRAINT `fk_ve_ghe1` FOREIGN KEY (`ghe_maghe`) REFERENCES `ghe` (`maghe`),
  CONSTRAINT `fk_ve_khachhang1` FOREIGN KEY (`khachhang_makhachhang`) REFERENCES `khachhang` (`makhachhang`),
  CONSTRAINT `fk_ve_suatchieu1` FOREIGN KEY (`suatchieu_masuatchieu`) REFERENCES `suatchieu` (`masuatchieu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ve`
--

LOCK TABLES `ve` WRITE;
/*!40000 ALTER TABLE `ve` DISABLE KEYS */;
/*!40000 ALTER TABLE `ve` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-16 17:31:22
