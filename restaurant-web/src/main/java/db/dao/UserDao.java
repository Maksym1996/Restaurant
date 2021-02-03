package db.dao;

import java.sql.SQLException;
import java.util.List;

import db.entity.User;

public interface UserDao {

	List<User> getAllUsers() throws Exception;

	int insertUser(User model) throws Exception;
 
	User getUser(String email, String password) throws Exception;

	boolean updateUser(User model) throws Exception;

}
