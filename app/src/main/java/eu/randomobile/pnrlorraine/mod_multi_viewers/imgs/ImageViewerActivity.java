package eu.randomobile.pnrlorraine.mod_multi_viewers.imgs;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;

public class ImageViewerActivity extends Activity {
	public static final String PARAM_KEY_RECURSO = "recurso";

	// Argumentos
	public ResourceFile paramRecurso;

	// Controles
	ImageView imageView;
	// Para el tratamiento del menu
	ImageMap mImageMap = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_multi_viewers__visor_imgs_layout);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			paramRecurso = b.getParcelable(PARAM_KEY_RECURSO);
		}
		mImageMap = (ImageMap) findViewById(R.id.menu_gripImages);
		mImageMap.setAttributes(true, false, (float) 1.0, "images");
		mImageMap.setImageResource(R.drawable.foto);
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

	private void capturarControles() {
		imageView = (ImageView) findViewById(R.id.imageView);
	}

	private void configurarFormulario() {
		if (paramRecurso != null) {
			BitmapManager.INSTANCE.loadBitmap(paramRecurso.getFileUrl(),
					imageView, 1000, 700);
			if (paramRecurso.getFileBody() != null
					&& !paramRecurso.getFileBody().equals("")) {
				final Typeface font = Typeface.createFromAsset(
						this.getAssets(), "fonts/Roboto-Light.ttf");
				final TextView txt = (TextView) this
						.findViewById(R.id.txt_descripcion);
				txt.setTypeface(font);
				//txt.setText(paramRecurso.getFileBody());
				txt.setText(
						Html.fromHtml(paramRecurso.getFileBody() + "<br>"),
						TextView.BufferType.SPANNABLE);
				txt.setVisibility(LinearLayout.VISIBLE);
			}
			if (paramRecurso.getFileTitle() != null
					&& !paramRecurso.getFileTitle().equals("")) {
				final Typeface font = Typeface.createFromAsset(
						this.getAssets(), "fonts/scala-sans-bold.ttf");
				final TextView txt = (TextView) this
						.findViewById(R.id.txt_titulo);
				txt.setTypeface(font);
				txt.setText(paramRecurso.getFileTitle());
				txt.setVisibility(LinearLayout.VISIBLE);
			}
			if (paramRecurso.getCopyright() != null && !paramRecurso.getCopyright().equals("")) {
				String copyright = paramRecurso.getCopyright();
				if (!copyright.contains("\u00a9")) {
					copyright = "\u00a9 " + copyright;
				}
				final Typeface font = Typeface.createFromAsset(
						this.getAssets(), "fonts/scala-sans-bold.ttf");
				final TextView txt = (TextView) this
						.findViewById(R.id.txt_copyright);
				txt.setTypeface(font);
				txt.setText(copyright);
				txt.setVisibility(LinearLayout.VISIBLE);
			}
		} else {
			Util.mostrarMensaje(ImageViewerActivity.this, "Sin imagen",
					"No hay imagen que mostrar");
		}

	}

	private void cargaActivityHome() {
		Intent intent = new Intent(ImageViewerActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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

}
