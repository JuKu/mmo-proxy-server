CREATE TABLE IF NOT EXISTS `sectors` (
`sectorID` int(10) NOT NULL,
  `title` varchar(255) NOT NULL,
  `map_info_path` varchar(600) NOT NULL,
  `max_players` int(10) NOT NULL DEFAULT '200'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `sectors`
--
ALTER TABLE `sectors`
 ADD PRIMARY KEY (`sectorID`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `sectors`
--
ALTER TABLE `sectors`
MODIFY `sectorID` int(10) NOT NULL AUTO_INCREMENT;