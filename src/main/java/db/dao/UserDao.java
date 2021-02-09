package db.dao;

import java.util.List;

import db.entity.User;

public interface UserDao {

	List<User> getAllUsers() throws Exception;
	
	List<User> getRegisteredUsers(String registered) throws Exception;

	int insertUser(User model) throws Exception;
 
	User getUser(String email, String password) throws Exception;
	
	User getUser(int id) throws Exception;

	boolean updateUser(User model) throws Exception;
	
	boolean deleteUser(int id) throws Exception;

}
