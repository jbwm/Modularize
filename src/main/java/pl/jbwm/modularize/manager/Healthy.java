package pl.jbwm.modularize.manager;

public interface Healthy {

    /**
     * Do something if server is unhealthy
     */
    void ifUnhealthy();

    /**
     * Do something when server health is back
     */
    void ifBackToHealth();



}
