package propulsar.pgb.domainlayer.firebase_objects;

public class Usuario_FirebaseObj {

    public Long user;
    public Mensaje_FirebaseObj ultimoMensaje;
    public String nombre;
    public Long lastTimestamp;

    public Long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(Long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Mensaje_FirebaseObj getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(Mensaje_FirebaseObj ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public String toString(){
        return "user="+user+" nombre="+nombre+" lastTimestamp="+lastTimestamp+"ultimoMensaje[mensaje="+ultimoMensaje.getMensaje()+" vieneDeUsuario="+ultimoMensaje.isVieneDeUsuario()+"]";
    }

}
