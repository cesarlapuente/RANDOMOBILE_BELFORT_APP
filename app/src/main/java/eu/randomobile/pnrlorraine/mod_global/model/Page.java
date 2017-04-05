package eu.randomobile.pnrlorraine.mod_global.model;

public class Page {
    private int nid;
    private String title;
    private String body;

    public Page (int nid, String title, String body){
        this.nid = nid;
        this.title = title;
        this.body = body;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
