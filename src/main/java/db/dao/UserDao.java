package db.dao;

import java.util.List;

import db.entity.User;
import exception.DBException;

public interface UserDao {

	List<User> getUsersForManager() throws DBException;

	List<User> getUsersByRegistered(String registered) throws DBException;

	User getUserByNumberAndPass(String email, String password) throws DBException;

	User getUserById(int id) throws DBException;

	User getUserByNumber(String phoneNumber) throws DBException;

	int insertUser(User model) throws DBException;

	boolean updateUser(User model) throws DBException;

}
