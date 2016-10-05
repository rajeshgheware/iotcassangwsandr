package iot.sensors.location.domain;

import java.util.Date;
import java.util.UUID;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.CassandraType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import com.datastax.driver.core.DataType.Name;

@Table(value = "sensor_events")
public class SensorEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * IMEI of the device
	 */
	@PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String imei;// entity name: Android, some device

	/**
	 * Type of the sensor (light, proximity, location, magnetometer, gyroscope, accelerometer)
	 */
	@PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String sensor;// entity name: Android, some device

	@PrimaryKeyColumn(name = "uuid", ordinal = 6, type = PrimaryKeyType.CLUSTERED)
	@CassandraType(type = Name.UUID)
	private UUID uuid;// universal unique identifier

	@PrimaryKeyColumn(name = "received_when", ordinal = 4, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private Date received_when;// timeuuid

	@Column
	private String status;// NEW, BEING PROCESSED (VALIDATED / SENT to DEST),
							// PROCESSED (SUCCESS:Accepted by DEST / FAILED:
							// Rejected by DEST)

	@Column
	private String payload;

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public Date getReceived_when() {
		return received_when;
	}

	public void setReceived_when(Date received_when) {
		this.received_when = received_when;
	}

}
