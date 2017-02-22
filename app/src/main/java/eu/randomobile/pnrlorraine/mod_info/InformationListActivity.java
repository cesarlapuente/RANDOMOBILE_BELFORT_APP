package eu.randomobile.pnrlorraine.mod_info;

import java.util.ArrayList;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_events.EventsListActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.model.Info;
import eu.randomobile.pnrlorraine.mod_global.model.Info.InfosInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InformationListActivity extends Activity implements InfosInterface {

	public static final String PARAM_KEY_ONLY_PASTORAL	=	"pastoral_only";
	
	MainApp app;
	
	boolean paramSoloPastoralInfo = false;
	
	ListView listaInfos;

	// Array con los elementos que contendra
	ArrayList<Info> arrayInfos = null;

	// Adaptador para la lista de elementos
	ListInfoAdapter poiAdaptador;

	Button btnVolver;
	Button btnHome;
	TextView txtTitulo;
	
	Button btnEventos;

	RelativeLayout panelCargando;
	
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_info__layout_lista_info);
		
		// Recoger par‡metros
		Bundle b = getIntent().getExtras();
		if(b != null){
			paramSoloPastoralInfo = b.getBoolean(PARAM_KEY_ONLY_PASTORAL);
		}
		
		// Obtener la app
		this.app = (MainApp)getApplication();

		capturarControles();
		escucharEventos();
		inicializarForm();
		recargarForm();
	}

	public void onResume() {
		super.onResume();
		
		
	}

	private void capturarControles() {

		listaInfos = (ListView) findViewById(R.id.listaInfos);
		btnVolver = (Button) findViewById(R.id.btnVolver);
		btnHome = (Button) findViewById(R.id.btnHome);
		txtTitulo = (TextView) findViewById(R.id.txtNombre);
		btnEventos = (Button) findViewById(R.id.btnEventos);
		panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);

	}
	
	private void escucharEventos(){
		btnVolver.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
		
		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(InformationListActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		listaInfos.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				Info infoPulsado = arrayInfos.get(index);
				Intent intent = new Intent(InformationListActivity.this, InformationDetailActivity.class);
				intent.putExtra(InformationDetailActivity.PARAM_KEY_NID, infoPulsado.getId());
				startActivity(intent);
			}

		});
		
		btnEventos.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Abrir la pantalla de eventos
				Intent intent = new Intent(InformationListActivity.this,
						EventsListActivity.class);
				startActivity(intent);
			}
		});
	}

	@SuppressLint("NewApi")
	private void inicializarForm(){
    	//poner estilos (fuente, color, ...)
    	if(android.os.Build.VERSION.SDK_INT >= 11){
        	ActionBar ab = getActionBar();
        	if(ab != null){
        		ab.hide();
        	}
        }
    	
    	// Fuente
    	Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
		this.txtTitulo.setTypeface(tfBubleGum);
		this.btnEventos.setTypeface(tfBubleGum);
    	
    	
    	// Poner el t’tulo
    	if(paramSoloPastoralInfo == true){
    		this.txtTitulo.setText( getResources().getString(R.string.mod_info__info_pastoral) );
    	}else{
    		this.txtTitulo.setText( getResources().getString(R.string.mod_info__informacion) );
    	}
    	

    	panelCargando.setVisibility(View.GONE);
    }
	
	
	private void recargarForm() {
		recargarDatos();
		
	}
	
	
	private void recargarDatos() {
		if(DataConection.hayConexion(this)){
			// Si hay conexi—n, recargar los datos
			panelCargando.setVisibility(View.VISIBLE);
			Info.infosInterface = this;
			if(paramSoloPastoralInfo){
				Info.cargarListaInfoPastoral(app);
			}else{
				Info.cargarListaInfo(app);
			}
		}else{
			// Si no hay conexi—n a Internet
			Util.mostrarMensaje(
					this, 
					getResources().getString(R.string.mod_global__sin_conexion_a_internet), 
					getResources().getString(R.string.mod_global__no_dispones_de_conexion_a_internet) );
		}
	}
	
	

	public class ListInfoAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context ctx;
		private ArrayList<Info> listaItems;

		public class ViewHolder {
			ImageView imgView;
			TextView lblTitulo;
			TextView lblDetalle;
			int index;
		}

		public ListInfoAdapter(Context _ctx, ArrayList<Info> _items) {
			this.listaItems = _items;
			this.ctx = _ctx;
			mInflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			if (listaItems != null) {
				return listaItems.size();
			} else {
				return 0;
			}
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.mod_info__layout_item_lista_info, null);
				holder.imgView = (ImageView) convertView.findViewById(R.id.imgView);
				holder.lblTitulo = (TextView) convertView.findViewById(R.id.lblTitulo);
				holder.lblDetalle = (TextView) convertView.findViewById(R.id.lblDetalle);
				
				// Fuente
		    	Typeface tfBentonBold = Util.fontBenton_Bold(ctx);
		    	Typeface tfVentonLight = Util.fontBenton_Boo(ctx);
				holder.lblTitulo.setTypeface(tfBentonBold);
				holder.lblDetalle.setTypeface(tfVentonLight);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Recoger el item
			Info item = listaItems.get(position);
			
			// T’tulo
			holder.lblTitulo.setText( item.getNombre() );

			// Detalle
			if(item.getDescripcion() != null){
				String strConPuntos = item.getDescripcion() + "...";
				holder.lblDetalle.setText(Html.fromHtml(strConPuntos), TextView.BufferType.SPANNABLE);
			}else{
				holder.lblDetalle.setText( getResources().getString(R.string.mod_info__sin_descripcion) );
			}
			

			// Imagen
			if(item.getImagenDestacada() != null && !item.getImagenDestacada().equals("") && !item.getImagenDestacada().equals("null")){
				BitmapManager.INSTANCE.loadBitmap(item.getImagenDestacada(), holder.imgView, 200, 200);
			}else{
				holder.imgView.setImageResource(R.drawable.ic_launcher);
			}

			return convertView;
		}
	}


	
	
	@Override
	public void seCargoListaInfos(ArrayList<Info> infos) {
		if (infos != null) {
			this.arrayInfos = infos;
			listaInfos.setAdapter(new ListInfoAdapter(this, arrayInfos));
		}
		panelCargando.setVisibility(View.GONE);
	}

	@Override
	public void producidoErrorAlCargarListaInfos(String error) {
		Log.d("Milog", "producidoErrorAlCargarListaInfos");
		panelCargando.setVisibility(View.GONE);
	}

	@Override
	public void seCargoInfo(Info info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void producidoErrorAlCargarInfo(String error) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
