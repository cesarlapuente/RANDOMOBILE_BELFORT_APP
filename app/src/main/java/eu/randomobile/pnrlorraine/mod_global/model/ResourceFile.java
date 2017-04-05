package eu.randomobile.pnrlorraine.mod_global.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import org.json.JSONObject;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

public class ResourceFile extends Resource implements Parcelable {

	private String fid;
	private String fileName;
	private String fileUrl;
	private String fileBody;
	private String fileMime;
	private String fileType;
	private String fileTitle;
	private String copyright;

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileMime() {
		return fileMime;
	}

	public void setFileMime(String fileMime) {
		this.fileMime = fileMime;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileTitle() {
		return fileTitle;
	}

	public void setFileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getFileBody() {
		return fileBody;
	}

	public void setFileBody(String fileBody) {
		this.fileBody = fileBody;
	}

	// Interface para comunicarse con las llamadas asincronas
	public static ResourceFileInterface resourceFileInterface;

	public static interface ResourceFileInterface {
		public void seSubioFichero(String fid, String uri);

		public void producidoErrorAlSubirFichero(String error);
	}

	public static void fileUpload(Application application,
			String base64EncodedString, String fileName) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("file", base64EncodedString);
		params.put("filename", fileName);
		params.put("filepath", "public://" + fileName);

		MainApp app = (MainApp) application;

		app.clienteDrupal.customMethodCallPost("file",
				new AsyncHttpResponseHandler() {
					public void onSuccess(String response) {

						Log.d("Milog", "Respuesta de subir fichero: "
								+ response);
						if (response != null && !response.equals("")) {

							try {
								JSONObject dicRes = new JSONObject(response);
								if (dicRes != null) {
									String fid = dicRes.getString("fid");
									String uri = dicRes.getString("uri");

									if (ResourceFile.resourceFileInterface != null) {
										ResourceFile.resourceFileInterface
												.seSubioFichero(fid, uri);
										return;
									}

								}

							} catch (Exception e) {
								Log.d("Milog", "Excepcion al subir fichero: "
										+ e.toString());
							}
						}

						// Informar al delegate
						if (ResourceFile.resourceFileInterface != null) {
							Log.d("Milog",
									"Antes de informar al delegate de un error");
							ResourceFile.resourceFileInterface
									.producidoErrorAlSubirFichero("Error al recoger respuesta");
						}

					}

					public void onFailure(Throwable error) {
						Log.d("Milog", "Respuesta de subir fichero erronea: "
								+ error);
						// Informar al delegate
						if (ResourceFile.resourceFileInterface != null) {
							Log.d("Milog",
									"Antes de informar al delegate de un error");
							ResourceFile.resourceFileInterface
									.producidoErrorAlSubirFichero(error
											.toString());
						}
					}
				}, params);
	}

	// Convierte una imagen a byte array
	public static byte[] convertImageToByteArray(File f) {
		Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap
															// object
		byte[] b = baos.toByteArray();
		return b;
	}

	public static String convertByteArrayToB64(byte[] byteArray) {
		String b64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
		return b64;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(fid);
		dest.writeString(fileName);
		dest.writeString(fileUrl);
		dest.writeString(fileMime);
		dest.writeString(fileType);
		dest.writeString(fileBody);
		dest.writeString(fileTitle);
		dest.writeString(copyright);
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public static final Parcelable.Creator<ResourceFile> CREATOR = new Parcelable.Creator<ResourceFile>() {

		public ResourceFile createFromParcel(Parcel in) {
			ResourceFile complaint = new ResourceFile();
			complaint.setFid(in.readString());
			complaint.setFileName(in.readString());
			complaint.setFileUrl(in.readString());
			complaint.setFileMime(in.readString());
			complaint.setFileType(in.readString());
			complaint.setFileBody(in.readString());
			complaint.setFileTitle(in.readString());
			complaint.setCopyright(in.readString());
			return complaint;
		}

		@Override
		public ResourceFile[] newArray(int size) {
			return new ResourceFile[size];
		}
	};
}
