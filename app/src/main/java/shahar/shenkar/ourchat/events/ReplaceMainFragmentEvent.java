package shahar.shenkar.ourchat.events;

public class ReplaceMainFragmentEvent {
    private final String message;

    public ReplaceMainFragmentEvent(String message) {
        this.message = message;
        System.out.println("Event Fired: " + "ReplaceMainFragmentEvent-"+message);
    }

    public String getMessage() {
        return message;
    }
}
