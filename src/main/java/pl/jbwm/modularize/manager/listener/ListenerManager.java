package pl.jbwm.modularize.manager.listener;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {

    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void callPostReflectListener(Object object) {

        for (Listener l : listeners)
            if(l instanceof PostReflectListener postReflectListener)
                postReflectListener.reflectionComplete(object);
    }

}
