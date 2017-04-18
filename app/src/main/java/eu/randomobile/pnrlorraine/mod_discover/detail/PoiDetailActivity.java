package eu.randomobile.pnrlorraine.mod_discover.detail;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_checkin.CheckinActivity;
import eu.randomobile.pnrlorraine.mod_events.EventsListActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.libraries.download.DownloadUrl;
import eu.randomobile.pnrlorraine.mod_global.libraries.download.DownloadUrl.OnTaskCompletedInterface;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.CapaBase;
import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.Poi.PoisInterface;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_global.model.User;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_multi_viewers.imgs.GridImagesActivity;
import eu.randomobile.pnrlorraine.mod_multi_viewers.vids.ListVideosActivity;
import eu.randomobile.pnrlorraine.mod_offline.OfflinePoi;
import eu.randomobile.pnrlorraine.mod_offline.OfflinePoi.PoisModeOfflineInterface;
import eu.randomobile.pnrlorraine.mod_offline.OfflineRoute.RoutesModeOfflineInterface;
import eu.randomobile.pnrlorraine.mod_share.Share;
import eu.randomobile.pnrlorraine.mod_vote.VoteActivity;
import eu.randomobile.pnrlorraine.mod_discover.detail.RouteDetailActivity;


public class PoiDetailActivity extends Activity implements PoisInterface, PoisModeOfflineInterface, LocationListener, OnTaskCompletedInterface {
	public static final String PARAM_KEY_NID = "nid";
	public static final String PARAM_KEY_DISTANCE = "distance";
	public static final String PARAM_KEY_DESNIVEL = "desnivel";
	public static final String PARAM_KEY_NUMBERVOTES = "nvotos";
	public static final String PARAM_KEY_VALORATION = "valoracion";
	private static final long MIN_TIME_BW_UPDATES = 50*1000;
	LocationManager locationManager;

	String paramNid;
	double paramDistanceMeters;
	double paramDesnivelMeters;
	int paramNumberVotes = 0;
	int paramValoration;

	MainApp app;
	// Para el tratamiento del menu
	ImageMap mImageMap = null;
	Poi miPoi;
	private MapView mapa;
	Callout callout;
	GraphicsOverlay capaGeometrias;
	TextView txtTitulo;

	ImageView imageViewPrincipal;
	RelativeLayout panelImgViewPrincipal;
	ImageView imgViewCategoria;
	LinearLayout layoutBotonesMenuMas;
	TextView lblSeccionCategoria;
	TextView lblCategoria;
	TextView lblSeccionDistancia;
	TextView lblDistancia;
	TextView lblSeccionDesnivel;
	TextView lblDesnivel;
	TextView lblDescripcion;
	TextView lblSectionValoracion;
	ImageView imgViewValoracion;

	// RelativeLayout layoutVerEnMapa;
	ImageButton btnValorar;
	Button btnCompartir;

	ImageButton btnImgs;
	ImageButton btnVids;
	ImageButton btnMapa;
	Button btnMenuMasMas;
	Button btnMenuMasTelecarga;
	Button btnMenuMasValorar;
	Button btnMenuMasCompartir;

