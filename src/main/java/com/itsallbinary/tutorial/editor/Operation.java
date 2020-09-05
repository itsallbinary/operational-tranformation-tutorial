package com.itsallbinary.tutorial.editor;

public class Operation {

	private String sessionId;
	private String action;
	private String character;
	private Integer position;
	private long versionBeforeThisOp;
	private long versionAfterThisOp;
	private boolean isAck;
	private Long timestamp;

	@Override
	protected Operation clone() {
		Operation op = new Operation();
		op.setSessionId(this.sessionId);
		op.setAction(this.action);
		op.setCharacter(this.character);
		op.setPosition(this.position);
		op.setVersionBeforeThisOp(versionBeforeThisOp);
		op.setVersionAfterThisOp(this.versionAfterThisOp);
		op.setTimestamp(this.timestamp);
		return op;
	}

	@Override
	public String toString() {
		return "sessionId:" + sessionId + ",action:" + action + ",character:" + character + ",position:" + position
				+ ",versionBeforeThisOp:" + versionBeforeThisOp + ",versionAfterThisOp:" + versionAfterThisOp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public long getVersionBeforeThisOp() {
		return versionBeforeThisOp;
	}

	public void setVersionBeforeThisOp(long versionBeforeThisOp) {
		this.versionBeforeThisOp = versionBeforeThisOp;
	}

	public long getVersionAfterThisOp() {
		return versionAfterThisOp;
	}

	public void setVersionAfterThisOp(long versionAfterThisOp) {
		this.versionAfterThisOp = versionAfterThisOp;
	}

	public boolean isAck() {
		return isAck;
	}

	public void setAck(boolean isAck) {
		this.isAck = isAck;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

}
