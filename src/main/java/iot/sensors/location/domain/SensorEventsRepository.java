package iot.sensors.location.domain;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorEventsRepository extends CassandraRepository<SensorEvents> {

	 @Query("select * from events_count_by_sensor where sensor = ?0")
	 SensorEvents findBySensor(String sensor);
	 
	 @Query("UPDATE events_count_by_sensor set events_count=events_count+1 where sensor = ?0")
	 SensorEvents updateSensorEvents(String sensor);

}