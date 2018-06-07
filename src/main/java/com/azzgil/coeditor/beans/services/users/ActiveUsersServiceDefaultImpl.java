package com.azzgil.coeditor.beans.services.users;

import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ActiveUsersServiceDefaultImpl implements ActiveUsersService {

    private static final int INITIAL_CAPACITY = 16;

    @Value("${coeditor.rest.users_check_delay}")
    private long activeUsersCheckDelay;

    @Value("${coeditor.rest.active_user_expire_time}")
    private long activeUserExpireTime;

    @Value("${coeditor.rest.active_users_collapse_size}")
    private int collapseSize;

    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> updateHandler;
    private HashMap<Integer, HashMap<String, Date>> activeUsers;

    @PostConstruct
    public void initialize() {
        activeUsers = new HashMap<>(INITIAL_CAPACITY);
        executorService = Executors.newScheduledThreadPool(1);
        updateHandler = executorService.scheduleWithFixedDelay(this::updateActiveUsers, 0,
                activeUsersCheckDelay, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutdown() {
        updateHandler.cancel(true);
    }

    private void updateActiveUsers() {
        long time = new Date().getTime();
        for (Integer documentId : activeUsers.keySet()) {
            HashMap<String, Date> active = activeUsers.get(documentId);

            if (active != null) {
                active.entrySet().removeIf(e -> time - e.getValue().getTime() >= activeUserExpireTime);
            }
        }
    }

    @Override
    public void registerActiveUser(Integer documentId, String username) {
        if (!activeUsers.containsKey(documentId)) {
            HashMap<String, Date> active = new HashMap<>();
            active.put(username, new Date());
            activeUsers.put(documentId, active);
        } else {
            activeUsers.get(documentId).put(username, new Date());
        }
    }

    @Override
    public String getActiveUsersOf(Integer documentId) {
        if (activeUsers.containsKey(documentId)) {

            Set<String> active = activeUsers.get(documentId).keySet();
            return active.stream()
                    .limit(collapseSize)
                    .reduce((str, acc) -> acc + ", " + str)
                    .map(s -> "Active users: " + s)
                    .map(s -> active.size() > collapseSize ? s.concat(" and " + (active.size() - collapseSize) + " more"): s)
                    .orElse("");

        } else {
            return "document is not being viewed by anyone";
        }
    }
}
