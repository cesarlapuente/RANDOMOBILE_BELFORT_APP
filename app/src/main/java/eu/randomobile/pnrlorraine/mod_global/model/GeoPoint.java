package eu.randomobile.pnrlorraine.mod_global.model;

import android.os.Parcel;
import android.os.Parcelable;


public class GeoPoint implements Parcelable {
	private double latitude;
	private double longitude;
	private double altitude;
	
	
	public static float calculateDistance(GeoPoint gp1, GeoPoint gp2){
		return GeoPoint.calculateDistance( (float)gp1.getLatitude(), (float)gp2.getLatitude(), (float)gp1.getLongitude(), (float)gp2.getLongitude());
	}
	
	
	public static float calculateDistance(float lat1, float lat2, float lon1, float lon2){
		double radioTierra = 3958.75;
		double dLat = 0;
		double dLng = 0;
		if(lat1 > lat2){
			dLat = Math.toRadians(lat1 - lat2);
		}else{
			dLat = Math.toRadians(lat2 - lat1);
		}
		if(lon1 > lon2){
			dLng = Math.toRadians(lon1 - lon2);
		}else{
			dLng = Math.toRadians(lon2 - lon1);
		}
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = radioTierra * c;
	    int meterConversion = 1609;
	    return (float) (dist * meterConversion);
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeDouble(altitude);
	}


	public static final Parcelable.Creator<GeoPoint> CREATOR = new Parcelable.Creator<GeoPoint>() {

		public GeoPoint createFromParcel(Parcel in) {
			GeoPoint complaint = new GeoPoint();
			complaint.setLatitude(in.readDouble());
			complaint.setLongitude(in.readDouble());
			complaint.setAltitude(in.readDouble());
			return complaint;
		}

		@Override
		public GeoPoint[] newArray(int size) {
			return new GeoPoint[size];
		}
	};
	
	
	
}
