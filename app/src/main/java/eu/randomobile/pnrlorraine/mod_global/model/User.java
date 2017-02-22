package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.libraries.rot13.Rot13;
import eu.randomobile.pnrlorraine.mod_login.LoginActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class User {

	private static int puntuacion;
	
	
	
	public static int getPuntuacion() {
		return puntuacion;
	}

	public static void setPuntuacion(int puntuacion) {
		User.puntuacion = puntuacion;
	}


	// Interface para comunicarse con las llamadas as�ncronas del objeto Usuario
	public static UsuarioInterface usuarioInterface;
	public static interface UsuarioInterface {
		public void seHizoLoginConExito();
		public void producidoErrorAlHacerLogin(String error);
		
		public void seHizoLogoutConExito();
		public void producidoErrorAlHacerLogout(String error);
			
		public void seRegistroUsuarioConExito();
		public void producidoErrorAlRegistrarUsuario(String error);
	}
	
	
	// Interface para comunicarse con las llamadas as�ncronas del objeto Usuario
	public static ResetPasswordInterface resetPasswordInterface;
		public static interface ResetPasswordInterface {
			public void seEnvioEmail(boolean success, int errorCode, String code);
			public void producidoErrorAlEnviarEmail(String error);
			
			public void seReseteoPassword(boolean success, int errorCode);
			public void producidoErrorAlResetearPassword(String error);
	}
	
	public static void login(Application application, String mail, String password){

		Log.d("JmLog","Le login est : "+mail+" le mot de passe : "+password);
		String passB64 = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
		String passB64Reverse = new StringBuffer(passB64).reverse().toString();
		String passB64ReverseRot13 = Rot13.cipher(passB64Reverse);

		final MainApp app = (MainApp)application;
		
		app.clienteDrupal.basicLogin(new AsyncHttpResponseHandler() {

			public void onSuccess(String response) {
				Log.d("Milog", "Acabado de hacer login. Ha recibido respuesta: " + response);

				if (response != null && !response.equals("")) {
					try {

						JSONObject resObj = new JSONObject(response);

						// Recuperado el sessid
						String sessId = resObj.getString("sessid");

						Object userObj = resObj.get("user");
						if (userObj.getClass().getName().equals(JSONObject.class.getName())) {
							JSONObject userDic = (JSONObject) userObj;

							Object userUid = userDic.get("uid");
							// Recuperado el userName
							String userName = userDic.getString("name");
							// Recuperado el userMail
							//String userMail = userDic.getString("mail");

							Object field_nombre = userDic.get("field_first_name");
							Object field_apellidos = userDic.get("field_last_name");
							Object field_country = userDic.get("field_country");

							if (userUid.getClass().getName().equals(String.class.getName())) {
								// Recuperado el uid
								String uidStr = (String) userUid;

								SharedPreferences.Editor editor = app.preferencias.edit();

								// Recoger el nombre de pila del usuario
								if (field_nombre != null && field_nombre.getClass().getName().equals(JSONObject.class.getName())) {
									Log.d("Milog", "Nombre no es nulo");
									JSONObject dicFieldNombre = (JSONObject) field_nombre;
									Object undObj = dicFieldNombre.get("und");
									if (undObj != null && undObj.getClass().getName().equals(JSONArray.class.getName())) {
										JSONArray undArray = (JSONArray) undObj;
										Object obj1Und = undArray.get(0);
										if (obj1Und != null && obj1Und.getClass().getName().equals(JSONObject.class.getName())) {
											JSONObject obj1Dic = (JSONObject) obj1Und;
											String value = obj1Dic.getString("value");
											if (value != null) {
												// Poner el valor en el nombre
												editor.putString(app.COOKIE_KEY_NOMBRE_USUARIO_LOGUEADO, value);
											}
										}
									}
								} else {
									Log.d("Milog", "Nombre ES nulo");
								}

								// Recoger los apellidos del usuario
								if (field_apellidos != null && field_apellidos.getClass().getName().equals(JSONObject.class.getName())) {
									Log.d("Milog", "Apellidos no es nulo");
									JSONObject dicFieldApellidos = (JSONObject) field_apellidos;
									Object undObj = dicFieldApellidos.get("und");
									if (undObj != null && undObj.getClass().getName().equals(JSONArray.class.getName())) {
										JSONArray undArray = (JSONArray) undObj;
										Object obj1Und = undArray.get(0);
										if (obj1Und != null && obj1Und.getClass().getName().equals(JSONObject.class.getName())) {
											JSONObject obj1Dic = (JSONObject) obj1Und;
											String value = obj1Dic.getString("value");
											if (value != null) {
												// Poner el valor en el nombre
												editor.putString(app.COOKIE_KEY_APELLIDOS_USUARIO_LOGUEADO, value);
											}
										}
									}
								} else {
									Log.d("Milog", "Apellidos ES nulo");
								}


								if (field_country != null && field_country.getClass().getName().equals(JSONObject.class.getName())) {
									JSONObject dicFieldCountry = (JSONObject) field_country;
									Object undObj = dicFieldCountry.get("und");
									if (undObj != null && undObj.getClass().getName().equals(JSONArray.class.getName())) {
										JSONArray undArray = (JSONArray) undObj;
										Object obj1Und = undArray.get(0);
										if (obj1Und != null && obj1Und.getClass().getName().equals(JSONObject.class.getName())) {
											JSONObject obj1Dic = (JSONObject) obj1Und;
											String value = obj1Dic.getString("iso2");
											if (value != null) {
												editor.putString(app.COOKIE_KEY_PAIS_USUARIO_LOGUEADO, value);
											}
										}
									}
								}




								editor.putString(app.COOKIE_KEY_ID_SESION_USUARIO_LOGUEADO, sessId);
								editor.putString(app.COOKIE_KEY_ID_USUARIO_LOGUEADO, uidStr);
								editor.putString(app.COOKIE_KEY_NICK_USUARIO_LOGUEADO, userName);
								//editor.putString(app.COOKIE_KEY_EMAIL_USUARIO_LOGUEADO, userMail);
								editor.commit();

								if (User.usuarioInterface != null) {
									User.usuarioInterface.seHizoLoginConExito();
									return;
								}

							}

						}

					} catch (Exception e) {
						Log.d("Milog", "Excepcion en login: " + e.toString());
					}
				}


				if (User.usuarioInterface != null) {
					User.usuarioInterface.producidoErrorAlHacerLogin("Error al hacer login");
				}


			}

			public void onFailure(Throwable error) {
				Log.d("Milog", "Acabado de hacer login. Fallo al hacer login. " + error.toString());
				if (User.usuarioInterface != null) {
					User.usuarioInterface.producidoErrorAlHacerLogin(error.getMessage());
				}
			}
		}, mail, passB64ReverseRot13);
	}


	public static void logout(final Application application){
		
		MainApp app = (MainApp)application;
		
		String sessId = app.preferencias.getString(app.COOKIE_KEY_ID_SESION_USUARIO_LOGUEADO, null);
		
		if(sessId == null){
			if(User.usuarioInterface != null){
				User.usuarioInterface.seHizoLogoutConExito();
			}
			return;
		}
		
		
		app.clienteDrupal.userLogout(new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta: " + response);
				MainApp app = (MainApp)application;
				SharedPreferences.Editor editor = app.preferencias.edit();
    			editor.putString(app.COOKIE_KEY_ID_USUARIO_LOGUEADO, null);
    			editor.putString(app.COOKIE_KEY_NICK_USUARIO_LOGUEADO, null);
    			editor.putString(app.COOKIE_KEY_ID_USUARIO_LOGUEADO, null);
    			editor.commit();
				
				if(User.usuarioInterface != null){
					User.usuarioInterface.seHizoLogoutConExito();
				}

			}
			
			public void onFailure(Throwable error) {
				Log.d("JmLog","OnFAILURE ->"+ error.getMessage());
				if (error.getMessage().contains("User is not logged in")) {
					Log.d("JmLog","OnFAILURE Contains not logged");
					MainApp app = (MainApp)application;
					SharedPreferences.Editor editor = app.preferencias.edit();
        			editor.putString(app.COOKIE_KEY_ID_USUARIO_LOGUEADO, null);
        			editor.putString(app.COOKIE_KEY_NICK_USUARIO_LOGUEADO, null);
        			editor.commit();
				}
				if(User.usuarioInterface != null)
					User.usuarioInterface.producidoErrorAlHacerLogout(error.getMessage());
			}
		}
		, sessId);
	}
	
	
	
	public static void registro(Application application, final HashMap<String, String> user){
		final MainApp app = (MainApp)application;
		
		app.clienteDrupal.userRegister(new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				Log.d("Milog", "Respuesta: " + response);
				
				boolean exitoEnRegistro = false;
				
				if(response != null){
					
					try {
						JSONObject resObj = new JSONObject(response);
						String uidStr = resObj.getString("uid");
						String name = user.get("name");
						
						/*Object field_nombre = resObj.get("field_first_name");
                		Object field_apellidos = resObj.get("field_last_name");
                		Object field_country = resObj.get("field_country");
                		Object field_ccaa = resObj.get("field_ccaa");*/

						SharedPreferences.Editor editor = app.preferencias.edit();
            			editor.putString(app.COOKIE_KEY_ID_USUARIO_LOGUEADO, uidStr);
            			editor.putString(app.COOKIE_KEY_NICK_USUARIO_LOGUEADO, name);
            			editor.commit();

						exitoEnRegistro = true;
						
					} catch (JSONException e) {
						Log.d("Milog", "Excepcion al parsear el JSON de crear usuario. " + e.toString());
					} catch (Exception e){
						Log.d("Milog", "Excepcion al crear usuario. " + e.toString());
					}
				}

				
				if(exitoEnRegistro){
					if(User.usuarioInterface != null){
						User.usuarioInterface.seRegistroUsuarioConExito();
					}
				}else{
					if(User.usuarioInterface != null){
						User.usuarioInterface.producidoErrorAlRegistrarUsuario("No se pudo registrar el usuario");
					}
				}
			}
			
			public void onFailure(Throwable error) {
				if(User.usuarioInterface != null){
					Log.d("Milog", "Error: " + error.toString());
					
					String strError = error.toString();
					String errorDevolverUsuario = "";
					if( strError.contains("The name") && strError.contains("The e-mail") ){
						// Error en el name y en el mail (ya existen ambos)
						errorDevolverUsuario = app.getString(R.string.mod_register__nombre_usuario_email_ya_existen);
					}else if( strError.contains("The name") ){
						// Error en el name (ya existe)
						errorDevolverUsuario = app.getString(R.string.mod_register__nombre_usuario_ya_existe);
					}else if( strError.contains("The e-mail") ){
						// Error en el email (ya existe)
						errorDevolverUsuario = app.getString(R.string.mod_register__nombre_usuario_ya_existe);
					}else {
						errorDevolverUsuario = app.getString(R.string.mod_register__email_ya_existe);
					}
					
					User.usuarioInterface.producidoErrorAlRegistrarUsuario(errorDevolverUsuario);
				}
			}
		}
		, user);
	}

	
	
	
	
	

	
	// Enviar un email para recuperar la contrase�a
	public static void sendEmailToRecoverPassword(Application application, String email){
		
		MainApp app = (MainApp)application;

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mail", email);
		String randomKeyCrypt = app.drupalSecurity.getCryptedRandomKey();
		params.put("key", randomKeyCrypt);

		app.clienteDrupal.customMethodCallPost("basic/reset", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de usuario/reset: " + response);
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		
	                		boolean success = dicRes.getBoolean("success");
	                		int error = dicRes.getInt("error");
	                		
	                		String code = null /*dicRes.getString("code")*/;
	                		
	                		if(User.resetPasswordInterface != null){
	                			User.resetPasswordInterface.seEnvioEmail(success, error, code);
	                			return;
	                		}
	                	}

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion usuario/reset: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(User.resetPasswordInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			User.resetPasswordInterface.producidoErrorAlEnviarEmail("Error al recoger respuesta");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(User.resetPasswordInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			User.resetPasswordInterface.producidoErrorAlEnviarEmail(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	// Cambiar la contrase�a
	public static void setNewPassword(Application application, String email, String newPass){

		MainApp app = (MainApp)application;
		
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("mail", email);
			String randomKeyCrypt = app.drupalSecurity.getCryptedRandomKey();
			params.put("key", randomKeyCrypt);
			String passCrypt = app.drupalSecurity.encrypt(newPass);
			params.put("password", passCrypt);

			app.clienteDrupal.customMethodCallPost("basic/chgPass", new AsyncHttpResponseHandler(){
				public void onSuccess(String response) {
					
					Log.d("Milog", "Respuesta de usuario/chgPass: " + response);
					if(response != null && !response.equals("")){
						
						try {
		                	JSONObject dicRes = new JSONObject(response);
		                	if(dicRes != null){
		                		
		                		boolean success = dicRes.getBoolean("success");
		                		int error = dicRes.getInt("error");
		                		
		                		if(User.resetPasswordInterface != null){
		                			User.resetPasswordInterface.seReseteoPassword(success, error);
		                			return;
		                		}
		                	}

		                } catch (Exception e) {
							Log.d("Milog", "Excepcion usuario/chgPass: " + e.toString());
						}
					}
					
					// Informar al delegate
		    		if(User.resetPasswordInterface != null){
		    			Log.d("Milog", "Antes de informar al delegate de un error");
		    			User.resetPasswordInterface.producidoErrorAlResetearPassword("Error al recoger respuesta");
		    		}
					
					
					
				}
				
				public void onFailure(Throwable error) {
					// Informar al delegate
					if(User.resetPasswordInterface != null){
		    			Log.d("Milog", "Antes de informar al delegate de un error");
		    			User.resetPasswordInterface.producidoErrorAlResetearPassword(error.toString());
		    		}
				}
			}, 
			params);
			
		}
	
	
	
	
	
	
	// Pregunta si quiere iniciar sesi�n en el momento que se llame a la funci�n
	public static void askForloginHere(final Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(R.string.mod_login__iniciar_sesion);
		builder.setMessage(R.string.mod_login__es_necesario_iniciar_sesion);

		builder.setPositiveButton(R.string.mod_login__iniciar_sesion, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent intent = new Intent(context, LoginActivity.class);

				context.startActivity(intent);
			}
		});
		builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(context, R.string.mod_login__es_necesario_iniciar_sesion,Toast.LENGTH_LONG).show();
			}
		});

		builder.show();



		/*
		AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
		String iniSes = ctx.getString(R.string.mod_login__iniciar_sesion);
		String msg = ctx.getString(R.string.mod_login__es_necesario_iniciar_sesion);
		String cancelar = ctx.getString(R.string.mod_global__cancelar);
		dialogo.setTitle(iniSes);
		dialogo.setMessage(msg);

		
		dialogo.setPositiveButton(iniSes, new AlertDialog.OnClickListener() {
			public void onClick(AlertDialog arg0, int arg1) {
				Intent intent = new Intent(ctx, LoginActivity.class);
				ctx.startActivity(inisLoggedIntent);
			}
		});
		
		dialogo.show();
		*/
	}
	
	
	// Devuelve un booleano indicando si el usuario est� logueado o no
	public static boolean isLoggedIn(Application application){
		MainApp app = (MainApp)application;
		String idUsuario = app.preferencias.getString(app.COOKIE_KEY_ID_USUARIO_LOGUEADO, null);
		if(idUsuario != null){
			return true;
		}
		return false;
	}
	
	
}
