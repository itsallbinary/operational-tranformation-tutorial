package com.itsallbinary.tutorial.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import com.google.gson.Gson;
import com.itsallbinary.tutorial.websocket.MyWebSocketEndpoint;

public class Document {

	private static final String DELETE = "DELETE";

	private static final String INSERT = "INSERT";

	private Gson gson = new Gson();

	private StringBuilder documentText = new StringBuilder();

	private List<Operation> executedOperations = new ArrayList<>();

	private Long version = 0L;

	/*
	 * Synchronize to make it one thread at a time.
	 */
	public synchronized void apply(Operation operation) throws IOException {

		System.out.println("executedOperations = " + executedOperations + " ||||| ");

		System.out.println("action = " + operation.getAction() + " character = " + operation.getCharacter()
				+ " position = " + operation.getPosition());

		Operation xOp = transform(operation);
		// System.out.println("Before = " + operation);
		// System.out.println("After = " + xOp);

		if (INSERT.equals(operation.getAction())) {

			if (xOp.getPosition() >= documentText.length()) {
				documentText.append(xOp.getCharacter());
			} else {
				documentText.insert(xOp.getPosition(), xOp.getCharacter());
			}

		} else if (DELETE.equals(operation.getAction())) {
			documentText.deleteCharAt(operation.getPosition());
		}
		version++;
		replicate(xOp);

		System.out.println("#### SERVER | version: " + version + " | documentText: " + documentText);
	}

	private Operation transform(Operation currentOp) {

		Operation transformedOp = currentOp.clone();

		for (Operation pastOp : executedOperations) {
			// System.out.println("transform check pas op - " + pastOp);
			if (pastOp.getVersionBeforeThisOp() >= currentOp.getVersionBeforeThisOp()) {
				if (pastOp != null && currentOp.getSessionId().equals(pastOp.getSessionId())) {
					// return currentOp;
					// No changes as this past op was by same session id.
					// System.out.println("Op by Same session - " + currentOp.getSessionId());
				} else {
					// System.out.println("Op by different session - " + currentOp.getSessionId());
					if (pastOp != null && pastOp.getPosition() < currentOp.getPosition()) {
						// System.out.println("Position is before so change - " +
						// currentOp.getSessionId());
						// Operation currentOpClone = currentOp.clone();
						int position = transformedOp.getPosition();
						if (INSERT.equals(pastOp.getAction())) {
							position = position + 1;
						} else if (DELETE.equals(pastOp.getAction())) {
							position = position - 1;
						}
						// System.out.println("New position - " + position);
						transformedOp.setPosition(position);
						System.out.println("------Transformed------\n" + transformedOp);
						// return currentOpClone;
					} else {
						// System.out.println("Position not before so no change - " );
						// return currentOp;
					}
				}
			}
		}

		return transformedOp;

	}

	private void replicate(Operation op) {

		executedOperations.add(op);

		op.setVersionAfterThisOp(version);

		for (Session sess : MyWebSocketEndpoint.sessions.values()) {
			if (!op.getSessionId().equals(sess.getId())) {
				// System.out.println("Sending to = " + sess.getId());
				try {
					sess.getBasicRemote().sendText(gson.toJson(op));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Operation opAck = op.clone();
				opAck.setAck(true);
				try {
					sess.getBasicRemote().sendText(gson.toJson(opAck));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Map<String, Object> getContentWihVersion() {

		Map<String, Object> content = new HashMap<>();
		content.put("content", documentText.toString());
		content.put("version", version);

		return content;

	}

}
