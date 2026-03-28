package com.sumukh.apigateway.event;

public class RequestEvent {

    private String clientId;
    private long timeStamp;
    private boolean isAllowed;
    private String eventId;

    public RequestEvent() {

    }

    public RequestEvent(String clientId, long timeStamp, boolean isAllowed, String eventId) {
        this.clientId = clientId;
        this.timeStamp = timeStamp;
        this.isAllowed = isAllowed;
        this.eventId = eventId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public void setAllowed(boolean allowed) {
        isAllowed = allowed;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
