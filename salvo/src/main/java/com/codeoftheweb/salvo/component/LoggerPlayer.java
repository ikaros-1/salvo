package com.codeoftheweb.salvo.component;

import com.codeoftheweb.salvo.ActivePlayerStore;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.List;

@Component
public class LoggerPlayer implements HttpSessionBindingListener {

    private String username;
    private ActivePlayerStore activePlayerStore;

    public LoggerPlayer(String username, ActivePlayerStore activePlayerStore) {
        this.username = username;
        this.activePlayerStore = activePlayerStore;
    }

    public LoggerPlayer() {}

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        List<String> users = activePlayerStore.getPlayers();
        LoggerPlayer user = (LoggerPlayer) event.getValue();
        if (!users.contains(user.getUsername())) {
            users.add(user.getUsername());
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        List<String> users = activePlayerStore.getPlayers();
        LoggerPlayer user = (LoggerPlayer) event.getValue();
        if (users.contains(user.getUsername())) {
            users.remove(user.getUsername());
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ActivePlayerStore getActivePlayerStore() {
        return activePlayerStore;
    }

    public void setActivePlayerStore(ActivePlayerStore activePlayerStore) {
        this.activePlayerStore = activePlayerStore;
    }
}