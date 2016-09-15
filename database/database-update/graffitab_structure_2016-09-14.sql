# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: devdb.graffitab.com (MySQL 5.5.49-0ubuntu0.14.04.1)
# Database: graffitab
# Generation Time: 2016-09-14 17:01:31 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table activity
# ------------------------------------------------------------

DROP TABLE IF EXISTS `activity`;

CREATE TABLE `activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `created_on` bigint(20) NOT NULL,
  `activity_type` varchar(50) NOT NULL,
  `commented_item_id` bigint(20) DEFAULT NULL,
  `comment_id` bigint(20) DEFAULT NULL,
  `created_item_id` bigint(20) DEFAULT NULL,
  `followed_user_id` bigint(20) DEFAULT NULL,
  `liked_item_id` bigint(20) DEFAULT NULL,
  `order_key` int(11) NOT NULL,
  `ip_address` varchar(100) DEFAULT NULL,
  `user_agent` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`user_id`),
  KEY `id` (`id`,`user_id`),
  KEY `activity_followed_user_id` (`followed_user_id`),
  KEY `activity_commented_item_fk` (`commented_item_id`),
  KEY `activity_created_item_fk` (`created_item_id`),
  KEY `activity_liked_item_fk` (`liked_item_id`),
  KEY `activity_comment_fk` (`comment_id`),
  CONSTRAINT `activity_commented_item_fk` FOREIGN KEY (`commented_item_id`) REFERENCES `streamable` (`id`),
  CONSTRAINT `activity_comment_fk` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `activity_created_item_fk` FOREIGN KEY (`created_item_id`) REFERENCES `streamable` (`id`),
  CONSTRAINT `activity_followed_user_id` FOREIGN KEY (`followed_user_id`) REFERENCES `gt_user` (`id`),
  CONSTRAINT `activity_liked_item_fk` FOREIGN KEY (`liked_item_id`) REFERENCES `streamable` (`id`),
  CONSTRAINT `activity_user_fk` FOREIGN KEY (`user_id`) REFERENCES `gt_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table asset
# ------------------------------------------------------------

DROP TABLE IF EXISTS `asset`;

CREATE TABLE `asset` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(40) CHARACTER SET utf8 NOT NULL,
  `asset_type` varchar(20) CHARACTER SET utf8 NOT NULL,
  `width` int(10) DEFAULT NULL,
  `height` int(10) DEFAULT NULL,
  `thumbnail_width` int(10) DEFAULT NULL,
  `thumbnail_height` int(10) DEFAULT NULL,
  `state` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table comment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `streamable_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `text` varchar(5000) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `created_on` bigint(20) NOT NULL,
  `order_key` int(11) NOT NULL,
  `updated_on` bigint(20) DEFAULT NULL,
  `is_deleted` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  KEY `itemId` (`streamable_id`),
  KEY `userId` (`user_id`),
  KEY `id` (`id`,`streamable_id`),
  CONSTRAINT `comments_streamable_fk` FOREIGN KEY (`streamable_id`) REFERENCES `streamable` (`id`),
  CONSTRAINT `comments_user_fk` FOREIGN KEY (`user_id`) REFERENCES `gt_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table conversation
# ------------------------------------------------------------

DROP TABLE IF EXISTS `conversation`;

CREATE TABLE `conversation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `imageId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table conversationImage
# ------------------------------------------------------------

DROP TABLE IF EXISTS `conversationImage`;

CREATE TABLE `conversationImage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `conversationId` bigint(20) NOT NULL,
  `image` longblob NOT NULL,
  PRIMARY KEY (`id`),
  KEY `conversationId` (`conversationId`),
  KEY `id` (`id`,`conversationId`),
  CONSTRAINT `del_conversationImage_on_conversation` FOREIGN KEY (`conversationId`) REFERENCES `conversation` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table conversationMessage
# ------------------------------------------------------------

DROP TABLE IF EXISTS `conversationMessage`;

CREATE TABLE `conversationMessage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `conversationId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `text` varchar(10000) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `date` bigint(20) NOT NULL,
  `state` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `conversationId` (`conversationId`),
  KEY `userId` (`userId`),
  KEY `id` (`id`,`conversationId`),
  CONSTRAINT `del_message_on_conversation` FOREIGN KEY (`conversationId`) REFERENCES `conversation` (`id`) ON DELETE CASCADE,
  CONSTRAINT `del_message_on_person` FOREIGN KEY (`userId`) REFERENCES `gt_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table conversationMessageSeen
# ------------------------------------------------------------

DROP TABLE IF EXISTS `conversationMessageSeen`;

CREATE TABLE `conversationMessageSeen` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `messageId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `date` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `messageId` (`messageId`),
  KEY `userId` (`userId`),
  KEY `id` (`id`,`messageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table conversationUser
# ------------------------------------------------------------

DROP TABLE IF EXISTS `conversationUser`;

CREATE TABLE `conversationUser` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `conversationId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `conversationId` (`conversationId`),
  KEY `userId` (`userId`),
  KEY `id` (`id`,`conversationId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table DATABASECHANGELOG
# ------------------------------------------------------------

DROP TABLE IF EXISTS `DATABASECHANGELOG`;

CREATE TABLE `DATABASECHANGELOG` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table DATABASECHANGELOGLOCK
# ------------------------------------------------------------

