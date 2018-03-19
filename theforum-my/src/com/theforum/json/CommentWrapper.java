package com.theforum.json;

public class CommentWrapper {
	private Long id;
	private String username;
	private String body;

	public CommentWrapper() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CommentWrapper(Long id, String username, String body) {
		super();
		this.id = id;
		this.username = username;
		this.body = body;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}



}


