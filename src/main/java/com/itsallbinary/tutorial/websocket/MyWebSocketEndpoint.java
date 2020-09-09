package com.itsallbinary.tutorial.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.itsallbinary.tutorial.editor.Document;
import com.itsallbinary.tutorial.editor.Operation;

/**
 * Indicates that this class is a websocket endpoint with URL "/server-endpoint"
 */
@ServerEndpoint("/server-endpoint")
public class MyWebSocketEndpoint {

	private Gson gson = new Gson();

	public static Map<String, Session> sessions = new HashMap<>();

	public static Document document = new Document();

	/**
	 * Container calls this method when browser connects to this endpoint.
	 */
	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Session Open [" + session.getId() + "]");

		sessions.put(session.getId(), session);

		System.out.println("sessions = " + sessions);

		Map<String, Object> returnVal = new HashMap<>();
		returnVal.putAll(document.getContentWihVersion());
		returnVal.put("sessionId", session.getId());

		try {
			session.getBasicRemote().sendText(gson.toJson(returnVal));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Container calls this method when websocket connection is closed by browser or
	 * server.
	 */
	@OnClose
	public void onClose(Session session) {
		System.out.println("Session Close [" + session.getId() + "]");
		sessions.remove(session.getId());
	}

	/**
	 * Process message received from browser.
	 * 
	 * @throws IOException
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		System.out.println("------ Message received [" + session.getId() + "]------\n" + message);
		Operation operation = gson.fromJson(message, Operation.class);
		operation.setSessionId(session.getId());
		document.apply(operation);
	}

	/**
	 * Container calls this method when there is an error in websocket
	 * communication.
	 */
	@OnError
	public void onError(Throwable t) {
		System.out.println("Error - " + t.getMessage());
		t.printStackTrace();
	}
}
