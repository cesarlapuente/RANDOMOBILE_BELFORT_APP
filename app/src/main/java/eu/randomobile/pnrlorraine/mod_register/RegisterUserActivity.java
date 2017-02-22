package eu.randomobile.pnrlorraine.mod_register;

import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.Country;
import eu.randomobile.pnrlorraine.mod_global.model.Departamento;
import eu.randomobile.pnrlorraine.mod_global.model.Sesion;
import eu.randomobile.pnrlorraine.mod_global.model.User;
import eu.randomobile.pnrlorraine.mod_global.model.Sesion.SesionInterface;
import eu.randomobile.pnrlorraine.mod_global.model.User.UsuarioInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_login.LoginActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class RegisterUserActivity extends Activity implements
		UsuarioInterface, SesionInterface {

	MainApp app;
	ImageMap mImageMap;
	
	// Controles
	Button btnRegistro;
	Button btnCancelar;
	EditText tbEmail;
	EditText tbPassword;
	EditText tbPasswordRepeat;
	EditText tbNombre;
	EditText tbApellidos;
	Spinner spinnerPaises;
	Spinner spinnerComunidades;

	ArrayList<Country> arrayPaises;
	ArrayList<Departamento> arrayComunidades;

	CountryAdapter adapterPaises;
	ComunidadAdapter adapterComunidades;

	Country paisSeleccionado;
	Departamento comunidadSeleccionada;
	TextView lblEmail;
	TextView lblPassword;
	TextView lblPasswordRepeat;
	TextView lblGPersonales;
	TextView lblNombre;
	TextView lblApellidos;
	TextView lblPaises;
	RelativeLayout panelCargando;
	ProgressBar progressBarCargando;
	
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_register__activity_registro);
		
		this.app = (MainApp)getApplication();

		// Capturar controles
		this.capturarControles();

		// Escuchar eventos
		this.escucharEventos();

		// InicializarForm
		this.inicializarForm();

	}

	public void onPause() {
		super.onPause();

	}

	public void onResume() {
		super.onResume();

		User.usuarioInterface = this;
		Sesion.sesionInterface = this;
	}

	private void capturarControles() {
        this.mImageMap = (ImageMap)findViewById(R.id.menuRegistro);
        this.mImageMap.setAttributes(true, false, (float)1.0, "mapa_Conexion");
	    this.mImageMap.setImageResource(R.drawable.menu_registro);
		this.btnRegistro = (Button) findViewById(R.id.btnRegistro);
		this.btnCancelar = (Button) findViewById(R.id.btnCancelar);
		this.tbEmail = (EditText) findViewById(R.id.tbEmail);
		this.tbPassword = (EditText) findViewById(R.id.tbPassword);
		this.tbPasswordRepeat = (EditText) findViewById(R.id.tbPasswordRepeat);
		this.tbNombre = (EditText) findViewById(R.id.tbNombre);
		this.tbApellidos = (EditText) findViewById(R.id.tbApellidos);
		this.spinnerPaises = (Spinner) findViewById(R.id.spinnerPaises);
		this.spinnerComunidades = (Spinner) findViewById(R.id.spinnerComunidades);
		this.lblEmail = (TextView) findViewById(R.id.lblEmail);
		this.lblPassword = (TextView) findViewById(R.id.lblPassword);
		this.lblPasswordRepeat = (TextView) findViewById(R.id.lblPasswordRepeat);
		this.lblGPersonales = (TextView) findViewById(R.id.lblGPersonales);
		this.lblNombre = (TextView) findViewById(R.id.lblNombre);
		this.lblApellidos = (TextView) findViewById(R.id.lblApellidos);
		this.lblPaises = (TextView) findViewById(R.id.lblPaises);

		this.panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
		this.progressBarCargando = (ProgressBar) findViewById(R.id.progressBarCargando);

	}

	private void escucharEventos() {
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
		this.btnCancelar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});

		this.btnRegistro.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				confirmarRegistro();
			}
		});

		this.spinnerPaises
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int index, long arg3) {
						paisSeleccionado = arrayPaises.get(index);
						Log.d("Milog",
								"Code pulsado: " + paisSeleccionado.getCode());
						if (paisSeleccionado.getCode().equals("ES")) {
							arrayComunidades = Departamento
									.getListComunidades(RegisterUserActivity.this);
							adapterComunidades = new ComunidadAdapter(
									RegisterUserActivity.this,
									android.R.layout.simple_spinner_item,
									arrayComunidades);
							spinnerComunidades.setAdapter(adapterComunidades);
							spinnerComunidades.setVisibility(View.VISIBLE);
						} else {
							spinnerComunidades.setVisibility(View.GONE);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		this.spinnerComunidades
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int index, long arg3) {
						comunidadSeleccionada = arrayComunidades.get(index);

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void inicializarForm() {

		this.arrayPaises = Country.getListCountries();
		this.adapterPaises = new CountryAdapter(this, android.R.layout.simple_spinner_item, this.arrayPaises);
		spinnerPaises.setAdapter(adapterPaises);
		
		// Marcar Francia como pa�s por defecto
		if(this.arrayPaises != null){
			Log.d("Milog", "Num de paises en el array: " + arrayPaises.size());
			for(int i=0; i<arrayPaises.size(); i++){
				Country c = arrayPaises.get(i);
				if(c.getCode().equalsIgnoreCase("FR")){
					Log.d("Milog", "El �ndice de espa�a es: " + i + "  Su codigo es: " + c.getIso());
					spinnerPaises.setSelection(i);
					break;
				}else{
					Log.d("Milog", "Este indice no es de Espa�a. Su codigo es: " + c.getIso());
				}
			}
		}
		
		
		this.arrayComunidades = Departamento.getListComunidades(this);
		this.adapterComunidades = new ComunidadAdapter(this,
				android.R.layout.simple_spinner_item, this.arrayComunidades);
		spinnerComunidades.setAdapter(adapterComunidades);
		
		// Marcar Madrid como CCAA por defecto
		if(this.comunidadSeleccionada != null){
			for(int i=0; i<arrayComunidades.size(); i++){
				Departamento c = arrayComunidades.get(i);
				if(c.getCode().equalsIgnoreCase("ES-MD")){
					spinnerComunidades.setSelection(i);
					break;
				}
			}
		}
				

		// Ocultar la rueda de cargando
		this.panelCargando.setVisibility(View.GONE);
		
		// Ocultar el combo de comunidades y su etiqueta
		this.spinnerComunidades.setVisibility(View.GONE);
		
		//Tipograf�as
		Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
		Typeface tfBentonMedium = Util.fontBenton_Medium(this);
		this.tbApellidos.setTypeface(tfBubleGum);
		this.tbEmail.setTypeface(tfBubleGum);
		this.tbNombre.setTypeface(tfBubleGum);
		this.tbPassword.setTypeface(tfBubleGum);
		this.tbPasswordRepeat.setTypeface(tfBubleGum);
		this.btnRegistro.setTypeface(tfBubleGum);
		this.btnCancelar.setTypeface(tfBubleGum);
		
		this.lblApellidos.setTypeface(tfBentonMedium);
		this.lblEmail.setTypeface(tfBentonMedium);
		//this.lblGPersonales.setTypeface(tfBentonMedium);
		this.lblNombre.setTypeface(tfBentonMedium);
		this.lblPaises.setTypeface(tfBentonMedium);
		this.lblPassword.setTypeface(tfBentonMedium);
		this.lblPasswordRepeat.setTypeface(tfBentonMedium);
	}

	private void cargaActivityHome() {
		Intent intent = new Intent(RegisterUserActivity.this,
				MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void confirmarRegistro() {
		AlertDialog.Builder dialogo = new AlertDialog.Builder(
				RegisterUserActivity.this);
		dialogo.setTitle("");
		dialogo.setMessage( getResources().getString(R.string.mod_register__deseas_crear_tu_cuenta) );
		dialogo.setNegativeButton( getResources().getString(R.string.mod_register__cancelar) ,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		dialogo.setPositiveButton( getResources().getString(R.string.mod_register__crear_cuenta) ,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						registro();
					}
				});

		dialogo.show();
	}

	private void registro() {

		if (DataConection.hayConexion(RegisterUserActivity.this)) {
			if (validarCampos()) {

				// Mostrar la rueda de carga
				panelCargando.setVisibility(View.VISIBLE);
				// Bloquear el bot�n de registro
				btnRegistro.setEnabled(false);
				// Bloquear el bot�n de cancelar
				btnCancelar.setEnabled(false);
				// Bloquear todos los campos
				tbEmail.setEnabled(false);
				tbPassword.setEnabled(false);
				tbPasswordRepeat.setEnabled(false);

				HashMap<String, String> params = new HashMap<String, String>();
				params.put("mail", tbEmail.getText().toString());
				params.put("pass", tbPassword.getText().toString());

				// Rellenar el pa�s (en principio con el idioma del dispositivo)
				String langCode = Country.getDeviceCurrentCountry();
				Log.d("Milog", "Idioma seleccionado el el dispositivo: " + langCode);
				if(langCode.equalsIgnoreCase("es") || langCode.equalsIgnoreCase("en") || langCode.equalsIgnoreCase("fr")){
					params.put("language", langCode);
				}else{
					params.put("language", "en");
				}
				
				
				/*
				// Antes s�lo con el idioma seleccionado (s�lo se tiene en cuenta si es de Espa�a o no)
				if (paisSeleccionado.getCode().equalsIgnoreCase("es")) {
					params.put("language", "es");
				} else {
					params.put("language", "en");
				}
				*/
				params.put("field_first_name[und][0][value]", tbNombre
						.getText().toString());
				params.put("field_last_name[und][0][value]", tbApellidos
						.getText().toString());

				params.put("field_country[und][iso2]", paisSeleccionado
						.getCode().toUpperCase());

				if (paisSeleccionado.getCode().equalsIgnoreCase("ES")) {
					params.put("field_ccaa[und][value]", comunidadSeleccionada
							.getCode().toUpperCase());
				}

				// Cambiar el texto del bot�n
				//btnRegistro.setText("Creando cuenta...");

				// Registrar usuario
				User.registro(app, params);

			}
		} else {
			Util.mostrarMensaje(RegisterUserActivity.this,
					getResources().getString(R.string.mod_global__sin_conexion_a_internet),
					getResources().getString(R.string.mod_global__no_dispones_de_conexion_a_internet) );
		}

	}

	private boolean validarCampos() {

		if (tbEmail.getText().toString() == null
				|| tbEmail.getText().toString().equals("")) {
			Util.mostrarMensaje(RegisterUserActivity.this, getResources().getString(R.string.mod_register__campos_vacios),
					getResources().getString(R.string.mod_register__por_favor_rellena_el_campo_email) );
			return false;
		}

		if (!Util.validateEmail(tbEmail.getText().toString())) {
			Util.mostrarMensaje(
					RegisterUserActivity.this,
					getResources().getString(R.string.mod_register__campo_email),
					getResources().getString(R.string.mod_register__campo_email_no_valido) );
			return false;
		}

		if (tbPassword.getText().toString() == null
				|| tbPassword.getText().toString().equals("")) {
			Util.mostrarMensaje(RegisterUserActivity.this, getResources().getString(R.string.mod_register__campos_vacios),
					getResources().getString(R.string.mod_register__por_favor_rellena_el_campo_contrasena) );
			return false;
		}

		if (tbPasswordRepeat.getText().toString() == null
				|| tbPasswordRepeat.getText().toString().equals("")) {
			Util.mostrarMensaje(RegisterUserActivity.this, getResources().getString(R.string.mod_register__campos_vacios),
					getResources().getString(R.string.mod_register__por_favor_rellena_el_campo_repite_contrasena) );
			return false;
		}

		if (!tbPassword.getText().toString()
				.equals(tbPasswordRepeat.getText().toString())) {
			Util.mostrarMensaje(
					RegisterUserActivity.this,
					getResources().getString(R.string.mod_register__contrasenas),
					getResources().getString(R.string.mod_register__las_contrasenas_no_coinciden) );
			return false;
		}

		if (paisSeleccionado == null) {
			Util.mostrarMensaje(RegisterUserActivity.this, getResources().getString(R.string.mod_register__pais),
					getResources().getString(R.string.mod_register__debes_seleccionar_un_pais_para_registrarte) );
			return false;
		}

		if (paisSeleccionado.getCode().equalsIgnoreCase("ES")
				&& comunidadSeleccionada == null) {
			Util.mostrarMensaje(RegisterUserActivity.this,
					getResources().getString(R.string.mod_register__comunidad_autonoma),
					getResources().getString(R.string.mod_register__debes_seleccionar_una_ccaa_para_registrarte) );
			return false;
		}

		return true;
	}

	@Override
	public void seHizoLogoutConExito() {
		// TODO Auto-generated method stub

	}

	@Override
	public void producidoErrorAlHacerLogout(String error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void seRegistroUsuarioConExito() {
		Log.d("Milog", "Exito al registrar un usuario");
		
		// Si se ha registrado con �xito guardar el nombre, los apellidos, el pa�s, comunidad auto�noma y el ranking
		SharedPreferences.Editor editor = app.preferencias.edit();
		editor.putString(app.COOKIE_KEY_NOMBRE_USUARIO_LOGUEADO, this.tbNombre.getText().toString() );
		editor.putString(app.COOKIE_KEY_APELLIDOS_USUARIO_LOGUEADO, this.tbApellidos.getText().toString() );
		
		Country pais = (Country)this.spinnerPaises.getSelectedItem();
		if(pais != null){
			editor.putString(app.COOKIE_KEY_PAIS_USUARIO_LOGUEADO, pais.getIso() );
		}
		
		Departamento com = (Departamento)this.spinnerComunidades.getSelectedItem();
		if(com != null){
			editor.putString(app.COOKIE_KEY_COMUNIDAD_AUTONOMA_USUARIO_LOGUEADO, com.getCode() );
		}
		
		editor.commit();
		

		// Cambiar el texto del bot�n
		btnRegistro.setText( getResources().getString(R.string.mod_register__entrando) );

		// Volvemos a login
		Intent intent = new Intent(RegisterUserActivity.this,
				MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();

	}

	@Override
	public void producidoErrorAlRegistrarUsuario(String error) {
		Log.d("Milog", "Error al registrar un usuario");
		// Ocultar la rueda de carga
		panelCargando.setVisibility(View.GONE);
		// Desbloquear el bot�n de registro
		btnRegistro.setEnabled(true);
		// Desbloquear el bot�n de cancelar
		btnCancelar.setEnabled(true);
		// Desbloquear todos los campos
		tbEmail.setEnabled(true);
		tbPassword.setEnabled(true);
		tbPasswordRepeat.setEnabled(true);

		// Cambiar el texto del bot�n
		//btnRegistro.setText("Registro");

		Util.mostrarMensaje(RegisterUserActivity.this, getResources().getString(R.string.mod_register__error_al_registrar), error);

	}

	@Override
	public void seHizoLoginConExito() {

	}

	@Override
	public void producidoErrorAlHacerLogin(String error) {

	}

	@Override
	public void seComproboLaSesion(boolean sigueActiva) {
		// TODO Auto-generated method stub

	}

	@Override
	public void producidoErrorAlComprobarSesion(String error) {
		// TODO Auto-generated method stub

	}

	private class CountryAdapter extends ArrayAdapter<Country> {
		public CountryAdapter(Activity context, int resource, ArrayList<Country> data) {
			super(context, resource, data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) { // Ordinary
																				// view
																				// in
																				// Spinner,
																				// we
																				// use
																				// android.R.layout.simple_spinner_item
			return super.getView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			// This view starts when we click the
			// spinner.
			return super.getView(position, convertView, parent);
		}
	}

	private class ComunidadAdapter extends ArrayAdapter<Departamento> {

		public ComunidadAdapter(Activity context, int resource,
				ArrayList<Departamento> data) {
			super(context, resource, data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) { // Ordinary
																				// view
																				// in
																				// Spinner,
																				// we
																				// use
																				// android.R.layout.simple_spinner_item
			return super.getView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) { // This view starts when we click the
									// spinner.
			return super.getView(position, convertView, parent);
		}
	}

}
