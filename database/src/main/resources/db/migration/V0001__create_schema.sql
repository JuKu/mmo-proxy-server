CREATE TABLE IF NOT EXISTS `${prefix}sectors` (
`sectorID` int(10) NOT NULL,
  `title` varchar(255) NOT NULL,
  `map_info_path` varchar(600) NOT NULL,
  `max_players` int(10) NOT NULL DEFAULT '200'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indizes für die Tabelle `sectors`
--
ALTER TABLE `${prefix}sectors`
 ADD PRIMARY KEY (`sectorID`);

--
-- AUTO_INCREMENT für Tabelle `sectors`
--
ALTER TABLE `${prefix}sectors`
MODIFY `sectorID` int(10) NOT NULL AUTO_INCREMENT;

CREATE TABLE IF NOT EXISTS `${prefix}characters` (
`cid` int(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` enum('PLAYER','NPC') NOT NULL DEFAULT 'PLAYER',
  `userid` int(10) NOT NULL DEFAULT '-1',
  `data` text NOT NULL,
  `current_sectorID` int(10) NOT NULL DEFAULT '1',
  `auto_join` int(10) NOT NULL DEFAULT '0',
  `visible` int(10) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indizes für die Tabelle `${prefix}characters`
--
ALTER TABLE `${prefix}characters`
 ADD PRIMARY KEY (`cid`), ADD KEY `userid` (`userid`), ADD KEY `current_sectorID` (`current_sectorID`), ADD KEY `auto_join` (`auto_join`);

--
-- AUTO_INCREMENT für Tabelle `${prefix}characters`
--
ALTER TABLE `${prefix}characters`
MODIFY `cid` int(10) NOT NULL AUTO_INCREMENT;

CREATE TABLE IF NOT EXISTS `${prefix}races` (
`id` int(10) NOT NULL,
  `title` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `mmo_races`
--
ALTER TABLE `${prefix}races`
 ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `mmo_races`
--
ALTER TABLE `${prefix}races`
MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;