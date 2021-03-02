package br.com.reign.loftylibrary.activity.notifications;

public class Notification {

    private String text;
    private long timestamp;
    private String fromId;
    private String fromName;

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getFromId() {
        return fromId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public String getFromName() {
        return fromName;
    }
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}