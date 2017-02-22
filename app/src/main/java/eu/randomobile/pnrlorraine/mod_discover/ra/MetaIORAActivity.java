//// Copyright 2007-2014 metaio GmbH. All rights reserved.
//package eu.randomobile.pnrlorraine.mod_discover.ra;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.locks.Lock;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.text.TextPaint;
//import android.util.Log;
//import android.view.View;
//
//import com.metaio.cloud.plugin.util.MetaioCloudUtils;
//import com.metaio.sdk.ARELInterpreterAndroidJava;
//import com.metaio.sdk.ARViewActivity;
//import com.metaio.sdk.MetaioDebug;
//import com.metaio.sdk.jni.AnnotatedGeometriesGroupCallback;
//import com.metaio.sdk.jni.EGEOMETRY_FOCUS_STATE;
//import com.metaio.sdk.jni.IAnnotatedGeometriesGroup;
//import com.metaio.sdk.jni.IGeometry;
//import com.metaio.sdk.jni.IMetaioSDKCallback;
//import com.metaio.sdk.jni.IRadar;
//import com.metaio.sdk.jni.ImageStruct;
//import com.metaio.sdk.jni.LLACoordinate;
//import com.metaio.sdk.jni.Rotation;
//import com.metaio.sdk.jni.SensorValues;
//import com.metaio.sdk.jni.TrackingValuesVector;
//import com.metaio.sdk.jni.Vector3d;
//import com.metaio.tools.io.AssetsManager;
//
//import eu.randomobile.pnrlorraine.MainApp;
//import eu.randomobile.pnrlorraine.R;
//import eu.randomobile.pnrlorraine.mod_discover.detail.PoiDetailActivity;
//import eu.randomobile.pnrlorraine.mod_global.Util;
//import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
//import eu.randomobile.pnrlorraine.mod_global.environment.GPS;
//import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
//import eu.randomobile.pnrlorraine.mod_global.model.Poi;
//import eu.randomobile.pnrlorraine.mod_global.model.Poi.PoisInterface;
//import eu.randomobile.pnrlorraine.mod_notification.Cache;
//
//public class MetaIORAActivity extends ARViewActivity implements PoisInterface,
//		LocationListener {
//	private static int MAX_DISTANCE_FROM_USER_TO_POI = 20000;
//
//	private GPS gps = null;
//	private MainApp app = null;
//	private IGeometry mMetaioMan;
//	private IGeometry mImagePlane;
//	private IGeometry mMoviePlane;
//	private IGeometry mTruck;
//	private int mSelectedModel;
//	private IAnnotatedGeometriesGroup mAnnotatedGeometriesGroup;
//	private MyAnnotatedGeometriesGroupCallback mAnnotatedGeometriesGroupCallback;
//
//	private List<IGeometry> geometries = new ArrayList<IGeometry>();
//
//	private IRadar mRadar;
//	// private IBillboardGroup mBillBoardGroup;
//	// private List<GeometryBundle> geometriesList;
//
//	private MetaioSDKCallbackHandler mCallbackHandler;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		mMetaioMan = null;
//		mImagePlane = null;
//		mMoviePlane = null;
//		mTruck = null;
//		try {
//			this.app = (MainApp) getApplication();
//			AssetsManager.extractAllAssets(this, true);
//			mCallbackHandler = new MetaioSDKCallbackHandler();
//			this.gps = new GPS(this, this);
//			this.gps.startLocating();
//		} catch (Exception ex) {
//		}
//	}
//
//	private void recargarDatos() {
//		if (DataConection.hayConexion(this)) {
//			Poi.poisInterface = this;
//			Poi.cargarListaPoisOrdenadosDistancia(getApplication(), // Aplicacion
//					gps.getLastLocation().getLatitude(), // Latitud
//					gps.getLastLocation().getLongitude(), // Longitud
//					0, // Radio en Kms
//					0, // Nœmero de elementos por p‡gina
//					0, // P‡gina
//					null, // Tid de la categor’a que queremos filtrar
//					null // Texto a buscar
//			);
//		} else {
//			// Si no hay conexi—n a Internet
//			Util.mostrarMensaje(
//					this,
//					getResources().getString(
//							R.string.mod_global__sin_conexion_a_internet),
//					getResources()
//							.getString(
//									R.string.mod_global__no_dispones_de_conexion_a_internet));
//		}
//
//		// Poner el texto al bot—n que hace de combo
//		// this.btnCategorias.setText(this.categoryName);
//	}
//
//	private IGeometry createPOIGeometry(LLACoordinate lla, final File file) {
//		// final File path = AssetsManager.getAssetPathAsFile(
//		// getApplicationContext(), "ExamplePOI.obj");
//		if (file != null && file.exists()) {
//			IGeometry geo = metaioSDK.createGeometry(file);
//			geo.setTranslationLLA(lla);
//			geo.setLLALimitsEnabled(true);
//			geo.setScale(100);
//			return geo;
//		} else {
//			MetaioDebug.log(Log.ERROR, "Missing files for POI geometry");
//			return null;
//		}
//	}
//
//	private IGeometry createPOIGeometryFromImage(LLACoordinate lla,
//			final File file) {
//		// final File path = AssetsManager.getAssetPathAsFile(
//		// getApplicationContext(), "ExamplePOI.obj");
//		if (file != null && file.exists()) {
//			IGeometry geo = metaioSDK.createGeometryFromImage(file);
//			geo.setTranslationLLA(lla);
//			geo.setLLALimitsEnabled(true);
//			geo.setScale(100);
//			return geo;
//		} else {
//			MetaioDebug.log(Log.ERROR, "Missing files for POI geometry");
//			return null;
//		}
//	}
//
//	@Override
//	protected int getGUILayout() {
//		return R.layout.tutorial_content_types;
//	}
//
//	public void onButtonClick(View v) {
//		finish();
//	}
//
//	public void onModelButtonClick(View v) {
//		setActiveModel(0);
//	}
//
//	public void onImageButtonClick(View v) {
//		setActiveModel(1);
//	}
//
//	public void onMovieButtonClick(View v) {
//		setActiveModel(3);
//	}
//
//	public void onTruckButtonClick(View v) {
//		setActiveModel(2);
//	}
//
//	@Override
//	protected void loadContents() {
//		try {
//			mAnnotatedGeometriesGroup = metaioSDK
//					.createAnnotatedGeometriesGroup();
//			mAnnotatedGeometriesGroupCallback = new MyAnnotatedGeometriesGroupCallback();
//			mAnnotatedGeometriesGroup
//					.registerCallback(mAnnotatedGeometriesGroupCallback);
//			metaioSDK.setTrackingConfiguration("GPS", false);
//			metaioSDK.setLLAObjectRenderingLimits(5, 200);
//
//			// Set render frustum accordingly
//			metaioSDK.setRendererClippingPlaneLimits(10, 220000);
//
//			mRadar = metaioSDK.createRadar();
//			if (mRadar != null) {
//				File file = AssetsManager.getAssetPathAsFile(
//						getApplicationContext(), "metaio/radar.png");
//				if (file.exists()) {
//					mRadar.setBackgroundTexture(file);
//				}
//				File file2 = AssetsManager.getAssetPathAsFile(
//						getApplicationContext(), "metaio/yellow.png");
//				if (file2.exists()) {
//					mRadar.setObjectsDefaultTexture(file2);
//				}
//				mRadar.setRelativeToScreen(IGeometry.ANCHOR_TL);
//			}
//			if (Cache.filteredPois != null)
//				this.cargarPOIs(Cache.filteredPois);
//			else if (Cache.arrayPois != null) {
//				this.cargarPOIs(Cache.arrayPois);
//			} else {
//				recargarDatos();
//			}
//		} catch (Exception e) {
//			MetaioDebug.log(Log.ERROR, "loadContents failed, see stack trace");
//		}
//	}
//
//	private void openSpace(final int eger) {
//		try {
//
//		} catch (Exception e) {
//		}
//	}
//
//	/**
//	 * Carga los POIs para mostrar en la realidad aumentada.
//	 *
//	 * @param pois
//	 *            Lista de POIs a cargar.
//	 */
//	private void cargarPOIs(final List<Poi> pois) {
//		try {
//			if (pois != null && pois.size() > 0) {
//				LocationManager lm = (LocationManager) app
//						.getApplicationContext().getSystemService(
//								Context.LOCATION_SERVICE);
//				Location l = lm.getLastKnownLocation(lm.getProviders(true).get(
//						0));
//				LLACoordinate userLocation = new LLACoordinate(l.getLatitude(),
//						l.getLongitude(), 0, 0);
//				for (Poi p : pois) {
//					try {
//						if (p.getCategory() != null) {
//							LLACoordinate coordinates = new LLACoordinate(p
//									.getCoordinates().getLatitude(), p
//									.getCoordinates().getLongitude(), 0, 0);
//							double distance = coordinates
//									.distanceTo(userLocation);
//							if (distance <= MAX_DISTANCE_FROM_USER_TO_POI) {
//								// IGeometry geometry = createPOIGeometry(
//								// coordinates, this.getCategoryFile(p
//								// .getCategory().getName()));
//								IGeometry geometry = createPOIGeometryFromImage(
//										coordinates,
//										this.getImageFileByCategory(p
//												.getCategory().getName()));
//								this.geometries.add(geometry);
//								this.mAnnotatedGeometriesGroup.addGeometry(
//										geometry, p);
//								if (this.mRadar != null) {
//									this.mRadar.add(geometry);
//								}
//							}
//						}
//					} catch (Exception ex) {
//					}
//				}
//			}
//		} catch (Exception ex) {
//		}
//	}
//
//	private File getCategoryFile(final String category) {
//		File file = null;
//		try {
//			if (category.equals("Chambre d'hôtes")
//					|| category.equals("Hôtellerie")
//					|| category.equals("Hébergement collectif")
//					|| category.equals("Hôtellerie de plein air")
//					|| category.equals("Meublé")
//					|| category.equals("Résidence")) {
//				file = AssetsManager.getAssetPathAsFile(this,
//						"metaio/hotel.obj");
//			} else if (category.equals("Musée")
//					|| category.equals("Patrimoine Naturel")
//					|| category.equals("Site et Monument")
//					|| category.equals("Office de Tourisme")
//					|| category.equals("Parc et Jardin")) {
//				file = AssetsManager.getAssetPathAsFile(this,
//						"metaio/descubrir.obj");
//			} else if (category.equals("Restauration")) {
//				file = AssetsManager.getAssetPathAsFile(this,
//						"metaio/restaurante.obj");
//			}
//		} catch (Exception ex) {
//		}
//		return file;
//	}
//
//	private File getImageFileByCategory(final String category) {
//		File file = null;
//		try {
//			if (category.equals("Chambre d'hôtes")
//					|| category.equals("Hôtellerie")
//					|| category.equals("Hébergement collectif")
//					|| category.equals("Hôtellerie de plein air")
//					|| category.equals("Meublé")
//					|| category.equals("Résidence")) {
//				file = AssetsManager.getAssetPathAsFile(this,
//						"metaio/hotel.png");
//			} else if (category.equals("Musée")
//					|| category.equals("Patrimoine Naturel")
//					|| category.equals("Site et Monument")
//					|| category.equals("Office de Tourisme")
//					|| category.equals("Parc et Jardin")) {
//				file = AssetsManager.getAssetPathAsFile(this,
//						"metaio/descubrir.png");
//			} else if (category.equals("Restauration")) {
//				file = AssetsManager.getAssetPathAsFile(this,
//						"metaio/restaurante.png");
//			}
//		} catch (Exception ex) {
//		}
//		return file;
//	}
//
//	@Override
//	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
//		return mCallbackHandler;
//	}
//
//	private void setActiveModel(int modelIndex) {
//		mSelectedModel = modelIndex;
//
//		mMetaioMan.setVisible(modelIndex == 0);
//		mImagePlane.setVisible(modelIndex == 1);
//		mTruck.setVisible(modelIndex == 2);
//		mMoviePlane.setVisible(modelIndex == 3);
//
//		if (modelIndex != 3) {
//			mMoviePlane.stopMovieTexture();
//		}
//
//		// Start or pause movie according to tracking state
//		mCallbackHandler.onTrackingEvent(metaioSDK.getTrackingValues());
//	}
//
//	final private class MetaioSDKCallbackHandler extends IMetaioSDKCallback {
//		@Override
//		public void onSDKReady() {
//			// show GUI after SDK is ready
//			runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					mGUIView.setVisibility(View.VISIBLE);
//				}
//			});
//		}
//
//		@Override
//		public void onTrackingEvent(TrackingValuesVector trackingValues) {
//			super.onTrackingEvent(trackingValues);
//
//			// We only have one COS, so there can only ever be one
//			// TrackingValues structure passed.
//			// Play movie if the movie button was selected and we're currently
//			// tracking.
//			if (trackingValues.isEmpty()
//					|| !trackingValues.get(0).isTrackingState()) {
//				if (mMoviePlane != null) {
//					mMoviePlane.pauseMovieTexture();
//				}
//			} else {
//				if (mMoviePlane != null && mSelectedModel == 3) {
//					mMoviePlane.startMovieTexture(true);
//				}
//			}
//		}
//	}
//
//	final class MyAnnotatedGeometriesGroupCallback extends
//			AnnotatedGeometriesGroupCallback {
//		Bitmap mAnnotationBackground, mEmptyStarImage, mFullStarImage;
//		int mAnnotationBackgroundIndex;
//		ImageStruct texture;
//		String[] textureHash = new String[1];
//		TextPaint mPaint;
//		Lock geometryLock;
//
//		Bitmap inOutCachedBitmaps[] = new Bitmap[] { mAnnotationBackground,
//				mEmptyStarImage, mFullStarImage };
//		int inOutCachedAnnotationBackgroundIndex[] = new int[] { mAnnotationBackgroundIndex };
//
//		public MyAnnotatedGeometriesGroupCallback() {
//			mPaint = new TextPaint();
//			mPaint.setFilterBitmap(true); // enable dithering
//			mPaint.setAntiAlias(true); // enable anti-aliasing
//		}
//
//		@Override
//		public IGeometry loadUpdatedAnnotation(IGeometry geometry,
//				Object userData, IGeometry existingAnnotation) {
//			if (userData == null) {
//				return null;
//			}
//
//			if (existingAnnotation != null) {
//				// We don't update the annotation if e.g. distance has changed
//				return existingAnnotation;
//			}
//
//			// String title = (String) userData; // as passed to addGeometry
//			String title = ((Poi) userData).getTitle();
//			LLACoordinate location = geometry.getTranslationLLA();
//			float distance = (float) MetaioCloudUtils
//					.getDistanceBetweenTwoCoordinates(location,
//							mSensors.getLocation());
//			Bitmap thumbnail = BitmapFactory.decodeResource(getResources(),
//					R.drawable.ic_launcher);
//			try {
//				texture = ARELInterpreterAndroidJava.getAnnotationImageForPOI(
//						title, title, distance, "5", thumbnail, null,
//						metaioSDK.getRenderSize(), MetaIORAActivity.this,
//						mPaint, inOutCachedBitmaps,
//						inOutCachedAnnotationBackgroundIndex, textureHash);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (thumbnail != null)
//					thumbnail.recycle();
//				thumbnail = null;
//			}
//
//			mAnnotationBackground = inOutCachedBitmaps[0];
//			mEmptyStarImage = inOutCachedBitmaps[1];
//			mFullStarImage = inOutCachedBitmaps[2];
//			mAnnotationBackgroundIndex = inOutCachedAnnotationBackgroundIndex[0];
//
//			IGeometry resultGeometry = null;
//
//			if (texture != null) {
//				if (geometryLock != null) {
//					geometryLock.lock();
//				}
//
//				try {
//					// Use texture "hash" to ensure that SDK loads new texture
//					// if texture changed
//					resultGeometry = metaioSDK.createGeometryFromImage(
//							textureHash[0], texture, true, false);
//				} finally {
//					if (geometryLock != null) {
//						geometryLock.unlock();
//					}
//				}
//			}
//
//			return resultGeometry;
//		}
//
//		@Override
//		public void onFocusStateChanged(IGeometry geometry, Object userData,
//				EGEOMETRY_FOCUS_STATE oldState, EGEOMETRY_FOCUS_STATE newState) {
//			try {
//				if (newState == EGEOMETRY_FOCUS_STATE.EGFS_SELECTED) {
//					Poi poi = (Poi) userData;
//					Intent intent = new Intent(MetaIORAActivity.this,
//							PoiDetailActivity.class);
//					intent.putExtra(PoiDetailActivity.PARAM_KEY_NID,
//							poi.getNid());
//					intent.putExtra(PoiDetailActivity.PARAM_KEY_DISTANCE,
//							poi.getDistanceMeters());
//					int desnivel = 0;
//					if (gps != null) {
//						if (gps.getLastLocation() != null) {
//							desnivel = (int) (poi.getCoordinates()
//									.getAltitude() - gps.getLastLocation()
//									.getAltitude());
//						}
//					}
//					intent.putExtra(PoiDetailActivity.PARAM_KEY_DESNIVEL,
//							desnivel);
//					intent.putExtra(PoiDetailActivity.PARAM_KEY_NUMBERVOTES,
//							poi.getVote().getNumVotes());
//					intent.putExtra(PoiDetailActivity.PARAM_KEY_VALORATION, poi
//							.getVote().getValue());
//					BitmapManager.INSTANCE.cache.remove(poi.getMainImage());
//					startActivity(intent);
//					MetaioDebug.log("onFocusStateChanged for "
//							+ (String) userData + ", " + oldState + "->"
//							+ newState);
//				}
//			} catch (Exception ex) {
//			}
//		}
//	}
//
//	@Override
//	protected void onGeometryTouched(final IGeometry geometry) {
//		MetaioDebug.log("Geometry selected: " + geometry);
//
//		mSurfaceView.queueEvent(new Runnable() {
//
//			@Override
//			public void run() {
//				if (mRadar != null) {
//					mRadar.setObjectsDefaultTexture(AssetsManager
//							.getAssetPathAsFile(getApplicationContext(),
//									"metaio/yellow.png"));
//					mRadar.setObjectTexture(geometry, AssetsManager
//							.getAssetPathAsFile(getApplicationContext(),
//									"metaio/red.png"));
//					mAnnotatedGeometriesGroup.setSelectedGeometry(geometry);
//				}
//			}
//		});
//	}
//
//	@Override
//	public void onDrawFrame() {
//		if (metaioSDK != null && mSensors != null) {
//			SensorValues sensorValues = mSensors.getSensorValues();
//			float heading = 0.0f;
//			if (sensorValues.hasAttitude()) {
//				float m[] = new float[9];
//				sensorValues.getAttitude().getRotationMatrix(m);
//
//				Vector3d v = new Vector3d(m[6], m[7], m[8]);
//				v.normalize();
//
//				heading = (float) (-Math.atan2(v.getY(), v.getX()) - Math.PI / 2.0);
//			}
//			// IGeometry geos[] = new IGeometry[] { mLondonGeo, mParisGeo,
//			// mRomeGeo, mTokyoGeo };
//			Rotation rot = new Rotation((float) (Math.PI / 2.0), 0.0f, -heading);
//			for (IGeometry geo : geometries) {
//				if (geo != null) {
//					geo.setRotation(rot);
//				}
//			}
//		}
//		super.onDrawFrame();
//	}
//
//	@Override
//	protected void onDestroy() {
//		// Break circular reference of Java objects
//		if (mAnnotatedGeometriesGroup != null) {
//			mAnnotatedGeometriesGroup.registerCallback(null);
//		}
//		if (mAnnotatedGeometriesGroupCallback != null) {
//			mAnnotatedGeometriesGroupCallback.delete();
//			mAnnotatedGeometriesGroupCallback = null;
//		}
//		super.onDestroy();
//	}
//
//	@Override
//	public void onLocationChanged(Location location) {
//		try {
//			if (this.gps.getLastLocation() != null) {
//
//				// Parar el gps si ya tenemos una coordenada
//				this.gps.stopLocating();
//
//				// Guardar la coordenada en las preferencias
//				SharedPreferences.Editor editor = app.preferencias.edit();
//				editor.putFloat(app.FILTER_KEY_LAST_LOCATION_LATITUDE,
//						(float) this.gps.getLastLocation().getLatitude());
//				editor.putFloat(app.FILTER_KEY_LAST_LOCATION_LONGITUDE,
//						(float) this.gps.getLastLocation().getLongitude());
//				editor.putFloat(app.FILTER_KEY_LAST_LOCATION_ALTITUDE,
//						(float) this.gps.getLastLocation().getAltitude());
//				editor.commit();
//
//				// Recargar los datos
//				// this.recargarDatos();
//			}
//		} catch (Exception ex) {
//		}
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//	}
//
//	@Override
//	public void seCargoListaPois(final ArrayList<Poi> pois) {
//		if (pois != null) {
//			mSurfaceView.queueEvent(new Runnable() {
//				@Override
//				public void run() {
//					cargarPOIs(pois);
//				}
//
//			});
//		}
//
//		// Message m = new Message();
//		// m.what = MENSAJE_CARGAR_POIS;
//		// m.obj = pois;
//		// this.handler.sendMessage(m);
//		// // cargarPOIs(pois);
//		// }
//		Log.d("Milog", "seCargoListaPois");
//	}
//
//	@Override
//	public void producidoErrorAlCargarListaPois(String error) {
//		Log.d("Milog", "producidoErrorAlCargarListaPois: " + error);
//	}
//
//	@Override
//	public void seCargoPoi(Poi poi) {
//	}
//
//	@Override
//	public void producidoErrorAlCargarPoi(String error) {
//	}
//}
