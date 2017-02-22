package eu.randomobile.pnrlorraine.mod_multi_viewers.vids;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceLink;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_multi_viewers.imgs.GridImagesActivity;

public class ListVideosActivity extends Activity {
	private ImageMap mImageMap = null;
	TextView emptyListView;
	public static final String PARAM_KEY_ARRAY_RECURSOS = "array_recursos";
	
	// Par‡metros
	public ArrayList<ResourceLink> paramRecursos;
	LinkAdapter adapter;
	ListView listView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_multi_viewers__list_resources_layout);
        Bundle b = getIntent().getExtras();
        if(b != null){
        	paramRecursos = b.getParcelableArrayList(PARAM_KEY_ARRAY_RECURSOS);
        }

		capturarControles();
		capturarEventos();
		//cargarForm();
		rellenarListView();
	}

	/**
	 * Captura los elementos de la vista.
	 */
	private void capturarControles() {
		mImageMap = (ImageMap) findViewById(R.id.map_videos_list);
		mImageMap.setAttributes(true, false, (float) 1.0, "mapa_lista_videos");
		mImageMap.setImageResource(R.drawable.menu_videos);
		this.listView = (ListView)findViewById(R.id.listaRoutes);
		emptyListView = (TextView) findViewById(android.R.id.empty);
		listView.setEmptyView(emptyListView);
	}

	/**
	 * Captura los eventos que pueden ocurrir en la actividad.
	 */
	private void capturarEventos() {
		mImageMap
				.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
					@Override
					public void onImageMapClicked(int id, ImageMap imageMap) {

						if (mImageMap.getAreaAttribute(id, "name").equals(
								"HOME")) {
							cargaActivityHome();
						} else if (mImageMap.getAreaAttribute(id, "name")
								.equals("BACK")) {
							finish();
						}
					}

					@Override
					public void onBubbleClicked(int id) {
						// react to info bubble for area being tapped

					}
				});
    	this.listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
    			ResourceLink recPulsado = paramRecursos.get(index);
    			if(DataConection.hayConexion(ListVideosActivity.this)){
    				
    				String url = "";
    				
    				if(recPulsado.getUrl().startsWith("http://") || recPulsado.getUrl().startsWith("https://")){
    					url = recPulsado.getUrl();
    				}else{
    					url = "http://" + recPulsado.getUrl();
    				}
    				
    				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    				startActivity(myIntent);
    			}else{
    				//Util.mostrarMensaje(ListVideosActivity.this, "Sin conexi—n a Internet", "Para poder ver los enlaces debes estar conectado a Internet");
    				Util.mostrarMensaje(
    						ListVideosActivity.this,
    						getResources().getString(
    								R.string.mod_global__sin_conexion_a_internet),
    						getResources()
    								.getString(
    										R.string.mod_global__no_dispones_de_conexion_a_internet));
    			}
    			
			}
		});
	}

	/**
	 * Intenta conseguir la información de los vídeos a cargar en el listado de
	 * vídeos del POI.
	 */
	@SuppressWarnings("unchecked")
	private void cargarForm() {
		try {
			List<ResourceFile> videos = (ArrayList<ResourceFile>) this
					.getIntent().getExtras()
					.getParcelable(GridImagesActivity.PARAM_KEY_ARRAY_RECURSOS);
			if (videos.size() == 0)
				emptyListView.setVisibility(View.VISIBLE);
		} catch (Exception ex) {
			Log.w("ListVideosActivity",
					"Error al cargar la lista de la actividad con los vídeos del POI.");
					 emptyListView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Carga la actividad principal eliminando de la pila de actividades todas
	 * las actividades cargadas.
	 */
	private void cargaActivityHome() {
		Intent intent = new Intent(ListVideosActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

    private void rellenarListView(){
    	if(paramRecursos != null){
    		adapter = new LinkAdapter(ListVideosActivity.this, R.layout.mod_multi_viewers__layout_item_lista_recursos, paramRecursos);
        	listView.setAdapter(adapter);
            try {
            	adapter.notifyDataSetChanged();
    		} catch (Exception e) {
    			Log.d("Milog", e.toString());
    		}
    	}
    	
    }

	
	
	
	
	public static class LinkAdapter extends ArrayAdapter<ResourceLink>{
		private ArrayList<ResourceLink> items;
		private LayoutInflater inflator;

	    public LinkAdapter(Context context, int textViewResourceId, ArrayList<ResourceLink> items) {
	            super(context, textViewResourceId, items);
	            inflator = (LayoutInflater) context  
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	            this.items = items;
	    }
	    
	    
	    private static class ViewHolder {  
	        ImageView imageView;
	        TextView lblTitulo;
	    } 

	    public View getView(int position, View convertView, ViewGroup parent) {
	    	ViewHolder holder;  
	    	  
	        if (convertView == null) {  
	            convertView = inflator.inflate(R.layout.mod_multi_viewers__layout_item_lista_recursos, null);  
	            
	            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
	            TextView lblTitulo = (TextView) convertView.findViewById(R.id.lblTitulo);

	            holder = new ViewHolder();  
	            holder.imageView = imageView;
	            holder.lblTitulo = lblTitulo;
	            
	            convertView.setTag(holder); 
	        
	        } else {  
	            holder = (ViewHolder) convertView.getTag();  
	        }  
	  
	        
	        ResourceLink rec = items.get(position);
	        
	        // Ponemos nombre
	        if(rec.getTitle() == null || rec.getTitle().equals("")){
	        	holder.lblTitulo.setText("(V’deo sin nombre)");
	        }else{
	        	holder.lblTitulo.setText(rec.getTitle());
	        }

	        //holder.imageView.setImageResource(R.drawable.icono_listado_video);
	        holder.imageView.setImageResource(R.drawable.menuinferior_video);
	        return convertView;
	    	
	    }

	    
	}

}
