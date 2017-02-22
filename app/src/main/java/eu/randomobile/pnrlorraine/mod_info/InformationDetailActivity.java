package eu.randomobile.pnrlorraine.mod_info;



import java.util.ArrayList;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.model.Info;
import eu.randomobile.pnrlorraine.mod_global.model.Info.InfosInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InformationDetailActivity extends Activity implements InfosInterface{

	public static final String PARAM_KEY_NID	=	"nid";
	
	// Id del elemento del que vamos a mostrar su información en esta Activity
	String paramIdItem;

	// Controles del formulario
	TextView txtNombre;
	TextView txtDescripcion;
	ImageView imagenDestacada;
	TextView lblTitulo;
	Button btnVolver;
	Button btnHome;
	RelativeLayout panelCargando;

	
	// Array con los elementos que contendrá la lista de vídeos
	Info objInfo = null;
	
	
	MainApp app;
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_info__ficha_info_activity);
        
        app = (MainApp)getApplication();
        if(android.os.Build.VERSION.SDK_INT >= 11){
        	ActionBar ab = getActionBar();
        	if(ab != null){
        		ab.hide();
        	}
        }
        
        // Obtenemos el id y la categoria del Punto seleccionado
        Bundle b = getIntent().getExtras();
        if(b != null){
        	this.paramIdItem = b.getString(PARAM_KEY_NID);
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
		lblTitulo = (TextView)findViewById(R.id.lblTitulo);
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
				Intent intent = new Intent(InformationDetailActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
    }
    
    private void inicializarFicha(){
    	//pooner estilos (fuente, color, ...)
    	Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
    	Typeface tfBenton = Util.fontBenton_Boo(this);
    	Typeface tfBentonBold = Util.fontBenton_Bold(this);
		this.txtNombre.setTypeface(tfBubleGum);
		this.txtDescripcion.setTypeface(tfBenton);
		this.lblTitulo.setTypeface(tfBentonBold);
    	panelCargando.setVisibility(View.GONE);
    	
    	//cargar los datos de las informaciones
    	if(DataConection.hayConexion(this)){
    		panelCargando.setVisibility(View.VISIBLE);
    		Info.infosInterface = this;
    		Info.cargarInfo(this.app, this.paramIdItem);
    	}else{
    		Util.mostrarMensaje(
					this, 
					getResources().getString(R.string.mod_global__sin_conexion_a_internet), 
					getResources().getString(R.string.mod_global__no_dispones_de_conexion_a_internet) );
    	}
    }



	@Override
	public void seCargoListaInfos(ArrayList<Info> infos) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void producidoErrorAlCargarListaInfos(String error) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void seCargoInfo(Info info) {
		if(info != null){
			this.objInfo = info;

			//cargamos los valores en los controles
			txtNombre.setText(this.objInfo.getNombre());
			lblTitulo.setText(this.objInfo.getNombre());
			
			Log.d("Milog", "Descripcion: " + this.objInfo.getDescripcion());
			if(!this.objInfo.getDescripcion().equals("null")){
				  txtDescripcion.setText(this.objInfo.getDescripcion());
 
				  txtDescripcion.setText(Html.fromHtml(this.objInfo.getDescripcion()), TextView.BufferType.SPANNABLE);
				  
			}else{
				txtDescripcion.setText(getString(R.string.mod_global__sin_datos));
			}

			if(!this.objInfo.getImagenDestacada().equals("")){
				BitmapManager.INSTANCE.loadBitmap(this.objInfo.getImagenDestacada(), imagenDestacada, 417, 300);
			}else{
				imagenDestacada.setImageResource(R.drawable.ic_launcher);
			}
			
		}
		panelCargando.setVisibility(View.GONE);
	}



	@Override
	public void producidoErrorAlCargarInfo(String error) {
		// TODO Auto-generated method stub
		panelCargando.setVisibility(View.GONE);
	}
    
	
}
