package eu.randomobile.pnrlorraine.mod_checkin;

import java.util.ArrayList;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_discover.detail.PoiDetailActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.model.Checkin;
import eu.randomobile.pnrlorraine.mod_global.model.Checkin.CheckinInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CheckinsListActivity extends Activity implements CheckinInterface {
	
	public static final String PARAM_KEY_NID_POI	=	"nid_poi";
	public static final String PARAM_KEY_UID_USER	=	"uid_user";
	
	ImageMap mImageMap;
	String paramNidPoi;
	String paramUidUser;
	
	MainApp app;

	ListView listaCheckins;

	// Array con los elementos que contendra
	ArrayList<Checkin> arrayCheckins = null;

	// Adaptador para la lista de items
	ListCheckinsAdapter checkinAdaptador;


	
	RelativeLayout panelCargando;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_checkin__layout_lista_checkins);
		
		Bundle b = getIntent().getExtras();
		if(b != null){
			paramNidPoi = b.getString(PARAM_KEY_NID_POI);
			paramUidUser = b.getString(PARAM_KEY_UID_USER);
		}

		// Obtener la app
		this.app = (MainApp) getApplication();

		capturarControles();

		escucharEventos();

		inicializarForm();
		
		recargarDatos();
	}

	public void onResume() {
		super.onResume();

	}

	private void capturarControles() {
        mImageMap = (ImageMap)findViewById(R.id.map_menuCheckinList);
        mImageMap.setAttributes(true, false, (float)1.0, "mapa_CheckinList");
	    //mImageMap.setImageResource(R.drawable.mapa_checkin);
		listaCheckins = (ListView) findViewById(R.id.listaCheckins);
		
		panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
	}

	private void escucharEventos() {

		listaCheckins.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				Checkin checkinPulsado = arrayCheckins.get(index);
				Intent intent = new Intent(CheckinsListActivity.this, PoiDetailActivity.class);
				intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, checkinPulsado.getPoi().getNid());
				startActivity(intent);
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
		Intent intent = new Intent(CheckinsListActivity.this,
				MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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

		panelCargando.setVisibility(View.GONE);
		
		
		String strTitulo = "";
		if(paramNidPoi != null && paramUidUser == null){
			//strTitulo = getResources().getString(R.string.mod_checkin__checkins_en_poi);
			mImageMap.setImageResource(R.drawable.mapa_checkin_aqui);
		}else if(paramNidPoi == null && paramUidUser != null){
			strTitulo = getResources().getString(R.string.mod_checkin__mis_checkins);
		}else if(paramNidPoi == null && paramUidUser == null){
			strTitulo = getResources().getString(R.string.mod_checkin__todos_los_checkins);
		}else if(paramNidPoi != null && paramUidUser != null){
			//strTitulo = getResources().getString(R.string.mod_checkin__mis_checkins_en_poi);
			mImageMap.setImageResource(R.drawable.mapa_mis_checkin);
		}
		//this.txtTitulo.setText( strTitulo );

	}


	private void recargarDatos() {
		if (DataConection.hayConexion(this)) {
			// Si hay conexi—n, recargar los datos
			panelCargando.setVisibility(View.VISIBLE);
			Checkin.checkinInterface = this;
			Checkin.cargarListaCheckins(app, paramNidPoi, paramUidUser);
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

	public class ListCheckinsAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context ctx;
		private ArrayList<Checkin> listaItems;

		public class ViewHolder {
			RelativeLayout layoutFondo;
			ImageView imgView;
			TextView lblTitulo;
			TextView lblDetalle;
			ImageView imgViewAccessoryArrow;
			int index;
		}

		public ListCheckinsAdapter(Context _ctx, ArrayList<Checkin> _items) {
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
				Log.d("Milog", "1");
				convertView = mInflater.inflate(
						R.layout.mod_checkin__layout_item_lista_checkins, null);
				holder.layoutFondo = (RelativeLayout) convertView
						.findViewById(R.id.layoutFondo);
				holder.imgView = (ImageView) convertView
						.findViewById(R.id.imgView);
				holder.lblTitulo = (TextView) convertView
						.findViewById(R.id.lblTitulo);
				holder.lblDetalle = (TextView) convertView
						.findViewById(R.id.lblDetalle);
				holder.imgViewAccessoryArrow = (ImageView) convertView
						.findViewById(R.id.accesory);

				// Poner fuentes
				Typeface tfBentonMedium = Util.fontBenton_Medium(ctx);
				Typeface tfBentonBoo = Util.fontBenton_Boo(ctx);
				holder.lblTitulo.setTypeface(tfBentonMedium);
				holder.lblDetalle.setTypeface(tfBentonBoo);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Recoger el item
			Checkin item = listaItems.get(position);
			
			// Poner el t’tulo
			holder.lblTitulo.setText(item.getTitle());
			
			// Poner el body
			if(item.getBody() != null && !item.getBody().equals("")&& !item.getBody().equals("null")){
				holder.lblDetalle.setText(item.getBody());
			}
			
			
			if(item.getImage() != null && !item.getImage().equals("") && !item.getImage().equals("null")){
				BitmapManager.INSTANCE.loadBitmap(item.getImage(), holder.imgView, 200, 200);
			}

			// Poner la flechita de la derecha en funci—n de si la celda es par
			// o no
			if (position % 2 == 0) {
				// Poner flecha 1
				holder.imgViewAccessoryArrow
						.setImageResource(R.drawable.flecha_azuloscuro1);
			} else {
				// Poner flecha 2
				holder.imgViewAccessoryArrow
						.setImageResource(R.drawable.flecha_azuloscuro2);
			}

			return convertView;
		}
	}



	public void seCargoListaCheckins(ArrayList<Checkin> checkins) {
		if (checkins != null) {
			this.arrayCheckins = checkins;
			checkinAdaptador = new ListCheckinsAdapter(this, arrayCheckins);
			listaCheckins.setAdapter(checkinAdaptador);
		}
		panelCargando.setVisibility(View.GONE);
		Log.d("Milog", "seCargoListaCheckins");
	}


	public void producidoErrorAlCargarListaCheckins(String error) {
		panelCargando.setVisibility(View.GONE);
	}
	
	
	
	
	
	@Override
	public void seCargoCheckin(Checkin checkin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void producidoErrorAlCargarCheckin(String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seHaRealizadoCheckin(boolean res, String nidCheckin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void producidoErrorAlRealizarCheckin(String strError, int errorCode) {
		// TODO Auto-generated method stub
		
	}

}
