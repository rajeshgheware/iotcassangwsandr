package iot.sensors.location.domain;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

@Table(value = "sensors_by_device")
public class DeviceSensors {

	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String imei;// entity name: Android, some device

	/**
	 * Type of the sensor (light, proximity, location, magnetometer, gyroscope, accelerometer)
	 */
	@PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String sensor_used;

	@Column
	private Date sensor_used_when;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getSensor_used() {
		return sensor_used;
	}

	public void setSensor_used(String sensor_used) {
		this.sensor_used = sensor_used;
	}

	public Date getSensor_used_when() {
		return sensor_used_when;
	}

	public void setSensor_used_when(Date sensor_used_when) {
		this.sensor_used_when = sensor_used_when;
	}
}
