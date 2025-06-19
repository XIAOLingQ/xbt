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
-- Table structure for table `ai_question`
--

DROP TABLE IF EXISTS `ai_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `topic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '生成主题',
  `batch_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '生成批次ID',
  `question_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题类型 (e.g., single_choice, text_answer)',
  `question_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题内容',
  `options` json DEFAULT NULL COMMENT '选项 (for choice questions)',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '正确答案',
  `analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '题目解析',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `student_answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '学生答案',
  `is_correct` tinyint(1) DEFAULT NULL COMMENT '是否正确',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_student_id` (`student_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='AI生成问题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_question`
--

LOCK TABLES `ai_question` WRITE;
/*!40000 ALTER TABLE `ai_question` DISABLE KEYS */;
INSERT INTO `ai_question` VALUES (1,2,'数据结构','9afd948f-9f56-4ec3-aab5-1d7b65f1c557','single_choice','以下属于非线性数据结构的是？','[{\"option\": \"A\", \"description\": \"栈\"}, {\"option\": \"B\", \"description\": \"队列\"}, {\"option\": \"C\", \"description\": \"二叉树\"}, {\"option\": \"D\", \"description\": \"单链表\"}]','C','线性结构包括栈、队列、单链表等（参考资料中明确列出线性结构包含栈、队列、单链表），而二叉树属于树形结构，树形结构是非线性数据结构（参考资料中树形结构归类为非线性结构）。因此正确答案为C。','2025-06-15 09:29:10','C',1),(2,2,'数据结构','9afd948f-9f56-4ec3-aab5-1d7b65f1c557','single_choice','以下哪种查找算法要求数据必须有序才能使用？','[{\"option\": \"A\", \"description\": \"线性查找\"}, {\"option\": \"B\", \"description\": \"二分查找\"}, {\"option\": \"C\", \"description\": \"哈希查找\"}, {\"option\": \"D\", \"description\": \"顺序查找\"}]','B','二分查找（二分搜索）的核心原理是通过比较中间元素与目标值的大小，逐步缩小搜索范围，这要求数据必须有序（参考资料中明确提到“二分查找”属于搜索类别）。线性查找、顺序查找无需数据有序，哈希查找依赖哈希函数映射，也不要求数据有序。因此正确答案为B。','2025-06-15 09:29:10','B',1),(3,2,'数据结构','4e83ca7b-ad2c-463a-a4bc-7bd7c8045e57','single_choice','以下数据结构中，属于线性结构的是？','[{\"option\": \"A\", \"description\": \"栈\"}, {\"option\": \"B\", \"description\": \"二叉树\"}, {\"option\": \"C\", \"description\": \"红黑树\"}, {\"option\": \"D\", \"description\": \"无向图\"}]','A','线性结构的特点是数据元素之间存在一对一的线性关系。参考资料中明确提到线性结构包括栈（Stack）、队列（Queue）、链表等；而二叉树、红黑树属于树形结构（非线性），无向图属于图形结构（非线性），因此正确答案为A。','2025-06-15 10:31:10','A',1),(4,2,'数据结构','4e83ca7b-ad2c-463a-a4bc-7bd7c8045e57','single_choice','以下查找算法中，要求数据必须有序才能使用的是？','[{\"option\": \"A\", \"description\": \"线性查找\"}, {\"option\": \"B\", \"description\": \"二分查找\"}, {\"option\": \"C\", \"description\": \"顺序查找\"}, {\"option\": \"D\", \"description\": \"随机查找\"}]','B','二分查找（Binary Search）的核心思想是通过比较中间元素与目标值的大小，逐步缩小搜索范围，因此要求数据必须有序。参考资料中提到“二分查找”属于搜索分类，而线性查找、顺序查找（与线性查找类似）和随机查找均不要求数据有序。因此正确答案为B。','2025-06-15 10:31:10','B',1),(25,2,'操作系统','eaf4f794-dce3-4189-bc40-4a7af7144deb','single_choice','操作系统的基本特征不包括以下哪一项？','[{\"option\": \"A\", \"description\": \"并发\"}, {\"option\": \"B\", \"description\": \"共享\"}, {\"option\": \"C\", \"description\": \"确定性\"}, {\"option\": \"D\", \"description\": \"异步\"}]','C','操作系统的基本特征包括并发、共享、虚拟和异步。确定性指程序执行结果可预测，并非操作系统的基本特征。','2025-06-15 13:38:49',NULL,NULL),(26,2,'操作系统','eaf4f794-dce3-4189-bc40-4a7af7144deb','single_choice','操作系统开始出现于哪个发展阶段？','[{\"option\": \"A\", \"description\": \"手工操作阶段\"}, {\"option\": \"B\", \"description\": \"批处理阶段\"}, {\"option\": \"C\", \"description\": \"分时操作系统阶段\"}, {\"option\": \"D\", \"description\": \"实时操作系统阶段\"}]','B','手工操作阶段无操作系统；批处理阶段（单道/多道）开始出现操作系统，用于自动管理作业执行。','2025-06-15 13:38:49',NULL,NULL),(27,2,'操作系统','eaf4f794-dce3-4189-bc40-4a7af7144deb','single_choice','当进程等待的I/O操作完成后，其状态会从哪种状态转换为就绪态？','[{\"option\": \"A\", \"description\": \"运行态\"}, {\"option\": \"B\", \"description\": \"就绪态\"}, {\"option\": \"C\", \"description\": \"等待态\"}, {\"option\": \"D\", \"description\": \"终止态\"}]','C','进程等待I/O时处于等待态（阻塞态），I/O完成后，进程获得资源，转为就绪态等待CPU调度。','2025-06-15 13:38:49',NULL,NULL),(28,2,'操作系统','eaf4f794-dce3-4189-bc40-4a7af7144deb','single_choice','分时操作系统常用的调度算法是？','[{\"option\": \"A\", \"description\": \"先来先服务（FCFS）\"}, {\"option\": \"B\", \"description\": \"时间片轮转（RR）\"}, {\"option\": \"C\", \"description\": \"短作业优先（SJF）\"}, {\"option\": \"D\", \"description\": \"优先级调度\"}]','B','分时系统需保证用户交互响应，时间片轮转通过固定时间片分配CPU，确保各进程公平占用CPU。','2025-06-15 13:38:49',NULL,NULL),(29,2,'操作系统','eaf4f794-dce3-4189-bc40-4a7af7144deb','single_choice','以下哪项不是死锁发生的必要条件？','[{\"option\": \"A\", \"description\": \"互斥条件\"}, {\"option\": \"B\", \"description\": \"请求和保持条件\"}, {\"option\": \"C\", \"description\": \"资源充足条件\"}, {\"option\": \"D\", \"description\": \"循环等待条件\"}]','C','死锁的四个必要条件是互斥、请求和保持、不可抢占、循环等待。资源充足不会导致死锁。','2025-06-15 13:38:49',NULL,NULL),(30,2,'操作系统','eaf4f794-dce3-4189-bc40-4a7af7144deb','single_choice','为解决不同用户文件重名问题，文件系统通常采用哪种目录结构？','[{\"option\": \"A\", \"description\": \"单级目录\"}, {\"option\": \"B\", \"description\": \"二级目录\"}, {\"option\": \"C\", \"description\": \"多级目录\"}, {\"option\": \"D\", \"description\": \"无目录结构\"}]','C','多级目录（树形目录）通过路径名唯一标识文件，允许不同用户或目录下使用相同文件名。','2025-06-15 13:38:49',NULL,NULL),(31,2,'操作系统','eaf4f794-dce3-4189-bc40-4a7af7144deb','single_choice','虚拟内存管理的基础是？','[{\"option\": \"A\", \"description\": \"交换技术\"}, {\"option\": \"B\", \"description\": \"覆盖技术\"}, {\"option\": \"C\", \"description\": \"局部性原理\"}, {\"option\": \"D\", \"description\": \"动态链接\"}]','C','虚拟内存利用程序运行的局部性原理（时间局部性和空间局部性），仅加载部分页面到内存即可运行。','2025-06-15 13:38:49',NULL,NULL),(32,2,'操作系统','eaf4f794-dce3-4189-bc40-4a7af7144deb','single_choice','用户程序通过哪种方式请求操作系统服务？','[{\"option\": \"A\", \"description\": \"系统调用\"}, {\"option\": \"B\", \"description\": \"中断\"}, {\"option\": \"C\", \"description\": \"异常\"}, {\"option\": \"D\", \"description\": \"进程通信\"}]','A','系统调用是用户程序访问操作系统服务的接口，通过陷入指令（如trap）从用户态切换到核心态。','2025-06-15 13:38:49',NULL,NULL),(33,2,'操作系统','eaf4f794-dce3-4189-bc40-4a7af7144deb','single_choice','以下页面置换算法中，哪一种理论上缺页率最低？','[{\"option\": \"A\", \"description\": \"先进先出（FIFO）\"}, {\"option\": \"B\", \"description\": \"最近最少使用（LRU）\"}, {\"option\": \"C\", \"description\": \"最优置换（OPT）\"}, {\"option\": \"D\", \"description\": \"最不常用（LFU）\"}]','C','OPT算法选择未来最长时间不被访问的页面置换，是理论最优解，但实际无法实现（需预知未来访问序列）。','2025-06-15 13:38:49',NULL,NULL),(34,2,'操作系统','eaf4f794-dce3-4189-bc40-4a7af7144deb','single_choice','微内核体系结构的特点是？','[{\"option\": \"A\", \"description\": \"所有服务都运行在核心态\"}, {\"option\": \"B\", \"description\": \"仅核心功能（如进程调度）在核心态，其他服务在用户态\"}, {\"option\": \"C\", \"description\": \"内核代码量庞大，性能更高\"}, {\"option\": \"D\", \"description\": \"不支持多线程\"}]','B','微内核将核心功能（如进程通信、线程调度）保留在核心态，其他服务（如文件系统）运行在用户空间，提高灵活性和可靠性。','2025-06-15 13:38:49',NULL,NULL),(35,2,'计算机网络','d5a757e8-32d0-4e88-813d-e7631ddf8a84','single_choice','计算机网络的主要目的是实现以下哪项功能？','[{\"option\": \"A\", \"description\": \"资源共享\"}, {\"option\": \"B\", \"description\": \"数据存储\"}, {\"option\": \"C\", \"description\": \"文件打印\"}, {\"option\": \"D\", \"description\": \"视频会议\"}]','A','参考资料中明确指出，计算机网络的定义是通过连接实现信息传输和资源共享的系统，因此主要目的是资源共享。','2025-06-15 13:44:37',NULL,NULL),(36,2,'计算机网络','d5a757e8-32d0-4e88-813d-e7631ddf8a84','single_choice','以下哪种属于按地理范围分类的网络类型？','[{\"option\": \"A\", \"description\": \"星形拓扑\"}, {\"option\": \"B\", \"description\": \"局域网（LAN）\"}, {\"option\": \"C\", \"description\": \"TCP/IP协议\"}, {\"option\": \"D\", \"description\": \"光纤传输\"}]','B','参考资料中提到网络按地理范围分类包括LAN（局域网）、MAN（城域网）、WAN（广域网）等，因此正确答案为局域网（LAN）。','2025-06-15 13:44:37',NULL,NULL),(37,2,'计算机网络','d5a757e8-32d0-4e88-813d-e7631ddf8a84','single_choice','OSI参考模型的第四层是？','[{\"option\": \"A\", \"description\": \"物理层\"}, {\"option\": \"B\", \"description\": \"数据链路层\"}, {\"option\": \"C\", \"description\": \"传输层\"}, {\"option\": \"D\", \"description\": \"应用层\"}]','C','OSI参考模型共七层，顺序为物理层（1）、数据链路层（2）、网络层（3）、传输层（4）、会话层（5）、表示层（6）、应用层（7），因此第四层是传输层。','2025-06-15 13:44:37',NULL,NULL),(38,2,'计算机网络','d5a757e8-32d0-4e88-813d-e7631ddf8a84','single_choice','TCP/IP模型的网际层对应OSI参考模型的哪一层？','[{\"option\": \"A\", \"description\": \"物理层\"}, {\"option\": \"B\", \"description\": \"网络层\"}, {\"option\": \"C\", \"description\": \"传输层\"}, {\"option\": \"D\", \"description\": \"应用层\"}]','B','TCP/IP模型的网际层（IP层）主要负责网络间的寻址和路由，与OSI参考模型的网络层功能一致。','2025-06-15 13:44:37',NULL,NULL),(39,2,'计算机网络','d5a757e8-32d0-4e88-813d-e7631ddf8a84','single_choice','以下哪种拓扑结构属于广播型网络？','[{\"option\": \"A\", \"description\": \"星型拓扑\"}, {\"option\": \"B\", \"description\": \"总线型拓扑\"}, {\"option\": \"C\", \"description\": \"环形拓扑\"}, {\"option\": \"D\", \"description\": \"网状拓扑\"}]','B','参考资料指出，广播型网络的典型结构包括总线型网络（多点共享链路），而星型、环形、网状属于点到点结构。','2025-06-15 13:44:37',NULL,NULL),(40,2,'计算机网络','d5a757e8-32d0-4e88-813d-e7631ddf8a84','single_choice','网络协议的三要素是？','[{\"option\": \"A\", \"description\": \"数据格式、编码、信号电平\"}, {\"option\": \"B\", \"description\": \"语法、语义、时序\"}, {\"option\": \"C\", \"description\": \"控制信息、速度匹配、同步\"}, {\"option\": \"D\", \"description\": \"接口、服务、协议\"}]','B','协议是通信规则的集合，其核心三要素为语法（格式）、语义（含义）、时序（顺序），参考资料明确提及此定义。','2025-06-15 13:44:37',NULL,NULL),(41,2,'计算机网络','d5a757e8-32d0-4e88-813d-e7631ddf8a84','single_choice','以下哪项不属于分层结构的优点？','[{\"option\": \"A\", \"description\": \"各层间相互独立\"}, {\"option\": \"B\", \"description\": \"促进标准化工作\"}, {\"option\": \"C\", \"description\": \"增加系统复杂性\"}, {\"option\": \"D\", \"description\": \"易于实现和维护\"}]','C','参考资料指出分层结构的优点包括各层独立、促进标准化、易于实现和维护，而“增加复杂性”是缺点，因此不属于优点。','2025-06-15 13:44:37',NULL,NULL),(42,2,'计算机网络','d5a757e8-32d0-4e88-813d-e7631ddf8a84','single_choice','用于连接不同网络并选择传输路径的设备是？','[{\"option\": \"A\", \"description\": \"集线器\"}, {\"option\": \"B\", \"description\": \"路由器\"}, {\"option\": \"C\", \"description\": \"网桥\"}, {\"option\": \"D\", \"description\": \"交换机\"}]','B','路由器工作在网络层，负责不同网络间的寻址和路径选择，是连接广域网和局域网的关键设备。','2025-06-15 13:44:37',NULL,NULL),(43,2,'计算机网络','d5a757e8-32d0-4e88-813d-e7631ddf8a84','single_choice','IP电话主要使用哪种数据交换技术？','[{\"option\": \"A\", \"description\": \"电路交换\"}, {\"option\": \"B\", \"description\": \"报文交换\"}, {\"option\": \"C\", \"description\": \"分组交换\"}, {\"option\": \"D\", \"description\": \"信元交换\"}]','C','参考资料提到IP电话基于分组交换技术，将语音数据分割为小分组传输，提高效率和灵活性。','2025-06-15 13:44:37',NULL,NULL),(44,2,'计算机网络','d5a757e8-32d0-4e88-813d-e7631ddf8a84','single_choice','衡量网络中数据传输速率的指标是？','[{\"option\": \"A\", \"description\": \"带宽\"}, {\"option\": \"B\", \"description\": \"时延\"}, {\"option\": \"C\", \"description\": \"吞吐量\"}, {\"option\": \"D\", \"description\": \"速率\"}]','D','速率（比特率）是单位时间内传输的二进制位数，直接衡量数据传输的快慢，是核心性能指标。','2025-06-15 13:44:37',NULL,NULL);
/*!40000 ALTER TABLE `ai_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message`
--

DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `course_id` bigint NOT NULL COMMENT '课程ID，作为聊天室的标识',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_sender_id` (`sender_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='课程聊天消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
/*!40000 ALTER TABLE `chat_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '课程描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '课程封面图片URL',
  `course_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程码',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-进行中，2-已结束',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_course_code` (`course_code`) USING BTREE,
  KEY `idx_teacher_id` (`teacher_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'数据结构','程序=数据结构+算法','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/1_1749369379900.png','F5FC94',1,1,'2025-06-08 07:55:58','2025-06-08 07:56:21'),(5,'操作系统','操作系统','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/temp_1749456738827.png','9566FB',1,1,'2025-06-09 08:12:29','2025-06-09 08:12:29'),(6,'高等数学','高数','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/temp_1750227934256.png','43FBE5',1,1,'2025-06-18 06:25:44','2025-06-18 06:25:44'),(7,'线性代数','线代','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/temp_1750227957152.png','AA5072',1,1,'2025-06-18 06:26:02','2025-06-18 06:26:02'),(8,'计算机组成原理','计组','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/temp_1750227980586.png','17AEB9',1,1,'2025-06-18 06:26:25','2025-06-18 06:26:25'),(9,'数据库系统概念','数据库','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/temp_1750228002602.png','8E9B03',1,1,'2025-06-18 06:26:50','2025-06-18 06:26:50'),(10,'计算机导论','计算机导论','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/temp_1750228028344.png','C4A051',1,1,'2025-06-18 06:27:21','2025-06-18 06:27:21'),(11,'python编程','python编程','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/11_1750298818781.png','9CCD2F',1,1,'2025-06-18 06:28:36','2025-06-19 02:07:02'),(12,'深度学习与计算机视觉','深度学习与计算机视觉','http://sxh8oib6z.hb-bkt.clouddn.com/course-covers/temp_1750228130205.png','319AC1',1,1,'2025-06-18 06:29:07','2025-06-18 06:29:07');
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
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_course_student` (`course_id`,`student_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='课程学生关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_student`
--

LOCK TABLES `course_student` WRITE;
/*!40000 ALTER TABLE `course_student` DISABLE KEYS */;
INSERT INTO `course_student` VALUES (4,5,2,'2025-06-09 08:12:42',0,1,'2025-06-09 08:12:42','2025-06-09 08:12:42'),(5,11,2,'2025-06-18 11:20:02',0,1,'2025-06-18 11:20:02','2025-06-18 11:20:02'),(6,10,1,'2025-06-18 11:22:43',0,1,'2025-06-18 11:22:43','2025-06-18 11:22:43'),(7,10,2,'2025-06-18 11:22:52',0,1,'2025-06-18 11:22:52','2025-06-18 11:22:52'),(8,1,1,'2025-06-18 11:29:20',0,1,'2025-06-18 11:29:20','2025-06-18 11:29:20'),(9,1,4,'2025-06-18 11:29:30',0,1,'2025-06-18 11:29:30','2025-06-18 11:29:30');
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
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '视频URL (节才有)',
  `duration` int DEFAULT NULL COMMENT '视频时长(秒, 节才有)',
  `order_num` int NOT NULL COMMENT '排序号',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='课程目录与视频表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_video`
--

LOCK TABLES `course_video` WRITE;
/*!40000 ALTER TABLE `course_video` DISABLE KEYS */;
INSERT INTO `course_video` VALUES (1,1,0,1,'引言',NULL,NULL,1,'2025-06-08 08:09:54','2025-06-08 08:09:54'),(8,5,0,1,'引言',NULL,NULL,1,'2025-06-09 08:20:46','2025-06-09 08:20:46'),(9,5,8,2,'简介','http://sxh8oib6z.hb-bkt.clouddn.com/videos/5/9_39ca918d-01ec-4ba6-9af3-910de44cab5d.mp4',NULL,1,'2025-06-09 08:20:51','2025-06-15 13:16:08'),(11,1,1,2,'112','http://sxh8oib6z.hb-bkt.clouddn.com/videos/1/11_1750298347748.mp4',NULL,1,'2025-06-17 07:26:05','2025-06-19 02:00:17');
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
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '作业描述',
  `start_time` timestamp NOT NULL COMMENT '开始时间',
  `end_time` timestamp NOT NULL COMMENT '结束时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_course_id` (`course_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='作业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homework`
--

LOCK TABLES `homework` WRITE;
/*!40000 ALTER TABLE `homework` DISABLE KEYS */;
INSERT INTO `homework` VALUES (37,1,'测试','请填写作业描述...','2025-06-27 16:00:00','2025-07-26 16:00:00','2025-06-15 13:08:28','2025-06-18 09:44:46'),(38,1,'新作业 - 2025年6月19日 上午10:03:58','请填写作业描述...','2025-06-18 18:03:59','2025-06-25 18:03:59','2025-06-19 02:03:59','2025-06-19 02:03:59');
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
  `question_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题内容',
  `question_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题类型 (e.g., single_choice, text_answer)',
  `options` json DEFAULT NULL COMMENT '选项 (for choice questions)',
  `answer` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '问题答案',
  `score` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_homework_id` (`homework_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='作业问题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homework_question`
--

LOCK TABLES `homework_question` WRITE;
/*!40000 ALTER TABLE `homework_question` DISABLE KEYS */;
INSERT INTO `homework_question` VALUES (22,37,'萨空了的节目你离开','programming',NULL,NULL,'10'),(23,37,'新的选阿文择题adsfa','choice','[{\"text\": \"a\"}, {\"text\": \"d\"}, {\"text\": \"d\"}, {\"text\": \"d\"}]','C','5');
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
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_homework_student` (`homework_id`,`student_id`) USING BTREE,
  KEY `idx_student_id` (`student_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='学生作业提交表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homework_submission`
--

LOCK TABLES `homework_submission` WRITE;
/*!40000 ALTER TABLE `homework_submission` DISABLE KEYS */;
INSERT INTO `homework_submission` VALUES (9,37,2,2,0,'{\"18\": {\"file\": {\"url\": \"http://sxh8oib6z.hb-bkt.clouddn.com/homework/b49b87bb-fe77-4cc4-abb6-abfa48df9e72.json\", \"name\": \"1.json\"}, \"text\": \"\"}, \"19\": \"D\"}','2025-06-15 13:09:28','2025-06-15 13:09:27','2025-06-15 13:09:27'),(10,37,4,2,5,'{\"22\": {\"file\": null, \"text\": \"\"}, \"23\": \"C\"}','2025-06-19 02:11:30','2025-06-19 02:11:29','2025-06-19 02:11:29');
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
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_student_video` (`student_id`,`video_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='学生视频学习进度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_video_progress`
--

LOCK TABLES `student_video_progress` WRITE;
/*!40000 ALTER TABLE `student_video_progress` DISABLE KEYS */;
INSERT INTO `student_video_progress` VALUES (1,2,2,116,'2025-06-08 08:44:39','2025-06-15 12:47:55');
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
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `role` tinyint NOT NULL COMMENT '角色：1-学生，2-教师',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像URL',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE,
  UNIQUE KEY `uk_email` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'XIAOLQ','$2a$10$miyohcW5UXtEMpH2C2X2OeP7P8X6tHf6eGEvwq6JKRKf9FAhhXNkS','2204240513@mail.wtu.edu.cn',2,'xiaolq','http://sxh8oib6z.hb-bkt.clouddn.com/avatars/1_1750146255683.png','2025-06-08 07:55:21','2025-06-08 07:55:21'),(2,'2204240513','$2a$10$kn/3mCboYeBHyoa8dj7wJ.4Ea7Qlzs.XCeeVu7oAwvWUM8hRrltdW','3061784569@qq.com',1,'spl','http://sxh8oib6z.hb-bkt.clouddn.com/avatars/2_1749457544611.png','2025-06-08 07:55:32','2025-06-08 07:55:32'),(3,'0000','$2a$10$aMC3Ey3qr41PCJ3Agg5nbujF3XN/EEhEfrwzuVHJgkNd8NQUYZgui','sunpeiling502@gmail.com',2,'spl',NULL,'2025-06-09 07:45:51','2025-06-09 07:45:51'),(4,'2204240512','$2a$10$yd2tZORS2H3q147mSGHeQuoWlSwnDLDR53auyhyj7JbYBZQLhQJ1q','306178456@qq.com',1,'xxx','http://sxh8oib6z.hb-bkt.clouddn.com/avatars/4_1750251642620.png','2025-06-18 11:28:29','2025-06-18 11:28:29');
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

-- Dump completed on 2025-06-19 10:14:35
