package eu.randomobile.pnrlorraine.mod_home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;

public class AdvicesActivity extends Activity {
    private MainApp app;

    private ImageMap mImageMap;

    private TableLayout tableLayout;

    private LinearLayout btn_LinearLayout_1L;
    private LinearLayout btn_LinearLayout_1R;
    private LinearLayout btn_LinearLayout_2L;
    private LinearLayout btn_LinearLayout_2R;

    private TextView textView_1L;
    private TextView textView_1R;
    private TextView textView_2L;
    private TextView textView_2R;

    private TextView textView_web;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_home__menu_advices);

        app = (MainApp) getApplication();

        initializeContent();
        // Para que la activite cargue una URL por defecto.
        buttonPressed(btn_LinearLayout_1L);
    }

    private void initializeContent() {
        mImageMap = (ImageMap) findViewById(R.id.menu_balisage);
        mImageMap.setAttributes(true, false, (float) 1.0, "mapa_consejos");
        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap) {
                if (mImageMap.getAreaAttribute(id, "name").equals("HOME")) {
                    cargaActivityHome();
                } else if (mImageMap.getAreaAttribute(id, "name").equals(
                        "BACK")) {
                    finish();
                } else {
                    mImageMap.showBubble(id);
                }
            }

            @Override
            public void onBubbleClicked(int id) {

            }
        });
        mImageMap.setImageResource(R.drawable.menu_consejos);

        tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        btn_LinearLayout_1L = (LinearLayout) findViewById(R.id.btn_LinearLayout_1L);
        btn_LinearLayout_1L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed(btn_LinearLayout_1L);
            }
        });
        btn_LinearLayout_1R = (LinearLayout) findViewById(R.id.btn_LinearLayout_1R);
        btn_LinearLayout_1R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed(btn_LinearLayout_1R);
            }
        });
        btn_LinearLayout_2L = (LinearLayout) findViewById(R.id.btn_LinearLayout_2L);
        btn_LinearLayout_2L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed(btn_LinearLayout_2L);
            }
        });
        btn_LinearLayout_2R = (LinearLayout) findViewById(R.id.btn_LinearLayout_2R);
        btn_LinearLayout_2R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed(btn_LinearLayout_2R);
            }
        });

        textView_1L = (TextView) findViewById(R.id.txt_1L);
        textView_1R = (TextView) findViewById(R.id.txt_1R);
        textView_2L = (TextView) findViewById(R.id.txt_2L);
        textView_2R = (TextView) findViewById(R.id.txt_2R);

        textView_web = (TextView) findViewById(R.id.txtWebAdvices);
        //textView_web.setBackgroundColor(ContextCompat.getColor(AdvicesActivity.this, R.color.blue_search));
    }

    private void cargaActivityHome() {
        Intent intent = new Intent(AdvicesActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void onResume() {
        super.onResume();
        mImageMap.mBubbleMap.clear();
        mImageMap.postInvalidate();
    }

    private void buttonPressed(LinearLayout linearLayout) {
        textView_1L.setTextColor(ContextCompat.getColor(AdvicesActivity.this, R.color.white));
        textView_1L.setText(Html.fromHtml(app.getDBHandler().getPageById(94).getTitle()).toString());
        textView_1R.setTextColor(ContextCompat.getColor(AdvicesActivity.this, R.color.white));
        textView_1R.setText(Html.fromHtml(app.getDBHandler().getPageById(95).getTitle()).toString());
        textView_2L.setTextColor(ContextCompat.getColor(AdvicesActivity.this, R.color.white));
        textView_2L.setText(Html.fromHtml(app.getDBHandler().getPageById(96).getTitle()).toString());
        textView_2R.setTextColor(ContextCompat.getColor(AdvicesActivity.this, R.color.white));
        textView_2R.setText(Html.fromHtml(app.getDBHandler().getPageById(97).getTitle()).toString());

        switch (linearLayout.getId()) {
            case R.id.btn_LinearLayout_1L: {
                textView_1L.setTextColor(ContextCompat.getColor(AdvicesActivity.this, R.color.btn_pressed));
                textView_web.setText(Html.fromHtml(app.getDBHandler().getPageById(94).getBody()).toString());
                break;
            }
            case R.id.btn_LinearLayout_1R: {
                textView_1R.setTextColor(ContextCompat.getColor(AdvicesActivity.this, R.color.btn_pressed));
                textView_web.setText(Html.fromHtml(app.getDBHandler().getPageById(95).getBody()).toString());
                break;
            }
            case R.id.btn_LinearLayout_2L: {
                textView_2L.setTextColor(ContextCompat.getColor(AdvicesActivity.this, R.color.btn_pressed));
                textView_web.setText(Html.fromHtml(app.getDBHandler().getPageById(96).getBody()).toString());
                break;
            }
            case R.id.btn_LinearLayout_2R: {
                textView_2R.setTextColor(ContextCompat.getColor(AdvicesActivity.this, R.color.btn_pressed));
                textView_web.setText(Html.fromHtml(app.getDBHandler().getPageById(97).getBody()).toString());
                break;
            }
        }
    }
}