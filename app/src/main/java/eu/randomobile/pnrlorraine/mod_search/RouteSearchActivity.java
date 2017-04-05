package eu.randomobile.pnrlorraine.mod_search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.Locale;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;

public class RouteSearchActivity extends Activity {
	MainApp app;
	ImageMap mImageMap = null;
	TextView lblValProximidad = null;
	TextView lblValDuracion = null;
	TextView lblValDistancia = null;
	TextView lblValDificultad = null;
	EditText edKeyWord = null;
	Button btnDificultadBaja = null;
	Button btnDificultadMedia = null;
	Button btnDificultadAlta = null;
	Button btnDificultadMuyAlta = null;
	Button btnSearch = null;
	SeekBar sbProximidad = null;
	SeekBar sbDuracion = null;
	SeekBar sbDistancia = null;
	CheckBox cbProximidad = null;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_discover__layout_search_route);

		// Obtener la app
		this.app = (MainApp)getApplication();
		//Para el tratamiento del menu
        mImageMap = (ImageMap)findViewById(R.id.map_routeSearch);
        mImageMap.setAttributes(true, false, (float)1.0, "menu_busqueda");
	    mImageMap.setImageResource(R.drawable.pantalla_busqueda);
		// Recoger par‡metros
		Bundle b = getIntent().getExtras();
		if(b != null){
//			paramNid = b.getString(PARAM_KEY_NID);
//			paramDistanceMeters = b.getDouble(PARAM_KEY_DISTANCE);
		}
		capturarControles();
		inicializarForm();
		escucharEventos();
		
