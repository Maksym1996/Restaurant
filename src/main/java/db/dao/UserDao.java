package db.dao;

import java.util.List;

import db.entity.User;
import db.entity.UserWithPerformedOrders;
import exception.DBException;

/**
 * The interface describing the general view of the data access methods for the
 * user entity
 *
 */
public interface UserDao {

	List<User> getUsersForManager() throws DBException;

	List<User> getUsersByRegistered(String registered) throws DBException;

	User getUserByNumberAndPass(String email, String password) throws DBException;

	User getUserById(int id) throws DBException;

	User getUserByNumber(String phoneNumber) throws DBException;

	int insertUser(User model) throws DBException;

	boolean updateUser(User model) throws DBException;

	List<UserWithPerformedOrders> getUserAndHimCountPerformedOrders() throws DBException ;

}
