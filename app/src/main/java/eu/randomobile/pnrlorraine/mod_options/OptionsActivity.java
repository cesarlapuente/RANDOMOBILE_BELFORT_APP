package eu.randomobile.pnrlorraine.mod_options;

import java.util.ArrayList;





import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_global.model.User;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_home.PNRActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_login.LoginActivity;
import eu.randomobile.pnrlorraine.mod_notification.Cache;
import eu.randomobile.pnrlorraine.mod_offline.OfflineRoute;
import eu.randomobile.pnrlorraine.mod_offline.OfflineRoute.RoutesModeOfflineInterface;

public class OptionsActivity extends Activity implements RoutesModeOfflineInterface {
	MainApp app;
	ImageMap mImageMap;
	RelativeLayout panelCargando;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_options__options_activity);

		app = (MainApp) getApplication();
		capturarControles();
		capturarEventos();
		inicializarFicha();
	}

	private void capturarControles() {
		mImageMap = (ImageMap) findViewById(R.id.map_menuOpciones);
		mImageMap.setAttributes(true, false, (float) 1.0, "mapa_Opciones");
		mImageMap.setImageResource(R.drawable.menu_opciones);
		panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
	}

	private void capturarEventos() {
		try {
			TextView txt = (TextView) this
					.findViewById(R.id.txt_identificacion);
			txt.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					if (User.isLoggedIn(app))
						mostrarDialogoDesconexion();
					else {
						Intent intent = new Intent(OptionsActivity.this, LoginActivity.class);
						// Intent intent = new Intent(OptionsActivity.this,
						// CreateNotificationActivity.class);
						startActivity(intent);
					}
				}
			});
			txt = (TextView) this.findViewById(R.id.txt_download);
			txt.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					teleCargar();
				}
			});
			txt = (TextView) this.findViewById(R.id.txt_info);
			txt.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent intent = new Intent(OptionsActivity.this, PNRActivity.class);
					startActivity(intent);
				}
			});
			CheckBox check = (CheckBox) this
					.findViewById(R.id.check_login_status_message);
			check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						// The toggle is enabled
						Cache.notificationEnabled = true;
					} else {
						// The toggle is disabled
						Cache.notificationEnabled = false;
					}
				}
			});
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
		} catch (Exception ex) {
		}
	}

	private void mostrarDialogoDesconexion() {
		AlertDialog.Builder builder = new AlertDialog.Builder(OptionsActivity.this);
		builder.setMessage(getString(R.string.mod_options__desconectar))
				.setTitle(getString(R.string.mod_options__yaconectado));
		// Add the buttons
		builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	   User.logout(app);
					   Log.d("Logout : ", "btn oui");
		}
		       });
		builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		           }
		       });
		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private void inicializarFicha() {
		try {
			CheckBox check = (CheckBox) this
					.findViewById(R.id.check_login_status_message);
			check.setChecked(Cache.notificationEnabled);
		} catch (Exception ex) {
		}
	}
	
	private void teleCargar() {
		if (DataConection.hayConexion(this)) {
			// Si hay conexi�n, recargar los datos
			panelCargando.setVisibility(View.VISIBLE);
			OfflineRoute.routesInterface = this;
			OfflineRoute.fillRoutesTable(app);
		} else {
			// Si no hay conexi�n a Internet
			Util.mostrarMensaje(
					this,
					getResources().getString(
							R.string.mod_global__sin_conexion_a_internet),
					getResources()
							.getString(
									R.string.mod_global__no_dispones_de_conexion_a_internet));
		}
	}
	public void onResume() {
		super.onResume();

	}

	private void cargaActivityHome() {
		Intent intent = new Intent(OptionsActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void seCargoListaRoutesOffline(ArrayList<Route> routes) {
		// TODO Auto-generated method stub
		panelCargando.setVisibility(View.GONE);
		Util.mostrarMensaje(
				this, 
				getResources()
						.getString(
						R.string.mod_options__descargar),
				getResources()
						.getString(
								R.string.mod_options__descargar_exito));
		
	}

	@Override
	public void producidoErrorAlCargarListaRoutesOffline(String error) {
		// TODO Auto-generated method stub
		panelCargando.setVisibility(View.GONE);
		Util.mostrarMensaje(
				this, 
				getResources()
						.getString(
						R.string.mod_options__descargar),
				getResources()
						.getString(
								R.string.mod_options__descargar_error));
	}

	@Override
	public void seCargoRouteOffline(Route route) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void producidoErrorAlCargarRouteOffline(String error) {
		// TODO Auto-generated method stub
		
	}
}