//		recargarDatos();
	}
	private void capturarControles() {

		lblValProximidad = (TextView) findViewById(R.id.lb_ValProximidad);
		lblValDuracion = (TextView) findViewById(R.id.lb_ValDuracion);
		lblValDistancia = (TextView) findViewById(R.id.lb_ValDistancia);
		lblValDificultad = (TextView) findViewById(R.id.lb_ValDificultad);
		edKeyWord = (EditText) findViewById(R.id.et_Keyword);
		sbProximidad = (SeekBar) findViewById(R.id.sb_Proximidad);
		sbDuracion = (SeekBar) findViewById(R.id.sb_Duracion);
		sbDistancia = (SeekBar) findViewById(R.id.sb_Distancia);
//		sbProximidad.setProgressDrawable(getResources()
//				.getDrawable(R.drawable.progress_bar));
//		sbDuracion.setProgressDrawable(getResources()
//				.getDrawable(R.drawable.progress_bar));
//		sbDistancia.setProgressDrawable(getResources()
//				.getDrawable(R.drawable.progress_bar));
		btnDificultadBaja = (Button) findViewById(R.id.btn_facil);
		btnDificultadMedia = (Button) findViewById(R.id.btn_media);
		btnDificultadAlta = (Button) findViewById(R.id.btn_dificil);
		btnDificultadMuyAlta = (Button) findViewById(R.id.btn_muydificil);
		btnSearch = (Button) findViewById(R.id.btn_buscar);
		cbProximidad = (CheckBox) findViewById(R.id.cb_Proximidad);

//		listaRoutes = (ListView) findViewById(R.id.listaRoutes);
//		panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
	}
	
	private void inicializarForm() {
		sbProximidad.setMax(100);
		sbDuracion.setMax(10);
		sbDistancia.setMax(50);
		sbDuracion.setProgress(RouteSearch.getDuracion()/60);
		sbDistancia.setProgress(RouteSearch.getLongitud()/1000);
		sbProximidad.setProgress(RouteSearch.getDistanciaMetros()/1000);
		lblValProximidad.setText(Integer.toString(RouteSearch.getDistanciaMetros()/1000) + " km");
		lblValDuracion.setText(Integer.toString(RouteSearch.getDuracion()/60) + " h");
		lblValDistancia.setText(Integer.toString(RouteSearch.getLongitud()/1000) + " km");
		lblValDificultad.setText(RouteSearch.getDificultad());
		cbProximidad.setChecked(RouteSearch.getCheckDistancia());
		if (RouteSearch.getKeyword() != null)
			edKeyWord.setText(RouteSearch.getKeyword());
		if (RouteSearch.getDificultad().equals("Muy f?cil"))
			btnDificultadBaja.setSelected(true);
		else if (RouteSearch.getDificultad().equals("F?cil"))
			btnDificultadMedia.setSelected(true);
		else if (RouteSearch.getDificultad().equals("Medio"))
			btnDificultadAlta.setSelected(true);
		else if (RouteSearch.getDificultad().equals("Dif?cil"))
			btnDificultadMuyAlta.setSelected(true);
	}
	public void onResume() {
		super.onResume();
		mImageMap.mBubbleMap.clear();
		mImageMap.postInvalidate();
	}
	
	private void cargaActivityHome() {
		Intent intent = new Intent(RouteSearchActivity.this,
				MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void cargaActivityOptions() {
		// Abrir la pantalla de opciones
		Intent intent = new Intent(RouteSearchActivity.this,
				OptionsActivity.class);
		startActivity(intent);
	}
	
	private void escucharEventos() {
        // add a click handler to react when areas are tapped
        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler()
        {
			@Override
			public void onImageMapClicked(int id, ImageMap imageMap)
			{
				
					// when the area is tapped, show the name in a 
					// text bubble
					mImageMap.showBubble(id);
					switch (mImageMap.getAreaAttribute(id, "name")) 
					{
						case "PLUS": cargaActivityOptions();
									break;
						case "HOME": cargaActivityHome();
						             break;
						case  "BACK": finish();
									  break;
						default: mImageMap.showBubble(id);
					}

			}

			@Override
			public void onBubbleClicked(int id)
			{
				// react to info bubble for area being tapped
				
			}
		});
        
	  sbProximidad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		  public void onStopTrackingTouch(SeekBar arg0) {
		       }
		
		  public void onStartTrackingTouch(SeekBar arg0) {
		       }
		  //When progress level of seekbar2 is changed
		  public void onProgressChanged(SeekBar arg0,
		       int progress, boolean arg2) {
		   lblValProximidad.setText(Integer.toString(progress) + " km");
		   RouteSearch.setDistancia(progress * 1000);
	    }
	  });

	  sbDuracion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		  public void onStopTrackingTouch(SeekBar arg0) {
		       }
		
		  public void onStartTrackingTouch(SeekBar arg0) {
		       }
		  //When progress level of seekbar2 is changed
		  public void onProgressChanged(SeekBar arg0,
		       int progress, boolean arg2) {
		   lblValDuracion.setText(Integer.toString(progress) + " h");
		   RouteSearch.setDuracion(progress * 60);
	    }
	  });
	  
	  sbDistancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		  public void onStopTrackingTouch(SeekBar arg0) {
		       }
		
		  public void onStartTrackingTouch(SeekBar arg0) {
		       }
		  //When progress level of seekbar2 is changed
		  public void onProgressChanged(SeekBar arg0,
		       int progress, boolean arg2) {
			  if (progress == 0)
				  progress = 1;
			  lblValDistancia.setText(Integer.toString(progress) + " km");
			  RouteSearch.setLongitud(progress * 1000);
	    }
	  });
	  
	  cbProximidad.setOnClickListener(new OnClickListener() {	 
			  @Override
			  public void onClick(View v) {
		                //is chkProximidad checked?
				if (((CheckBox) v).isChecked())
					RouteSearch.setCheckDistancia(true);
				else
					RouteSearch.setCheckDistancia(false);
		 
			  }
	  });
	  
	  
	  btnDificultadBaja.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            lblValDificultad.setText(getResources().getString(R.string.muy_facil));
	            btnDificultadBaja.setTextColor(getResources().getColor(R.color.brown_search));
	            btnDificultadMedia.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadAlta.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadMuyAlta.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadBaja.setSelected(true);
	            btnDificultadMedia.setSelected(false);
	            btnDificultadAlta.setSelected(false);
	            btnDificultadMuyAlta.setSelected(false);
	            RouteSearch.setDificultad(getApplicationContext().getResources().getString(R.string.muy_facil));
	        }
	    });
	  
	  btnDificultadMedia.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            lblValDificultad.setText(getResources().getString(R.string.facil));
	            btnDificultadBaja.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadMedia.setTextColor(getResources().getColor(R.color.brown_search));
	            btnDificultadAlta.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadMuyAlta.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadBaja.setSelected(false);
	            btnDificultadMedia.setSelected(true);
	            btnDificultadAlta.setSelected(false);
	            btnDificultadMuyAlta.setSelected(false);
	            RouteSearch.setDificultad(getApplicationContext().getResources().getString(R.string.facil));
	        }
	    });
	  btnDificultadAlta.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            lblValDificultad.setText(getResources().getString(R.string.medio));
	            btnDificultadBaja.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadMedia.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadAlta.setTextColor(getResources().getColor(R.color.brown_search));
	            btnDificultadMuyAlta.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadBaja.setSelected(false);
	            btnDificultadMedia.setSelected(false);
	            btnDificultadAlta.setSelected(true);
	            btnDificultadMuyAlta.setSelected(false);
	            RouteSearch.setDificultad(getApplicationContext().getResources().getString(R.string.medio));
	        }
	    });
	  btnDificultadMuyAlta.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            lblValDificultad.setText(getResources().getString(R.string.dificil));
	            btnDificultadBaja.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadMedia.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadAlta.setTextColor(getResources().getColor(R.color.black));
	            btnDificultadMuyAlta.setTextColor(getResources().getColor(R.color.brown_search));
	            btnDificultadBaja.setSelected(false);
	            btnDificultadMedia.setSelected(false);
	            btnDificultadAlta.setSelected(false);
	            btnDificultadMuyAlta.setSelected(true);
	            RouteSearch.setDificultad(getApplicationContext().getResources().getString(R.string.dificil));
	        }
	    });
	  
	  edKeyWord.addTextChangedListener(new TextWatcher() {
		  

		   public void beforeTextChanged(CharSequence s, int start, 
		     int count, int after) {
		   }
		 
		   public void onTextChanged(CharSequence s, int start, 
		     int before, int count) {
				Locale locale = new Locale("fr", "FR");
			   String keyword = s.toString().trim().toLowerCase(locale);
			   keyword = Normalizer.normalize(keyword, Normalizer.Form.NFD);
			   keyword = keyword.replaceAll("\\p{M}", "");
			   RouteSearch.setKeyword(keyword);
		   }

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
		  });
	  
	  btnSearch.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            Intent intent = new Intent();
	            intent.putExtra("op", "buscar");
	            setResult(RESULT_OK, intent);
	            finish();
	        }
	    });

	}
	
}
