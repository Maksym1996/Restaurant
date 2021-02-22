package db.entity;

public class UserWithPerformedOrders {
	
	private String firstName;
	private String lastName;
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	private String phoneNumber;
	private int countOrders;
	
	public void setCountOrders(int countOrders) {
		this.countOrders = countOrders;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public int getCountOrders() {
		return countOrders;
	}

}
