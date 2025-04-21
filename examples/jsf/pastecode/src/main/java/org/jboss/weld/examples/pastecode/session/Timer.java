package org.jboss.weld.examples.pastecode.session;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Timeout;
import jakarta.ejb.TimerService;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * Responsible for starting the timer for printing recently added code fragments
 *
 * @author Pete Muir
 * @author Jozef Hartinger
 */
@Startup
@Singleton
public class Timer {

    private static final int INTERVAL = 30 * 1000;

    @Resource
    private TimerService timerService;

    @Inject
    private Event<TimerEvent> event;

    @PostConstruct
    void startTimer() {
        timerService.createTimer(0, INTERVAL, null);
    }

    @Timeout
    void timeout() {
        event.fire(new TimerEvent());
    }
}
