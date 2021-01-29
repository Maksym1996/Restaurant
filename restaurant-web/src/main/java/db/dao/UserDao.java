package db.dao;

import java.util.List;

import db.entity.User;

public interface UserDao {

	List<User> getAllUsers();

	int insertUser(User model);

	User getUser(String email);

	boolean updateUser(User model);

}
