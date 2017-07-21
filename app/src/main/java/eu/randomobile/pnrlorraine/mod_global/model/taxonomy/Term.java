package eu.randomobile.pnrlorraine.mod_global.model.taxonomy;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Term implements Serializable {
	private String tid;
	private String name;
	private String description;

	//Modif Thibault

	public Term(String tid, String name, String description) {
		this.tid = tid;
		this.name = name;
		this.description = description;
	}

	public Term() {
	}

	//
	
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
