package eu.randomobile.pnrlorraine.mod_multi_viewers.links;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceLink;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;

public class ListLinksActivity extends Activity {
	
	public static final String PARAM_KEY_ARRAY_RECURSOS = "array_recursos";
	
	// Par‡metros
	public ArrayList<ResourceLink> paramRecursos;
	
	// Controles
	Button btnCerrar;
	TextView txtTitulo;
	Button btnHome;
	ListView listView;
	
	LinkAdapter adapter;

	TextView emptyListView;
	
	MainApp app;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_multi_viewers__list_resources_layout);
        
        app = (MainApp)getApplication();
        
        Bundle b = getIntent().getExtras();
        if(b != null){
        	paramRecursos = b.getParcelableArrayList(PARAM_KEY_ARRAY_RECURSOS);
        }

        // Capturar controles
        this.capturarControles();
        
        // Escuchar eventos
        this.escucharEventos();
        
        // Cargar lista
    	this.rellenarListView();
        
    	this.txtTitulo.setText(getResources().getString(R.string.mod_multi_viewers__galeria_enlaces));
    	
    	Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
    	this.txtTitulo.setTypeface(tfBubleGum);	
        
    }

    public void onPause(){
    	super.onPause();
    	
    }
    
    public void onResume(){
    	super.onResume();

        
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        Log.d("Milog", "Cambio la configuracion");
    }

    
    
    private void capturarControles(){
    	this.btnCerrar = (Button)findViewById(R.id.btnVolver);
//    	this.listView = (ListView)findViewById(R.id.listView);
    	this.btnHome = (Button)findViewById(R.id.btnHome);
    	this.txtTitulo = (TextView)findViewById(R.id.txtNombre);
    	emptyListView = (TextView)findViewById(android.R.id.empty);
    	listView.setEmptyView(emptyListView);
    }
    
    
    private void escucharEventos(){
    	this.btnCerrar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
    	this.btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(ListLinksActivity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
    	this.listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
    			ResourceLink recPulsado = paramRecursos.get(index);
    			if(DataConection.hayConexion(ListLinksActivity.this)){
    				
    				String url = "http://courtaous.trekkmobile.com";
    				
    				if(recPulsado.getUrl().startsWith("http://") || recPulsado.getUrl().startsWith("https://")){
    					url = recPulsado.getUrl();
    				}else{
    					url = "http://" + recPulsado.getUrl();
    				}
    				
    				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    				startActivity(myIntent);
    			}else{
    				Util.mostrarMensaje(ListLinksActivity.this, "Sin conexi—n a Internet", "Para poder ver los enlaces debes estar conectado a Internet");
    			}
    			
			}
		});
    	
    }

    
    
    
    
    
    
    
    
    
    private void rellenarListView(){
    	if(paramRecursos != null){
    		adapter = new LinkAdapter(ListLinksActivity.this, R.layout.mod_multi_viewers__layout_item_lista_recursos, paramRecursos);
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
	        	holder.lblTitulo.setText("(Video sans titre)");
	        }else{
	        	holder.lblTitulo.setText(rec.getTitle());
	        }

	        holder.imageView.setImageResource(R.drawable.icono_listado_enlace);

	        return convertView;
	    	
	    }

	    
	}

    
}
