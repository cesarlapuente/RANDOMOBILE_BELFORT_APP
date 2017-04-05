package eu.randomobile.pnrlorraine.mod_vote;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_discover.detail.RouteDetailActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.Vote;
import eu.randomobile.pnrlorraine.mod_global.model.Vote.VoteInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class VoteActivity extends Activity implements VoteInterface {

	public static final String PARAM_KEY_NID_ITEM_A_VALORAR =	"nid_item_a_valorar";
	public static final String PARAM_KEY_TITLE_ITEM_A_VALORAR =	"title_item_a_valorar";
	
	String paramNidItem;
	String paramTitleItem;
	
	TextView txtTituloItem;
	TextView txtSeccionValoracionActual;
	TextView txtValoracionActual;
	TextView txtNumVotos;
	RatingBar ratingBar;
	
	Button btnEnviarValoracion;

	RelativeLayout panelCargando;

	MainApp app;
	ImageMap mImageMap = null;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_vote__vote_activity);
        
        app = (MainApp)getApplication();
		//Para el tratamiento del menu
        mImageMap = (ImageMap)findViewById(R.id.menu_valorar);
        mImageMap.setAttributes(true, false, (float)1.0, "calificar");
	    mImageMap.setImageResource(R.drawable.calificar_activity);
        
        Bundle b = getIntent().getExtras();
        if(b != null){
        	paramNidItem = b.getString(PARAM_KEY_NID_ITEM_A_VALORAR);
        	paramTitleItem = b.getString(PARAM_KEY_TITLE_ITEM_A_VALORAR);
        }
 
        this.capturarControles();
        
        this.inicializarFormulario();

        this.escucharEventos();

        this.cargarValoracionActual();

    }
	




	public void onPause(){
    	super.onPause();
    }
    
    public void onResume(){
    	super.onResume();

    }
    
    
    private void cargarValoracionActual(){
    	if (DataConection.hayConexion(this)) {
			Vote.voteInterface = this;
			//String uid = app.preferencias.getString(app.COOKIE_KEY_ID_USUARIO_LOGUEADO, null);
			Vote.getVote(app, paramNidItem, null);
		} else {
			// Si no hay conexi—n a Internet
			Util.mostrarMensaje(
					this,
					getResources().getString(
							R.string.mod_global__sin_conexion_a_internet),
					getResources()
							.getString(
									R.string.mod_global__no_dispones_de_conexion_a_internet));
		}
    }
    
    
    private void enviarValoracion(int valuePercent){
    	if (DataConection.hayConexion(this)) {
    		panelCargando.setVisibility(View.VISIBLE);
			Vote.voteInterface = this;
			Vote.setVote(app, paramNidItem, valuePercent);
		} else {
			// Si no hay conexi—n a Internet
			Util.mostrarMensaje(
					this,
					getResources().getString(
							R.string.mod_global__sin_conexion_a_internet),
					getResources()
							.getString(
									R.string.mod_global__no_dispones_de_conexion_a_internet));
		}
    }
    

    
    
    private void capturarControles(){
		txtTituloItem = (TextView) findViewById(R.id.txtTituloItem);
		btnEnviarValoracion = (Button)findViewById(R.id.btnVotar);
		txtSeccionValoracionActual = (TextView) findViewById(R.id.txtSeccionValoracionActual);
		txtValoracionActual = (TextView) findViewById(R.id.txtValoracionActual);
		txtNumVotos = (TextView)findViewById(R.id.txtNumVotos);
		ratingBar  = (RatingBar)findViewById(R.id.ratingBar);
		panelCargando = (RelativeLayout)findViewById(R.id.panelCargando);
	}
	
	private void inicializarFormulario(){
		// Poner fuente
		Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
		this.txtTituloItem.setTypeface(tfBubleGum);
		this.txtSeccionValoracionActual.setTypeface(tfBubleGum);
		this.txtValoracionActual.setTypeface(tfBubleGum);
		this.btnEnviarValoracion.setTypeface(tfBubleGum);
		this.txtNumVotos.setTypeface(tfBubleGum);
		this.txtTituloItem.setText( paramTitleItem );
	}

	private void cargaActivityHome() {
		Intent intent = new Intent(VoteActivity.this,
				MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	private void escucharEventos(){

		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,	boolean fromUser) {
				Log.d("Milog", "Rating: " + rating);
			}
		});
        
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

        btnEnviarValoracion.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				float rating = ratingBar.getRating();
				int votePercent = 0;
				if(rating == 1){
					votePercent = 20;
				}else if(rating == 2){
					votePercent = 40;
				}else if(rating == 3){
					votePercent = 60;
				}else if(rating == 4){
					votePercent = 80;
				}else if(rating == 5){
					votePercent = 100;
				}else{
					votePercent = 100;
				}
				
				enviarValoracion(votePercent);
				
			}
		});


	}



	public void seEnvioVoto(int countVotes, int avgResults) {
		panelCargando.setVisibility(View.GONE);
		
		String strSecValActual = getResources().getString(R.string.mod_vote__media_actual);
		this.txtSeccionValoracionActual.setText(strSecValActual);
		
		String strValActual = avgResults + " / 100";
		this.txtValoracionActual.setText(strValActual);
		
		String strTotalVotes = getResources().getString(R.string.mod_vote__total_votos) + " " + countVotes;
		this.txtNumVotos.setText(strTotalVotes);
		
	}

	public void producidoErrorAlVotar(String error) {
		panelCargando.setVisibility(View.GONE);
		String strError = getResources().getString(R.string.mod_global__error);
		Util.mostrarMensaje(this, strError, strError);
	}



	public void seCargoVoto(int countVotes, int avgResults) {
		
		String strSecValActual = getResources().getString(R.string.mod_vote__media_actual);
		this.txtSeccionValoracionActual.setText(strSecValActual);
		
		String strValActual = avgResults + " / 100";
		this.txtValoracionActual.setText(strValActual);
		
		String strTotalVotes = getResources().getString(R.string.mod_vote__total_votos) + " " + countVotes;
		this.txtNumVotos.setText(strTotalVotes);
		
		
		
	}

	public void producidoErrorAlCargarVoto(String error) {
		
	}


    
    
}
