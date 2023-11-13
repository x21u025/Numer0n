CREATE TABLE IF NOT EXISTS `accesslog` (
  `no` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `process` varchar(50) NOT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `session` varchar(64) DEFAULT NULL,
  `user` varchar(50) DEFAULT NULL,
  `page` varchar(1024) DEFAULT NULL,
  `message` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `chatlog` (
  `no` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `player` varchar(50) DEFAULT NULL,
  `message` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `gamelog` (
  `no` int(11) NOT NULL AUTO_INCREMENT,
  `gameid` varchar(64) NOT NULL,
  `date` datetime NOT NULL,
  `process` varchar(50) NOT NULL,
  `player` varchar(50) DEFAULT NULL,
  `number` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `gamelogs` (
  `no` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `id` varchar(64) NOT NULL,
  PRIMARY KEY (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `record` (
  `id` varchar(50) NOT NULL,
  `win` int(11) NOT NULL DEFAULT 0,
  `lose` int(11) NOT NULL DEFAULT 0,
  `draw` int(11) NOT NULL DEFAULT 0,
  `surrender` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `score` (
  `id` varchar(50) NOT NULL,
  `score` int(11) NOT NULL DEFAULT 1500,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user` (
  `id` varchar(32) NOT NULL,
  `pass` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL,
  `admin` tinyint(1) NOT NULL DEFAULT 0,
  `hidden` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
