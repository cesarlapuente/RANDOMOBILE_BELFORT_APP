package eu.randomobile.pnrlorraine.mod_global.model;

public class Resource {

    private String idParent;
    private String id;

    public Resource(String idParent, String id) {
        this.idParent = idParent;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }
}