DROP TABLE IF EXISTS `DATABASECHANGELOGLOCK`;

CREATE TABLE `DATABASECHANGELOGLOCK` (
  `ID` int(11) NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table device
# ------------------------------------------------------------

DROP TABLE IF EXISTS `device`;

CREATE TABLE `device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `token` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `os_type` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL,
  `order_key` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`user_id`),
  KEY `id` (`id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table external_provider
# ------------------------------------------------------------

DROP TABLE IF EXISTS `external_provider`;

CREATE TABLE `external_provider` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `external_provider_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `external_user_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `access_token` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `order_key` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `external_provider_user_fk` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table following
# ------------------------------------------------------------

DROP TABLE IF EXISTS `following`;

CREATE TABLE `following` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `following_id` bigint(20) NOT NULL,
  `order_key` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`user_id`),
  KEY `followingId` (`following_id`),
  KEY `id` (`id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table gt_like
# ------------------------------------------------------------

DROP TABLE IF EXISTS `gt_like`;

CREATE TABLE `gt_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `streamable_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `order_key` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `itemId` (`streamable_id`),
  KEY `userId` (`user_id`),
  KEY `id` (`id`,`streamable_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table gt_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `gt_user`;

CREATE TABLE `gt_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `password` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `firstname` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `lastname` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `website` varchar(140) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `about` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar_asset_id` bigint(20) DEFAULT NULL,
  `cover_asset_id` bigint(20) DEFAULT NULL,
  `created_on` bigint(20) NOT NULL,
  `updated_on` bigint(20) DEFAULT NULL,
  `failed_logins` int(10) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `gt_user_avatar_asset_id` (`avatar_asset_id`),
  KEY `gt_user_cover_asset_id` (`cover_asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table gt_user_metadata
# ------------------------------------------------------------

DROP TABLE IF EXISTS `gt_user_metadata`;

CREATE TABLE `gt_user_metadata` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `metadata_key` varchar(50) CHARACTER SET utf8 NOT NULL,
  `metadata_value` varchar(1000) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `gt_user_metadata_fk` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table hashtag
# ------------------------------------------------------------

DROP TABLE IF EXISTS `hashtag`;

CREATE TABLE `hashtag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tag` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `streamable_id` bigint(20) NOT NULL,
  `order_key` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `itemId` (`streamable_id`),
  KEY `tag` (`tag`),
  KEY `id` (`id`,`streamable_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table location
# ------------------------------------------------------------

DROP TABLE IF EXISTS `location`;

CREATE TABLE `location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `address` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `order_key` int(11) NOT NULL,
  `created_on` bigint(20) NOT NULL,
  `updated_on` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`user_id`),
  KEY `id` (`id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table notification
# ------------------------------------------------------------

DROP TABLE IF EXISTS `notification`;

CREATE TABLE `notification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `is_read` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N',
  `created_on` bigint(20) NOT NULL,
  `notification_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `commenter_id` bigint(20) DEFAULT NULL,
  `commented_item_id` bigint(20) DEFAULT NULL,
  `comment_id` bigint(20) DEFAULT NULL,
  `follower_id` bigint(20) DEFAULT NULL,
  `liked_item_id` bigint(20) DEFAULT NULL,
  `liker_id` bigint(20) DEFAULT NULL,
  `mentioned_item_id` bigint(20) DEFAULT NULL,
  `mentioner_id` bigint(20) DEFAULT NULL,
  `order_key` int(11) NOT NULL,
  `mentioned_comment_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`user_id`),
  KEY `id` (`id`,`user_id`),
  KEY `notification_commenter_id` (`commenter_id`),
  KEY `notification_follower_id` (`follower_id`),
  KEY `notification_liker_id` (`liker_id`),
  KEY `notification_mentioner_id` (`mentioner_id`),
  KEY `notification_commented_item_fk` (`commented_item_id`),
  KEY `notification_liked_item_fk` (`liked_item_id`),
  KEY `notification_mentioned_item_fk` (`mentioned_item_id`),
  KEY `notification_comment_fk` (`comment_id`),
  KEY `notification_mentioned_comment_fk` (`mentioned_comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table session
# ------------------------------------------------------------

DROP TABLE IF EXISTS `session`;

CREATE TABLE `session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `session_id` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` blob,
  `version` int(11) NOT NULL,
  `created_on` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`user_id`),
  KEY `id` (`id`,`user_id`),
  KEY `sessionId` (`session_id`),
  KEY `userId_2` (`user_id`,`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table streamable
# ------------------------------------------------------------

DROP TABLE IF EXISTS `streamable`;

CREATE TABLE `streamable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `created_on` bigint(20) NOT NULL,
  `streamable_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_private` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N',
  `is_flagged` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N',
  `asset_id` bigint(20) NOT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `order_key` int(11) NOT NULL,
  `roll` double DEFAULT NULL,
  `yaw` double DEFAULT NULL,
  `pitch` double DEFAULT NULL,
  `is_deleted` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N',
  `updated_on` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `del_streamable_on_person` (`user_id`),
  KEY `id` (`id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='This table corresponds to the stream for one user instance. This means that all media that that the user creates will be stored in here and can be used for displaying the user''s profile.';




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
