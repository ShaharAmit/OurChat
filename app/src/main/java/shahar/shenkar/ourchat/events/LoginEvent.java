package shahar.shenkar.ourchat.events;

public class LoginEvent {
    private final Boolean isSuccess;

    public LoginEvent(Boolean isSuccess) {
        this.isSuccess = isSuccess;
        System.out.println("Event Fired: " + "LoginEvent");
    }
    public Boolean getBool() {
        return isSuccess;
    }
}