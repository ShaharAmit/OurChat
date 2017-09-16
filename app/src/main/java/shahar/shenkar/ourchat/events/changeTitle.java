package shahar.shenkar.ourchat.events;

public class changeTitle {
    private final String message;

    public changeTitle(String message) {
        this.message = message;
        System.out.println("Event Fired: " + "changeTitle-"+message);
    }
    public String getMessage() {
        return message;
    }
}
