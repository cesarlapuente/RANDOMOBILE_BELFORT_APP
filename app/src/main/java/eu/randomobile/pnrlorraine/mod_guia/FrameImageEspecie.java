package eu.randomobile.pnrlorraine.mod_guia;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.model.Especie;

/**
 * Created by David on 15/4/16.
 */
public class FrameImageEspecie extends DialogFragment {
    private MainApp app;

    private ImageView img_Picture;
    private ImageButton btn_closeDialog;

    private String id_especie;

    private Drawable drawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_image_especie, container);

        app = (MainApp) getActivity().getApplication();

        id_especie = app.getId_especie_ficha();

        img_Picture = (ImageView) view.findViewById(R.id.img_picture);

        for (Especie especie : app.getEspecies()) {
            if (especie.getNid().equals(id_especie)) {
                ImageLoader.getInstance().displayImage(especie.getImage(), img_Picture);

            }
        }

        btn_closeDialog = (ImageButton) view.findViewById(R.id.btn_close_dialog);
        btn_closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
