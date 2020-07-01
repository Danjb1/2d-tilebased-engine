package com.danjb.engine.application;

public interface StateContext {

    void changeState(State newState);

    State getState();

}
