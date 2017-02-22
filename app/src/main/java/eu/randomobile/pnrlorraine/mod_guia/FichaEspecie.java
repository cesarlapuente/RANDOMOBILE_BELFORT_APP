package eu.randomobile.pnrlorraine.mod_guia;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.list.RoutesListActivity;
import eu.randomobile.pnrlorraine.mod_global.model.Especie;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;

public class FichaEspecie extends Activity {
    MainApp app;

    // Para el tratamiento del menu
    private ImageMap mImageMap = null;
    private ImageView ivMain = null;
    private TextView tvTitle = null;
    private TextView tvType = null;
    private TextView tvDescription = null;

    private Especie especie;

    // Buttons
    private LinearLayout btnEspaciosRelacionados;
    private ImageButton btnGaleria = null;
    private ImageButton btnAudio = null;
    private ImageButton btnPuntuar = null;
    private ImageButton btnVideo = null;
    private ImageButton btnMapa = null;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_especie);

        app = (MainApp) this.getApplication();


        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString(app.KEY_ESPECIES_NID);
        especie = filterEspecie(app.getEspecies(), id);

        initImigeMap();
        setVariables();
        setButtons();
    }

    private Especie filterEspecie(ArrayList<Especie> especies, String id) {
        Especie result = null;

        for (Especie especie : especies) {
            if (especie.getNid().equals(id))
                result = especie;
        }

        return result;
    }

    private void initImigeMap() {
        mImageMap = (ImageMap) findViewById(R.id.map_especie);
        mImageMap.setAttributes(true, false, (float) 1.0, "lista_item_especie");
        mImageMap.setImageResource(R.drawable.detalle_especie);

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
                            case "PLUS":
                                cargaActivityOptions();
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

    private void cargaActivityHome() {
        Intent intent = new Intent(FichaEspecie.this, MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    private void cargaActivityOptions() {
        Intent intent = new Intent(FichaEspecie.this, OptionsActivity.class);

        startActivity(intent);
    }

    private void setVariables() {
        //Main Image
        try {
            ivMain = (ImageView) findViewById(R.id.ivMain);
            ImageLoader.getInstance().displayImage(especie.getImage(), ivMain);

            ivMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    app.setId_especie_ficha(especie.getNid());
                    FrameImageEspecie frame = new FrameImageEspecie();
                    frame.show(getFragmentManager(), "imageFragment");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Title
        try {
            tvTitle = (TextView) findViewById(R.id.lblTitleHeader);
            tvTitle.setText(especie.getTitle());

        } catch (Exception e) {
            tvTitle.setText("Null");
            e.printStackTrace();
        }

        //Type
        try {
            tvType = (TextView) findViewById(R.id.lblTypeEspecie);
            tvType.setText(especie.getTypeName());

        } catch (Exception e) {
            tvType.setText("Null");
            e.printStackTrace();
        }

        //Description
        try {
            tvDescription = (TextView) findViewById(R.id.lblDescripctionEspecie);
            tvDescription.setText(especie.getBody());

        } catch (Exception e) {
            tvDescription.setText("Null");
            e.printStackTrace();
        }
    }

    private void setButtons() {
        btnEspaciosRelacionados = (LinearLayout) findViewById(R.id.layout_Btn_EspaciosRelacionados);
        btnEspaciosRelacionados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Espacios relacionados: ", "Boton pulsado");

                // Toast.makeText(FichaEspecie.this, R.string.txt_function_disabled, Toast.LENGTH_LONG).show();

                if (app.getEspeciesListaEspacios() != null && app.getEspeciesListaEspacios().length > 0) {
                    app.setPoisOfEspecie(especie.getEspacios());

                    Intent intent = new Intent(FichaEspecie.this, RoutesListActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("Filtrar por especie", "1");
                    intent.putExtras(bundle);

                    startActivity(intent);




                    // Espacios Relacionados -v-

                    /*
                    Intent intent = new Intent(FichaEspecie.this, GuiaNaturalezaEspacios.class);

                    Bundle bundle = new Bundle();
                    Log.d("OnClick() sais: ", "Nid" + especie.getNid());
                    bundle.putString("Filtrar por especie", especie.getNid());
                    intent.putExtras(bundle);

                    startActivity(intent);
                    */
                } else {
                    // No hay espacios asociados a esta especie.

                    Log.d("Espacios relacionados: ", "No hay espacios asociados a esta especie");
                }
            }
        });

        btnGaleria = (ImageButton) findViewById(R.id.btn_galeria);
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnAudio = (ImageButton) findViewById(R.id.btn_audio);
        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPuntuar = (ImageButton) findViewById(R.id.btn_puntuar);
        btnPuntuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnVideo = (ImageButton) findViewById(R.id.btn_video);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnMapa = (ImageButton) findViewById(R.id.btn_mapa);
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
