package iot.sensors.location.rest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import iot.sensors.location.domain.OnlineUsers;
import iot.sensors.location.domain.OnlineUsersRepository;

@Controller
public class IamAlive {
	
	Logger logger = Logger.getLogger(IamAlive.class);
	
	@Autowired
	OnlineUsersRepository onlineUsersRepository;
	@Autowired
	LiveUpdatesToBrowser liveUpdatesToBrowser;
	/**
	 * Connected web browser client send heart beat messages to this endpoint /app/i-am-alive
	 * @param message
	 * @param connectionId
	 * @param ip
	 * @throws Exception
	 */
	@MessageMapping("/i-am-alive")
	@ResponseStatus(HttpStatus.OK)
//	public void live(String message, @Header(IpHeaders.CONNECTION_ID) String connectionId,@Header(IpHeaders.IP_ADDRESS) String ip) throws Exception{
	public void live(@RequestBody Client client, SimpMessageHeaderAccessor simpMessageHeader) throws Exception{
		String address = simpMessageHeader.getSessionAttributes().get("address").toString();
		//table online_users
		OnlineUsers onlineUsers = new OnlineUsers();
		onlineUsers.setSession_id(address+"/"+simpMessageHeader.getSessionId());
		onlineUsers.setIp(address);
		onlineUsersRepository.save(onlineUsers);
		logger.info("Websocket message from:"+address+"/"+simpMessageHeader.getSessionId()+" >>"+client.getMessage());
	}
	
}
class Client{
	
	public String name;
	public String message;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
