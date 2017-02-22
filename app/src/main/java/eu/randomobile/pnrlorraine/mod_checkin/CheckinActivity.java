package eu.randomobile.pnrlorraine.mod_checkin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.CapaBase;
import eu.randomobile.pnrlorraine.mod_global.model.Checkin;
import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_global.model.Checkin.CheckinInterface;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile.ResourceFileInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;


import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.bing.BingMapsLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.MapView;import com.esri.android.map.LocationDisplayManager.AutoPanMode;



public class CheckinActivity extends Activity implements CheckinInterface, ResourceFileInterface, LocationListener {

	public static final String PARAM_KEY_NID_ITEM_A_HACER_CHECKIN =	"nid_item_a_hacer_checkin";
	public static final String PARAM_KEY_TITLE_ITEM_A_HACER_CHECKIN =	"title_item_a_hacer_checkin";
	public static final String PARAM_KEY_COORDINATES_ITEM_A_HACER_CHECKIN =	"coords_item_a_hacer_checkin";
	ImageMap mImageMap;
	
	String paramNidItem;
	String paramTitleItem;
	GeoPoint paramCoordinatesItemAHacerCheckin;

	LocationManager locationManager;
	
	TextView lblTituloPunto;
	TextView lblPonAquiComentario;
	EditText tbComentario;
	MapView mapa;
	Button btnHacerCheckin;
	TextView lblDistancia;
	Button btnCheckinsEnEstePunto;
	Button btnCheckinsDeUsuarioEnEstePunto;
	Button btnHacerFoto;
	RelativeLayout panelCargando;
	
	GeoPoint ultimaUbicacion;
	GeoPoint ubicacionPunto;
	
	private static final int TAKE_PHOTO_CODE = 1;
	private static String RUTA_GUARDO_FOTO = "";

	private static String ESTADO_ESPERANDO_AL_GPS = "Esperando al GPS para ubicarte";
	private static String ESTADO_CERCA = "Perfecto, te encuentras cerca del lugar";
	private static String ESTADO_LEJOS = "Acércate más al lugar para hacer checkin";
	
	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0 /*50 * 1000*/; // 300 seconds (5 min)
	

	GraphicsLayer capaGeometrias;

