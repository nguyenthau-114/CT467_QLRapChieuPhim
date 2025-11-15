CREATE DATABASE  IF NOT EXISTS `htql_rap_phim` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `htql_rap_phim`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: htql_rap_phim
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
  `tencombo` varchar(45) DEFAULT NULL,
  `giacombo` double DEFAULT NULL,
  `mota` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`macombo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bapnuoc`
--

LOCK TABLES `bapnuoc` WRITE;
/*!40000 ALTER TABLE `bapnuoc` DISABLE KEYS */;
INSERT INTO `bapnuoc` VALUES ('BN01','Combo nhỏ',50000,'1 bắp + 1 nước'),('BN02','Combo lớn',80000,'1 bắp + 2 nước');
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
  `sohang` float DEFAULT NULL,
  `soghe` int DEFAULT NULL,
  `loaighe` varchar(45) DEFAULT NULL,
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
INSERT INTO `ghe` VALUES ('G01',1,1,'Thường','PC01'),('G02',1,2,'Thường','PC01'),('G03',1,3,'Thường','PC01'),('G04',1,4,'VIP','PC01'),('G05',2,1,'VIP','PC02'),('G06',2,2,'Thường','PC02');
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
  `soluongcombo` int DEFAULT NULL,
  `ngaymua` date DEFAULT NULL,
  `tongtien` double DEFAULT NULL,
  `khachhang_makhachhang` varchar(45) NOT NULL,
  `bapnuoc_macombo` varchar(45) NOT NULL,
  `Nhanvien_manhanvien` varchar(45) NOT NULL,
  PRIMARY KEY (`mahoadon`),
  KEY `fk_hoadon_khachhang1_idx` (`khachhang_makhachhang`),
  KEY `fk_hoadon_bapnuoc1_idx` (`bapnuoc_macombo`),
  KEY `fk_hoadon_Nhanvien1_idx` (`Nhanvien_manhanvien`),
  CONSTRAINT `fk_hoadon_bapnuoc1` FOREIGN KEY (`bapnuoc_macombo`) REFERENCES `bapnuoc` (`macombo`),
  CONSTRAINT `fk_hoadon_khachhang1` FOREIGN KEY (`khachhang_makhachhang`) REFERENCES `khachhang` (`makhachhang`),
  CONSTRAINT `fk_hoadon_Nhanvien1` FOREIGN KEY (`Nhanvien_manhanvien`) REFERENCES `nhanvien` (`manhanvien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hoadon`
--

LOCK TABLES `hoadon` WRITE;
/*!40000 ALTER TABLE `hoadon` DISABLE KEYS */;
INSERT INTO `hoadon` VALUES ('HD01',1,'2025-11-08',130000,'KH01','BN01','NV01'),('HD02',2,'2025-11-09',170000,'KH02','BN02','NV02');
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
  `tenkhachhang` varchar(45) DEFAULT NULL,
  `sdt` varchar(15) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`makhachhang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khachhang`
--

LOCK TABLES `khachhang` WRITE;
/*!40000 ALTER TABLE `khachhang` DISABLE KEYS */;
INSERT INTO `khachhang` VALUES ('KH01','Nguyễn Văn A','0909123456','vana@gmail.com'),('KH02','Trần Thị B','0909456789','thib@gmail.com');
/*!40000 ALTER TABLE `khachhang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nhanvien`
--

DROP TABLE IF EXISTS `nhanvien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nhanvien` (
  `manhanvien` varchar(45) NOT NULL,
  `tennhanvien` varchar(45) DEFAULT NULL,
  `chucvu` varchar(45) DEFAULT NULL,
  `sdt` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`manhanvien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nhanvien`
--

LOCK TABLES `nhanvien` WRITE;
/*!40000 ALTER TABLE `nhanvien` DISABLE KEYS */;
INSERT INTO `nhanvien` VALUES ('m01','myt','bán hàng','03652','ouut'),('m02','my','trực','0987','fgfdgr'),('m03','df','dfds','098','csdf'),('NV01','Lê Văn C','Quản lý','0909988776','levanc@gmail.com'),('NV02','Phạm Thị D','Thu ngân','0911223344','phamthid@gmail.com');
/*!40000 ALTER TABLE `nhanvien` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phim`
--

DROP TABLE IF EXISTS `phim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phim` (
  `maphim` varchar(45) NOT NULL,
  `tenphim` varchar(255) DEFAULT NULL,
  `theloai` varchar(45) DEFAULT NULL,
  `daodien` varchar(45) DEFAULT NULL,
  `thoiluong` int DEFAULT NULL,
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
INSERT INTO `phim` VALUES ('P001','Avengers: Endgame','Hành động','Anthony Ruphimsso',180,'2019-04-26',13),('P002','Inside Out 2','Hoạt hình','Kelsey Mann',120,'2024-06-15',3),('P003','Con Nhót Mót Chồng','Hài','Vũ Ngọc Đãng',110,'2023-03-10',16);
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
  `tenphong` varchar(45) DEFAULT NULL,
  `soghe` int DEFAULT NULL,
  `loaiphong` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`maphong`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phongchieu`
--

LOCK TABLES `phongchieu` WRITE;
/*!40000 ALTER TABLE `phongchieu` DISABLE KEYS */;
INSERT INTO `phongchieu` VALUES ('PC01','Phòng 1',100,'2D'),('PC02','Phòng 2',80,'3D'),('PC03','Phòng 3',100,'2D');
/*!40000 ALTER TABLE `phongchieu` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_before_insert_phong_auto_id` BEFORE INSERT ON `phongchieu` FOR EACH ROW BEGIN
    DECLARE v_prefix VARCHAR(10) DEFAULT COALESCE(@PHONG_PREFIX, 'PC');
    DECLARE v_width  INT         DEFAULT COALESCE(@PHONG_WIDTH, 2);
    DECLARE v_next   INT;

    -- Nếu người dùng không truyền maphong (NULL hoặc rỗng) thì tự sinh
    IF NEW.maphong IS NULL OR TRIM(NEW.maphong) = '' THEN
        -- Lấy số lớn nhất đang có theo prefix, rồi +1
        SELECT COALESCE(
                 MAX(CAST(SUBSTRING(maphong, LENGTH(v_prefix) + 1) AS UNSIGNED))
               , 0
               ) + 1
          INTO v_next
          FROM phongchieu
         WHERE maphong REGEXP CONCAT('^', v_prefix, '[0-9]+$');

        SET NEW.maphong = CONCAT(v_prefix, LPAD(v_next, v_width, '0'));
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_before_delete_phong_check_suatchieu` BEFORE DELETE ON `phongchieu` FOR EACH ROW BEGIN
    -- Biến phiên tùy chọn (nếu không set từ ứng dụng thì mặc định 'FUTURE'):
    --  @PHONG_DELETE_MODE = 'FUTURE' | 'ANY'
    DECLARE v_mode VARCHAR(10) DEFAULT COALESCE(@PHONG_DELETE_MODE, 'FUTURE');

    IF v_mode = 'ANY' THEN
        -- Chặn nếu còn bất kỳ suất chiếu nào gắn với phòng
        IF EXISTS (
            SELECT 1
            FROM suatchieu sc
            WHERE sc.phongchieu_maphong = OLD.maphong
        ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Khong the xoa phong: dang co suat chieu gan voi phong nay (ANY).';
        END IF;
    ELSE
        -- FUTURE (mặc định): chặn nếu còn suất chiếu chưa diễn ra
        IF EXISTS (
            SELECT 1
            FROM suatchieu sc
            WHERE sc.phongchieu_maphong = OLD.maphong
              AND TIMESTAMP(sc.Ngaychieu, sc.giochieu) >= NOW()
        ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Khong the xoa phong: van con suat chieu chua dien ra (FUTURE).';
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `suatchieu`
--

DROP TABLE IF EXISTS `suatchieu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suatchieu` (
  `masuatchieu` varchar(45) NOT NULL,
  `Ngaychieu` date DEFAULT NULL,
  `giochieu` time DEFAULT NULL,
  `giave` float DEFAULT NULL,
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
INSERT INTO `suatchieu` VALUES ('SC01','2025-11-08','18:00:00',80000,'P001','PC01'),('SC02','2025-11-08','20:30:00',120000,'P002','PC02'),('SC03','2025-11-08','20:30:00',100000,'P001','PC03'),('SC04','2025-11-08','12:10:00',79000,'P001','PC01'),('SC05','2025-11-09','01:00:00',89000,'P001','PC03'),('SC06','2025-11-09','12:00:00',89000,'P001','PC01'),('SC07','2025-11-02','16:00:00',1200000,'P001','PC03');
/*!40000 ALTER TABLE `suatchieu` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_before_insert_suatchieu_auto_id` BEFORE INSERT ON `suatchieu` FOR EACH ROW BEGIN
    DECLARE v_max INT DEFAULT 0;
    DECLARE v_next INT DEFAULT 1;
    -- Nếu mã chưa có, tự động sinh
    IF NEW.masuatchieu IS NULL OR NEW.masuatchieu = '' THEN
        SELECT MAX(CAST(SUBSTRING(masuatchieu, 3) AS UNSIGNED))
        INTO v_max
        FROM suatchieu
        WHERE masuatchieu REGEXP '^SC[0-9]+$';
        IF v_max IS NOT NULL THEN
            SET v_next = v_max + 1;
        END IF;
        SET NEW.masuatchieu = CONCAT('SC', LPAD(v_next, 2, '0'));
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `ve`
--

DROP TABLE IF EXISTS `ve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ve` (
  `mave` varchar(45) NOT NULL,
  `ngaydat` date DEFAULT NULL,
  `giave` double DEFAULT NULL,
  `trangthai` varchar(45) DEFAULT NULL,
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
INSERT INTO `ve` VALUES ('V001','2025-11-08',80000,'Đã thanh toán','SC01','KH01','G01'),('V002','2025-11-08',85000,'Đã thanh toán','SC01','KH01','G02'),('V003','2025-11-09',80000,'Chưa thanh toán','SC01','KH02','G03'),('V004','2025-11-09',90000,'Đã hủy','SC02','KH01','G04'),('V005','2025-11-10',95000,'Đã thanh toán','SC02','KH02','G05');
/*!40000 ALTER TABLE `ve` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'htql_rap_phim'
--

--
-- Dumping routines for database 'htql_rap_phim'
--
/*!50003 DROP FUNCTION IF EXISTS `fn_kiemtra_lichtrung` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `fn_kiemtra_lichtrung`(
    p_maphong VARCHAR(10),
    p_ngaychieu DATE,
    p_giochieu TIME,
    p_maphim VARCHAR(10)
) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
    DECLARE v_trung TINYINT(1) DEFAULT 0;
    DECLARE v_tgbatdau DATETIME;
    DECLARE v_tgketthuc DATETIME;
    DECLARE v_thoiluong INT DEFAULT 0;

    SELECT thoiluong INTO v_thoiluong FROM phim WHERE maphim = p_maphim;
    IF v_thoiluong IS NULL THEN RETURN 0; END IF;

    SET v_tgbatdau = TIMESTAMP(p_ngaychieu, p_giochieu);
    SET v_tgketthuc = v_tgbatdau + INTERVAL v_thoiluong MINUTE + INTERVAL 30 MINUTE;

    SELECT EXISTS(
        SELECT 1
        FROM suatchieu sc
        JOIN phim p ON p.maphim = sc.phim_maphim
        WHERE sc.phongchieu_maphong = p_maphong
          AND TIMESTAMP(sc.ngaychieu, sc.giochieu) < v_tgketthuc
          AND TIMESTAMP(sc.ngaychieu, sc.giochieu) + INTERVAL p.thoiluong MINUTE + INTERVAL 30 MINUTE > v_tgbatdau
    )
    INTO v_trung;

    RETURN v_trung;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-15 15:27:18
