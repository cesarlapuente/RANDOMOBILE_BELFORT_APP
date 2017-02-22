package eu.randomobile.pnrlorraine.mod_global.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ResourcePoi extends Resource implements Parcelable {
	private String body;
	private String title;
	private int numberInRoute;
	private double longitude;
	private double latitude;
	private int type;
	private int nid;

	public int getNid() {
		return nid;
	}

	public void setNid(int nid) {
		this.nid = nid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public int getNumber() {
		return numberInRoute;
	}
	public void setNumber(int numberInRoute) {
		this.numberInRoute = numberInRoute;
	}

	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(body);
		dest.writeString(title);
		dest.writeInt(numberInRoute);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeInt(type);
		dest.writeInt(nid);
	}

	public static final Parcelable.Creator<ResourcePoi> CREATOR = new Parcelable.Creator<ResourcePoi>() {

		public ResourcePoi createFromParcel(Parcel in) {
			ResourcePoi complaint = new ResourcePoi();
			complaint.setBody(in.readString());
			complaint.setTitle(in.readString());
			complaint.setNumber(in.readInt());
			complaint.setLongitude(in.readDouble());
			complaint.setLatitude(in.readDouble());
			complaint.setType(in.readInt());
			complaint.setNid(in.readInt());
			return complaint;
		}

		@Override
		public ResourcePoi[] newArray(int size) {
			return new ResourcePoi[size];
		}
	};
	
	
}
