package eu.randomobile.pnrlorraine.mod_recover_pass;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.User;
import eu.randomobile.pnrlorraine.mod_global.model.User.ResetPasswordInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_login.LoginActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPassActivity extends Activity implements ResetPasswordInterface {

	// Controles
	ImageMap mImageMap;
	Button btnEnviarEmail;
	Button btnResetPassword;
	EditText tbEmail;
	EditText tbCodigo;
	EditText tbNewPassword;
	EditText tbNewPasswordRepeat;
	
	TextView lblTextoExplicativo1;
	TextView lblTextoExplicativo2;
	TextView lblTextoExplicativo3;

	RelativeLayout panelCargando;
	ProgressBar progressBarCargando;
	TextView lblCargando;
	
	String codigoRecibido;
	
	MainApp app;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_recover_pass__layout_reset_pass);
		
		app = (MainApp)getApplication();

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

	}

	private void capturarControles() {
		this.btnEnviarEmail = (Button) findViewById(R.id.btnEnviarMail);
		this.btnResetPassword = (Button) findViewById(R.id.btnReestablecerPassword);
		this.tbEmail = (EditText) findViewById(R.id.tbEmail);
		this.tbCodigo = (EditText) findViewById(R.id.tbCodigo);
		this.tbNewPassword = (EditText) findViewById(R.id.tbNewPassword);
		this.tbNewPasswordRepeat = (EditText) findViewById(R.id.tbNewPasswordRepeat);
		this.lblTextoExplicativo1 = (TextView)findViewById(R.id.lblMensaje1);
		this.lblTextoExplicativo2 = (TextView)findViewById(R.id.lblMensaje2);
		this.lblTextoExplicativo3 = (TextView)findViewById(R.id.lblMensaje3);
		this.panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
		this.progressBarCargando = (ProgressBar) findViewById(R.id.progressBarCargando);
		this.lblCargando = (TextView)findViewById(R.id.lblCargando);
        this.mImageMap = (ImageMap)findViewById(R.id.menuRecoverPassword);
        this.mImageMap.setAttributes(true, false, (float)1.0, "mapa_RecoverPassword");
	    this.mImageMap.setImageResource(R.drawable.menu_recover_password);
	}
	
	private void inicializarForm() {

//		if (android.os.Build.VERSION.SDK_INT >= 11) {
//			ActionBar ab = getActionBar();
//			if (ab != null) {
//				ab.hide();
//			}
//		}

		// Configurar la fuente y el estilo
		Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
		Typeface tfBentonMedium = Util.fontBenton_Medium(this);
		
		
		this.tbEmail.setTypeface(tfBubleGum);
		this.tbCodigo.setTypeface(tfBubleGum);
		this.tbNewPassword.setTypeface(tfBubleGum);
		this.tbNewPasswordRepeat.setTypeface(tfBubleGum);
		
		this.lblTextoExplicativo1.setTypeface(tfBentonMedium);
		this.lblTextoExplicativo2.setTypeface(tfBentonMedium);
		this.lblTextoExplicativo3.setTypeface(tfBentonMedium);
		
		this.btnEnviarEmail.setTypeface(tfBubleGum);
		this.btnResetPassword.setTypeface(tfBubleGum);
		
		this.lblCargando.setTypeface(tfBubleGum);

		// Ocultar la rueda de cargando
		this.panelCargando.setVisibility(View.GONE);
		
		// Ocultar la parte que no interesa al iniciar
		mostrarParteRecuperarPass(false);
		
		
	}

	private void escucharEventos() {
		this.btnEnviarEmail.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				/*
				// Encrypt
				String textoAEncriptar = tbEmail.getText().toString();
				String textoEncriptado  = Globales.drupalSecurity.encrypt(textoAEncriptar);
				
				Log.d("Milog", "Texto a encriptar: " + textoAEncriptar + "  Texto encriptado: " + textoEncriptado);
				
				
				// Decrypt
				String textoADesencriptar = textoEncriptado;
				String textoDesencriptado = Globales.drupalSecurity.decrypt(textoADesencriptar);
				
				Log.d("Milog", "Texto a desenencriptar: " + textoADesencriptar + "  Texto desencriptado: " + textoDesencriptado);
				
				*/
				
				if(validarEmail()){
					if(DataConection.hayConexion(ResetPassActivity.this)){
						mostrarCargando(true, getString(R.string.mod_recover_pass__enviando));
						User.resetPasswordInterface = ResetPassActivity.this;
						User.sendEmailToRecoverPassword( app, tbEmail.getText().toString().trim() );
					}else{
						Util.mostrarMensaje(ResetPassActivity.this,
								getResources().getString(R.string.mod_global__sin_conexion_a_internet),
								getResources().getString(R.string.mod_global__no_dispones_de_conexion_a_internet) );
					}
				}
			}
		});
		
		this.btnResetPassword.setOnClickListener(new  OnClickListener() {
			public void onClick(View arg0) {
				if(validarCodeYPasswords()){
					if(DataConection.hayConexion(ResetPassActivity.this)){
						mostrarCargando(true, getString(R.string.mod_recover_pass__enviando));
						User.resetPasswordInterface = ResetPassActivity.this;
						User.setNewPassword( app, tbEmail.getText().toString(), tbNewPassword.getText().toString() );
					}else{
						Util.mostrarMensaje(ResetPassActivity.this,
								getResources().getString(R.string.mod_global__sin_conexion_a_internet),
								getResources().getString(R.string.mod_global__no_dispones_de_conexion_a_internet) );
					}
				}
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
	
		Intent intent = new Intent(ResetPassActivity.this,
				MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	
	
	private void mostrarParteEnviarMail(boolean mostrar){
		if(mostrar){
			this.lblTextoExplicativo1.setVisibility(View.VISIBLE);
			this.tbEmail.setVisibility(View.VISIBLE);
			this.btnEnviarEmail.setVisibility(View.VISIBLE);
		}else{
			this.lblTextoExplicativo1.setVisibility(View.GONE);
			this.tbEmail.setVisibility(View.GONE);
			this.btnEnviarEmail.setVisibility(View.GONE);
		}
	}
	
	private void mostrarParteRecuperarPass(boolean mostrar){
		if(mostrar){
			this.lblTextoExplicativo2.setVisibility(View.VISIBLE);
			this.lblTextoExplicativo3.setVisibility(View.VISIBLE);
			this.tbCodigo.setVisibility(View.VISIBLE);
			this.tbNewPassword.setVisibility(View.VISIBLE);
			this.tbNewPasswordRepeat.setVisibility(View.VISIBLE);
			this.btnResetPassword.setVisibility(View.VISIBLE);
		}else{
			this.lblTextoExplicativo2.setVisibility(View.GONE);
			this.lblTextoExplicativo3.setVisibility(View.GONE);
			this.tbCodigo.setVisibility(View.GONE);
			this.tbNewPassword.setVisibility(View.GONE);
			this.tbNewPasswordRepeat.setVisibility(View.GONE);
			this.btnResetPassword.setVisibility(View.GONE);
		}
	}
	
	private void mostrarCargando(boolean mostrar, String texto){
		if(mostrar){
			panelCargando.setVisibility(View.VISIBLE);
		}else{
			panelCargando.setVisibility(View.GONE);
		}
		
		if(texto != null && !texto.equals("")){
			lblCargando.setText(texto);
		}
	}
	
	
	private boolean validarEmail(){
		if (tbEmail.getText().toString() == null
				|| tbEmail.getText().toString().equals("")) {
			String titulo = getString(R.string.mod_recover_pass__campos_vacios);
			String mensaje = getString(R.string.mod_recover_pass__por_favor_rellena_el_campo_correo_electronico);
			Util.mostrarMensaje(ResetPassActivity.this, titulo,	mensaje);
			return false;
		}

		if (!Util.validateEmail(tbEmail.getText().toString())) {
			String titulo = getString(R.string.mod_recover_pass__campo_correo_electronico);
			String mensaje = getString(R.string.mod_recover_pass__el_campo_correo_electronico_no_contiene_direccion_valida);
			Util.mostrarMensaje(
					ResetPassActivity.this,
					titulo,
					mensaje);
			return false;
		}
		
		return true;
	}
	
	private boolean validarCodeYPasswords(){
		
		
		if (tbCodigo.getText().toString() == null
				|| tbCodigo.getText().toString().equals("")) {
			String titulo = getString(R.string.mod_recover_pass__campos_vacios);
			String mensaje = getString(R.string.mod_recover_pass__por_favor_introduce_codigo_enviado);
			Util.mostrarMensaje(ResetPassActivity.this, titulo,	mensaje);
			return false;
		}
		
		
		String codIntroducido = codigoRecibido;
		String mailIntroducido = tbEmail.getText().toString();
		String codDesencriptado = app.drupalSecurity.decrypt(codIntroducido);
		Log.d("Milog", "CodIntrod: " + codIntroducido + " mailIntroducido: " + mailIntroducido);
		Log.d("Milog", "CodDesencriptado: " + codDesencriptado);
		
		if( !codDesencriptado.equals(mailIntroducido)  ){
			String titulo = getString(R.string.mod_recover_pass__codigo_erroneo);
			String mensaje = getString(R.string.mod_recover_pass__por_favor_introduce_un_codigo_valido);
			Util.mostrarMensaje(ResetPassActivity.this, titulo,	mensaje);
			return false;
		}
		
		
		if (tbNewPassword.getText().toString() == null
				|| tbNewPassword.getText().toString().equals("")) {
			String titulo = getString(R.string.mod_recover_pass__campos_vacios);
			String mensaje = getString(R.string.mod_recover_pass__por_favor_introduce_una_contrasena);
			Util.mostrarMensaje(ResetPassActivity.this, titulo,	mensaje);
			return false;
		}

		if (tbNewPasswordRepeat.getText().toString() == null
				|| tbNewPasswordRepeat.getText().toString().equals("")) {
			String titulo = getString(R.string.mod_recover_pass__campos_vacios);
			String mensaje = getString(R.string.mod_recover_pass__por_favor_repite_la_contrasena);
			Util.mostrarMensaje(ResetPassActivity.this, titulo,	mensaje);
			return false;
		}
		
		if (!tbNewPassword.getText().toString()
				.equals(tbNewPasswordRepeat.getText().toString())) {
			String titulo = getString(R.string.mod_recover_pass__contrasenas);
			String mensaje = getString(R.string.mod_recover_pass__las_contrasenas_que_has_introducido_no_coinciden);
			Util.mostrarMensaje(ResetPassActivity.this, titulo,	mensaje);
			return false;
		}
		
		return true;
	}


	public void seEnvioEmail(boolean success, int errorCode, String code) {
		// Ocultar cargando
		mostrarCargando(false, null);
		
		if(errorCode == 0){
			
			if(success){
				
				// Rellenar el c—digo
				codigoRecibido = code;
				tbCodigo.setText(code);
				
				// Mostrar mensaje
				String mensaje = getString(R.string.mod_recover_pass__en_breves_momentos_recibiras_un_correo_con_codigo);
				Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
				
				// Ocultar la parte del mail
				mostrarParteEnviarMail(false);
				
				// Mostrar la parte del c—digo y las contrase–as
				mostrarParteRecuperarPass(true);
				
			}else{
				String titulo = getString(R.string.mod_global__error);
				String mensaje = getString(R.string.mod_recover_pass__error_al_recuperar_contrasena);
				Util.mostrarMensaje(this, titulo, mensaje);
			}
			
			
		}else if(errorCode == 2){
			String titulo = getString(R.string.mod_global__error);
			String mensaje = getString(R.string.mod_recover_pass__el_correo_no_existe);
			Util.mostrarMensaje(this, titulo, mensaje);
		}else if(errorCode == 3){
			String titulo = getString(R.string.mod_global__error);
			String mensaje = getString(R.string.mod_recover_pass__ocurrido_problema_al_recuperar_contrasena_en_el_servidor);
			Util.mostrarMensaje(this, titulo, mensaje);	
		}else{
			String titulo = getString(R.string.mod_global__error);
			String mensaje = getString(R.string.mod_recover_pass__error_al_recuperar_contrasena);
			Util.mostrarMensaje(this, titulo, mensaje);
		}
		
		
	}

	public void producidoErrorAlEnviarEmail(String error) {
		// Ocultar cargando
		mostrarCargando(false, null);
		
		String titulo = getString(R.string.mod_global__error);
		String mensaje = getString(R.string.mod_recover_pass__error_al_enviar_tu_email);
		Util.mostrarMensaje(this, titulo, mensaje);
	}

	
	public void seReseteoPassword(boolean success, int errorCode) {
		// Ocultar cargando
		mostrarCargando(false, null);
		
		if(errorCode == 0){
			
			if(success){
				
				// Mostrar mensaje
				String mensaje = getString(R.string.mod_recover_pass__contrasena_cambiada_con_exito);
				Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
				
				// Salir de la pantalla
				finish();
				
			}else{
				String titulo = getString(R.string.mod_global__error);
				String mensaje = getString(R.string.mod_recover_pass__error_al_recuperar_contrasena);
				Util.mostrarMensaje(this, titulo, mensaje);
			}
			
			
		}else if(errorCode == 2){
			String titulo = getString(R.string.mod_global__error);
			String mensaje = getString(R.string.mod_recover_pass__el_correo_no_existe);
			Util.mostrarMensaje(this, titulo, mensaje);
		}else if(errorCode == 3){
			String titulo = getString(R.string.mod_global__error);
			String mensaje = getString(R.string.mod_recover_pass__ocurrido_problema_al_recuperar_contrasena_en_el_servidor);
			Util.mostrarMensaje(this, titulo, mensaje);	
		}else{
			String titulo = getString(R.string.mod_global__error);
			String mensaje = getString(R.string.mod_recover_pass__error_al_recuperar_contrasena);
			Util.mostrarMensaje(this, titulo, mensaje);
		}
	}

	public void producidoErrorAlResetearPassword(String error) {
		// Ocultar cargando
		mostrarCargando(false, null);
				
		String titulo = getString(R.string.mod_global__error);
		String mensaje = getString(R.string.mod_recover_pass__error_al_reestablecer_contrasena);
		Util.mostrarMensaje(this, titulo,mensaje);
	}


}
