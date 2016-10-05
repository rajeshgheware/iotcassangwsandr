package iot.sensors.location.domain;

import java.util.Date;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

@Table(value = "online_devices")
public class OnlineDevices {

	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String imei;// entity name: Android, some device

	@Column
	private Date last_event_sent;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Date getLast_event_sent() {
		return last_event_sent;
	}

	public void setLast_event_sent(Date last_event_sent) {
		this.last_event_sent = last_event_sent;
	}


}
