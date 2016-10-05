package iot.sensors.location.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import iot.sensors.location.domain.DeviceEvents;
import iot.sensors.location.domain.DeviceEventsRepository;
import iot.sensors.location.domain.DeviceLocation;
import iot.sensors.location.domain.DeviceLocationRepository;
import iot.sensors.location.domain.DeviceSensorsRepository;
import iot.sensors.location.domain.OnlineDevices;
import iot.sensors.location.domain.OnlineDevicesRepository;
import iot.sensors.location.domain.OnlineUsersRepository;
import iot.sensors.location.domain.SensorEvent;
import iot.sensors.location.domain.SensorEventRepository;
import iot.sensors.location.domain.SensorEvents;
import iot.sensors.location.domain.SensorEventsRepository;

@Controller
public class LiveUpdatesToBrowser {

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
	SimpMessagingTemplate simpMessagingTemplate;

	@EventListener(value = { SensorEvent.class })
	@SendTo("/topic/devices-total")
	@RequestMapping(value = "/rest/devices-total")
	public @ResponseBody int deviceEvents() {

		Iterable<DeviceEvents> iteratorable = deviceEventsRepository.findAll();
		List<DeviceEvents> devices = new ArrayList<DeviceEvents>();
		Iterator iterator = iteratorable.iterator();
		while (iterator.hasNext()) {
			DeviceEvents deviceEvents = (DeviceEvents) iterator.next();
			if (deviceEvents.getImei().length() == 15) {
				devices.add(deviceEvents);
			}
		}
		simpMessagingTemplate.convertAndSend("/topic/devices-total", devices.size());
		return devices.size();
	}

	@EventListener(value = { SensorEvent.class })
	@SendTo("/topic/devices-online")
	@RequestMapping(value="/rest/devices-online")
	public @ResponseBody long onlineDevices() {
		long count = onlineDevicesRepository.count();
		simpMessagingTemplate.convertAndSend("/topic/devices-online", count);
		return count;
	}

	@EventListener(value = { SensorEvent.class })
	@SendTo("/topic/users-online")
	@RequestMapping(value = "/rest/users-online")
	public @ResponseBody long onlineUsers() {
		long count = onlineUsersRepository.count();
		simpMessagingTemplate.convertAndSend("/topic/users-online", count);
		return count;
	}

	@EventListener(value = { SensorEvent.class })
	@SendTo("/topic/sensor-events")
	@RequestMapping(value="/rest/sensor-events")
	public @ResponseBody List<SensorEvents> sensorEvents() {
		Iterable<SensorEvents> iteratorable = sensorEventsRepository.findAll();
		List<SensorEvents> sensors = new ArrayList<SensorEvents>();
		Iterator iterator = iteratorable.iterator();
		while (iterator.hasNext()) {
			SensorEvents sensorEvents = (SensorEvents) iterator.next();
			sensors.add(sensorEvents);
		}
		Collections.sort(sensors);
		simpMessagingTemplate.convertAndSend("/topic/sensor-events", sensors);
		return sensors;
	}

	@EventListener(value = { SensorEvent.class })
	@SendTo("/topic/device-location")
	@RequestMapping(value="/rest/device-location")
	public @ResponseBody List<DeviceLocation> deviceLocation() {
		Iterable<DeviceLocation> iteratorable = deviceLocationRepository.findAll();
		List<DeviceLocation> devices = new ArrayList<DeviceLocation>();
		Iterator iterator = iteratorable.iterator();
		while (iterator.hasNext()) {
			DeviceLocation deviceLocation = (DeviceLocation) iterator.next();
			if (deviceLocation.getImei().length() == 15) {
				devices.add(deviceLocation);
			}
		}
		simpMessagingTemplate.convertAndSend("/topic/devices-location", devices);
		return devices;

	}

}
