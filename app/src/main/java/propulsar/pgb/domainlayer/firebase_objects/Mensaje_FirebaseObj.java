package propulsar.pgb.domainlayer.firebase_objects;

public class Mensaje_FirebaseObj {

    private String mensaje;
    private Long timestamp;
    private String imgURL;
    private Long user;
    private boolean vieneDeUsuario;

    public Mensaje_FirebaseObj(){}

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public boolean isVieneDeUsuario() {
        return vieneDeUsuario;
    }

    public void setVieneDeUsuario(boolean vieneDeUsuario) {
        this.vieneDeUsuario = vieneDeUsuario;
    }

}