	RelativeLayout panelCargando;
	RelativeLayout panelCargandoPoi;
	GeoPoint ultimaUbicacion;
	GeoPoint ubicacionPunto;
	ProgressBar pb;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_discover__layout_detail_poi);

		// Obtener la app
		this.app = (MainApp) getApplication();

		// Recoger par�metros
		Bundle b = getIntent().getExtras();
		if (b != null) {
			paramNid = b.getString(PARAM_KEY_NID);
			paramDistanceMeters = b.getDouble(PARAM_KEY_DISTANCE);
			paramDesnivelMeters = b.getInt(PARAM_KEY_DESNIVEL);
			paramNumberVotes = b.getInt(PARAM_KEY_NUMBERVOTES);
			paramValoration = b.getInt(PARAM_KEY_VALORATION);
		}
		
		capturarControles();
		escucharEventos();
		inicializarForm();
		inicializarMapa();
		recargarDatos();
	}

	public void onResume() {
		super.onResume();
		mImageMap.mBubbleMap.clear();
		mImageMap.postInvalidate();
		//Activar GPS
		if (mapa.getVisibility() == View.VISIBLE) {
			LocationDisplay ls = mapa.getLocationDisplay();
	//		ls.setLocationListener(new MyLocationListener());
	//		ls.setAutoPanMode(AutoPanMode.OFF);
			ls.startAsync();
			if(locationManager == null){
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			}
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, 10, PoiDetailActivity.this);
		}
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		//Parar GPS
		LocationDisplay ls = mapa.getLocationDisplay();
		//ls.setLocationListener(new MyLocationListener());
		
		if(ls != null){
		    ls.stop();		 
		}
		
	   	if (locationManager != null) {
			locationManager.removeUpdates(PoiDetailActivity.this);
			locationManager = null;
		}		
		
	}
	
	private void capturarControles() {
		mapa = (MapView) findViewById(R.id.mapa);
		mapa.setMap(new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16));


		// Para el tratamiento del menu

		mImageMap = (ImageMap) findViewById(R.id.map_poiDetail);
		mImageMap.setAttributes(true, false, (float) 1.0, "lista_item_poi");
		mImageMap.setImageResource(R.drawable.detalle_poi);

		txtTitulo = (TextView) findViewById(R.id.lblTitleHeader);
		layoutBotonesMenuMas = (LinearLayout) findViewById(R.id.layoutBotonesMenuMas);
		imageViewPrincipal = (ImageView) findViewById(R.id.imgViewPrincipal);
		imgViewCategoria = (ImageView) findViewById(R.id.imgViewCategory);
		// layoutVerEnMapa = (RelativeLayout)
		// findViewById(R.id.layoutVerEnMapa);
		lblSeccionCategoria = (TextView) findViewById(R.id.lblCategoria);
		lblCategoria = (TextView) findViewById(R.id.lblValCategoria);
		lblSeccionDistancia = (TextView) findViewById(R.id.lblDistancia);
		lblDistancia = (TextView) findViewById(R.id.lblValDistancia);
		lblSeccionDesnivel = (TextView) findViewById(R.id.lblDesnivel);
		lblDesnivel = (TextView) findViewById(R.id.lblValDesnivel);
		lblSectionValoracion = (TextView) findViewById(R.id.lblValoracion);
		imgViewValoracion = (ImageView) findViewById(R.id.imgViewValoracion);
		// lblVerEnMapa = (TextView) findViewById(R.id.lblVerEnMapa);
		lblDescripcion = (TextView) findViewById(R.id.lblDescripcion);
		btnValorar = (ImageButton) findViewById(R.id.btnValorar);
		btnCompartir = (Button) findViewById(R.id.btnMenuMas_Compartir);
		btnImgs = (ImageButton) findViewById(R.id.btnImgs);
		btnVids = (ImageButton) findViewById(R.id.btnVids);
		btnMapa = (ImageButton) findViewById(R.id.btnAbrirMapa);
		panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
		panelCargandoPoi = (RelativeLayout) findViewById(R.id.panelCargandoPoi);
		btnMenuMasMas = (Button) findViewById(R.id.btnMenuMas_Mas);
		btnMenuMasTelecarga = (Button) findViewById(R.id.btnMenuMas_Telecarga);
		btnMenuMasValorar = (Button) findViewById(R.id.btnMenuMas_Valorar);
		btnMenuMasCompartir = (Button) findViewById(R.id.btnMenuMas_Compartir);
		panelImgViewPrincipal = (RelativeLayout) findViewById(R.id.contenedorMapa);
		pb = (ProgressBar) findViewById(R.id.progressBarMapas);
	}

	private void escucharEventos() {
		mImageMap
				.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
					@Override
					public void onImageMapClicked(int id, ImageMap imageMap) {

						// when the area is tapped, show the name in a
						// text bubble
						mImageMap.showBubble(id);
						switch (mImageMap.getAreaAttribute(id, "name")) {
						case "HOME":
							if (locationManager != null)
								locationManager.removeUpdates(PoiDetailActivity.this);
							locationManager = null;
							cargaActivityHome();
							break;
						case "BACK":
							if (locationManager != null)
								locationManager.removeUpdates(PoiDetailActivity.this);
							locationManager = null;
							finish();
							break;
						case "CHECK-IN":
							Toast.makeText(getApplicationContext(), "Eventos", Toast.LENGTH_SHORT).show();
							cargaActivityCheckIn();
							break;
						case "EVENTS":
							cargaActivityEventos();
							break;
						case "PLUS":
							Share.compartir(app, PoiDetailActivity.this, miPoi);
							break;
						default:
							mImageMap.showBubble(id);
						}

					}

					@Override
					public void onBubbleClicked(int id) {
						// react to info bubble for area being tapped

					}
				});

		// layoutVerEnMapa.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		// Intent intent = new Intent(PoiDetailActivity.this,
		// ConcreteMapActivity.class);
		// intent.putExtra(ConcreteMapActivity.PARAM_KEY_NID_MOSTRAR,
		// miPoi.getNid());
		// intent.putExtra(ConcreteMapActivity.PARAM_KEY_TYPE_DRUPAL,
		// app.DRUPAL_TYPE_POI);
		// startActivity(intent);
		// }
		// });

		btnValorar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (User.isLoggedIn(app)) {
					Intent intent = new Intent(PoiDetailActivity.this,
							VoteActivity.class);
					intent.putExtra(VoteActivity.PARAM_KEY_NID_ITEM_A_VALORAR,
							miPoi.getNid());
					intent.putExtra(
							VoteActivity.PARAM_KEY_TITLE_ITEM_A_VALORAR,
							miPoi.getTitle());
					startActivity(intent);
				} else {
					User.askForloginHere(PoiDetailActivity.this);
				}
			}
		});
		btnCompartir.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Share.compartir(app, PoiDetailActivity.this, miPoi);
			}
		});

		btnImgs.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(PoiDetailActivity.this,
						GridImagesActivity.class);
				intent.putParcelableArrayListExtra(
						GridImagesActivity.PARAM_KEY_ARRAY_RECURSOS,
						miPoi.getImages());
				startActivity(intent);
			}
		});

		btnVids.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(PoiDetailActivity.this,
						ListVideosActivity.class);
				intent.putParcelableArrayListExtra(
						GridImagesActivity.PARAM_KEY_ARRAY_RECURSOS,
						miPoi.getEnlaces());
				startActivity(intent);
			}
		});


		btnMenuMasMas.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				layoutBotonesMenuMas.setVisibility(View.GONE);
			}
		});
		
		btnMapa.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (mapa.getVisibility() == View.GONE) {
					activarMapa();
					imageViewPrincipal.setVisibility(View.GONE);
					mapa.setVisibility(View.VISIBLE);
					if (miPoi != null)
						ponerPoiEnMapa(miPoi);
				} else {
					imageViewPrincipal.setVisibility(View.VISIBLE);
					mapa.setVisibility(View.GONE);
				}
			}
		});

		btnMenuMasCompartir.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Share.compartir(app, PoiDetailActivity.this, miPoi);
			}
		});
		
		btnMenuMasTelecarga.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//layoutBotonesMenuMas.setVisibility(View.GONE);
				panelCargando.setVisibility(View.VISIBLE);
				DownloadUrl.completedInterface = PoiDetailActivity.this;
				OfflinePoi.poisInterface = PoiDetailActivity.this;
				OfflinePoi.fillPoisItem(app, miPoi.getNid());
			}
		});
		btnMenuMasValorar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (User.isLoggedIn(app)) {
					Intent intent = new Intent(PoiDetailActivity.this,
							VoteActivity.class);
					intent.putExtra(VoteActivity.PARAM_KEY_NID_ITEM_A_VALORAR,
							miPoi.getNid());
					intent.putExtra(
							VoteActivity.PARAM_KEY_TITLE_ITEM_A_VALORAR,
							miPoi.getTitle());
					startActivity(intent);
				} else {
					User.askForloginHere(PoiDetailActivity.this);
				}
			}
		});
		

	}
	
	private void activarMapa() {

		this.mapa.getMap().addDoneLoadingListener(new Runnable() {
			@Override
			public void run() {
				mapa.getLocationDisplay().startAsync();
				mapa.getLocationDisplay().setAutoPanMode(LocationDisplay.AutoPanMode.OFF);//.setAutoPan(false);

				if(locationManager == null){
					locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				}
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, 10, PoiDetailActivity.this);
				//mapa.setViewpointCenterAsync(new Point(miPoi.getCoordinates().getLatitude(), miPoi.getCoordinates().getLongitude()), 5);
				mapa.setViewpointAsync(new Viewpoint(miPoi.getCoordinates().getLongitude(), miPoi.getCoordinates().getLatitude(), 10000));
			}
		});

	}

	private void representarPunto(){
		if(ubicacionPunto != null){
			Point puntoProyectado = (Point) GeometryEngine.project(new Point(ubicacionPunto.getLongitude(), ubicacionPunto.getLatitude()), SpatialReference.create(102100));
			int pointColor = Color.MAGENTA;
			SimpleMarkerSymbol sym = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, pointColor, 10);
		    Graphic gr = new Graphic(puntoProyectado, null, sym );
			capaGeometrias.getGraphics().add(gr);
		}
	}

	@SuppressLint("NewApi")
	private void inicializarForm() {
		// poner estilos (fuente, color, ...)
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			ActionBar ab = getActionBar();
			if (ab != null) {
				ab.hide();
			}
		}
		Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
		Typeface tfBentonBold = Util.fontBenton_Bold(this);
		Typeface tfBentonBoo = Util.fontBenton_Boo(this);

		/*
		lblSeccionCategoria.setTypeface(tfBentonBoo);
		lblCategoria.setTypeface(tfBentonBold);
		lblSeccionDistancia.setTypeface(tfBentonBoo);
		lblDistancia.setTypeface(tfBentonBold);
		lblSeccionDesnivel.setTypeface(tfBentonBoo);
		lblDesnivel.setTypeface(tfBentonBold);
		// lblVerEnMapa.setTypeface(tfBubleGum);
		lblDescripcion.setTypeface(tfBentonBoo);
		*/

		panelCargando.setVisibility(View.GONE);
	}

	private void inicializarMapa() {

		ponerCapaBase();



		capaGeometrias = new GraphicsOverlay();
		mapa.getGraphicsOverlays().add(capaGeometrias);
		//mapa.setScale(5000);
		//centrarEnExtentCapa(capaGeometrias);
	}

	private void recargarDatos() {
		if (DataConection.hayConexion(this)) {
			// Si hay conexi�n, recargar los datos
			panelCargando.setVisibility(View.VISIBLE);
			Poi.poisInterface = this;
			Poi.cargarPoi(app, paramNid);

		} else {
			OfflinePoi.poisInterface = this;
			OfflinePoi.cargarPoiOffline(app, paramNid);
			// Si no hay conexi�n a Internet
			Util.mostrarMensaje(this,
					getResources().getString(R.string.mod_global__sin_conexion_a_internet),
					getResources().getString(R.string.mod_global__no_dispones_de_conexion_a_internet));
		}
	}

	private void cargaActivityHome() {
		Intent intent = new Intent(PoiDetailActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void cargaActivityCheckIn() {
		if (User.isLoggedIn(app)) {
			Intent intent = new Intent(PoiDetailActivity.this, CheckinActivity.class);
			intent.putExtra(CheckinActivity.PARAM_KEY_NID_ITEM_A_HACER_CHECKIN, miPoi.getNid());
			intent.putExtra(CheckinActivity.PARAM_KEY_TITLE_ITEM_A_HACER_CHECKIN, miPoi.getTitle());
			intent.putExtra(CheckinActivity.PARAM_KEY_COORDINATES_ITEM_A_HACER_CHECKIN, miPoi.getCoordinates());
			startActivity(intent);

		} else {
			User.askForloginHere(PoiDetailActivity.this);
		}
	}

	private void cargaActivityEventos() {
		Intent intent = new Intent(PoiDetailActivity.this, EventsListActivity.class);
		intent.putExtra(EventsListActivity.PARAM_KEY_NID_POI_EVENTOS_ASOCIADOS, miPoi.getNid());
		Bundle b = new Bundle();
		b.putSerializable(EventsListActivity.PARAM_KEY_CATEGORIA_POI, miPoi.getCategory());
		intent.putExtras(b);
		intent.putExtra(EventsListActivity.PARAM_KEY_COORDENADAS, miPoi.getCoordinates());
		startActivity(intent);
	}

	public void seCargoPoi(Poi poi) {
		if (poi != null) {
			this.miPoi = poi;

			// Poner el t�tulo
			this.txtTitulo.setText(this.miPoi.getTitle());

			// Poner la categor�a
			if (this.miPoi.getCategory() != null) {
				this.lblCategoria.setText(this.miPoi.getCategory().getName());

			} else {
				this.lblCategoria.setText(getResources().getString(R.string.mod_global__sin_datos));
			}

			// Poner la imagen de categoria
			if (miPoi.getCategory() != null) {
				// BitmapManager.INSTANCE.loadBitmap(item.getCategory().getIcon(),
				// holder.imgView, 36, 40);
				String category;
				category = miPoi.getCategory().getName();

				int clase =Integer.valueOf(miPoi.getCategory().getTid());

				switch (clase) {
					case 25: //Offices de tourisme
						imgViewCategoria.setBackgroundResource(R.drawable.icono_info);
						break;
					case 36: //Monuments
						imgViewCategoria.setBackgroundResource(R.drawable.icono_descubrir);
						break;
					case 28: //Musées
						imgViewCategoria.setBackgroundResource(R.drawable.icono_descubrir);
						break;
					case 30: //Patrimoine naturel
						imgViewCategoria.setBackgroundResource(R.drawable.icono_naturaleza);
						break;
					case 26:
					case 47:
					case 48:
					case 49:
					case 50:
					case 51: //Alojamientos
						imgViewCategoria.setBackgroundResource(R.drawable.icono_hotel);
						break;
					case 27: //Restauracion
						imgViewCategoria.setBackgroundResource(R.drawable.icono_restaurant);
						break;
					default:
						imgViewCategoria.setBackgroundResource(R.drawable.poi_icono);
				}

			} else {
				//imgViewCategoria.setImageResource(R.drawable.ic_launcher);
			}

			// Poner la distancia
			String strDistance = "";
			if (paramDistanceMeters == 0) {
				float calculatedDistance = GeoPoint.calculateDistance((float)poi.getCoordinates().getLatitude(), (float)RouteDetailActivity.lastLatitude, 
						(float)poi.getCoordinates().getLongitude(), (float)RouteDetailActivity.lastLongitude);
				paramDistanceMeters = calculatedDistance;
			}

			if (paramDistanceMeters > 1000) {
				strDistance = (int) (paramDistanceMeters / 1000) + " Kms.";

			} else {
				strDistance = (int) paramDistanceMeters + " m.";
			}

			this.lblDistancia.setText(strDistance);

			// Poner el desnivel
			if (paramDesnivelMeters == 0) {
				if (miPoi.getCoordinates() != null) {
					this.lblDesnivel.setText("+" + (int) miPoi.getCoordinates().getAltitude() + " m.");
				}
			} else {
				if (paramDesnivelMeters > 0) {
					this.lblDesnivel.setText("+" + paramDesnivelMeters + " m.");
				} else {
					this.lblDesnivel.setText("-" + paramDesnivelMeters + " m.");
				}
			}

			// Poner la descripci�n
			if (miPoi.getBody() != null && !miPoi.getBody().equals("") && !miPoi.getBody().equals("null")) {
				this.lblDescripcion.setText(Html.fromHtml(miPoi.getBody() + "<br>"), TextView.BufferType.SPANNABLE);Linkify.addLinks(this.lblDescripcion, Linkify.ALL);
			} else {
				this.lblDescripcion.setText(getResources().getString(R.string.mod_global__sin_datos));
			}

			if (miPoi.getImages() != null) {
				if (miPoi.getImages().size() > 0) {
					ResourceFile rf = miPoi.getImages().get(0);
					// BitmapManager.INSTANCE.loadBitmap(rf.getFileUrl(),
					// imageViewPrincipal, 500, 400);
					DisplayMetrics displayMetrics = app.getResources().getDisplayMetrics();
					float dpHeight = panelImgViewPrincipal.getHeight() / displayMetrics.density;
					float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
					BitmapManager.INSTANCE.loadBitmap(rf.getFileUrl(), imageViewPrincipal, (int) dpWidth, (int) dpHeight);
				}

				else
					imageViewPrincipal.setImageResource(R.drawable.no_picture);
			}
			else
				imageViewPrincipal.setImageResource(R.drawable.no_picture);

			// Poner numero de avis e imagen votos
			String valString = app.getApplicationContext().getResources().getString(R.string.mod_discover__nota);

			if (paramNumberVotes != 0) {
				lblSectionValoracion.setText(valString + " (" + String.valueOf(paramNumberVotes) + " avis)");
				if (paramValoration <= 10) {
					// Si es menor o igual a 0
					imgViewValoracion.setImageResource(R.drawable.puntuacion_0_estrellas);
				} else if (paramValoration > 10 && paramValoration < 30) {
					// Si est� entre 1 y 24
					imgViewValoracion.setImageResource(R.drawable.puntuacion_1_estrellas);
				} else if (paramValoration >= 30 && paramValoration < 50) {
					// Si est� entre 25 y 49
					imgViewValoracion.setImageResource(R.drawable.puntuacion_2_estrellas);
				} else if (paramValoration >= 50 && paramValoration < 70) {
					// Si est� entre 50 y 74
					imgViewValoracion.setImageResource(R.drawable.puntuacion_3_estrellas);
				} else if (paramValoration >= 70 && paramValoration <= 90) {
					// Si est� entre 75 y 90
					imgViewValoracion.setImageResource(R.drawable.puntuacion_4_estrellas);
				} else {
					imgViewValoracion.setImageResource(R.drawable.puntuacion_5_estrellas);
				}
			} else {
				lblSectionValoracion.setText(valString + " (" + "0" + " avis)");
				imgViewValoracion.setImageResource(R.drawable.puntuacion_0_estrellas);
			}
		}
		panelCargando.setVisibility(View.GONE);
	}

	public void producidoErrorAlCargarPoi(String error) {
		Log.d("Milog", "producidoErrorAlCargarPoi: " + error);
		panelCargando.setVisibility(View.GONE);
		Util.mostrarMensaje(this, getResources().getString(R.string.mod_global__error), getResources().getString(R.string.mod_global__error));
		finish();
	}

	private void ponerPoiEnMapa(Poi poi) {
		capaGeometrias.getGraphics().clear();
		GeoPoint gp = poi.getCoordinates();
		ubicacionPunto = gp;

		//Point puntoProyectado = (Point) GeometryEngine.project(new Point(gp.getLongitude(), gp.getLatitude()), SpatialReferences.getWgs84());
		Point puntoProyectado = (Point) new Point(gp.getLongitude(), gp.getLatitude(), SpatialReferences.getWgs84());

		ArrayList<Object> geometrias = new ArrayList<Object>();
		geometrias.add(puntoProyectado);

		String icon = null;
		if (poi.getCategory() != null && poi.getCategory().getIcon() != null) {
			icon = poi.getCategory().getIcon();
		}

		dibujarGeometrias(geometrias, poi.getTitle(), poi.getClass().getName(), poi.getNid(), poi.getCategory().getTid(), icon);

		Object geomObj = geometrias.get(0);
		final Point punto = (Point) geomObj;
		//double scale = 5000.0;
		//mapa.centerAt(punto, true);
		//mapa.setScale(scale);

		//mapa.centerAt(miPoi.getCoordinates().getLatitude(), miPoi.getCoordinates().getLongitude(), false);
		mapa.setViewpointAsync(new Viewpoint(miPoi.getCoordinates().getLongitude(), miPoi.getCoordinates().getLatitude(), 10000));
		//mapa.setViewpointCenterAsync(new Point(miPoi.getCoordinates().getLatitude(), miPoi.getCoordinates().getLongitude()), 5);
	}

	private void dibujarGeometrias(ArrayList<Object> geometrias, String paramNombre, String paramNombreClase, String paramNid, String paramCat, final String urlIcon) {
		int polygonFillColor = Color.rgb(55, 132, 218);
		int polygonBorderColor = Color.rgb(27, 87, 187);
		// int pointColor = Color.rgb(206, 240, 5);

		if (geometrias != null) {
			for (int j = 0; j < geometrias.size(); j++) {
				Object geomObj = geometrias.get(j);

				final HashMap<String, Object> attrs = new HashMap<String, Object>();
				attrs.put("clase", paramNombreClase);
				attrs.put("nid", paramNid);
				attrs.put("nombre", paramNombre);
				attrs.put("cat", paramCat);

				if (geomObj != null && geomObj.getClass().getName().equals(Polygon.class.getName())) {
					Polygon polygon = (Polygon) geomObj;

					SimpleFillSymbol sym = new SimpleFillSymbol();
					sym.setColor(polygonFillColor);
					sym.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, polygonBorderColor, 8));

					Graphic gr = new Graphic(polygon, attrs, sym);
					capaGeometrias.getGraphics().add(gr);

				} else if (geomObj != null && geomObj.getClass().getName().equals(Point.class.getName())) {
					final Point point = (Point) geomObj;

					PictureMarkerSymbol sym = null;

					Log.d("ParamCat:", paramCat.toString());

                    switch (paramCat) {
                        case "25": //Offices de tourisme
                            sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_info));
                            break;
                        case "36": //Monuments
                        case "28": //Musées
                            sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_descubrir));
                            break;
                        case "30": //Patrimoine naturel
                            sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_naturaleza));
                            break;
                        case "26":
                        case "47":
                        case "48":
                        case "49":
                        case "50":
                        case "51": //Alojamientos
                            sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_hotel));
                            break;
                        case "27": //Restauracion
                            sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.icono_restaurant));
                            break;
                        default:
							sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.poi_icono));
                    }

					Graphic gr = new Graphic(point, attrs, sym);
					capaGeometrias.getGraphics().add(gr);

					// Centrar en el extent de la capa
					// centrarEnExtentCapa(capaGeometrias);l

					// Centrar en el poi y hacer zoom
					//mapa.setViewpointAsync(new Point(miPoi.getCoordinates().getLongitude(), miPoi.getCoordinates().getLatitude()));
					mapa.setViewpointAsync(new Viewpoint(miPoi.getCoordinates().getLongitude(), miPoi.getCoordinates().getLatitude(), 5));

				} else if (geomObj != null && geomObj.getClass().getName().equals(Polyline.class.getName())) {
					Polyline polyline = (Polyline) geomObj;

					int color = Color.BLUE;

					Graphic gr = new Graphic(polyline, attrs, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, color, 10));
					capaGeometrias.getGraphics().add(gr);
				}
			}
		}
	}

	public void ponerCapaBase() {
		if(! DataConection.hayConexion(this)){
		    String basemapurl = Util.getUrlGeneralBaseLayerOffline(app);
		    ArcGISTiledLayer baseLayer;
		    baseLayer = new ArcGISTiledLayer(basemapurl);
			mapa.getMap().getOperationalLayers().add(baseLayer);
			mapa.getMap().setMaxScale(1000);
			return;
		}

		CapaBase capaSeleccionada = app.capaBaseSeleccionada;
		Log.d("Milog", "Identificador: " + capaSeleccionada.getIdentificador());
		Log.d("Milog", "Etiqueta: " + capaSeleccionada.getEtiqueta());

		Object capaBase = capaSeleccionada.getMapLayer();
		Log.d("Milog", "Object capaBase");

		// Correcci�n, para que no cambie la capa base cuando la seleccionada es
		// la misma que ya estaba (ahorra datos)
		LayerList capas = mapa.getMap().getOperationalLayers();
		if (capas != null) {
			Log.d("Milog", "capas no es nulo");
			if (capas.size() > 0) {

				Log.d("Milog", "Hay alguna capa");
				Object capa0 = capas.get(0);
				Log.d("Milog", "Tenemos capa0");
				// si la capa base seleccionada es del mismo tipo que la capa 0
				if (capaBase.getClass().getName()
						.equals(capa0.getClass().getName())) {
					Log.d("Milog",
							"La clase de la capa base es igual que la clase de la capa0");
					 if (capaBase.getClass() == ArcGISVectorTiledLayer.class) {
						Log.d("Milog", "capaBase es de tipo TiledMap");
						 ArcGISTiledLayer capaBaseCasted = (ArcGISTiledLayer) capaBase;
						 ArcGISTiledLayer capa0Casted = (ArcGISTiledLayer) capa0;
						String strUrlCapaBaseCasted = capaBaseCasted.getUri().toString();
						String strUrlCapa0Casted = capa0Casted.getUri().toString();
						if (strUrlCapaBaseCasted.equals(strUrlCapa0Casted)) {
							return;
						} else {
							mapa.getMap().getOperationalLayers().remove(0);
							Log.d("Milog",
									"PUNTO INTERMEDIO TILED: el mapa tiene "
											+ mapa.getMap().getOperationalLayers().size()
											+ " capas");
						}
					}
					Log.d("Milog", "La capa 0 es de clase "
							+ capa0.getClass().getName());
				} else {// si la capa base seleccionada no es del mismo tipo que
						// la capa 0
/*
					if (capaBase.getClass() == BingMapsLayer.class) {
						mapa.removeLayer(0);
					} else if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {
						mapa.removeLayer(0);
					}*/
				}
			}
			// btnAbrirCapas.setEnabled(true);
			if (capaBase.getClass() == ArcGISTiledLayer.class) {

				if (capas.size() > 0) {
					mapa.getMap().getOperationalLayers().add(0, (ArcGISTiledLayer) capaBase);
				} else {
					mapa.getMap().getOperationalLayers().add((ArcGISTiledLayer) capaBase);
				}

			} /*else if (capaBase.getClass() == BingMapsLayer.class) {

				if (capas.length > 0) {
					mapa.addLayer((BingMapsLayer) capaBase, 0);
				} else {
					mapa.addLayer((BingMapsLayer) capaBase);
				}

			}*/ else {
				// otro tipo de capa
			}

			app.capaBaseSeleccionada = capaSeleccionada;
			Log.d("Milog", "El mapa tiene " + mapa.getMap().getOperationalLayers().size()
					+ " capas");
		}
	}

	@Override
	public void seCargoListaPois(ArrayList<Poi> pois) {

	}

	@Override
	public void producidoErrorAlCargarListaPois(String error) {

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
			if (location == null)
				return;

			else if(location != null){									
					// Ha localizado ya por red, cambiar ahora al gps
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, PoiDetailActivity.this);
					
					
					Log.d("Milog", "Entra en onLocationChanged y la coordenada recibida no es nula");
					double locy = location.getLatitude();
					double locx = location.getLongitude();
					
					Log.d("Milog", "1");
					
					Point wgspoint = new Point(locx, locy);
				//Point mapPoint = (Point) GeometryEngine.project(wgspoint, mapa.getSpatialReference());
				Point mapPoint = new Point(locx, locy, SpatialReferences.getWgs84());
					
					Log.d("Milog", "2");
					
					
					// Hacer el extent entre nuestra ubicacion y el punto de checkin
					Envelope env ;
					Log.d("Milog", "2.1");
					Envelope NewEnv = capaGeometrias.getExtent();
					Log.d("Milog", "2.2");
					if(capaGeometrias.getGraphics() != null){
						for (int i=0 ; i<capaGeometrias.getGraphics().size(); i++){
							Log.d("Milog", "2.3");
					    	Point p = (Point) capaGeometrias.getGraphics().get(i).getGeometry();
					    	Log.d("Milog", "2.4");
					    	env = p.getExtent();
							// p.queryEnvelope(env);
					    	Log.d("Milog", "2.5");
					    	//NewEnv.merge(env);
							NewEnv.createFromInternal(env.getInternal());
					    	Log.d("Milog", "2.6");
						}
					}
					
					
					Log.d("Milog", "3");
				    env = mapPoint.getExtent();
					//mapPoint.queryEnvelope(env);
					//NewEnv.merge(env);
					NewEnv.createFromInternal(env.getInternal());
					//mapa.setExtent(NewEnv, 50);
					mapa.setViewpointGeometryAsync(NewEnv, 50);
					
					Log.d("Milog", "Recibida coordenada: " + location.getLatitude() + "  ,  " + location.getLongitude());

				Point punto = (Point) new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
				//Point punto = (Point) GeometryEngine.project(new Point(location.getLongitude(), location.getLatitude()), SpatialReference.create(102100));

			  		ultimaUbicacion = new GeoPoint();
			  		//ultimaUbicacion.setLatitude(location.getLatitude());
			  		//ultimaUbicacion.setLongitude(location.getLongitude());

			  		// Hacer el extent entre nuestra ubicacion y el punto de checkin
					Envelope env1 ;
					Envelope NewEnv1 = capaGeometrias.getExtent();
					if(capaGeometrias.getGraphics() != null){
						for (int i=0 ; i<capaGeometrias.getGraphics().size(); i++){
					    	Point p = (Point)capaGeometrias.getGraphics().get(i).getGeometry();
							env1 = p.getExtent();
					    	//p.queryEnvelope(env1);
							NewEnv1.createFromInternal(env1.getInternal());
					    	//NewEnv1.merge(env1);
						}
					}
				env1 = punto.getExtent();
					//punto.queryEnvelope(env1);
				NewEnv1.createFromInternal(env1.getInternal());
				//NewEnv1.merge(env1);
					mapa.setViewpointGeometryAsync(NewEnv1, 100);
					//mapa.setExtent(NewEnv1, 100);
			}

		mapa.setViewpointCenterAsync(new Point(miPoi.getCoordinates().getLongitude(), miPoi.getCoordinates().getLatitude(), SpatialReferences.getWgs84()), 10000);
		//mapa.centerAndZoom(miPoi.getCoordinates().getLatitude(), miPoi.getCoordinates().getLongitude(), 0.000005F);
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seCargoListaPoisOffline(ArrayList<Poi> pois) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void producidoErrorAlCargarListaPoisOffline(String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seCargoPoiOffline(Poi poi) {
		// TODO Auto-generated method stub
		seCargoPoi(poi);
		panelCargando.setVisibility(View.GONE);
		
	}

	@Override
	public void producidoErrorAlCargarPoiOffline(String error) {
		// TODO Auto-generated method stub
		panelCargando.setVisibility(View.GONE);
		
	}

	@Override
	public void seCompletoDescarga() {
		// TODO Auto-generated method stub
		panelCargandoPoi.setVisibility(View.GONE);
		Util.mostrarMensaje(PoiDetailActivity.this, getResources().getString(R.string.mod_discover__telecarga_completada_titulo),
				getResources().getString(R.string.mod_discover__telecarga_completada_info));
	}

	@Override
	public void setProgresoDescarga(int progress) {
		// TODO Auto-generated method stub
		//ProgressBar pb = (ProgressBar) panelCargandoMapas.getChildAt(0);
		PoiDetailActivity.this.pb.setProgress(progress);
	}

	@Override
	public void inicioDescarga() {
		// TODO Auto-generated method stub
		PoiDetailActivity.this.panelCargandoPoi.setVisibility(View.VISIBLE);
		
	}


}
