package eu.randomobile.pnrlorraine.mod_login;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.User;
import eu.randomobile.pnrlorraine.mod_global.model.User.UsuarioInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;
import eu.randomobile.pnrlorraine.mod_recover_pass.ResetPassActivity;
import eu.randomobile.pnrlorraine.mod_register.RegisterUserActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements UsuarioInterface {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	ImageMap mImageMap;
	
	// Values for email and password at the time of the login attempt.
	String mEmail;
	String mPassword;

	// UI references.
	EditText mEmailView;
	EditText mPasswordView;
	Button btnIniciarSesion;
	Button btnRegistro;
	Button btnRecoverPass;
	View mLoginFormView;
	View mLoginStatusView;
	TextView mLoginStatusMessageView;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_login__activity_login);
		capturarControles();
		capturarEventos();
		rellenarForm();
	}

	private void capturarControles() {
		mEmailView = (EditText) findViewById(R.id.email);
        mImageMap = (ImageMap)findViewById(R.id.menuConexion);
        mImageMap.setAttributes(true, false, (float)1.0, "mapa_Conexion");
	    mImageMap.setImageResource(R.drawable.menu_conexion);
	    
		mPasswordView = (EditText) findViewById(R.id.password);


		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		btnIniciarSesion = (Button)findViewById(R.id.sign_in_button);	
		btnRegistro = (Button)findViewById(R.id.register_button);
		btnRecoverPass = (Button)findViewById(R.id.recover_pass_button);
		
	}
	
	private void capturarEventos() {
		btnIniciarSesion.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						hacerLogin();
					}
				});
		
		btnRegistro.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
						startActivity(intent);
					}
				});
		btnRecoverPass.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(LoginActivity.this, ResetPassActivity.class);
						startActivity(intent);
					}
				});
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					hacerLogin();
					return true;
				}
				return false;
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
	
	private void rellenarForm() {
		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView.setText(mEmail);		
		// Poner fuentes
		Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
		this.mEmailView.setTypeface(tfBubleGum);
		this.mPasswordView.setTypeface(tfBubleGum);
		this.btnIniciarSesion.setTypeface(tfBubleGum);
		this.btnRegistro.setTypeface(tfBubleGum);
		this.btnRecoverPass.setTypeface(tfBubleGum);
		this.mLoginStatusMessageView.setTypeface(tfBubleGum);
	}
	
	private void cargaActivityHome() {
		Intent intent = new Intent(LoginActivity.this,
				MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        Log.d("Milog", "Cambio la configuracion");
    }

	
	public void hacerLogin() {

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			
			mPasswordView.setError( getResources().getString(R.string.mod_login__campo_requerido) );
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 3) {
			mPasswordView.setError( getResources().getString(R.string.mod_login__contrasena_incorrecta) );
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError( getResources().getString(R.string.mod_login__campo_requerido) );
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			/*mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;*/
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {

			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText( getResources().getString(R.string.mod_login__iniciando_sesion) );
			mostrarCargando(true);
			
			// ----- Hacer login
			login();
			
			/*Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			finish();*/
			
		}
	}

	
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void mostrarCargando(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	
	
	
	private void abrirPantallaPrincipal(){
		finish();
	}
	
	
	
	
	
	
	private void login(){
		if(DataConection.hayConexion(this)){
			User.usuarioInterface = this;
			User.login( getApplication() , mEmail, mPassword);

		}else{
			mostrarCargando(false);
			Util.mostrarMensaje(this, 
					getResources().getString(R.string.mod_global__sin_conexion_a_internet), 
					getResources().getString(R.string.mod_global__no_dispones_de_conexion_a_internet) );
		}
		
	}

	
	
	
	
	
	@Override
	public void seHizoLoginConExito() {
		mostrarCargando(false);
		abrirPantallaPrincipal();
	}

	@Override
	public void producidoErrorAlHacerLogin(String error) {
		mostrarCargando(false);

		if (error.contains("The username dndlm has not been activated or is blocked")) {
			Util.mostrarMensaje(this, getResources().getString(R.string.mod_login__error_al_iniciar_sesion),
					getResources().getString(R.string.mod_login__usuario_bloqueado_o_inactivo));
		} else {
			Util.mostrarMensaje(this, getResources().getString(R.string.mod_login__error_al_iniciar_sesion),
					getResources().getString(R.string.mod_login__no_se_puede_iniciar_sesion));
		}
	}

	
	
	
	
	
	@Override
	public void seHizoLogoutConExito() {
		// TODO Auto-generated method stub
		Log.d("Logout : ", "OK");
	}

	@Override
	public void producidoErrorAlHacerLogout(String error) {
		// TODO Auto-generated method stub
		Log.d("Logout : ", "Fail");
	}

	@Override
	public void seRegistroUsuarioConExito() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void producidoErrorAlRegistrarUsuario(String error) {
		// TODO Auto-generated method stub
		
	}
	
}
