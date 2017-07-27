package eu.randomobile.pnrlorraine.mod_global.model.taxonomy;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Term implements Serializable {
	private String tid;
	private String name;
	private String description;

	//Modif Thibault
	private String idParent;

	public Term(String tid, String name, String description, String idParent) {
		this.tid = tid;
		this.name = name;
		this.description = description;
		this.idParent = idParent;
	}

	public Term() {
	}

	public String getIdParent() {
		return idParent;
	}

	public void setIdParent(String idParent) {
		this.idParent = idParent;
	}

	@Override
	public String toString() {
		return "Term{" +
				"tid='" + tid + '\'' +
				"idp='" + getIdParent() + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				'}';
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
