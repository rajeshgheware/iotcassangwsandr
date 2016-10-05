package iot.sensors.location;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import iot.sensors.location.domain.DeviceLocation;
import iot.sensors.location.domain.DeviceLocationRepository;


public class DeviceLocationEventListener {
	
	Logger log = Logger.getLogger(DeviceLocationEventListener.class);
	@Autowired
	DeviceLocationRepository deviceLocationRepository;
	
	@EventListener(value={DeviceLocation.class})
	public void locationEvent(DeviceLocation deviceLocation){
		log.info("Device location received...reverse geocoding address for:"+deviceLocation);
//		deviceLocation.setLast_known_location("Bangalore India");
		deviceLocationRepository.save(deviceLocation);
	}

}
