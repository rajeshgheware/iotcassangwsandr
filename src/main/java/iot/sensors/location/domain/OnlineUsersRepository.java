package iot.sensors.location.domain;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnlineUsersRepository extends CassandraRepository<OnlineUsers> {

	// @Query("select * from message where valueDate = ?0")
	// Iterable findByFname(String valueDate);

}