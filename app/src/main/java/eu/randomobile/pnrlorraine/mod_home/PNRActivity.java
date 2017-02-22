package eu.randomobile.pnrlorraine.mod_home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;

public class PNRActivity extends Activity {
	private MainApp app;

	private ImageMap mImageMap;
	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_home__menu_pnr);

		app = (MainApp) getApplication();

		initializeContent();
	}

	private void initializeContent() {
		mImageMap = (ImageMap) findViewById(R.id.menu_randonees);
		mImageMap.setAttributes(true, false, (float) 1.0, "mapa_creditos");
		mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
			@Override
			public void onImageMapClicked(int id, ImageMap imageMap) {
				if (mImageMap.getAreaAttribute(id, "name").equals("HOME")) {
					cargaActivityHome();
				} else if (mImageMap.getAreaAttribute(id, "name").equals("BACK")) {
					finish();
				} else {
					mImageMap.showBubble(id);
				}
			}

			@Override
			public void onBubbleClicked(int id) {

			}
		});
		mImageMap.setImageResource(R.drawable.menu_creditos);

		textView = (TextView) findViewById(R.id.txtWebPNR);
		textView.setText(Html.fromHtml(app.getDBHandler().getPageById(93).getBody()).toString().trim());
	}

	private void cargaActivityHome() {
		Intent intent = new Intent(PNRActivity.this, MainActivity.class);
		startActivity(intent);
	}

	protected void onResume() {
		super.onResume();
		mImageMap.mBubbleMap.clear();
		mImageMap.postInvalidate();
	}
}