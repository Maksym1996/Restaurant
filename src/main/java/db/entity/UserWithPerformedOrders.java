package db.entity;

public class UserWithPerformedOrders {
	
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private int countOrders;

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
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
	
	@Override
	public String toString() {
		return "UserWithPerformedOrders [firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber="
				+ phoneNumber + ", countOrders=" + countOrders + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserWithPerformedOrders other = (UserWithPerformedOrders) obj;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		return true;
	}
}
