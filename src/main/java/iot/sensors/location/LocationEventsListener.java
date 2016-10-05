package iot.sensors.location;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.utils.UUIDs;

import iot.sensors.location.domain.DeviceEventsRepository;
import iot.sensors.location.domain.DeviceLocation;
import iot.sensors.location.domain.DeviceLocationRepository;
import iot.sensors.location.domain.DeviceSensors;
import iot.sensors.location.domain.DeviceSensorsRepository;
import iot.sensors.location.domain.OnlineDevices;
import iot.sensors.location.domain.OnlineDevicesRepository;
import iot.sensors.location.domain.OnlineUsersRepository;
import iot.sensors.location.domain.SensorEvent;
import iot.sensors.location.domain.SensorEventRepository;
import iot.sensors.location.domain.SensorEventsRepository;


@Service
public class LocationEventsListener {

	Logger log = Logger.getLogger(LocationEventsListener.class);

	@Autowired
	SensorEventRepository sensorEventRepository;
	@Autowired
	OnlineDevicesRepository onlineDevicesRepository;
	@Autowired
	DeviceSensorsRepository deviceSensorsRepository;
	@Autowired
	DeviceEventsRepository deviceEventsRepository;
	@Autowired
	DeviceLocationRepository deviceLocationRepository;
	@Autowired
	SensorEventsRepository sensorEventsRepository;
	@Autowired
	OnlineUsersRepository onlineUsersRepository;
	
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@ServiceActivator(inputChannel = "sensorLocationMessagesAndroid")
	public void location(String message) {
		log.info("Event received:" + message);
		String[] decoded = message.split("\\s+");
		try{
			Long.parseLong(decoded[1].trim());
			if(decoded[1].trim().length()!=15) return;
		}catch(Exception exe){
			log.warn("Ignoring since no IMEI number:"+exe.getMessage());
			return;
		}
		//table sensor_events
		SensorEvent event = new SensorEvent();
		event.setImei(decoded[1]);//IMEI
		event.setSensor(decoded[0]);//sensor
		event.setStatus("NEW");
		event.setReceived_when(new Date());
		event.setUuid(UUIDs.random());
		event.setPayload(message);
		sensorEventRepository.save(event);
		applicationEventPublisher.publishEvent(event);
		//table online_devices
		OnlineDevices onlineDevices = new OnlineDevices();
		onlineDevices.setImei(decoded[1]);
		onlineDevices.setLast_event_sent(new Date());
		onlineDevicesRepository.save(onlineDevices);
		//table sensors_by_device
		DeviceSensors deviceSensors = new DeviceSensors();
		deviceSensors.setImei(decoded[1]);
		deviceSensors.setSensor_used(decoded[0]);
		deviceSensors.setSensor_used_when(new Date());
		deviceSensorsRepository.save(deviceSensors);
		//table device_location
		if(decoded[0].startsWith("nmea") && message.contains("$GPRMC") && message.contains(",A,")){
			DeviceLocation deviceLocation = new DeviceLocation();
			deviceLocation.setImei(decoded[1]);
			deviceLocation.setLast_known_location(message);
			deviceLocation.setLast_known_location_time(new Date());
			deviceLocationRepository.save(deviceLocation);
			applicationEventPublisher.publishEvent(deviceLocation);
		}
		//table events_count_by_sensor
		sensorEventsRepository.updateSensorEvents(decoded[0]);
		//table events_count_by_device
		deviceEventsRepository.updateDeviceEvents(decoded[1]);
		
		log.info("Sensor event saved:" + message);
	}

	@ServiceActivator(inputChannel = "sensorAccelerometerMessagesAndroid")
	public void accelerometer(String message) {
		location(message);
	}
	@ServiceActivator(inputChannel = "otherMessagesChannel")
	public void ignoredMessages(String message) {
		location(message);
	}
}
