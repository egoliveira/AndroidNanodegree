package br.com.gielamo.popularmovies.controller;

import org.greenrobot.eventbus.EventBus;

public abstract class BusController {
    private final EventBus mBus;

    BusController() {
        mBus = new EventBus();
    }

    public void register(Object subscriber) {
        if (subscriber != null) {
            mBus.register(subscriber);
        }
    }

    public void unregister(Object subscriber) {
        if (subscriber != null) {
            mBus.unregister(subscriber);
        }
    }

    protected void post(Object message) {
        if (message != null) {
            mBus.post(message);
        }
    }
}
