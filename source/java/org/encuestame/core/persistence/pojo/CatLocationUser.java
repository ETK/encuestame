package org.encuestame.core.persistence.pojo;

// Generated 29-may-2009 13:17:50 by Hibernate Tools 3.2.2.GA

/**
 * CatLocationUser generated by hbm2java
 */
public class CatLocationUser implements java.io.Serializable {

	private CatLocationUserId id;
	private SecUsers secUsers;
	private Boolean state;

	public CatLocationUser() {
	}

	public CatLocationUser(CatLocationUserId id, SecUsers secUsers) {
		this.id = id;
		this.secUsers = secUsers;
	}

	public CatLocationUser(CatLocationUserId id, SecUsers secUsers,
			Boolean state) {
		this.id = id;
		this.secUsers = secUsers;
		this.state = state;
	}

	public CatLocationUserId getId() {
		return this.id;
	}

	public void setId(CatLocationUserId id) {
		this.id = id;
	}

	public SecUsers getSecUsers() {
		return this.secUsers;
	}

	public void setSecUsers(SecUsers secUsers) {
		this.secUsers = secUsers;
	}

	public Boolean getState() {
		return this.state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

}