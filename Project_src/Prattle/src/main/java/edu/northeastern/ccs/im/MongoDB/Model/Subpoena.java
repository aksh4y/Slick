package edu.northeastern.ccs.im.MongoDB.Model;

import java.time.LocalDate;

public class Subpoena {

//	private String _id;
	private String user1;
	private String user2;
	private String group;
	private LocalDate startDate;
	private LocalDate endDate;

	public Subpoena(String user1, String user2, String group, LocalDate fromDate, LocalDate toDate) {
		this.user1 = user1;
		this.user2 = user2;
		this.group = group;
		this.startDate = fromDate;
		this.endDate = toDate;
	}

	
//	public String get_id() {
//		return _id;
//	}
//
//
//	public void set_id(String _id) {
//		this._id = _id;
//	}


	public String getUser1() {
		return user1;
	}

	public void setUser1(String user1) {
		this.user1 = user1;
	}

	public String getUser2() {
		return user2;
	}

	public void setUser2(String user2) {
		this.user2 = user2;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	
}