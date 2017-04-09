package com.github.filipelipan.popularmovies.util;

import com.squareup.otto.Bus;

/**
 * Created by lispa on 09/04/2017.
 */

public class EventBus extends Bus {
    public static final EventBus bus = new EventBus();

    public static Bus getInstance() { return  bus; }

    private EventBus(){

    }
}
