package com.hxz.test.login.user.ws;


import com.hxz.test.login.common.websocket.MyEndpointConfigure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Slf4j
@Component
@ServerEndpoint(value = "/user/web",configurator = MyEndpointConfigure.class)
public class UserWSServer {


    @OnOpen
    public void onOpen(Session session) {
        log.error("on open::" + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        log.error("on close::" + session.getId());
    }

    @OnError
    public void onError(Session session,Throwable error) {
        log.error("on error::" + session.getId() + ":::" + error.getMessage());
    }

    @OnMessage
    public void onMessage(String message,Session session) {
        log.error("on message::" + session.getId() + "::" + message);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