	MainApp app;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_checkin__realizar_checkin_layout);
        
        app = (MainApp)getApplication();
        
        
        // Reinicializar la ruta en la que guardar la foto
	    RUTA_GUARDO_FOTO = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + app.CARPETA_SD + "/fotoCheckin" + Util.getCurrentTimeStampFormatoUNIX() + ".png";
	    
        
        Log.d("Milog", "1");
        
        ESTADO_ESPERANDO_AL_GPS = getResources().getString(R.string.mod_geocaching__esperando_al_gps_para_ubicarte);
        ESTADO_CERCA = getResources().getString(R.string.mod_geocaching__te_encuentras_cerca_del_lugar);
        ESTADO_LEJOS = getResources().getString(R.string.mod_geocaching__acercate_mas_al_lugar);
        
        Log.d("Milog", "2");
        
        Bundle b = getIntent().getExtras();
        if(b != null){
        	paramNidItem = b.getString(PARAM_KEY_NID_ITEM_A_HACER_CHECKIN);
        	paramTitleItem = b.getString(PARAM_KEY_TITLE_ITEM_A_HACER_CHECKIN);
        	ubicacionPunto = paramCoordinatesItemAHacerCheckin = b.getParcelable(PARAM_KEY_COORDINATES_ITEM_A_HACER_CHECKIN);
        }
 
        this.capturarControles();
        
        this.inicializarFormulario();
        
        this.inicializarMapa();

        this.escucharEventos();

        
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
	




	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		//Parar GPS
		LocationDisplayManager ls = mapa.getLocationDisplayManager();
		//ls.setLocationListener(new MyLocationListener());
		
		if(ls != null){
		    ls.stop();		 
		}
		
	   	if (locationManager != null) {
			locationManager.removeUpdates(CheckinActivity.this);
			locationManager = null;
		}		
		
	}
    
    public void onResume(){
    	super.onResume();
		LocationDisplayManager ls = mapa.getLocationDisplayManager();
//		ls.setLocationListener(new MyLocationListener());
//		ls.setAutoPanMode(AutoPanMode.OFF);
		ls.start();
		if(locationManager == null){
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, 10, CheckinActivity.this);

    }


    
    
    private void capturarControles(){
        mImageMap = (ImageMap)findViewById(R.id.map_menuCheckin);
        mImageMap.setAttributes(true, false, (float)1.0, "mapa_Checkin");
	    mImageMap.setImageResource(R.drawable.mapa_checkin);
		lblTituloPunto = (TextView) findViewById(R.id.lblTituloPunto);
		lblPonAquiComentario = (TextView)findViewById(R.id.lblPonAquiComentario);
		tbComentario = (EditText) findViewById(R.id.tbComentario);
		mapa = (MapView)findViewById(R.id.mapa);
		btnHacerCheckin = (Button)findViewById(R.id.btnCheckin);
		lblDistancia = (TextView)findViewById(R.id.lblEstadoGPS);
		btnCheckinsEnEstePunto = (Button) findViewById(R.id.btnCheckinsAqui);
		btnCheckinsDeUsuarioEnEstePunto = (Button) findViewById(R.id.btnMisCheckinsAqui);
		btnHacerFoto = (Button)findViewById(R.id.btnHacerFoto);
		panelCargando = (RelativeLayout)findViewById(R.id.panelCargando);
	}
	
	private void inicializarFormulario(){
		// Poner fuente
		Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
		this.lblTituloPunto.setTypeface(tfBubleGum);
		this.lblPonAquiComentario.setTypeface(tfBubleGum);
		this.tbComentario.setTypeface(tfBubleGum);
		this.lblDistancia.setTypeface(tfBubleGum);
		this.btnCheckinsDeUsuarioEnEstePunto.setTypeface(tfBubleGum);
		this.btnCheckinsEnEstePunto.setTypeface(tfBubleGum);
		this.btnHacerCheckin.setTypeface(tfBubleGum);
		this.btnHacerFoto.setTypeface(tfBubleGum);
		this.lblTituloPunto.setText( paramTitleItem );
		
		this.btnHacerCheckin.setVisibility(View.GONE);
	}
	
	
	
	private File getTempFile(Context context){
		  final File path = new File( Environment.getExternalStorageDirectory(), app.CARPETA_SD );
		  if(!path.exists()){
		    path.mkdir();
		  }
		  return new File(path, "imageCheckin_tmp.jpg");
	}
	
	

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();
	
		int height = bm.getHeight();
	
		float scaleWidth = ((float) newWidth) / width;
	
		float scaleHeight = ((float) newHeight) / height;
	
		// create a matrix for the manipulation
	
		Matrix matrix = new Matrix();
	
		// resize the bit map
	
		matrix.postScale(scaleWidth, scaleHeight);
	
		// recreate the new Bitmap
	
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	
		return resizedBitmap;

	}
	
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
		    switch(requestCode){
		      case TAKE_PHOTO_CODE:
		        final File file = getTempFile(this);
		        try {
		        	Log.d("Milog", "Antes de recoger el bitmap");
		        	
		        	BitmapFactory.Options o = new BitmapFactory.Options();
		        	o.inSampleSize = 2;
		        	Bitmap captureBmp = BitmapFactory.decodeFile(file.getAbsolutePath(), o);
		        	
		        	
		        	
		        	//Bitmap captureBmp = Media.getBitmap(getContentResolver(), Uri.fromFile(file) );
		        	Log.d("Milog", "Antes de redimensionar el bitmap");
		        	Bitmap resizedBmp = getResizedBitmap(captureBmp, 320, 480);
		        	
		        	Log.d("Milog", "Antes de crear el FileOutputStream");
		        	FileOutputStream out = new FileOutputStream(new File(RUTA_GUARDO_FOTO));
		        	Log.d("Milog", "Antes de comprimir el Bitmap al FileOutputStream");
		        	resizedBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		        	Log.d("Milog", "Antes de hacer el flush()");
		            out.flush();
		            Log.d("Milog", "Antes de hacer el close");
		            out.close();

		        	
		        	// do whatever you want with the bitmap (Resize, Rename, Add To Gallery, etc)
		        } catch (FileNotFoundException e) {
		        	e.printStackTrace();
		        } catch (IOException e) {
		        	e.printStackTrace();
		        } catch (Exception e){
		        	Log.d("Milog", "Excepción al guardar/comprimir el Bitmap: " + e.toString());
		        }
		      break;
		    }
		}
	}
    
    
    private void inicializarMapa(){
    	ponerCapaBase();
    	mapa.setEsriLogoVisible(false);
    	capaGeometrias = new GraphicsLayer();
    	mapa.addLayer(capaGeometrias);
	}
    
    
	
	private void escucharEventos(){
		mapa.setOnStatusChangedListener(new OnStatusChangedListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void onStatusChanged(Object source, STATUS status) {
				if (source == mapa && status == STATUS.INITIALIZED) {

					
					mapa.getLocationDisplayManager().start();
					mapa.getLocationDisplayManager().setAutoPanMode(AutoPanMode.OFF);//.setAutoPan(false);
					
					
					if(locationManager == null){
						locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					}
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, 10, CheckinActivity.this);
					
					
					
			        representarPunto();
			        

			        recalcularDistancia();
					
			        
			        
					
				}
			}
        });
        

        
        
        btnHacerCheckin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(ultimaUbicacion != null){
					try{
						// Si existe el fichero
						File file = new File(RUTA_GUARDO_FOTO);
						if(file.exists()){
							// Si existe
							byte[] byteArrayFile = ResourceFile.convertImageToByteArray(file);
							String b64File = ResourceFile.convertByteArrayToB64(byteArrayFile);

							// Subir el fichero
							if(DataConection.hayConexion(CheckinActivity.this)){
								panelCargando.setVisibility(View.VISIBLE);
								ResourceFile.resourceFileInterface = CheckinActivity.this;
								ResourceFile.fileUpload(
										app, 
										b64File, 
										file.getName());
							}
						}else{
							// No existe el fichero. Hacer checkin igualmente
							if(DataConection.hayConexion(CheckinActivity.this)){
								panelCargando.setVisibility(View.VISIBLE);
								Checkin.checkinInterface = CheckinActivity.this;
								Checkin.realizarCheckin(
										app, 
										paramNidItem, 
										String.valueOf(ultimaUbicacion.getLatitude()), 
										String.valueOf(ultimaUbicacion.getLongitude()), 
										String.valueOf(ultimaUbicacion.getAltitude()), 
										tbComentario.getText().toString(), 
										null
									);
							}
						}
						
						
					}catch (Exception e) {
						Log.d("Milog", "Excepcion: " + e.toString());
					}
					
				}
			}
		});
                
        btnCheckinsEnEstePunto.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(CheckinActivity.this, CheckinsListActivity.class);
				intent.putExtra(CheckinsListActivity.PARAM_KEY_NID_POI, paramNidItem);
				startActivity(intent);
			}
		});
		btnCheckinsDeUsuarioEnEstePunto.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(CheckinActivity.this, CheckinsListActivity.class);
				intent.putExtra(CheckinsListActivity.PARAM_KEY_NID_POI, paramNidItem);
				String uidUser = app.preferencias.getString(app.COOKIE_KEY_ID_USUARIO_LOGUEADO, null);
				intent.putExtra(CheckinsListActivity.PARAM_KEY_UID_USER, uidUser);
				startActivity(intent);
			}
		});
		
		btnHacerFoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				File ficheroFoto = new File(RUTA_GUARDO_FOTO);
				
				
				if(!ficheroFoto.exists()){
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(CheckinActivity.this)) ); 
					startActivityForResult(intent, TAKE_PHOTO_CODE);
				}
			}
		});
		
        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler()
        {
			@Override
			public void onImageMapClicked(int id, ImageMap imageMap)
			{
				
				if (mImageMap.getAreaAttribute(id, "name").equals("HOME")) {
					cargaActivityHome();
				}
				else if (mImageMap.getAreaAttribute(id, "name").equals("BACK")) {
					finish();
				}
			}

			@Override
			public void onBubbleClicked(int id)
			{
				// react to info bubble for area being tapped
				
			}
		});
        
	}
	
	private void cargaActivityHome() {
		Intent intent = new Intent(CheckinActivity.this,
				MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void representarPunto(){
		if(ubicacionPunto != null){
			Point puntoProyectado = GeometryEngine.project(ubicacionPunto.getLongitude(), ubicacionPunto.getLatitude(), app.spatialReference );
			int pointColor = Color.MAGENTA;
			SimpleMarkerSymbol sym = new SimpleMarkerSymbol(pointColor, 10, SimpleMarkerSymbol.STYLE.CIRCLE);
		    Graphic gr = new Graphic(puntoProyectado, sym , null);
			capaGeometrias.addGraphic(gr);
		}
		
	}
	

	


	
	private void recalcularDistancia(){
		
		Log.d("Milog", "Ub punto: " + ubicacionPunto + "  Ub usuario: " + ultimaUbicacion);
		
		if(ultimaUbicacion != null){
			
			
			
			boolean cerca = false;
			if(ubicacionPunto != null && ultimaUbicacion != null){
				
				float distance = GeoPoint.calculateDistance(ubicacionPunto, ultimaUbicacion);
				Log.d("Milog", "Distancia: " + distance);
				if(distance <= app.MAX_DISTANCE_MAKE_CHECKIN_METERS){
					cerca = true;
				}
			}
			
			
			if(cerca){
				// Cambiar la etiqueta de distancia para informar al usuario y color
				this.lblDistancia.setText(ESTADO_CERCA);
				this.lblDistancia.setBackgroundColor(Color.BLUE);
				// Habilitar el botÛn de checkin para que no pueda hacer checkin
				this.btnHacerCheckin.setVisibility(View.VISIBLE);
			}else{
				// Cambiar la etiqueta de distancia para informar al usuario y color
				this.lblDistancia.setText(ESTADO_LEJOS);
				this.lblDistancia.setBackgroundColor(Color.RED);
				// Deshabilitar el botÛn de checkin para que no pueda hacer checkin
				this.btnHacerCheckin.setVisibility(View.INVISIBLE);
			}

		}else{
			// Poner el texto de la etiqueta
			this.lblDistancia.setText(ESTADO_ESPERANDO_AL_GPS);
			this.lblDistancia.setBackgroundColor(Color.GRAY);
			this.btnHacerCheckin.setVisibility(View.INVISIBLE);
		}
	}
	
	
	
	

    
    private void ponerCapaBase(){
		CapaBase capaSeleccionada = app.capaBaseSeleccionada;
		Log.d("Milog", "Identificador: " + capaSeleccionada.getIdentificador());
		Log.d("Milog", "Etiqueta: " + capaSeleccionada.getEtiqueta());

		
		
		Object capaBase = capaSeleccionada.getMapLayer();
		Log.d("Milog", "Object capaBase");
		
		//CorrecciÛn, para que no cambie la capa base cuando la seleccionada es la misma que ya estaba (ahorra datos)
		Layer[] capas = mapa.getLayers();
		if(capas != null){
			Log.d("Milog", "capas no es nulo");
			if(capas.length > 0){
				
				Log.d("Milog", "Hay alguna capa");
				Object capa0 = capas[0];
				Log.d("Milog", "Tenemos capa0");
				//si la capa base seleccionada es del mismo tipo que la capa 0
				if(capaBase.getClass().getName().equals(capa0.getClass().getName())){
					Log.d("Milog", "La clase de la capa base es igual que la clase de la capa0");
					if(capaBase.getClass() == BingMapsLayer.class){
						Log.d("Milog", "capaBase es de tipo BING");
						BingMapsLayer capaBaseCasted = (BingMapsLayer)capaBase;
						BingMapsLayer capa0Casted = (BingMapsLayer)capa0;
							
						if(capaBaseCasted.getMapStyle().equals(capa0Casted.getMapStyle())){
							return;
						}else{
							mapa.removeLayer(0);
							Log.d("Milog", "PUNTO INTERMEDIO BING: el mapa tiene " + mapa.getLayers().length + " capas");
						}
					}else if(capaBase.getClass() == ArcGISTiledMapServiceLayer.class){
						Log.d("Milog", "capaBase es de tipo TiledMap");
						ArcGISTiledMapServiceLayer capaBaseCasted = (ArcGISTiledMapServiceLayer)capaBase;
						ArcGISTiledMapServiceLayer capa0Casted = (ArcGISTiledMapServiceLayer)capa0;
						String strUrlCapaBaseCasted = capaBaseCasted.getUrl().toString();
						String strUrlCapa0Casted = capa0Casted.getUrl().toString();
						if(strUrlCapaBaseCasted.equals(strUrlCapa0Casted)){
							return;
						}else{
							mapa.removeLayer(0);
							Log.d("Milog", "PUNTO INTERMEDIO TILED: el mapa tiene " + mapa.getLayers().length + " capas");
						}
					}
					Log.d("Milog", "La capa 0 es de clase " + capa0.getClass().getName());
				}else{//si la capa base seleccionada no es del mismo tipo que la capa 0

					if(capaBase.getClass() == BingMapsLayer.class){
						mapa.removeLayer(0);
					}else if(capaBase.getClass() == ArcGISTiledMapServiceLayer.class){
						mapa.removeLayer(0);
					}
				}
			}
			//btnAbrirCapas.setEnabled(true);
			if(capaBase.getClass() == ArcGISTiledMapServiceLayer.class){
				
				if(capas.length > 0){
					mapa.addLayer((ArcGISTiledMapServiceLayer)capaBase, 0);
				}else{
					mapa.addLayer((ArcGISTiledMapServiceLayer)capaBase);
				}

			}else if(capaBase.getClass() == BingMapsLayer.class){
				
				if(capas.length > 0){
					mapa.addLayer((BingMapsLayer)capaBase, 0);
				}else{
					mapa.addLayer((BingMapsLayer)capaBase);
				}

			}else{
				//otro tipo de capa
			}
			
			app.capaBaseSeleccionada = capaSeleccionada;
			Log.d("Milog", "El mapa tiene " + mapa.getLayers().length + " capas");
		}
	}

    
    
    
    

	public void seRealizoCheckin() {
		panelCargando.setVisibility(View.GONE);
		String strExito = getResources().getString(R.string.mod_checkin__checkin_realizado_con_exito);
		Toast.makeText(this, strExito, Toast.LENGTH_SHORT).show();
	}


	public void producidoErrorAlHacerCheckin(String error) {
		panelCargando.setVisibility(View.GONE);
		String strError = getResources().getString(R.string.mod_global__error);
		Toast.makeText(this, strError, Toast.LENGTH_SHORT).show();
		finish();
	}

    
	

	
	
	public void seSubioFichero(String fid, String uri) {
		// Se ha subido el fichero, creamos el checkin con ese fichero
		if(DataConection.hayConexion(CheckinActivity.this)){
			panelCargando.setVisibility(View.VISIBLE);
			Checkin.checkinInterface = CheckinActivity.this;
			Checkin.realizarCheckin(
					app, 
					paramNidItem, 
					String.valueOf(ultimaUbicacion.getLatitude()), 
					String.valueOf(ultimaUbicacion.getLongitude()), 
					String.valueOf(ultimaUbicacion.getAltitude()), 
					tbComentario.getText().toString(), 
					fid
				);
		}
	}

	public void producidoErrorAlSubirFichero(String error) {
		// Error al subir fichero. Creamos el checkin de todas formas
		if(DataConection.hayConexion(CheckinActivity.this)){
			panelCargando.setVisibility(View.VISIBLE);
			Checkin.checkinInterface = CheckinActivity.this;
			Checkin.realizarCheckin(
					app, 
					paramNidItem, 
					String.valueOf(ultimaUbicacion.getLatitude()), 
					String.valueOf(ultimaUbicacion.getLongitude()), 
					String.valueOf(ultimaUbicacion.getAltitude()), 
					tbComentario.getText().toString(), 
					null
				);
		}
	}
	
    


	public void seHaRealizadoCheckin(boolean res, String nidCheckin) {
		Toast.makeText(this, getResources().getString(R.string.mod_checkin__checkin_realizado_con_exito), Toast.LENGTH_SHORT).show();
		finish();
	}

	public void producidoErrorAlRealizarCheckin(String strError, int errorCode) {
		String stringError = getResources().getString(R.string.mod_global__error);
		if(errorCode == 1){
			stringError = getResources().getString(R.string.mod_checkin__limite_checkins);
		}

		Util.mostrarMensaje(this, "Error", stringError);
	}
	
	
	




	public void onLocationChanged(Location location) {
		if(location != null){
			
			// Ha localizado ya por red, cambiar ahora al gps
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, CheckinActivity.this);
			
			
			Log.d("Milog", "Entra en onLocationChanged y la coordenada recibida no es nula");
			double locy = location.getLatitude();
			double locx = location.getLongitude();
			
			Log.d("Milog", "1");
			
			Point wgspoint = new Point(locx, locy);
			Point mapPoint = (Point) GeometryEngine.project(wgspoint, SpatialReference.create(4326), mapa.getSpatialReference());
			
			Log.d("Milog", "2");
			
			
			// Hacer el extent entre nuestra ubicacion y el punto de checkin
			Envelope env = new Envelope();
			Log.d("Milog", "2.1");
			Envelope NewEnv = new Envelope();
			Log.d("Milog", "2.2");
			if(capaGeometrias.getGraphicIDs() != null){
				for (int i:capaGeometrias.getGraphicIDs()){
					Log.d("Milog", "2.3");
			    	Point p = (Point) capaGeometrias.getGraphic(i).getGeometry();
			    	Log.d("Milog", "2.4");
			    	p.queryEnvelope(env);
			    	Log.d("Milog", "2.5");
			    	NewEnv.merge(env);
			    	Log.d("Milog", "2.6");
				}
			}
			
			
			Log.d("Milog", "3");
			   
			mapPoint.queryEnvelope(env);
			NewEnv.merge(env);
			mapa.setExtent(NewEnv, 50);
			
			
			Log.d("Milog", "Recibida coordenada: " + location.getLatitude() + "  ,  " + location.getLongitude());
		   	Point punto = GeometryEngine.project(location.getLongitude(),
		   			location.getLatitude(), app.spatialReference);

	  		ultimaUbicacion = new GeoPoint();
	  		ultimaUbicacion.setLatitude(location.getLatitude());
	  		ultimaUbicacion.setLongitude(location.getLongitude());

	  		// Hacer el extent entre nuestra ubicacion y el punto de checkin
			Envelope env1 = new Envelope();
			Envelope NewEnv1 = new Envelope();
			if(capaGeometrias.getGraphicIDs() != null){
				for (int i:capaGeometrias.getGraphicIDs()){
			    	Point p = (Point)capaGeometrias.getGraphic(i).getGeometry();
			    	p.queryEnvelope(env1);
			    	NewEnv1.merge(env1);
				}
			}
			
			   
			punto.queryEnvelope(env1);
			NewEnv1.merge(env1);
			mapa.setExtent(NewEnv1, 50);
	  		recalcularDistancia();
		
		}else{
	   		recalcularDistancia();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}



	


	
	
	


	@Override
	public void seCargoListaCheckins(ArrayList<Checkin> checkins) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void producidoErrorAlCargarListaCheckins(String error) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void seCargoCheckin(Checkin checkin) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void producidoErrorAlCargarCheckin(String error) {
		// TODO Auto-generated method stub
		
	}





	





	





    
    
}
