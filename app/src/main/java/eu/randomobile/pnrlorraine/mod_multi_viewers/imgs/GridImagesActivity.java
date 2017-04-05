package eu.randomobile.pnrlorraine.mod_multi_viewers.imgs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;

public class GridImagesActivity extends Activity {

	public static final String PARAM_KEY_ARRAY_RECURSOS = "array_recursos";

	// Argumentos
	public ArrayList<ResourceFile> paramRecursos;

	GridView gridView;

	GridImageAdapter adapter;

	TextView emptyListView;

	MainApp app;
	// Para el tratamiento del menu
	ImageMap mImageMap = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_multi_viewers__grid_imgs_layout);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			paramRecursos = b.getParcelableArrayList(PARAM_KEY_ARRAY_RECURSOS);
		}

		app = (MainApp) getApplication(); // Para el tratamiento del menu
		mImageMap = (ImageMap) findViewById(R.id.menu_gripImages);
		mImageMap.setAttributes(true, false, (float) 1.0, "images");
		mImageMap.setImageResource(R.drawable.fotos_galeria);

		// Capturar controles
		this.capturarControles();

		// Escuchar eventos
		this.escucharEventos();

		// Configurar formulario
		this.configurarFormulario();
	}

	public void onPause() {
		super.onPause();

	}

	public void onResume() {
		super.onResume();

	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Log.d("Milog", "Cambio la configuracion");
	}

	private void cargaActivityHome() {
		Intent intent = new Intent(GridImagesActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void capturarControles() {
		gridView = (GridView) findViewById(R.id.gridView);
		emptyListView = (TextView) findViewById(android.R.id.empty);
		gridView.setEmptyView(emptyListView);

	}

	private void configurarFormulario() {

		if (paramRecursos != null) {

			Log.d("Milog", "Numero de im�genes: " + paramRecursos.size());

			adapter = new GridImageAdapter();
			gridView.setAdapter(adapter);
			try {
				adapter.notifyDataSetChanged();
			} catch (Exception ex) {
				Log.d("Milog", "Excepci�n en notify dataSet im�genes changed "
						+ ex.toString());
			}

		} else {
			//Util.mostrarMensaje(GridImagesActivity.this, "Sans images","Pas de images pour montrer");
		}

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
							cargaActivityHome();
							break;
						case "BACK":
							finish();
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

	}

	public class GridImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public GridImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return paramRecursos.size();
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
				convertView = mInflater.inflate(
						R.layout.mod_multi_viewers__layout_item_image_grid,
						null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Recoger el recurso
			final ResourceFile rec = paramRecursos.get(position);

			if (rec != null) {
				Log.d("Milog", "Imagen: " + rec.getFileUrl());
				BitmapManager.INSTANCE.loadBitmap(rec.getFileUrl(),
						holder.imageview, 100, 70);
			}

			holder.imageview.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(GridImagesActivity.this,
							ImageViewerActivity.class);
					intent.putExtra(ImageViewerActivity.PARAM_KEY_RECURSO, rec);
					BitmapManager.INSTANCE.cache.remove(rec.getFileUrl());
					startActivity(intent);
				}
			});

			return convertView;
		}
	}

	class ViewHolder {
		ImageView imageview;
		int index;
	}

}
