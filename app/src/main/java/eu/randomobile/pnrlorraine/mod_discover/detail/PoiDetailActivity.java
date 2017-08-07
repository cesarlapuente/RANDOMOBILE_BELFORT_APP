package eu.randomobile.pnrlorraine.mod_discover.detail;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
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

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
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

import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_checkin.CheckinActivity;
import eu.randomobile.pnrlorraine.mod_events.EventsListActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.CapaBase;
import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_global.model.User;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_multi_viewers.imgs.GridImagesActivity;
import eu.randomobile.pnrlorraine.mod_multi_viewers.vids.ListVideosActivity;
import eu.randomobile.pnrlorraine.mod_offline.database.PoiDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RessourceFileDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.RessourceLinkDAO;
import eu.randomobile.pnrlorraine.mod_offline.database.VoteDAO;
import eu.randomobile.pnrlorraine.mod_share.Share;
import eu.randomobile.pnrlorraine.mod_vote.VoteActivity;


public class PoiDetailActivity extends Activity {
	public static final String PARAM_KEY_NID = "nid";
	public static final String PARAM_KEY_DISTANCE = "distance";
	public static final String PARAM_KEY_DESNIVEL = "desnivel";
	public static final String PARAM_KEY_NUMBERVOTES = "nvotos";
	public static final String PARAM_KEY_VALORATION = "valoracion";
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
	private MapView mapa;

	private PoiDAO poiDAO;
	private RessourceFileDAO fileDAO;
	private RessourceLinkDAO linkDAO;
	private VoteDAO voteDAO;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_discover__layout_detail_poi);

		// Obtener la app
		this.app = (MainApp) getApplication();

		poiDAO = new PoiDAO(getApplicationContext());
		fileDAO = new RessourceFileDAO(getApplicationContext());
		linkDAO = new RessourceLinkDAO(getApplicationContext());
		voteDAO = new VoteDAO(getApplicationContext());

		// Recoger par�metros
		Bundle b = getIntent().getExtras();
		if (b != null) {
			paramNid = b.getString(PARAM_KEY_NID);
			paramDistanceMeters = b.getDouble(PARAM_KEY_DISTANCE);
			paramDesnivelMeters = b.getInt(PARAM_KEY_DESNIVEL);
			paramNumberVotes = b.getInt(PARAM_KEY_NUMBERVOTES);
			paramValoration = b.getInt(PARAM_KEY_VALORATION);
		}

		miPoi = poiDAO.getPoi(paramNid);
		miPoi.setImages(fileDAO.getListResourceFiles(paramNid, "images"));
		miPoi.setAudios(fileDAO.getListResourceFiles(paramNid, "audios"));
		miPoi.setVideos(fileDAO.getListResourceFiles(paramNid, "videos"));
		//miPoi.setEnlaces(linkDAO.getListResourceLinks());
		miPoi.setVote(voteDAO.getVote(paramNid));


		capturarControles();
		seCargoPoi(miPoi);
		escucharEventos();
		inicializarForm();
		inicializarMapa();
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
			/*if(locationManager == null){
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			}
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, 10, PoiDetailActivity.this);*/
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
		
	   	/*if (locationManager != null) {
			locationManager.removeUpdates(PoiDetailActivity.this);
			locationManager = null;
		}*/
		
	}
	
	private void capturarControles() {
		mapa = (MapView) findViewById(R.id.mapa);
		mapa.setMap(new ArcGISMap(Basemap.Type.IMAGERY, 47.6333, 6.8667, 16));


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
							/*if (locationManager != null)
								locationManager.removeUpdates(PoiDetailActivity.this);
							locationManager = null;*/
							cargaActivityHome();
							break;
						case "BACK":
							/*if (locationManager != null)
								locationManager.removeUpdates(PoiDetailActivity.this);
							locationManager = null;*/
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
				panelCargando.setVisibility(View.VISIBLE);
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
				mapa.getLocationDisplay().setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
				mapa.setViewpointAsync(new Viewpoint(miPoi.getCoordinates().getLongitude(), miPoi.getCoordinates().getLatitude(), 10000));
			}
		});

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

		panelCargando.setVisibility(View.GONE);
	}

	private void inicializarMapa() {

		ponerCapaBase();

		capaGeometrias = new GraphicsOverlay();
		mapa.getGraphicsOverlays().add(capaGeometrias);
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


			// Poner el t�tulo
			this.txtTitulo.setText(miPoi.getTitle());

			// Poner la imagen de categoria
			if (miPoi.getCat() != -1) {
				// BitmapManager.INSTANCE.loadBitmap(item.getCategory().getIcon(),
				// holder.imgView, 36, 40);
				String category;
				//category = miPoi.getCategory().getName();

				int clase = Integer.valueOf(miPoi.getCat());

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

	private void ponerPoiEnMapa(Poi poi) {
		capaGeometrias.getGraphics().clear();
		GeoPoint gp = poi.getCoordinates();
		ubicacionPunto = gp;

		Point puntoProyectado = new Point(gp.getLongitude(), gp.getLatitude(), SpatialReferences.getWgs84());

		ArrayList<Object> geometrias = new ArrayList<Object>();
		geometrias.add(puntoProyectado);

		String icon = null;
		if (poi.getCategory() != null && poi.getCategory().getIcon() != null) {
			icon = poi.getCategory().getIcon();
		}

		dibujarGeometrias(geometrias, poi.getTitle(), poi.getClass().getName(), poi.getNid(), String.valueOf(poi.getCat()), icon);

		Object geomObj = geometrias.get(0);
		final Point punto = (Point) geomObj;
		mapa.setViewpointCenterAsync(punto, 10000);
	}

	private void dibujarGeometrias(ArrayList<Object> geometrias, String paramNombre, String paramNombreClase, String paramNid, String paramCat, final String urlIcon) {
		int polygonFillColor = Color.rgb(55, 132, 218);
		int polygonBorderColor = Color.rgb(27, 87, 187);

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

		Object capaBase = capaSeleccionada.getMapLayer();

		// Correcci�n, para que no cambie la capa base cuando la seleccionada es
		// la misma que ya estaba (ahorra datos)
		LayerList capas = mapa.getMap().getOperationalLayers();
		if (capas != null) {
			if (capas.size() > 0) {

				Object capa0 = capas.get(0);
				// si la capa base seleccionada es del mismo tipo que la capa 0
				if (capaBase.getClass().getName()
						.equals(capa0.getClass().getName())) {
					 if (capaBase.getClass() == ArcGISVectorTiledLayer.class) {
						 ArcGISTiledLayer capaBaseCasted = (ArcGISTiledLayer) capaBase;
						 ArcGISTiledLayer capa0Casted = (ArcGISTiledLayer) capa0;
						String strUrlCapaBaseCasted = capaBaseCasted.getUri().toString();
						String strUrlCapa0Casted = capa0Casted.getUri().toString();
						if (strUrlCapaBaseCasted.equals(strUrlCapa0Casted)) {
							return;
						} else {
							mapa.getMap().getOperationalLayers().remove(0);
						}
					}
				}
			}
			if (capaBase.getClass() == ArcGISTiledLayer.class) {

				if (capas.size() > 0) {
					mapa.getMap().getOperationalLayers().add(0, (ArcGISTiledLayer) capaBase);
				} else {
					mapa.getMap().getOperationalLayers().add((ArcGISTiledLayer) capaBase);
				}

			}

			app.capaBaseSeleccionada = capaSeleccionada;
		}
	}
}
