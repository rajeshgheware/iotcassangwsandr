package iot.sensors.location.domain;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.CassandraType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import com.datastax.driver.core.DataType.Name;

@Table(value = "events_count_by_sensor")
public class SensorEvents implements Comparable<SensorEvents>{

	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String sensor;// entity name: Android, some device

	@Column
	@CassandraType(type = Name.COUNTER)
	private Long events_count;

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public Long getEvents_count() {
		return events_count;
	}

	public void setEvents_count(Long events_count) {
		this.events_count = events_count;
	}
    @Override
    public int compareTo(SensorEvents sEvents) {
        long compareCount = sEvents.getEvents_count();
        /* For Ascending order*/
//        return this.studentage-compareage;

        /* For Descending order do like this */
        return Long.valueOf(compareCount - this.events_count).intValue();
    }

}
