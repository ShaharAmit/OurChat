package shahar.shenkar.ourchat.events;

public class WidgetEvent {
    String msg;

    public String getMsg() {
        return msg;
    }

    public WidgetEvent(String msg) {
        System.out.println("Event Fired: " + "WidgetEvent-"+msg);
        this.msg = msg;
    }
}
