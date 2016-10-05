// source: IMEI
//type: gps/accelerometer/nmea/battery/gyroscope/magnetometer/light/proximity/gravity/rotaion/step counter/
CREATE TABLE sensors."sensor_events" (
	uuid uuid,
	imei text,
	sensor text,
	payload text,
	status text,
	received_when timestamp,
	PRIMARY KEY ((imei, sensor), received_when,uuid)
) WITH CLUSTERING ORDER BY (received_when desc);
//select count(*) from online_devices ;
CREATE TABLE sensors."online_devices" (
	imei text,
	last_event_sent timestamp,
	PRIMARY KEY (imei)
) WITH default_time_to_live = 3600;
CREATE TABLE sensors."sensors_by_device"(
	imei text,
	sensor_used text,
	sensor_used_when timestamp,
PRIMARY KEY(imei,sensor_used)
);
//Below table satisfies queries
//query#1  total devices 
//query#2 summation of event_count gives total events
CREATE TABLE sensors."events_count_by_device" (
   imei TEXT,
   events_count counter,
   PRIMARY KEY (imei)
);
//query#5  Devices by country (last known location data)
CREATE TABLE sensors."device_location" (
   imei TEXT,
   last_known_location text,
   last_known_location_time timestamp,
   PRIMARY KEY (imei)
);
//query#4 this will give events by sensor 
CREATE TABLE sensors."events_count_by_sensor" (
   sensor TEXT,
   events_count counter,
   PRIMARY KEY (sensor)
);
//query#3 online users
CREATE TABLE sensors."online_users" (
	session_id text,
	ip text,
	PRIMARY KEY (session_id)
) WITH default_time_to_live = 1800;
