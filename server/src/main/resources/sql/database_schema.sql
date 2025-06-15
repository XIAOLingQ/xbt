-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: xbt
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(100) NOT NULL COMMENT '课程标题',
  `description` text COMMENT '课程描述',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '课程封面图片URL',
  `course_code` varchar(20) NOT NULL COMMENT '课程码',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-进行中，2-已结束',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_code` (`course_code`),
  KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'数据结构','程序=数据结构+算法','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/1_1749369379900.png','F5FC94',1,1,'2025-06-08 07:55:58','2025-06-08 07:56:21'),(5,'操作系统','操作系统','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/temp_1749456738827.png','9566FB',1,1,'2025-06-09 08:12:29','2025-06-09 08:12:29');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_student`
--

DROP TABLE IF EXISTS `course_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `join_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `progress` int NOT NULL DEFAULT '0' COMMENT '学习进度百分比',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-正常，2-已退出',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_student` (`course_id`,`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程学生关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_student`
--

LOCK TABLES `course_student` WRITE;
/*!40000 ALTER TABLE `course_student` DISABLE KEYS */;
INSERT INTO `course_student` VALUES (1,1,2,'2025-06-08 08:43:16',100,1,'2025-06-08 08:43:16','2025-06-08 10:00:45'),(4,5,2,'2025-06-09 08:12:42',0,1,'2025-06-09 08:12:42','2025-06-09 08:12:42');
/*!40000 ALTER TABLE `course_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_video`
--

DROP TABLE IF EXISTS `course_video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_video` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID (章ID)',
  `type` tinyint NOT NULL COMMENT '类型: 1-章, 2-节(视频)',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `url` varchar(255) DEFAULT NULL COMMENT '视频URL (节才有)',
  `duration` int DEFAULT NULL COMMENT '视频时长(秒, 节才有)',
  `order_num` int NOT NULL COMMENT '排序号',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程目录与视频表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_video`
--

LOCK TABLES `course_video` WRITE;
/*!40000 ALTER TABLE `course_video` DISABLE KEYS */;
INSERT INTO `course_video` VALUES (1,1,0,1,'引言',NULL,NULL,1,'2025-06-08 08:09:54','2025-06-08 08:09:54'),(2,1,1,2,'常见的数据结构','http://sxh8oib6z.hb-bkt.clouddn.com/videos/1/2_1749372129693.mp4',NULL,1,'2025-06-08 08:42:01','2025-06-08 08:42:53'),(8,5,0,1,'引言',NULL,NULL,1,'2025-06-09 08:20:46','2025-06-09 08:20:46'),(9,5,8,2,'简介','http://sxh8oib6z.hb-bkt.clouddn.com/videos/5/9_1749457256228.mp4',NULL,1,'2025-06-09 08:20:51','2025-06-09 08:22:17');
/*!40000 ALTER TABLE `course_video` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homework`
--

DROP TABLE IF EXISTS `homework`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homework` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(100) NOT NULL COMMENT '作业标题',
  `description` text COMMENT '作业描述',
  `start_time` timestamp NOT NULL COMMENT '开始时间',
  `end_time` timestamp NOT NULL COMMENT '结束时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homework`
--

LOCK TABLES `homework` WRITE;
/*!40000 ALTER TABLE `homework` DISABLE KEYS */;
INSERT INTO `homework` VALUES (2,1,'新作业 - 2025年6月8日 下午4:45:14','请填写作业描述...','2025-06-08 00:45:14','2025-06-15 00:45:14','2025-06-08 08:45:14','2025-06-08 08:45:23'),(3,1,'新作业 - 2025年6月9日 下午4:15:34','请填写作业描述...','2025-06-09 00:15:35','2025-06-16 00:15:35','2025-06-09 08:15:34','2025-06-09 08:15:52'),(4,1,'新作业 - 2025年6月9日 下午4:16:45','请填写作业描述...','2025-06-09 00:16:46','2025-06-16 00:16:46','2025-06-09 08:16:45','2025-06-11 14:51:38');
/*!40000 ALTER TABLE `homework` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homework_question`
--

DROP TABLE IF EXISTS `homework_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homework_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `homework_id` bigint NOT NULL COMMENT '所属作业ID',
  `question_text` text NOT NULL COMMENT '问题内容',
  `question_type` varchar(50) NOT NULL COMMENT '问题类型 (e.g., single_choice, text_answer)',
  `options` json DEFAULT NULL COMMENT '选项 (for choice questions)',
  `answer` varchar(1000) DEFAULT NULL COMMENT '问题答案',
  `score` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_homework_id` (`homework_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业问题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homework_question`
--

LOCK TABLES `homework_question` WRITE;
/*!40000 ALTER TABLE `homework_question` DISABLE KEYS */;
INSERT INTO `homework_question` VALUES (2,2,'新的选择题','choice','[{\"text\": \"a\"}, {\"text\": \"a\"}, {\"text\": \"a\"}, {\"text\": \"a\"}]',NULL,NULL),(3,3,'新的选择题','choice','[{\"text\": \"a\"}, {\"text\": \"b\"}, {\"text\": \"c\"}, {\"text\": \"d\"}]',NULL,NULL),(10,4,'新的选择题','choice','[{\"text\": \"a\"}, {\"text\": \"b\"}, {\"text\": \"c\"}, {\"text\": \"d\"}]','D','6');
/*!40000 ALTER TABLE `homework_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homework_submission`
--

DROP TABLE IF EXISTS `homework_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homework_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `homework_id` bigint NOT NULL COMMENT '作业ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `status` tinyint NOT NULL COMMENT '状态：1-进行中，2-已提交，3-已批改',
  `score` int DEFAULT NULL COMMENT '得分',
  `answers` json DEFAULT NULL COMMENT '学生提交的答案列表',
  `submit_time` timestamp NULL DEFAULT NULL COMMENT '提交时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_homework_student` (`homework_id`,`student_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生作业提交表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homework_submission`
--

LOCK TABLES `homework_submission` WRITE;
/*!40000 ALTER TABLE `homework_submission` DISABLE KEYS */;
INSERT INTO `homework_submission` VALUES (1,2,2,2,NULL,'{\"2\": \"D\"}','2025-06-08 13:03:24','2025-06-08 08:53:14','2025-06-08 13:03:23'),(4,4,2,2,NULL,'{\"4\": \"D\"}','2025-06-09 08:19:21','2025-06-09 08:19:21','2025-06-09 08:19:21');
/*!40000 ALTER TABLE `homework_submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_video_progress`
--

DROP TABLE IF EXISTS `student_video_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_video_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `video_id` bigint NOT NULL COMMENT '视频ID (course_video.id)',
  `watch_duration` int NOT NULL DEFAULT '0' COMMENT '观看时长(秒)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '完成时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_video` (`student_id`,`video_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生视频学习进度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_video_progress`
--

LOCK TABLES `student_video_progress` WRITE;
/*!40000 ALTER TABLE `student_video_progress` DISABLE KEYS */;
INSERT INTO `student_video_progress` VALUES (1,2,2,55,'2025-06-08 08:44:39','2025-06-10 02:10:12');
/*!40000 ALTER TABLE `student_video_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `role` tinyint NOT NULL COMMENT '角色：1-学生，2-教师',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'XIAOLQ','$2a$10$miyohcW5UXtEMpH2C2X2OeP7P8X6tHf6eGEvwq6JKRKf9FAhhXNkS','2204240513@mail.wtu.edu.cn',2,'xiaolq',NULL,'2025-06-08 07:55:21','2025-06-08 07:55:21'),(2,'2204240513','$2a$10$kn/3mCboYeBHyoa8dj7wJ.4Ea7Qlzs.XCeeVu7oAwvWUM8hRrltdW','3061784569@qq.com',1,'spl','http://sxh8oib6z.hb-bkt.clouddn.com/avatars/2_1749457544611.png','2025-06-08 07:55:32','2025-06-08 07:55:32'),(3,'0000','$2a$10$aMC3Ey3qr41PCJ3Agg5nbujF3XN/EEhEfrwzuVHJgkNd8NQUYZgui','sunpeiling502@gmail.com',2,'spl',NULL,'2025-06-09 07:45:51','2025-06-09 07:45:51');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-11 22:55:30

--
-- Table structure for table `ai_question`
--

DROP TABLE IF EXISTS `ai_question`;
CREATE TABLE `ai_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `topic` varchar(255) NOT NULL COMMENT '生成主题',
  `batch_id` varchar(255) DEFAULT NULL COMMENT '生成批次ID',
  `question_type` varchar(50) NOT NULL COMMENT '问题类型 (e.g., single_choice, text_answer)',
  `question_text` text NOT NULL COMMENT '问题内容',
  `options` json DEFAULT NULL COMMENT '选项 (for choice questions)',
  `correct_answer` text COMMENT '正确答案',
  `analysis` text COMMENT '题目解析',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI生成问题表';

--
-- Table structure for table `ai_submission`
--

DROP TABLE IF EXISTS `ai_submission`;
CREATE TABLE `ai_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `ai_question_id` bigint NOT NULL COMMENT 'AI问题ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_answer` text COMMENT '学生答案',
  `is_correct` tinyint(1) DEFAULT NULL COMMENT '是否正确',
  `submitted_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`),
  KEY `idx_ai_question_id` (`ai_question_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI问题提交表';
