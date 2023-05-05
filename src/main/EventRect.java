package main;

import java.awt.Rectangle;

public class EventRect extends Rectangle {
    private int eventRectDefaultX, eventRectDefaultY;
    private boolean eventDone = false;
    
    public int getEventRectDefaultX(){
        return eventRectDefaultX;
    }

    public void setEventRectDefaultX(int eventRectDefaultX){
        this.eventRectDefaultX = eventRectDefaultX;
    }

    public int getEventRectDefaultY(){
        return eventRectDefaultY;
    }

    public void setEventRectDefaultY(int eventRectDefaultY){
        this.eventRectDefaultY = eventRectDefaultY;
    }

    public boolean getEventDone(){
        return eventDone;
    }

    public void setEventDone(boolean eventDone){
        this.eventDone = eventDone;
    }
}
