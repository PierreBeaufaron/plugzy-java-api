ALTER TABLE charging_station_group
DROP COLUMN IF EXISTS description;

ALTER TABLE charging_station_group
    ADD COLUMN description text;
