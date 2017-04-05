package eu.randomobile.pnrlorraine.mod_global.model.taxonomy;

import android.widget.Button;

/**
 * Created by David on 5/4/16.
 */
public class Web {
    private int id;
    private String url;

    private Button button;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
