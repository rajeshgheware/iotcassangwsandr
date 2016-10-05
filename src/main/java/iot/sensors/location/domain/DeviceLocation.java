package iot.sensors.location.domain;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

@Table(value = "device_location")
public class DeviceLocation {

	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String imei;

	@Column
	private String last_known_location;

	@Column
	private Date last_known_location_time;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getLast_known_location() {
		return last_known_location;
	}

	public void setLast_known_location(String last_known_location) {
		this.last_known_location = last_known_location;
	}

	public Date getLast_known_location_time() {
		return last_known_location_time;
	}

	public void setLast_known_location_time(Date last_known_location_time) {
		this.last_known_location_time = last_known_location_time;
	}

}
