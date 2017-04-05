package eu.randomobile.pnrlorraine.mod_global.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ResourceLink extends Resource implements Parcelable {
	private String url;
	private String title;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(url);
		dest.writeString(title);
	}

	public static final Parcelable.Creator<ResourceLink> CREATOR = new Parcelable.Creator<ResourceLink>() {

		public ResourceLink createFromParcel(Parcel in) {
			ResourceLink complaint = new ResourceLink();
			complaint.setUrl(in.readString());
			complaint.setTitle(in.readString());
			return complaint;
		}

		@Override
		public ResourceLink[] newArray(int size) {
			return new ResourceLink[size];
		}
	};
	
	
}
