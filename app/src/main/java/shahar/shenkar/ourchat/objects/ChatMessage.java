package shahar.shenkar.ourchat.objects;

import java.util.Date;

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private String messageUserId;
    private long messageTime;
    private String course = "";

    public ChatMessage(String messageText, String messageUser, String messageUserId, String cn) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
        this.messageUserId = messageUserId;
        course = cn;
    }

    public ChatMessage(){

    }

    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public String getCourse() {
        return course;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
