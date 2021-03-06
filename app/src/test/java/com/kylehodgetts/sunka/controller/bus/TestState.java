package com.kylehodgetts.sunka.controller.bus;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * Simple bus state with a counter
 */
public class TestState implements BusState {

    private int eventCalls;

    public TestState() {
        eventCalls = 0;
    }

    public TestState incrementEventCall(){
        eventCalls++;
        return this;
    }

    public int getEventCalls() {
        return eventCalls;
    }

}
