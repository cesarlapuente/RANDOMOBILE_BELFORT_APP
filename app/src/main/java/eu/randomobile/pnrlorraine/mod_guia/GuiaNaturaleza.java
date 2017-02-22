package eu.randomobile.pnrlorraine.mod_guia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;

public class GuiaNaturaleza extends Activity {
    private MainApp app;

    private ImageMap mImageMap;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guia_naturaleza);

        app = (MainApp) getApplication();

        // find the image map in the view
        mImageMap = (ImageMap) findViewById(R.id.menu_naturaleza_intro);
        mImageMap.setAttributes(true, false, (float) 1.0, "mapa_naturaleza_intro");
        mImageMap.setImageResource(R.drawable.naturaleza_introduccion_on);
        mImageMap.addOnImageMapClickedHandler(this.crearListenerMenu());

        textView = (TextView) findViewById(R.id.txtWebGuia);
        textView.setText(Html.fromHtml(app.getDBHandler().getPageById(92).getBody()).toString());
    }

    /**
     * Crea el listener para las im�genes de cabecera.
     *
     * @return
     */
    private ImageMap.OnImageMapClickedHandler crearListenerMenu() {
        ImageMap.OnImageMapClickedHandler listener = null;
        try {
            listener = new ImageMap.OnImageMapClickedHandler() {
                @Override
                public void onImageMapClicked(int id, ImageMap imageMap) {

                    if (mImageMap.getAreaAttribute(id, "name").equals("HOME")) {
                        cargaActivityHome();
                    } else if (mImageMap.getAreaAttribute(id, "name").equals(
                            "BACK")) {
                        finish();
                    } else if (mImageMap.getAreaAttribute(id, "name").equals(
                            "INTRO")) {
                        Log.d("Guia Naturaleza: ", "Intro pulsado");
                    } else if (mImageMap.getAreaAttribute(id, "name").equals(
                            "ESPECIES")) {
                        Log.d("Guia Naturaleza: ", "Especies pulsado");
                        goToEspecies();
                    } else if (mImageMap.getAreaAttribute(id, "name").equals(
                            "BUSCAR")) {
                        Log.d("Guia Naturaleza: ", "Buscar pulsado");
                    } else if (mImageMap.getAreaAttribute(id, "name").equals(
                            "ESPACIOS")) {
                        Log.d("Guia Naturaleza: ", "Espacios pulsado");
                        goToEspacios();
                    } else {
                        // when the area is tapped, show the name in a
                        // text bubble
                        mImageMap.showBubble(id);
                    }
                }

                @Override
                public void onBubbleClicked(int id) {
                }
            };
        } catch (Exception ex) {
        }
        return listener;
    }

    private void goToEspecies() {
        Intent intent=new Intent(GuiaNaturaleza.this, GuiaNaturalezaEspecies.class);
        startActivity(intent);
    }

    private void goToEspacios() {
        Intent intent=new Intent(GuiaNaturaleza.this, GuiaNaturalezaEspacios.class);
        startActivity(intent);
    }


    private void cargaActivityHome() {
        Intent intent = new Intent(GuiaNaturaleza.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void onResume() {
        super.onResume();
        mImageMap.mBubbleMap.clear();
        mImageMap.postInvalidate();
    }
}