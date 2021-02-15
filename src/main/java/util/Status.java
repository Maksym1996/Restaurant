package util;
import java.util.Arrays;
import java.util.List;

public enum Status {
	REJECTED("red"),
	PERFORMED("black"),
    DELIVERED_AND_PAID("green", PERFORMED),
    IN_DELIVERY("orange", DELIVERED_AND_PAID),
    COOKED("#FFD700", IN_DELIVERY, REJECTED),
    COOKING("yellow", COOKED),
    NEW("blue", COOKING, REJECTED);

    private final List<Status> nextStatuses;
    private final String color;

    Status(String color, Status ...nextStatuses) {
        this.nextStatuses = Arrays.asList(nextStatuses);
        this.color = color;
    }

    public List<Status> getNextStatuses() {
        return nextStatuses;
    }

    public String getColor() {
        return color;
    }

}