package iot.sensors.location.domain;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceEventsRepository extends CassandraRepository<DeviceEvents> {

	 @Query("select * from events_count_by_device where imei = ?0")
	 DeviceEvents findByDevice(String imei);
	 
	 @Query("UPDATE events_count_by_device set events_count=events_count+1 where imei = ?0")
	 DeviceEvents updateDeviceEvents(String imei);
	
}