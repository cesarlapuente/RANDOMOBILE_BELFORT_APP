package eu.randomobile.pnrlorraine.mod_about;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.About;
import eu.randomobile.pnrlorraine.mod_global.model.About.AboutInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutActivity extends Activity implements AboutInterface {


	// Controles del formulario
	TextView txtNombre;
	TextView txtDescripcion;
	ImageView imagenDestacada;
	Button btnVolver;
	Button btnHome;
	RelativeLayout panelCargando;

	
	// Array con los elementos que contendrá la lista de vídeos
	About objAbout = null;
	
	
	MainApp app;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_about__about_activity);
        
        app = (MainApp)getApplication();
        if(android.os.Build.VERSION.SDK_INT >= 11){
        	ActionBar ab = getActionBar();
        	if(ab != null){
        		ab.hide();
        	}
        }

        capturarControles();
        capturarEventos();
        inicializarFicha();
        
    }
    
    
    
    public void onResume(){
    	super.onResume();

    }
    
    
    
    // Captura los controles del layout (xml) para tenerlos en variables a nivel de clase
    private void capturarControles() {
    	
    	txtNombre = (TextView)findViewById(R.id.txtNombre);
    	imagenDestacada = (ImageView)findViewById(R.id.imagenDestacada);
		txtDescripcion = (TextView)findViewById(R.id.txtDescripcion);
		btnVolver = (Button)findViewById(R.id.btnVolver);
		btnHome = (Button)findViewById(R.id.btnHome);
		panelCargando = (RelativeLayout)findViewById(R.id.panelCargando);
	}

    private void capturarEventos() {
    	btnVolver.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

    	btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(AboutActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
    }
    
    private void inicializarFicha(){
    	//pooner estilos (fuente, color, ...)
    	Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
    	Typeface tfBenton = Util.fontBenton_Boo(this);
		this.txtNombre.setTypeface(tfBubleGum);
		this.txtDescripcion.setTypeface(tfBenton);
    	panelCargando.setVisibility(View.GONE);
    	
    	this.txtNombre.setText(getString(R.string.mod_about__acerca_de));
    	
    	//cargar los datos de las informaciones
    	if(DataConection.hayConexion(this)){
    		panelCargando.setVisibility(View.VISIBLE);
    		About.aboutInterface = this;
    		About.cargarAbout(this.app);
    	}else{
    		Util.mostrarMensaje(
					this, 
					getResources().getString(R.string.mod_global__sin_conexion_a_internet), 
					getResources().getString(R.string.mod_global__no_dispones_de_conexion_a_internet) );
    	}
    }


	@Override
	public void seCargoAbout(About about) {
		if(about != null){
			this.objAbout = about;
			//cargamos los valores en los controles
			// Introducimos el valor de las licencias y de la versi—n
			String appName = Util.getAppName(this);
			String appVersion = Util.getAppVersion(this);
			String txtVersionAppName = appName + " - " + appVersion;
			String txtLicencias = getString(R.string.mod_about__texto_acerca_de);
			
			String txtDescripcionModificado = txtVersionAppName + "<br><br>" + this.objAbout.getDescripcion() + "<br><br>" + txtLicencias;
			
			this.objAbout.setDescripcion(txtDescripcionModificado);

			if(!this.objAbout.getDescripcion().equals("null")){
				  txtDescripcion.setText(this.objAbout.getDescripcion());
				  txtDescripcion.setText(Html.fromHtml(this.objAbout.getDescripcion()), TextView.BufferType.SPANNABLE);
			}else{
				txtDescripcion.setText(getString(R.string.mod_global__sin_datos));
			}
			
		}
		panelCargando.setVisibility(View.GONE);
	}



	@Override
	public void producidoErrorAlCargarAbout(String error) {
		// TODO Auto-generated method stub
		panelCargando.setVisibility(View.GONE);
	}
    
	
}
