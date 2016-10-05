package iot.sensors.location.domain;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.CassandraType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import com.datastax.driver.core.DataType.Name;

@Table(value = "events_count_by_device")
public class DeviceEvents {

	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String imei;// entity name: Android, some device

	@Column
	@CassandraType(type = Name.COUNTER)
	private Long events_count;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Long getEvents_count() {
		return events_count;
	}

	public void setEvents_count(Long events_count) {
		this.events_count = events_count;
	}

}
